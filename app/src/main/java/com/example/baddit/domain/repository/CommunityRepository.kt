package com.example.baddit.domain.repository

import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.community.GetACommunityResponseDTO
import com.example.baddit.domain.model.community.GetCommunityListResponseDTO

interface  CommunityRepository {
    suspend fun getCommunities(): Result<GetCommunityListResponseDTO, DataError.NetworkError>;
    suspend fun getCommunity(communityName: String): Result<GetACommunityResponseDTO, DataError.NetworkError>;
}