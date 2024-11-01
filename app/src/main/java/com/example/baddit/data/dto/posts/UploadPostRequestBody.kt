package com.example.baddit.data.dto.posts

import retrofit2.http.Part
import java.io.File

data class UploadPostRequestBody (
    val title: String,
    val content: String,
    val type: String,
    val communityName: String?,
)