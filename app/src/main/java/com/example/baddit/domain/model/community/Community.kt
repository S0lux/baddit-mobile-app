package com.example.baddit.domain.model.community

data class Community(
    val id: String,
    val name: String,
    val ownerId: String,
    val description: String,
    val logoUrl: String,
    val bannerUrl: String,
    val status: String,
    val memberCount: Int,
    val deleted: Boolean,
    val createdAt: String,
    val updateAt: String
)

data class MutableCommunityResponseDTOItem (
    val id: String,
    val name: String,
    val ownerId: String,
    val description: String,
    val logoUrl: String,
    val bannerUrl: String,
    val status: String,
    val memberCount: Int,
    val deleted: Boolean,
    val createdAt: String,
    val updateAt: String
)

fun Community.toMutableCommunityResponseDTOItem() : MutableCommunityResponseDTOItem {
    return MutableCommunityResponseDTOItem(
        id = this.id,
        name = this.name,
        ownerId = this.ownerId,
        description = this.description,
        logoUrl = this.logoUrl,
        bannerUrl = this.bannerUrl,
        status = this.status,
        memberCount = this.memberCount,
        deleted = this.deleted,
        createdAt = this.createdAt,
        updateAt = this.updateAt
    )
}