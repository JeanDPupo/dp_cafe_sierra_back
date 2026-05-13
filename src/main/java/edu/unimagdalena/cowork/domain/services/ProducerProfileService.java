package edu.unimagdalena.cowork.domain.services;

import edu.unimagdalena.cowork.api.dto.ProducerProfileDtos;
import edu.unimagdalena.cowork.api.dto.ProductDtos;
import edu.unimagdalena.cowork.domain.entities.ProducerProfile;
import edu.unimagdalena.cowork.domain.entities.User;
import edu.unimagdalena.cowork.domain.exception.ResourceNotFoundException;
import edu.unimagdalena.cowork.domain.repositories.ProducerProfileRepository;
import java.util.List;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProducerProfileService {

    private final ProducerProfileRepository producerProfileRepository;
    private final UserService userService;
    private final ProductService productService;

    public ProducerProfileService(
            ProducerProfileRepository producerProfileRepository,
            UserService userService,
            @Lazy
            ProductService productService
    ) {
        this.producerProfileRepository = producerProfileRepository;
        this.userService = userService;
        this.productService = productService;
    }

    @Transactional(readOnly = true)
    public ProducerProfile getEntityById(Long id) {
        return producerProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de productor no encontrado"));
    }

    @Transactional(readOnly = true)
    public ProducerProfile getByUserId(Long userId) {
        return producerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario no tiene perfil de productor"));
    }

    @Transactional
    public ProducerProfileDtos.ProducerProfileResponse createOrUpdate(Long userId, ProducerProfileDtos.UpsertProducerProfileRequest request) {
        User user = userService.getById(userId);
        ProducerProfile profile = producerProfileRepository.findByUserId(userId).orElseGet(ProducerProfile::new);
        profile.setUser(user);
        apply(request, profile);
        producerProfileRepository.save(profile);
        return toResponse(profile, productService.getCatalogItemsByProducer(profile.getId()));
    }

    @Transactional(readOnly = true)
    public ProducerProfileDtos.ProducerProfileResponse getMyProfile(Long userId) {
        ProducerProfile profile = getByUserId(userId);
        return toResponse(profile, productService.getCatalogItemsByProducer(profile.getId()));
    }

    @Transactional(readOnly = true)
    public ProducerProfileDtos.ProducerProfileResponse getPublicProfile(Long profileId) {
        ProducerProfile profile = getEntityById(profileId);
        return toResponse(profile, productService.getCatalogItemsByProducer(profileId));
    }

    private void apply(ProducerProfileDtos.UpsertProducerProfileRequest request, ProducerProfile profile) {
        profile.setActiveSeller(request.activeSeller());
        profile.setBrandName(request.brandName());
        profile.setFarmName(request.farmName());
        profile.setBio(request.bio());
        profile.setStory(request.story());
        profile.setLocationText(request.locationText());
        profile.setGps(request.gps());
        profile.setYearsExperience(request.yearsExperience());
        profile.setCoverImageUrl(request.coverImageUrl());
    }

    public ProducerProfileDtos.ProducerProfileResponse toResponse(ProducerProfile profile, List<ProductDtos.ProductCatalogItemResponse> products) {
        return new ProducerProfileDtos.ProducerProfileResponse(
                profile.getId(),
                profile.getUser().getId(),
                profile.getUser().getFullName(),
                profile.isActiveSeller(),
                profile.getBrandName(),
                profile.getFarmName(),
                profile.getBio(),
                profile.getStory(),
                profile.getLocationText(),
                profile.getGps(),
                profile.getYearsExperience(),
                profile.getCoverImageUrl(),
                profile.getCreatedAt(),
                products
        );
    }
}
