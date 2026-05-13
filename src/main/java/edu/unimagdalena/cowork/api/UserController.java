package edu.unimagdalena.cowork.api;

import edu.unimagdalena.cowork.api.dto.ProducerProfileDtos;
import edu.unimagdalena.cowork.api.dto.ProductDtos;
import edu.unimagdalena.cowork.api.dto.UserDtos;
import edu.unimagdalena.cowork.domain.services.ProducerProfileService;
import edu.unimagdalena.cowork.domain.services.ProductService;
import edu.unimagdalena.cowork.domain.services.UserService;
import edu.unimagdalena.cowork.shared.security.SecurityUtils;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final ProducerProfileService producerProfileService;
    private final ProductService productService;

    public UserController(
            UserService userService,
            ProducerProfileService producerProfileService,
            ProductService productService
    ) {
        this.userService = userService;
        this.producerProfileService = producerProfileService;
        this.productService = productService;
    }

    @GetMapping("/users/me")
    public UserDtos.UserResponse me() {
        return userService.toResponse(userService.getById(SecurityUtils.currentUserId()));
    }

    @PatchMapping("/users/me")
    public UserDtos.UserResponse updateMe(@Valid @RequestBody UserDtos.UpdateUserRequest request) {
        return userService.updateUser(SecurityUtils.currentUserId(), request);
    }

    @GetMapping("/users/me/producer-profile")
    public ProducerProfileDtos.ProducerProfileResponse myProducerProfile() {
        return producerProfileService.getMyProfile(SecurityUtils.currentUserId());
    }

    @PostMapping("/users/me/producer-profile")
    public ProducerProfileDtos.ProducerProfileResponse createProducerProfile(
            @Valid @RequestBody ProducerProfileDtos.UpsertProducerProfileRequest request
    ) {
        return producerProfileService.createOrUpdate(SecurityUtils.currentUserId(), request);
    }

    @PatchMapping("/users/me/producer-profile")
    public ProducerProfileDtos.ProducerProfileResponse updateProducerProfile(
            @Valid @RequestBody ProducerProfileDtos.UpsertProducerProfileRequest request
    ) {
        return producerProfileService.createOrUpdate(SecurityUtils.currentUserId(), request);
    }

    @GetMapping("/producers/{id}")
    public ProducerProfileDtos.ProducerProfileResponse publicProducerProfile(@PathVariable Long id) {
        return producerProfileService.getPublicProfile(id);
    }

    @GetMapping("/users/me/farms")
    public java.util.List<ProducerProfileDtos.FarmResponse> myFarms() {
        return producerProfileService.getMyFarms(SecurityUtils.currentUserId());
    }

    @PostMapping("/users/me/farms")
    public ProducerProfileDtos.FarmResponse createFarm(
            @Valid @RequestBody ProducerProfileDtos.FarmRequest request
    ) {
        return producerProfileService.createFarm(SecurityUtils.currentUserId(), request);
    }

    @GetMapping("/users/me/products")
    public List<ProductDtos.ProductCatalogItemResponse> myProducts() {
        return productService.getMine(SecurityUtils.currentUserId());
    }
}
