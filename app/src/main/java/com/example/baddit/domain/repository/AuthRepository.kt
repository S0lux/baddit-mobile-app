package com.example.baddit.domain.repository

interface AuthRepository {
    suspend fun login(username: String, password: String)
    suspend fun register(email: String, username: String, password: String)
}