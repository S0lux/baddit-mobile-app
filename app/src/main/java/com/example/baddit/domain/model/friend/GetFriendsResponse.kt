package com.example.baddit.domain.model.friend

data class GetFriendsResponse(
    var currentFriends: Array<BaseFriendUser>,
    var incomingRequests: Array<IncomingFriendRequestDto>,
    var outgoingRequests: Array<OutgoingFriendRequestDto>
)