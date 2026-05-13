package edu.unimagdalena.cowork.domain.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class DomainMapper {

    private final ModelMapper modelMapper;

    public DomainMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public <T> T map(Object source, Class<T> targetClass) {
        return modelMapper.map(source, targetClass);
    }
}
