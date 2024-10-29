package com.example.baddit.presentation.utils

import android.os.Bundle
import android.util.Log
import androidx.navigation.NavType
import com.example.baddit.domain.model.posts.PostResponseDTOItem
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


val PostResponseNavType = object : NavType<PostResponseDTOItem>(
    isNullableAllowed = true
) {
    override fun get(bundle: Bundle, key: String): PostResponseDTOItem? {
        return bundle.getString(key)?.let<String, PostResponseDTOItem>(Json::decodeFromString)
    }

    override fun parseValue(value: String): PostResponseDTOItem {
        return Json.decodeFromString<PostResponseDTOItem>(value)
    }

    override fun put(bundle: Bundle, key: String, value: PostResponseDTOItem) {
        bundle.putString(key, Json.encodeToString(value))
    }

    override fun serializeAsValue(value: PostResponseDTOItem): String {
        val encodedAuthorUrl =
            URLEncoder.encode(value.author.avatarUrl, StandardCharsets.UTF_8.toString())

        val encodedCommunityUrl = if (value.community?.name != null) URLEncoder.encode(
            value.community.logoUrl,
            StandardCharsets.UTF_8.toString()
        ) else null

        val encodedContent = URLEncoder.encode(value.content, StandardCharsets.UTF_8.toString())
        val encodedTitle = URLEncoder.encode(value.title, StandardCharsets.UTF_8.toString())
        val encodedMediaUrls = value.mediaUrls.map { url ->
            URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
        }

        val temp = value.copy(
            author = value.author.copy(avatarUrl = encodedAuthorUrl),
            community = if (encodedCommunityUrl != null) value.community!!.copy(logoUrl = encodedAuthorUrl) else null,
            content = encodedContent,
            title = encodedTitle,
            mediaUrls = encodedMediaUrls
        )

        return Json.encodeToString(temp)
    }
}