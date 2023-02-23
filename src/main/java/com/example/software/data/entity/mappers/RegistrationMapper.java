package com.example.software.data.entity.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class RegistrationMapper {

    private final ModelMapper modelMapper;

    public RegistrationMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

//    public RegistrationDTO toDto(User user){
//        modelMapper.getConfiguration()
//                   .setMatchingStrategy(MatchingStrategies.LOOSE);
//        return modelMapper.map(user, RegistrationDTO.class);
//
//    }
//
//    public User toUser(RegistrationDTO registrationDTO){
//        modelMapper.getConfiguration()
//                   .setMatchingStrategy(MatchingStrategies.LOOSE);
//        return modelMapper.map(registrationDTO, User.class);
//    }
}
