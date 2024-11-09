package com.example.baddit.domain.repository

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.community.GetACommunityResponseDTO
import com.example.baddit.domain.model.community.GetCommunityListResponseDTO
import com.example.baddit.domain.model.community.Members
import com.example.baddit.domain.model.community.Moderators
import com.example.baddit.domain.model.community.MutableCommunityResponseDTOItem
import java.io.File

interface  CommunityRepository {
    suspend fun getCommunities(cursor: String? = null): Result<GetCommunityListResponseDTO, DataError.NetworkError>
    var communityCache: SnapshotStateList<MutableCommunityResponseDTOItem>
    suspend fun getCommunity(communityName: String): Result<GetACommunityResponseDTO, DataError.NetworkError>;
    suspend fun createCommunity(name: String, description: String): Result<Unit, DataError.NetworkError>
    suspend fun joinCommunity(communityName: String): Result<Unit, DataError.NetworkError>
    suspend fun leaveCommunity(communityName: String): Result<Unit, DataError.NetworkError>
    suspend fun updateCommunityLogo(communityName: String, imageFile: File): Result<Unit, DataError.NetworkError>
    suspend fun updateCommunityBanner(communityName: String, imageFile: File): Result<Unit, DataError.NetworkError>
    suspend fun deleteCommunity(communityName: String): Result<Unit, DataError.NetworkError>
    suspend fun getMembers(communityName: String): Result<Members, DataError.NetworkError>
    suspend fun getModerators(communityName: String): Result<Moderators, DataError.NetworkError>
    suspend fun moderateMember(communityName: String, memberName: String): Result<Unit, DataError.NetworkError>
    suspend fun unModerateMember(communityName: String, memberName: String): Result<Unit, DataError.NetworkError>
}