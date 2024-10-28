package com.example.baddit.domain.repository

import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.community.CommunityDTO
import com.example.baddit.domain.model.community.CommunityResponseDTO

interface CommunityRepository {
    suspend fun getCommunity(name: String): Result<CommunityResponseDTO, DataError.NetworkError>
}