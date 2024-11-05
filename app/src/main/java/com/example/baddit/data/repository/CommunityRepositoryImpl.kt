package com.example.baddit.data.repository

import com.example.baddit.data.dto.ErrorResponse
import com.example.baddit.data.dto.auth.RegisterRequestBody
import com.example.baddit.data.dto.community.CreateRequestBody
import com.example.baddit.data.remote.BadditAPI
import com.example.baddit.data.utils.safeApiCall
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.community.GetACommunityResponseDTO
import com.example.baddit.domain.model.community.GetCommunityListResponseDTO
import com.example.baddit.domain.repository.CommunityRepository
import com.google.gson.Gson
import javax.inject.Inject

class CommunityRepositoryImpl @Inject constructor(
    private val badditAPI: BadditAPI
) : CommunityRepository {
    override suspend fun getCommunities(): Result<GetCommunityListResponseDTO, DataError.NetworkError> {
        return safeApiCall { badditAPI.getCommunities() }
    }

    override suspend fun getCommunity(communityName: String): Result<GetACommunityResponseDTO, DataError.NetworkError> {
        return safeApiCall { badditAPI.getCommunity(communityName) }
    }

    override suspend fun createCommunity(
        name: String,
        description: String
    ): Result<Unit, DataError.NetworkError> {
        return safeApiCall { badditAPI.createCommunity(CreateRequestBody(name, description)) }
    }
}
