package com.vinee.arcampustreasurehunt.data

data class Block(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val clue: String,
    val video: String
)

data class College(
    val name: String,
    val blocks: List<Block>,
    val website: String
)

object CollegeLocations {
    
    // GVPCE College blocks with clues and videos
    private val gvpceBlocks = listOf(
        Block(
            "Civil Block", 
            17.8203, 
            83.3428, 
            "Where concrete ideas take solid shape, and structures rise from drawings",
            "gvpce_civil_block.mp4"
        ),
        Block(
            "Chemical Block", 
            17.8205, 
            83.3425, 
            "where liquids separate, gases combine and solids find new forms",
            "gvpce_chemical_block.mp4"
        ),
        Block(
            "EEE Block", 
            17.8213, 
            83.3415, 
            "Power plants, motors, and microchips all begin as lessons here",
            "gvpce_eee_block.mp4"
        ),
        Block(
            "ECE Block", 
            17.8215, 
            83.3412, 
            "From resistors to routers, everything begins in this block of signals",
            "gvpce_ece_block.mp4"
        ),
        Block(
            "CSE Block", 
            17.8213, 
            83.3411, 
            "Here, bugs are hunted not in gardens, but in lines of text",
            "gvpce_cse_block.mp4"
        ),
        Block(
            "IT Block", 
            17.8211, 
            83.3415, 
            "Where data finds meaning, and information turns into power.",
            "gvpce_it_block.mp4"
        ),
        Block(
            "Mechanical Block", 
            17.8207, 
            83.3426, 
            "Where gears turn,pistons move, and machines come alive on paper first.",
            "gvpce_mechanical_block.mp4"
        )
    )

    // MVGR College blocks with clues and videos
    private val mvgrBlocks = listOf(
        Block(
            "Mechanical Block", 
            18.06035, 
            83.40407, 
            "Where gears turn,pistons move, and machines come alive on paper first.",
            "mvgr_mech_block.mp4"
        ),
        Block(
            "ECE Block", 
            18.0601, 
            83.40469, 
            "From resistors to routers, everything begins in this block of signals",
            "mvgr_ece_block.mp4"
        ),
        Block(
            "CSE Block", 
            18.06094, 
            83.40532, 
            "Here, bugs are hunted not in gardens, but in lines of text",
            "mvgr_cse_block.mp4"
        ),
        Block(
            "Data Engineering Block", 
            18.06189, 
            83.40395, 
            "Where raw numbers are mined, cleaned, and turned into gold for decisions",
            "mvgr_data_block.mp4"
        ),
        Block(
            "Civil Block", 
            18.06108, 
            83.40532, 
            "Where concrete ideas take solid shape, and structures rise from drawings",
            "mvgr_civil_block.mp4"
        )
    )

    // List of all colleges
    val colleges = listOf(
        College("GVPCE", gvpceBlocks, "https://www.gvpce.ac.in/"),
        College("MVGR", mvgrBlocks, "https://www.mvgrce.com/")
    )
}
