package com.example.baddit.presentation.utils

import android.util.Log
import com.example.baddit.domain.model.posts.PostResponseDTOItem
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

fun decodePostResponseDTOItem(encodedItem: PostResponseDTOItem): PostResponseDTOItem {
    Log.d("DECODER", encodedItem.toString())
    val decodedAuthorUrl =
        URLDecoder.decode(encodedItem.author.avatarUrl, StandardCharsets.UTF_8.toString())

    val decodedContent = URLDecoder.decode(
        encodedItem.content.replace("%", "%25"),
        StandardCharsets.UTF_8.toString()
    )

    val decodedTitle =
        URLDecoder.decode(encodedItem.title.replace("%", "%25"), StandardCharsets.UTF_8.toString())

    val decodedMediaUrls = encodedItem.mediaUrls.map { url ->
        URLDecoder.decode(url, StandardCharsets.UTF_8.toString())
    }

    val decodedCommunityUrl = if (encodedItem.community?.name != null) URLDecoder.decode(
        encodedItem.community.logoUrl,
        StandardCharsets.UTF_8.toString()
    ) else null

    return encodedItem.copy(
        author = encodedItem.author.copy(avatarUrl = decodedAuthorUrl),
        community = if (decodedCommunityUrl != null) encodedItem.community!!.copy(logoUrl = decodedCommunityUrl) else null,
        content = decodedContent,
        title = decodedTitle,
        mediaUrls = decodedMediaUrls
    )
}