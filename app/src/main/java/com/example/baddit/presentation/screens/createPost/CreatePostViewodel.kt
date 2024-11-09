package com.example.baddit.presentation.screens.createPost

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.community.Community
import com.example.baddit.domain.model.community.GetACommunityResponseDTO
import com.example.baddit.domain.repository.AuthRepository
import com.example.baddit.domain.repository.CommunityRepository
import com.example.baddit.domain.repository.PostRepository
import com.example.baddit.presentation.utils.FieldState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class CreatePostViewodel @Inject constructor(
    private val auth: AuthRepository,
    private val community: CommunityRepository,
    private val post: PostRepository
) : ViewModel() {

    var title by mutableStateOf(FieldState())
    var content by mutableStateOf(FieldState())
    var selectedCommunity by mutableStateOf<String?>(null)
    var selectedCommunityLogo by mutableStateOf("")

    var selectedImageUri by mutableStateOf<Uri?>(Uri.EMPTY)
    val isLoggedIn = auth.isLoggedIn

    var user = auth.currentUser.value

    var communities = mutableListOf<Community>()
    var joinedCommunities = mutableListOf<GetACommunityResponseDTO>()


    var error by mutableStateOf("")

    var isPosting by mutableStateOf(false)


    fun onTitleChange(input: String) {
        title = title.copy(value = input, error = "")
    }

    fun onContentChange(input: String) {
        content = content.copy(value = input, error = "")
    }


    init {
        viewModelScope.launch(context = Dispatchers.IO) {
            when (val res = community.getCommunities()) {
                is Result.Error -> {
                    Log.d("error", error)
                }

                is Result.Success -> {
                    communities.addAll(res.data)
                }
            }
            if(isLoggedIn.value){
                joinedCommunities.clear()
                when (val res = auth.getMe()) {
                    is Result.Error -> {
                        error = when (res.error) {
                            DataError.NetworkError.NO_INTERNET -> "No internet connection."
                            DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Server is down."
                            DataError.NetworkError.UNAUTHORIZED -> "Wrong username/password"
                            DataError.NetworkError.CONFLICT -> "This error shouldn't happen unless something changed in the backend."
                            DataError.NetworkError.UNKNOWN_ERROR -> "An unknown error has occurred"
                            DataError.NetworkError.FORBIDDEN -> "Email not verified."
                            else -> "This error shouldn't happen unless something changed in the backend."
                        }
                        error = ""
                    }

                    is Result.Success -> {
                        if (res.data.communities.isNotEmpty()) {
                            res.data.communities.forEach {
                                when (val result = community.getCommunity(communityName = it.name)) {
                                    is Result.Error -> {
                                        Log.d("second join", result.error.toString())
                                        error = when (result.error) {
                                            DataError.NetworkError.NO_INTERNET -> "No internet connection."
                                            DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Server is down."
                                            DataError.NetworkError.UNAUTHORIZED -> "Wrong username/password"
                                            DataError.NetworkError.CONFLICT -> "This error shouldn't happen unless something changed in the backend."
                                            DataError.NetworkError.UNKNOWN_ERROR -> "An unknown error has occurred from finding joined."
                                            DataError.NetworkError.FORBIDDEN -> "Email not verified."
                                            else -> "This error shouldn't happen unless something changed in the backend."
                                        }
                                        error = ""
                                    }

                                    is Result.Success -> {
                                        joinedCommunities.add(result.data)
                                    }

                                }
                            }
                        }
                    }

                }
            }

        }
    }

    fun uploadTextPost(context: Context) {
        if (title.value.isEmpty()) title = title.copy(error = "Missing title")
        if (content.value.isEmpty()) content = content.copy(error = "Missing content")
        if (content.error.isEmpty() && title.error.isEmpty()) {
            viewModelScope.launch(context = Dispatchers.IO) {
                isPosting = true
                when (val res = post.upLoadPost(
                    title = title.value,
                    content = content.value,
                    communityName = selectedCommunity,
                    type = "TEXT",
                    image = uriToFile(context, selectedImageUri)
                )) {
                    is Result.Error -> {
                        isPosting = false
                        error = when (res.error) {
                            DataError.NetworkError.NO_INTERNET -> "No internet connection."
                            DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Server is down."
                            DataError.NetworkError.UNAUTHORIZED -> "Wrong username/password"
                            DataError.NetworkError.CONFLICT -> "This error shouldn't happen unless something changed in the backend."
                            DataError.NetworkError.UNKNOWN_ERROR -> "An unknown error has occurred."
                            DataError.NetworkError.FORBIDDEN -> "Email not verified."
                            else -> "This error shouldn't happen unless something changed in the backend."
                        }
                        delay(timeMillis = 3000)
                        error = ""
                    }

                    is Result.Success -> {
                        isPosting = false
                        error = "Success"
                    }

                }

            }

        }
    }

    fun uploadMediaPost(context: Context) {
        if (title.value.isEmpty()) title = title.copy(error = "Missing title")
        if (selectedImageUri!!.equals(Uri.EMPTY)||selectedImageUri==null) {
            viewModelScope.launch(Dispatchers.IO) {
                error = "Please choose an image"
                delay(timeMillis = 1000)
                error = ""
            }
        }
        if (title.error.isEmpty() && !selectedImageUri!!.equals(Uri.EMPTY) && selectedImageUri!=null) {
            viewModelScope.launch(Dispatchers.IO) {
                isPosting = true
                when (val res = post.upLoadPost(
                    title = title.value,
                    content = "",
                    type = "MEDIA",
                    communityName = selectedCommunity,
                    image = uriToFile(context, selectedImageUri)
                )) {
                    is Result.Error -> {
                        isPosting = false
                        error = when (res.error) {
                            DataError.NetworkError.NO_INTERNET -> "No internet connection."
                            DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Server is down."
                            DataError.NetworkError.UNAUTHORIZED -> "Wrong username/password"
                            DataError.NetworkError.CONFLICT -> "This error shouldn't happen unless something changed in the backend."
                            DataError.NetworkError.UNKNOWN_ERROR -> "An unknown error has occurred."
                            DataError.NetworkError.FORBIDDEN -> "Email not verified."
                            else -> "This error shouldn't happen unless something changed in the backend."
                        }
                        delay(timeMillis = 3000)
                        error = ""
                    }

                    is Result.Success -> {
                        isPosting = false
                        error = "Success"
                    }

                }
            }
        }

    }

    fun uriToFile(context: Context, uri: Uri?): File? {
        if (uri == Uri.EMPTY|| uri == null) {
            return null // Return null if URI is empty
        }
        val file = File(context.cacheDir, "image.jpg")
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }
                outputStream.flush()
            }
        }
        return file
    }
}
