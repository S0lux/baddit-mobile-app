package com.example.baddit.domain.model.profile

import com.example.baddit.domain.model.auth.GetMeResponseDTO
import com.example.baddit.domain.model.auth.GetOtherResponseDTO

sealed class UserProfile {
    data class OtherUser(val data: GetOtherResponseDTO) : UserProfile()
    data class Me(val data: GetMeResponseDTO) : UserProfile()
}