package com.vinee.arcampustreasurehunt.ui.ar

import android.location.Location
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.ar.core.Plane
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.node.ViewNode2
import com.vinee.arcampustreasurehunt.data.CollegeLocations
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vinee.arcampustreasurehunt.viewmodel.ArViewModel
import androidx.compose.runtime.livedata.observeAsState
import com.vinee.arcampustreasurehunt.location.LocationUpdatesLiveData

@Composable
fun ArCoreScreen(collegeName: String, modifier: Modifier = Modifier, viewModel: ArViewModel = viewModel()) {
    val activity = LocalContext.current as? ComponentActivity
    val context = LocalContext.current

    // Game state similar to the non-AR screen
    val college = remember { CollegeLocations.colleges.find { it.name == collegeName } }
    val currentBlockIndex = viewModel.currentBlockIndex
    val currentBlock = college?.blocks?.getOrNull(currentBlockIndex)

    // Shared state read by the AR label's Compose content inside ViewNode2
    val clueTitle = remember { mutableStateOf("") }
    val clueText = remember { mutableStateOf("") }
    LaunchedEffect(currentBlockIndex, currentBlock) {
        clueTitle.value = "\uD83D\uDD0D AR CLUE ${currentBlockIndex + 1}"
        clueText.value = currentBlock?.clue ?: "No clue available"
    }

    // Proximity-based auto "reached" detection using location updates (20 meters)
    val locationLiveData = remember { LocationUpdatesLiveData(context) }
    val currentLocation by locationLiveData.observeAsState()
    val distanceToTarget = remember(currentLocation, currentBlock) {
        val loc = currentLocation
        if (loc != null && currentBlock != null) {
            val target = Location("target").apply {
                latitude = currentBlock.latitude
                longitude = currentBlock.longitude
            }
            loc.distanceTo(target)
        } else Float.MAX_VALUE
    }
    LaunchedEffect(distanceToTarget) {
        if (distanceToTarget < 20f && viewModel.gameState == "clue") {
            viewModel.toReached()
        }
    }

    // A callback to clear the current AR label (set from within AndroidView factory)
    val clearLabelCallback = remember { mutableStateOf<(() -> Unit)?>(null) }
    // A callback to place/re-place the label manually from overlay
    val placeLabelCallback = remember { mutableStateOf<(() -> Unit)?>(null) }
    Box(modifier = modifier.fillMaxSize()) {
        // Status chip for quick visibility
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
        ) {
            androidx.compose.material3.Surface(
                color = Color.White.copy(alpha = 0.9f),
                shape = RoundedCornerShape(10.dp),
                shadowElevation = 2.dp
            ) {
                Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "AR • ${collegeName} • ${viewModel.gameState.uppercase()} • #${currentBlockIndex + 1}",
                        color = Color(0xFF7B1FA2),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                ARSceneView(
                    context = ctx,
                    sharedActivity = activity,
                    sharedLifecycle = activity?.lifecycle
                ).apply {
                    // Show detected planes
                    planeRenderer.isVisible = true

                    // Keep one label anchor node, create when a plane is found
                    var labelAnchor: AnchorNode? = null

                    // Expose clearer to overlay controls
                    clearLabelCallback.value = {
                        labelAnchor?.let { old ->
                            removeChildNode(old)
                            old.destroy()
                            labelAnchor = null
                        }
                    }

                    fun placeOrReplaceLabel() {
                        // Try a center-screen hit
                        val arHit = hitTestAR(
                            planeTypes = setOf(
                                Plane.Type.HORIZONTAL_UPWARD_FACING,
                                Plane.Type.VERTICAL
                            )
                        )
                        arHit?.createAnchor()?.let { anchor ->
                            // Clean old
                            labelAnchor?.let { old ->
                                removeChildNode(old)
                                old.destroy()
                            }
                            // Create with compose content bound to shared state
                            val anchorNode = AnchorNode(engine, anchor)
                            val viewNode = ViewNode2(
                                engine = engine,
                                windowManager = io.github.sceneview.SceneView.createViewNodeManager(ctx),
                                materialLoader = materialLoader,
                                unlit = true
                            ) {
                                MaterialTheme {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .background(Color(0xFFE1BEE7), RoundedCornerShape(12.dp))
                                            .border(2.dp, Color(0xFF7B1FA2), RoundedCornerShape(12.dp))
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = clueTitle.value,
                                            color = Color(0xFF7B1FA2),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = clueText.value,
                                            color = Color.Black,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                            viewNode.pxPerUnits = 1100f
                            anchorNode.addChildNode(viewNode)
                            addChildNode(anchorNode)
                            labelAnchor = anchorNode
                        }
                    }

                    // Auto-place when tracking updates and nothing placed yet
                    onSessionUpdated = { _, _ ->
                        if (labelAnchor == null) placeOrReplaceLabel()
                    }

                    // Tap to (re)place label at tapped position
                    setOnTouchListener { _, event ->
                            val arHit = hitTestAR(
                                xPx = event.x,
                                yPx = event.y,
                            planeTypes = setOf(
                                Plane.Type.HORIZONTAL_UPWARD_FACING,
                                Plane.Type.VERTICAL
                            )
                            )
                            arHit?.createAnchor()?.let { anchor ->
                                labelAnchor?.let { old ->
                                    removeChildNode(old)
                                    old.destroy()
                                }
                                val anchorNode = AnchorNode(engine, anchor)
                                val viewNode = ViewNode2(
                                    engine = engine,
                                    windowManager = io.github.sceneview.SceneView.createViewNodeManager(ctx),
                                    materialLoader = materialLoader,
                                    unlit = true
                                ) {
                                MaterialTheme {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .background(Color(0xFFE1BEE7), RoundedCornerShape(12.dp))
                                            .border(2.dp, Color(0xFF7B1FA2), RoundedCornerShape(12.dp))
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            text = clueTitle.value,
                                            color = Color(0xFF7B1FA2),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = clueText.value,
                                            color = Color.Black,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                            viewNode.pxPerUnits = 1100f
                            anchorNode.addChildNode(viewNode)
                            addChildNode(anchorNode)
                            labelAnchor = anchorNode
                        }
                        true
                    }
                    // Expose manual place callback
                    placeLabelCallback.value = { placeOrReplaceLabel() }
                }
            }
        )

        // Overlay: Next Clue button
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Manual place label
            Button(
                onClick = { placeLabelCallback.value?.invoke() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(bottom = 8.dp)
            ) { Text("\uD83D\uDCCD PLACE LABEL", fontWeight = FontWeight.Bold, color = Color(0xFF7B1FA2)) }
            // Optional utility: clear the AR label
            Button(
                onClick = { clearLabelCallback.value?.invoke() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(bottom = 8.dp)
            ) { Text("\uD83E\uDEA8 CLEAR LABEL", fontWeight = FontWeight.Bold, color = Color(0xFF7B1FA2)) }

            // Play Video / Next flow similar to non-AR
            if (viewModel.gameState == "reached" && currentBlock != null) {
                Button(
                    onClick = { viewModel.toVideo() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(bottom = 8.dp)
                ) { Text("\uD83C\uDFAC PLAY VIDEO", fontWeight = FontWeight.Bold, color = Color(0xFF7B1FA2)) }
            }

            Button(
                onClick = {
                    college?.let { viewModel.nextClue(it.blocks.size) }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = if (college == null || currentBlockIndex >= (college.blocks.size - 1)) "\uD83C\uDFC6 FINISH HUNT" else "\u27A1\uFE0F NEXT CLUE",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }
        }
    }
}
