package com.polis.app.model

import com.google.gson.annotations.SerializedName

data class Sorting(
    @SerializedName("property")
    val property: String,
    
    @SerializedName("direction")
    val direction: String
) 