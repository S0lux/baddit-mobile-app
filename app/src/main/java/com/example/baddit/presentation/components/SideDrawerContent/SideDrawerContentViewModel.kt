package com.example.baddit.presentation.components.SideDrawerContent

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.community.Community
import com.example.baddit.domain.model.community.GetACommunityResponseDTO
import com.example.baddit.domain.repository.AuthRepository
import com.example.baddit.domain.repository.CommunityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SideDrawerContentViewModel @Inject constructor(val auth: AuthRepository, val com: CommunityRepository) : ViewModel() {
    var joinedCommunities: SnapshotStateList<Community> = mutableStateListOf()

    var isLoggedIn = auth.isLoggedIn

    fun getJoinCommunity(){
        joinedCommunities.clear()
        viewModelScope.launch {
            delay(timeMillis = 10)
            val res = auth.currentUser.value?.communities
            if(res!=null){
                res.forEach {
                    when(val result = com.getCommunity(communityName = it.name)){
                        is Result.Error->{

                        }
                        is Result.Success->{
                            joinedCommunities.add(result.data.community)
                            Log.d("joined", result.toString())

                        }
                    }
                }
            }
        }
    }
}
