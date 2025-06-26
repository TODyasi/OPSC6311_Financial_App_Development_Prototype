package com.example.opsc_6311_poe_prototype_v2

data class Catagories(
    var categoryName: String? = null,
    var categoryBudget: Double = 0.0
) {
    // Empty constructor required for Firebase
    constructor() : this(null)
}
