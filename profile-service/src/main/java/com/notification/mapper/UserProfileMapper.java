package com.notification.mapper;

import org.mapstruct.Mapper;

import com.notification.dto.request.ProfileCreationRequest;
import com.notification.dto.response.UserProfileResponse;
import com.notification.entity.UserProfile;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    UserProfile toUserProfile(ProfileCreationRequest request);

    UserProfileResponse toUserProfileResponse(UserProfile entity);
}
