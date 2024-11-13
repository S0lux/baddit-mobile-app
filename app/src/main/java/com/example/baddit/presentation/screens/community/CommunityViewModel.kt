package com.example.baddit.presentation.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.community.GetACommunityResponseDTO
import com.example.baddit.domain.model.community.GetCommunityListResponseDTO
import com.example.baddit.domain.model.community.Members
import com.example.baddit.domain.model.community.Moderators
import com.example.baddit.domain.model.community.MutableCommunityResponseDTOItem
import com.example.baddit.domain.model.community.toMutableCommunityResponseDTOItem
import com.example.baddit.domain.model.posts.toMutablePostResponseDTOItem
import com.example.baddit.domain.repository.AuthRepository
import com.example.baddit.domain.repository.CommunityRepository
import com.example.baddit.domain.repository.PostRepository
import com.example.baddit.presentation.utils.FieldState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    val imageLoader: ImageLoader,
    val communityRepository: CommunityRepository,
    val postRepository: PostRepository,
    val authRepository: AuthRepository
) : ViewModel() {

    val communityList = mutableStateOf<GetCommunityListResponseDTO>(GetCommunityListResponseDTO())
    val community = mutableStateOf<GetACommunityResponseDTO?>(null)

    val listCommunity =  SnapshotStateList<MutableCommunityResponseDTOItem>()

    val memberList = mutableStateOf(Members())
    val moderatorList = mutableStateOf(Moderators())


    val me = authRepository.currentUser
    val loggedIn = authRepository.isLoggedIn;

    //posts
    val posts = postRepository.postCache;
    private var lastPostId: String? = null;
    var endReached = false;

    var nameState by mutableStateOf(FieldState())
        private set;

    var descriptionState by mutableStateOf(FieldState())
        private set;

    var isLoading by mutableStateOf(false)
        private set

    var isCreateDone by mutableStateOf(false)
        private set

    var isRefreshing by mutableStateOf(false)
        private set;

    var error by mutableStateOf("")

    var endCommunityReached = false;
    private var lastCursor: String? = null;
    var isRefreshingCommunity by mutableStateOf(false)

    fun refreshCommunities(){
        endCommunityReached = false;
        viewModelScope.launch {
            isRefreshingCommunity = true;
            when (val fetchCommunities = communityRepository.getCommunities()) {
                is Result.Error -> {
                    error = when (fetchCommunities.error) {
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to establish connection to server"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown network error has occurred"
                    }
                }

                is Result.Success -> {
                    communityRepository.communityCache.clear()
                    error = ""
                    if (!fetchCommunities.data.isEmpty())
                        lastCursor = fetchCommunities.data[fetchCommunities.data.size -2].id
                    communityRepository.communityCache.addAll(fetchCommunities.data.map { it.toMutableCommunityResponseDTOItem() })
                }
            }
            isRefreshingCommunity = false
        }
    }

    fun loadMoreCommunities(){
        if (endCommunityReached || isRefreshingCommunity) return
        viewModelScope.launch {
            isRefreshingCommunity = true
            when (val fetchCommunities = communityRepository.getCommunities(cursor = lastCursor)) {
                is Result.Error -> {
                    error = when (fetchCommunities.error) {
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to establish connection to server"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown network error has occurred"
                    }
                }
                is Result.Success -> {
                    error = ""
                    val newCommunities = fetchCommunities.data.filter { newCommunity ->
                        communityRepository.communityCache.none { existingCommunity ->
                            existingCommunity.id == newCommunity.id
                        }
                    }
                    if (newCommunities.isNotEmpty()) {
                        lastCursor = newCommunities.last().id
                        communityRepository.communityCache.addAll(newCommunities.map { it.toMutableCommunityResponseDTOItem() })
                    } else {
                        endCommunityReached = true
                    }
                }
            }
            isRefreshingCommunity = false
        }
    }


    fun fetchCommunityList() {
        viewModelScope.launch {
            when (val result = communityRepository.getCommunities()) {
                is Result.Error -> {
                    error = when (result.error) {
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to establish connection to server"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown network error has occurred"
                    }
                }

                is Result.Success -> {
                    communityList.value = result.data
                    error = ""
                }
            }
        }
    }

    fun fetchCommunity(communityName: String) {
        viewModelScope.launch {
            when (val result = communityRepository.getCommunity(communityName)) {
                is Result.Error -> {
                    error = when (result.error) {
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to establish connection to server"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown network error has occurred"
                    }
                }

                is Result.Success -> {
                    community.value = result.data
                    error = ""
                }
            }
        }
    }

    fun setName(input: String) {
        nameState = nameState.copy(value = input, error = "")
    }

    fun setDescription(input: String) {
        descriptionState = descriptionState.copy(value = input, error = "")
    }

    fun handleCreateCommunityError(error: DataError.NetworkError) {
        when (error) {
            DataError.NetworkError.CONFLICT -> {
                nameState = nameState.copy(error = "This community name is already taken.")

            }

            DataError.NetworkError.NO_INTERNET,
            DataError.NetworkError.INTERNAL_SERVER_ERROR,
            DataError.NetworkError.FORBIDDEN,
            DataError.NetworkError.TOKEN_INVALID,
            DataError.NetworkError.UNKNOWN_ERROR -> {
            }

            DataError.NetworkError.UNAUTHORIZED -> {
            }

        }
    }

    fun resetCreateState() {
        setName("")
        setDescription("")
        isLoading = false
        isCreateDone = false
    }

    suspend fun createCommunity(): Result<Unit, DataError.NetworkError> {
        isLoading = true
        val result = communityRepository.createCommunity(nameState.value, descriptionState.value)
        isLoading = false
        when (result) {
            is Result.Error -> handleCreateCommunityError(result.error)
            is Result.Success -> {
                isCreateDone = true
                refreshCommunities()
            }
        }

        return result;
    }

    fun createCommunityNonSuspend() {
        viewModelScope.launch {
            createCommunity()
        }
    }

    fun joinCommunity(communityName: String) {
        viewModelScope.launch {
            isLoading = true
            val result = communityRepository.joinCommunity(communityName)
            isLoading = false
            when (result) {
                is Result.Error -> {
                    error = when (result.error) {
                        DataError.NetworkError.CONFLICT -> "User already in this community"
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to establish connection to server"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown network error has occurred"
                    }
                }

                is Result.Success -> {
                    error = ""
                    fetchCommunity(communityName)
                    fetchMembers(communityName)
                }
            }
        }
    }

    fun leaveCommunity(communityName: String) {
        viewModelScope.launch {
            isLoading = true
            val result = communityRepository.leaveCommunity(communityName)
            isLoading = false
            when (result) {
                is Result.Error -> {
                    error = when (result.error) {
                        DataError.NetworkError.CONFLICT -> "User already out of this community"
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to establish connection to server"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown network error has occurred"
                    }
                }

                is Result.Success -> {
                    error = ""
                    fetchCommunity(communityName)
                    fetchMembers(communityName)
                }
            }
        }
    }

    fun updateCommunityBanner(communityName: String, imageFile: File) {
        viewModelScope.launch {
            isLoading = true
            val result = communityRepository.updateCommunityBanner(communityName, imageFile)
            isLoading = false
            when (result) {
                is Result.Error -> {
                    error = when (result.error) {
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to establish connection to server"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown network error has occurred"
                    }
                }

                is Result.Success -> {
                    error = ""
                    fetchCommunity(communityName)
                }
            }
        }
    }

    fun updateCommunityLogo(communityName: String, imageFile: File) {
        viewModelScope.launch {
            isLoading = true
            val result = communityRepository.updateCommunityLogo(communityName, imageFile)
            isLoading = false
            when (result) {
                is Result.Error -> {
                    error = when (result.error) {
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to establish connection to server"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown network error has occurred"
                    }
                }

                is Result.Success -> {
                    error = ""
                    fetchCommunity(communityName)
                }
            }
        }
    }

    fun deleteCommunity(communityName: String) {
        viewModelScope.launch {
            isLoading = true
            val result = communityRepository.deleteCommunity(communityName)
            isLoading = false
            when (result) {
                is Result.Error -> {
                    error = when (result.error) {
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to establish connection to server"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown network error has occurred"
                    }
                }

                is Result.Success -> {
                    error = ""
                }
            }
        }
    }

    fun refreshPosts(communityName: String) {
        endReached = false;
        viewModelScope.launch {
            isRefreshing = true;
            when (val fetchPosts = postRepository.getPosts(communityName = communityName)) {
                is Result.Error -> {
                    error = when (fetchPosts.error) {
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to establish connection to server"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown network error has occurred"
                    }
                }

                is Result.Success -> {
                    posts.clear()
                    error = ""
                    if (!fetchPosts.data.isEmpty())
                        lastPostId = fetchPosts.data.last().id
                    posts.addAll(fetchPosts.data.map { it.toMutablePostResponseDTOItem() })
                }
            }
            isRefreshing = false
        }
    }

    fun loadMorePosts(communityName: String) {
        if (endReached)
            return;
        viewModelScope.launch {
            isRefreshing = true;
            when (val fetchPosts =
                postRepository.getPosts(cursor = lastPostId, communityName = communityName)) {
                is Result.Error -> {
                    error = when (fetchPosts.error) {
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to establish connection to server"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown network error has occurred"
                    }
                }

                is Result.Success -> {
                    error = ""
                    if (fetchPosts.data.isNotEmpty()) {
                        lastPostId = fetchPosts.data.last().id
                        posts.addAll(fetchPosts.data.map { it.toMutablePostResponseDTOItem() })
                    } else {
                        endReached = true
                    }
                }
            }
            isRefreshing = false;
        }
    }

    fun fetchMembers(communityName: String) {
        viewModelScope.launch {
            isLoading = true
            when (val result = communityRepository.getMembers(communityName)) {
                is Result.Error -> {
                    error = when (result.error) {
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to establish connection to server"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown network error has occurred"
                    }
                    isLoading = false
                }

                is Result.Success -> {
                    error = ""
                    memberList.value = result.data
                    isLoading = false
                }
            }
        }
    }

    fun fetchModerators(communityName: String) {
        viewModelScope.launch {
            isLoading = true
            when (val result = communityRepository.getModerators(communityName)) {
                is Result.Error -> {
                    error = when (result.error) {
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to establish connection to server"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown network error has occurred"
                    }
                    isLoading = false
                }

                is Result.Success -> {
                    moderatorList.value = result.data
                    error = ""
                    isLoading = false
                }
            }
        }
    }

    fun moderateMember(communityName: String, memberName: String) {
        viewModelScope.launch {
            isLoading = true
            val result = communityRepository.moderateMember(communityName, memberName)
            isLoading = false
            when (result) {
                is Result.Error -> {
                    error = when (result.error) {
                        DataError.NetworkError.CONFLICT -> "User already in this community"
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to establish connection to server"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown network error has occurred"
                    }
                }

                is Result.Success -> {
                    error = ""
                    fetchCommunity(communityName)
                    fetchMembers(communityName)
                }
            }
        }
    }

    fun unModerateMember(communityName: String, memberName: String) {
        viewModelScope.launch {
            isLoading = true
            val result = communityRepository.unModerateMember(communityName, memberName)
            isLoading = false
            when (result) {
                is Result.Error -> {
                    error = when (result.error) {
                        DataError.NetworkError.CONFLICT -> "User already in this community"
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to establish connection to server"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown network error has occurred"
                    }
                }

                is Result.Success -> {
                    error = ""
                    fetchCommunity(communityName)
                    fetchMembers(communityName)
                }
            }
        }
    }

}
