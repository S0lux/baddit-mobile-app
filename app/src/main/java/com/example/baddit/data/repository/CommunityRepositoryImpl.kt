package com.example.baddit.data.repository

import com.example.baddit.data.remote.BadditAPI
import com.example.baddit.data.utils.safeApiCall
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.auth.LoginResponseDTO
import com.example.baddit.domain.model.community.CommunityDTO
import com.example.baddit.domain.model.community.CommunityResponseDTO
import com.example.baddit.domain.repository.CommunityRepository
import javax.inject.Inject

class CommunityRepositoryImpl @Inject constructor(
    private val badditAPI: BadditAPI
): CommunityRepository {
    override suspend fun getCommunity(name: String): Result<CommunityResponseDTO, DataError.NetworkError> {
        return safeApiCall { badditAPI.getCommunities(name = name) }
    }

}