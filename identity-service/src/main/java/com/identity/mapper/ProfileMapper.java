package com.identity.mapper;

import org.mapstruct.Mapper;

import com.identity.dto.request.ProfileCreationRequest;
import com.identity.dto.request.UserCreationRequest;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    ProfileCreationRequest toProfileCreationRequest(UserCreationRequest request);
}
