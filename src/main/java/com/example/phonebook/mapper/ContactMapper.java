package com.example.phonebook.mapper;

import com.example.phonebook.controller.dto.AddressDto;
import com.example.phonebook.controller.request.CreateContactRequestDto;
import com.example.phonebook.controller.request.UpdateContactRequestDto;
import com.example.phonebook.controller.response.ContactResponseDto;
import com.example.phonebook.entity.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import java.util.List;

@Component
public class ContactMapper {

    @Autowired
    private AddressMapper addressMapper;

    public ContactResponseDto toDto(Contact contact) {
        AddressDto addressDto = addressMapper.toDto(contact.getAddress());
        return new ContactResponseDto(contact.getId(), contact.getUserId(), contact.getFirstName(), contact.getLastName(), contact.getPhoneNumber(), addressDto, contact.getCreateAt(), contact.getUpdateAt());
    }

    public List<ContactResponseDto> toDtoList(List<Contact> contacts) {
        return contacts.stream().map(this::toDto).toList();
    }

    public Page<ContactResponseDto> toDtoPage(Page<Contact> contactPage) {
        List<ContactResponseDto> dtoList = toDtoList(contactPage.getContent());
        return new PageImpl<>(dtoList, contactPage.getPageable(), contactPage.getTotalElements());
    }

    public Contact toEntity(String userId, CreateContactRequestDto createContactRequestDto) {
        Contact contact = new Contact();
        contact.setUserId(userId);
        contact.setFirstName(createContactRequestDto.getFirstName());
        contact.setLastName(createContactRequestDto.getLastName());
        contact.setPhoneNumber(createContactRequestDto.getPhoneNumber());
        contact.setAddress(addressMapper.toEntity(createContactRequestDto.getAddress()));
        return contact;
    }

    public Contact toEntity(String userId, UpdateContactRequestDto updateContactRequestDto) {
        Contact contact = new Contact();
        contact.setUserId(userId);
        contact.setFirstName(updateContactRequestDto.getFirstName());
        contact.setLastName(updateContactRequestDto.getLastName());
        contact.setPhoneNumber(updateContactRequestDto.getPhoneNumber());
        contact.setAddress(addressMapper.toEntity(updateContactRequestDto.getAddress()));
        return contact;
    }
}
