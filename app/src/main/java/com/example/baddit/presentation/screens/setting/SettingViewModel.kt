package com.example.baddit.presentation.screens.setting

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baddit.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel(){

    val auth = authRepository;

    var oldPassword = mutableStateOf("");
    var newPassword = mutableStateOf("");
    var checkPassword= mutableStateOf("");
    fun setOldPassword(content: String){
        oldPassword.value = content;
    }

    fun setNewPassword(content: String){
        newPassword.value = content;
    }

    fun setCheckPassword(content: String){
        checkPassword.value = content;
    }

//    fun ChangePassword(){
//        viewModelScope.launch {
//            authRepository.changePassword(oldPassword.value, newPassword.value);
//        }
//
//        return false;
//    }

}