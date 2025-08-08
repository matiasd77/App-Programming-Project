package com.polis.app.model

import com.google.gson.annotations.SerializedName

data class ServerStatus(
    @SerializedName("code")
    val code: String,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("severity")
    val severity: String? = null
) 