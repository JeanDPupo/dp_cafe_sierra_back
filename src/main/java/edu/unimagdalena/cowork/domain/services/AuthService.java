package edu.unimagdalena.cowork.domain.services;

import edu.unimagdalena.cowork.api.dto.AuthDtos;
import edu.unimagdalena.cowork.domain.entities.RoleName;
import edu.unimagdalena.cowork.domain.entities.User;
import edu.unimagdalena.cowork.domain.exception.ConflictException;
import edu.unimagdalena.cowork.domain.repositories.UserRepository;
import edu.unimagdalena.cowork.shared.security.JwtService;
import java.time.Instant;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserService userService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Transactional
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("Ya existe un usuario con ese correo");
        }

        User user = new User();
        user.setFullName(request.fullName());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setPhone(request.phone());
        user.setWhatsappNumber(request.whatsappNumber());
        user.setRole(RoleName.USER);
        user.setEnabled(true);

        userRepository.save(user);
        return buildAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        User user = userService.getByEmail(request.email());
        return buildAuthResponse(user);
    }

    private AuthDtos.AuthResponse buildAuthResponse(User user) {
        String token = jwtService.generateToken(user);
        Instant expiresAt = Instant.now().plusMillis(jwtService.getExpirationMs());
        return new AuthDtos.AuthResponse(token, userService.toResponse(user), expiresAt);
    }
}
