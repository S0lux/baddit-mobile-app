package com.example.baddit.domain.repository

import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.community.GetACommunityResponseDTO
import com.example.baddit.domain.model.community.GetCommunityListResponseDTO
import java.io.File

interface  CommunityRepository {
    suspend fun getCommunities(): Result<GetCommunityListResponseDTO, DataError.NetworkError>;
    suspend fun getCommunity(communityName: String): Result<GetACommunityResponseDTO, DataError.NetworkError>;
    suspend fun createCommunity(name: String, description: String): Result<Unit, DataError.NetworkError>
    suspend fun joinCommunity(communityName: String): Result<Unit, DataError.NetworkError>
    suspend fun leaveCommunity(communityName: String): Result<Unit, DataError.NetworkError>
    suspend fun updateCommunityLogo(communityName: String, imageFile: File): Result<Unit, DataError.NetworkError>
    suspend fun updateCommunityBanner(communityName: String, imageFile: File): Result<Unit, DataError.NetworkError>
    suspend fun deleteCommunity(communityName: String): Result<Unit, DataError.NetworkError>
}