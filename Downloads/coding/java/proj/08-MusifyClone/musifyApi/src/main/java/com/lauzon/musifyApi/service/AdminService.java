package com.lauzon.musifyApi.service;

import com.lauzon.musifyApi.dto.response.ArtistResponse;
import com.lauzon.musifyApi.dto.response.UserResponse;
import com.lauzon.musifyApi.exceptions.ResourceNotFoundException;
import com.lauzon.musifyApi.exceptions.UserNotFoundException;

import java.util.List;
import java.util.Map;

public interface AdminService {

    ArtistResponse viewPendingArtistRequest(String requestId);

    List<ArtistResponse> viewAllPendingArtistRequest();

    Map<String, String> approveArtistRequest(String requestId);

    Map<String, String> rejectArtistRequest(String requestId);

    List<UserResponse> viewAllUsers();

    UserResponse viewUser(String userId);

    Map<String, String> makeUserAdmin(String userId);

}