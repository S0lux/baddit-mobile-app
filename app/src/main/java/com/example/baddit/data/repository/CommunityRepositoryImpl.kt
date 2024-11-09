package com.example.baddit.data.repository

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.baddit.data.dto.community.CreateRequestBody
import com.example.baddit.data.dto.community.ModerateMemberRequestBody
import com.example.baddit.data.remote.BadditAPI
import com.example.baddit.data.utils.safeApiCall
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.community.GetACommunityResponseDTO
import com.example.baddit.domain.model.community.GetCommunityListResponseDTO
import com.example.baddit.domain.model.community.Members
import com.example.baddit.domain.model.community.Moderators
import com.example.baddit.domain.model.community.MutableCommunityResponseDTOItem
import com.example.baddit.domain.repository.CommunityRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class CommunityRepositoryImpl @Inject constructor(
    private val badditAPI: BadditAPI
) : CommunityRepository {
    override suspend fun getCommunities(cursor: String?): Result<GetCommunityListResponseDTO, DataError.NetworkError> {
        val result = safeApiCall<GetCommunityListResponseDTO, DataError.NetworkError> {
            badditAPI.getCommunities(cursor = cursor)
        }
        return result
    }

    override var communityCache: SnapshotStateList<MutableCommunityResponseDTOItem> =
        mutableStateListOf()

    override suspend fun getCommunity(communityName: String): Result<GetACommunityResponseDTO, DataError.NetworkError> {
        return safeApiCall { badditAPI.getCommunity(communityName) }
    }

    override suspend fun createCommunity(
        name: String,
        description: String
    ): Result<Unit, DataError.NetworkError> {
        return safeApiCall { badditAPI.createCommunity(CreateRequestBody(name, description)) }
    }

    override suspend fun joinCommunity(communityName: String): Result<Unit, DataError.NetworkError> {
        return safeApiCall { badditAPI.joinCommunity(communityName) }
    }

    override suspend fun leaveCommunity(communityName: String): Result<Unit, DataError.NetworkError> {
        return safeApiCall { badditAPI.leaveCommunity(communityName) }
    }

    override suspend fun updateCommunityBanner(
        communityName: String,
        imageFile: File
    ): Result<Unit, DataError.NetworkError> {
        val requestBody = MultipartBody.Part.createFormData(
            "banner",
            imageFile.name,
            imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        )
        return safeApiCall { badditAPI.uploadBanner(communityName, requestBody) }
    }

    override suspend fun updateCommunityLogo(
        communityName: String,
        imageFile: File
    ): Result<Unit, DataError.NetworkError> {
        val requestBody = MultipartBody.Part.createFormData(
            "logo",
            imageFile.name,
            imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        )
        return safeApiCall { badditAPI.uploadLogo(communityName, requestBody) }
    }

    override suspend fun deleteCommunity(communityName: String): Result<Unit, DataError.NetworkError> {
        return safeApiCall { badditAPI.deleteCommunity(communityName) }
    }

    override suspend fun getMembers(communityName: String): Result<Members, DataError.NetworkError> {
        return safeApiCall { badditAPI.getMembers(communityName) }
    }

    override suspend fun getModerators(communityName: String): Result<Moderators, DataError.NetworkError> {
        return safeApiCall { badditAPI.getModerators(communityName) }
    }

    override suspend fun moderateMember(
        communityName: String,
        memberName: String
    ): Result<Unit, DataError.NetworkError> {
        return safeApiCall {
            badditAPI.moderateMember(
                communityName,
                ModerateMemberRequestBody(memberName)
            )
        }
    }

    override suspend fun unModerateMember(
        communityName: String,
        memberName: String
    ): Result<Unit, DataError.NetworkError> {
        return safeApiCall { badditAPI.unModerateMember(communityName, memberName) }
    }
}
