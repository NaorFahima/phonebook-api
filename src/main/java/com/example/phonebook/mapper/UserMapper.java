package com.example.phonebook.mapper;

import com.example.phonebook.controller.dto.AddressDto;
import com.example.phonebook.controller.request.CreateUserRequestDto;
import com.example.phonebook.controller.request.UpdateUserRequestDto;
import com.example.phonebook.controller.response.UserResponseDto;
import com.example.phonebook.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    @Autowired
    private AddressMapper addressMapper;

    public UserResponseDto toDto(User user) {
        AddressDto addressDto = addressMapper.toDto(user.getAddress());
        return new UserResponseDto(user.getId(), user.getFirstName(), user.getLastName(), user.getPhoneNumber(), addressDto, user.getCreateAt(), user.getUpdateAt());
    }

    public List<UserResponseDto> toDtoList(List<User> users) {
        return users.stream().map(this::toDto).toList();
    }

    public Page<UserResponseDto> toDtoPage(Page<User> userPage) {
        List<UserResponseDto> dtoList = toDtoList(userPage.getContent());
        return new PageImpl<>(dtoList, userPage.getPageable(), userPage.getTotalElements());
    }

    public User toEntity(CreateUserRequestDto userRequestDto) {
        User user = new User();
        user.setFirstName(userRequestDto.getFirstName());
        user.setLastName(userRequestDto.getLastName());
        user.setPhoneNumber(userRequestDto.getPhoneNumber());
        user.setAddress(addressMapper.toEntity(userRequestDto.getAddress()));
        return user;
    }

    public User toEntity(UpdateUserRequestDto userRequestDto) {
        User user = new User();
        user.setFirstName(userRequestDto.getFirstName());
        user.setLastName(userRequestDto.getLastName());
        user.setPhoneNumber(userRequestDto.getPhoneNumber());
        user.setAddress(addressMapper.toEntity(userRequestDto.getAddress()));
        return user;
    }

}