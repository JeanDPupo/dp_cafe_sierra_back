package edu.unimagdalena.cowork.domain.services;

import edu.unimagdalena.cowork.api.dto.UserDtos;
import edu.unimagdalena.cowork.domain.entities.User;
import edu.unimagdalena.cowork.domain.exception.ResourceNotFoundException;
import edu.unimagdalena.cowork.domain.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public User getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    @Transactional
    public UserDtos.UserResponse updateUser(Long userId, UserDtos.UpdateUserRequest request) {
        User user = getById(userId);
        if (request.fullName() != null) {
            user.setFullName(request.fullName());
        }
        if (request.phone() != null) {
            user.setPhone(request.phone());
        }
        if (request.whatsappNumber() != null) {
            user.setWhatsappNumber(request.whatsappNumber());
        }
        userRepository.save(user);
        return toResponse(user);
    }

    public UserDtos.UserResponse toResponse(User user) {
        return new UserDtos.UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getWhatsappNumber(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }
}
