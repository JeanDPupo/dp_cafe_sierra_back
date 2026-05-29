package edu.unimagdalena.cowork.domain.services;

import edu.unimagdalena.cowork.api.dto.ProducerProfileDtos;
import edu.unimagdalena.cowork.api.dto.ProductDtos;
import edu.unimagdalena.cowork.domain.entities.Farm;
import edu.unimagdalena.cowork.domain.entities.ProducerProfile;
import edu.unimagdalena.cowork.domain.entities.User;
import edu.unimagdalena.cowork.domain.exception.ResourceNotFoundException;
import edu.unimagdalena.cowork.domain.repositories.FarmRepository;
import edu.unimagdalena.cowork.domain.repositories.ProducerProfileRepository;
import java.util.List;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProducerProfileService {

    private final ProducerProfileRepository producerProfileRepository;
    private final FarmRepository farmRepository;
    private final UserService userService;
    private final ProductService productService;

    public ProducerProfileService(
            ProducerProfileRepository producerProfileRepository,
            FarmRepository farmRepository,
            UserService userService,
            @Lazy
            ProductService productService
    ) {
        this.producerProfileRepository = producerProfileRepository;
        this.farmRepository = farmRepository;
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

    @Transactional
    public ProducerProfileDtos.FarmResponse createFarm(Long userId, ProducerProfileDtos.FarmRequest request) {
        ProducerProfile profile = getByUserId(userId);
        Farm farm = new Farm();
        farm.setProducerProfile(profile);
        applyFarm(request, farm);
        farmRepository.save(farm);
        return toFarmResponse(farm);
    }

    @Transactional(readOnly = true)
    public List<ProducerProfileDtos.FarmResponse> getMyFarms(Long userId) {
        ProducerProfile profile = getByUserId(userId);
        return farmRepository.findByProducerProfileIdOrderByCreatedAtDesc(profile.getId()).stream()
                .map(this::toFarmResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Farm getFarmForProducer(Long producerProfileId, Long farmId) {
        return farmRepository.findByIdAndProducerProfileId(farmId, producerProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("Finca no encontrada para este productor"));
    }

    private void apply(ProducerProfileDtos.UpsertProducerProfileRequest request, ProducerProfile profile) {
        profile.setActiveSeller(request.activeSeller());
        if (request.brandName() != null && !request.brandName().isBlank()) profile.setBrandName(request.brandName());
        if (request.bio() != null) profile.setBio(request.bio());
        if (request.story() != null) profile.setStory(request.story());
        if (request.locationText() != null && !request.locationText().isBlank()) profile.setLocationText(request.locationText());
        if (request.gps() != null) profile.setGps(request.gps());
        if (request.yearsExperience() != null) profile.setYearsExperience(request.yearsExperience());
        if (request.coverImageUrl() != null) profile.setCoverImageUrl(request.coverImageUrl());
        if (request.paymentDetails() != null) profile.setPaymentDetails(request.paymentDetails());
        if (request.mercadopagoAccessToken() != null) profile.setMercadopagoAccessToken(request.mercadopagoAccessToken());
        if (request.mercadopagoPublicKey() != null) profile.setMercadopagoPublicKey(request.mercadopagoPublicKey());
        if (request.nequiPhone() != null) profile.setNequiPhone(request.nequiPhone());
    }

    private void applyFarm(ProducerProfileDtos.FarmRequest request, Farm farm) {
        farm.setName(request.name());
        farm.setLocationText(request.locationText());
        farm.setGps(request.gps());
        farm.setDescription(request.description());
        farm.setActive(request.active());
    }

    public ProducerProfileDtos.FarmResponse toFarmResponse(Farm farm) {
        return new ProducerProfileDtos.FarmResponse(
                farm.getId(),
                farm.getName(),
                farm.getLocationText(),
                farm.getGps(),
                farm.getDescription(),
                farm.isActive(),
                farm.getCreatedAt()
        );
    }

    public ProducerProfileDtos.ProducerProfileResponse toResponse(ProducerProfile profile, List<ProductDtos.ProductCatalogItemResponse> products) {
        return new ProducerProfileDtos.ProducerProfileResponse(
                profile.getId(),
                profile.getUser().getId(),
                profile.getUser().getFullName(),
                profile.getUser().getPhone(),
                profile.getUser().getWhatsappNumber(),
                profile.isActiveSeller(),
                profile.getBrandName(),
                profile.getBio(),
                profile.getStory(),
                profile.getLocationText(),
                profile.getGps(),
                profile.getYearsExperience(),
                profile.getCoverImageUrl(),
                profile.getPaymentDetails(),
                profile.getMercadopagoAccessToken(),
                profile.getMercadopagoPublicKey(),
                profile.getNequiPhone(),
                profile.getCreatedAt(),
                farmRepository.findByProducerProfileIdOrderByCreatedAtDesc(profile.getId()).stream()
                        .map(this::toFarmResponse)
                        .toList(),
                products
        );
    }
}
