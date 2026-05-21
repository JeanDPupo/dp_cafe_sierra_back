package edu.unimagdalena.cowork.domain.services;

import edu.unimagdalena.cowork.api.dto.ProductDtos;
import edu.unimagdalena.cowork.domain.entities.MediaType;
import edu.unimagdalena.cowork.domain.entities.ProcessMedia;
import edu.unimagdalena.cowork.domain.entities.ProducerProfile;
import edu.unimagdalena.cowork.domain.entities.Farm;
import edu.unimagdalena.cowork.domain.entities.Product;
import edu.unimagdalena.cowork.domain.entities.ProductProcess;
import edu.unimagdalena.cowork.domain.entities.ProductProcessStage;
import edu.unimagdalena.cowork.domain.entities.ProductStatus;
import edu.unimagdalena.cowork.domain.exception.BadRequestException;
import edu.unimagdalena.cowork.domain.exception.ForbiddenOperationException;
import edu.unimagdalena.cowork.domain.exception.ResourceNotFoundException;
import edu.unimagdalena.cowork.domain.repositories.CommentRepository;
import edu.unimagdalena.cowork.domain.repositories.ProcessMediaRepository;
import edu.unimagdalena.cowork.domain.repositories.ProductProcessRepository;
import edu.unimagdalena.cowork.domain.repositories.ProductRepository;
import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private static final Set<ProductProcessStage> REQUIRED_STAGES = EnumSet.of(
            ProductProcessStage.SIEMBRA,
            ProductProcessStage.CULTIVO,
            ProductProcessStage.COSECHA,
            ProductProcessStage.LAVADO_SECADO
    );

    private final ProductRepository productRepository;
    private final ProductProcessRepository productProcessRepository;
    private final ProcessMediaRepository processMediaRepository;
    private final ProducerProfileService producerProfileService;
    private final CommentRepository commentRepository;

    public ProductService(
            ProductRepository productRepository,
            ProductProcessRepository productProcessRepository,
            ProcessMediaRepository processMediaRepository,
            ProducerProfileService producerProfileService,
            CommentRepository commentRepository
    ) {
        this.productRepository = productRepository;
        this.productProcessRepository = productProcessRepository;
        this.processMediaRepository = processMediaRepository;
        this.producerProfileService = producerProfileService;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public ProductDtos.ProductDetailResponse create(Long userId, ProductDtos.ProductCreateRequest request) {
        ProducerProfile profile = producerProfileService.getByUserId(userId);
        validateSeller(profile);
        validateProcesses(request.processes());

        Product product = new Product();
        product.setProducerProfile(profile);
        product.setFarm(resolveFarm(profile, request.farmId()));
        product.setName(request.name());
        product.setVariety(request.variety());
        product.setPricePerKg(request.pricePerKg());
        product.setAvailableKg(request.availableKg());
        product.setDescription(request.description());
        product.setMainImageUrl(request.mainImageUrl());
        product.setStatus(ProductStatus.ACTIVE);
        productRepository.save(product);

        replaceProcesses(product, request.processes());
        return getById(product.getId());
    }

    @Transactional(readOnly = true)
    public Product getEntityById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
    }

    @Transactional(readOnly = true)
    public List<ProductDtos.ProductCatalogItemResponse> search(BigDecimal priceMin, BigDecimal priceMax, String location, String variety) {
        String normalizedLocation = blankToNull(location);
        String normalizedVariety = blankToNull(variety);

        return productRepository.findByStatusOrderByCreatedAtDesc(ProductStatus.ACTIVE).stream()
                .filter(product -> priceMin == null || product.getPricePerKg().compareTo(priceMin) >= 0)
                .filter(product -> priceMax == null || product.getPricePerKg().compareTo(priceMax) <= 0)
                .filter(product -> normalizedLocation == null
                        || containsIgnoreCase(product.getProducerProfile().getLocationText(), normalizedLocation))
                .filter(product -> normalizedVariety == null
                        || containsIgnoreCase(product.getVariety(), normalizedVariety))
                .map(this::toCatalogItem)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductDtos.ProductDetailResponse getById(Long productId) {
        Product product = getEntityById(productId);
        List<ProductDtos.ProductProcessResponse> processes = toProcessResponses(productId);
        List<edu.unimagdalena.cowork.domain.entities.Comment> comments =
                commentRepository.findByProductIdAndVisibleTrueOrderByCreatedAtDesc(productId);
        double average = comments.stream().mapToInt(c -> c.getRating()).average().orElse(0.0);
        return new ProductDtos.ProductDetailResponse(
                product.getId(),
                product.getName(),
                product.getVariety(),
                product.getFarm().getId(),
                product.getFarm().getName(),
                product.getPricePerKg(),
                product.getAvailableKg(),
                product.getDescription(),
                product.getMainImageUrl(),
                product.getStatus().name(),
                product.getCreatedAt(),
                producerProfileService.toResponse(product.getProducerProfile(), getCatalogItemsByProducer(product.getProducerProfile().getId())),
                processes,
                average,
                (long) comments.size()
        );
    }

    @Transactional(readOnly = true)
    public List<ProductDtos.ProductCatalogItemResponse> getCatalogItemsByProducer(Long producerProfileId) {
        return productRepository.findByProducerProfileIdOrderByCreatedAtDesc(producerProfileId)
                .stream()
                .map(this::toCatalogItem)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductDtos.ProductCatalogItemResponse> getMine(Long userId) {
        ProducerProfile profile = producerProfileService.getByUserId(userId);
        return getCatalogItemsByProducer(profile.getId());
    }

    @Transactional
    public ProductDtos.ProductDetailResponse update(Long userId, Long productId, ProductDtos.ProductUpdateRequest request) {
        Product product = assertOwnership(userId, productId);
        if (request.name() != null) product.setName(request.name());
        if (request.farmId() != null) product.setFarm(resolveFarm(product.getProducerProfile(), request.farmId()));
        if (request.variety() != null) product.setVariety(request.variety());
        if (request.pricePerKg() != null) product.setPricePerKg(request.pricePerKg());
        if (request.availableKg() != null) product.setAvailableKg(request.availableKg());
        if (request.description() != null) product.setDescription(request.description());
        if (request.mainImageUrl() != null) product.setMainImageUrl(request.mainImageUrl());
        if (request.status() != null) product.setStatus(ProductStatus.valueOf(request.status().toUpperCase()));
        productRepository.save(product);

        if (request.processes() != null) {
            validateProcesses(request.processes());
            replaceProcesses(product, request.processes());
        }
        return getById(productId);
    }

    @Transactional
    public void delete(Long userId, Long productId) {
        Product product = assertOwnership(userId, productId);
        product.setStatus(ProductStatus.INACTIVE);
        productRepository.save(product);
    }

    private Product assertOwnership(Long userId, Long productId) {
        Product product = getEntityById(productId);
        if (!product.getProducerProfile().getUser().getId().equals(userId)) {
            throw new ForbiddenOperationException("No puedes modificar un producto que no te pertenece");
        }
        return product;
    }

    private void validateSeller(ProducerProfile profile) {
        if (!profile.isActiveSeller()) {
            throw new ForbiddenOperationException("Debes activar tu perfil de productor para vender");
        }
    }

    private void validateProcesses(List<ProductDtos.ProductProcessRequest> processes) {
        Set<ProductProcessStage> incomingStages = processes.stream()
                .map(p -> ProductProcessStage.valueOf(p.stage().toUpperCase()))
                .collect(java.util.stream.Collectors.toSet());
        if (!incomingStages.containsAll(REQUIRED_STAGES)) {
            throw new BadRequestException("Faltan procesos obligatorios: SIEMBRA, CULTIVO, COSECHA y LAVADO_SECADO");
        }
        for (ProductDtos.ProductProcessRequest process : processes) {
            if (process.description().isBlank() || process.resultType().isBlank()) {
                throw new BadRequestException("Cada proceso debe tener descripcion y tipo de resultado");
            }
        }
    }

    private void replaceProcesses(Product product, List<ProductDtos.ProductProcessRequest> processes) {
        List<ProductProcess> existing = productProcessRepository.findByProductIdOrderByOrderIndexAsc(product.getId());
        if (!existing.isEmpty()) {
            for (ProductProcess process : existing) {
                processMediaRepository.deleteAll(processMediaRepository.findByProcessId(process.getId()));
            }
            productProcessRepository.deleteAll(existing);
        }
        for (ProductDtos.ProductProcessRequest request : processes) {
            ProductProcess process = new ProductProcess();
            process.setProduct(product);
            process.setStage(ProductProcessStage.valueOf(request.stage().toUpperCase()));
            process.setDescription(request.description());
            process.setResultType(request.resultType());
            process.setOrderIndex(request.orderIndex());
            productProcessRepository.save(process);
            if (request.media() != null) {
                for (ProductDtos.ProcessMediaRequest mediaRequest : request.media()) {
                    ProcessMedia media = new ProcessMedia();
                    media.setProcess(process);
                    media.setMediaType(MediaType.valueOf(mediaRequest.mediaType().toUpperCase()));
                    media.setUrl(mediaRequest.url());
                    media.setCaption(mediaRequest.caption());
                    processMediaRepository.save(media);
                }
            }
        }
    }

    private List<ProductDtos.ProductProcessResponse> toProcessResponses(Long productId) {
        return productProcessRepository.findByProductIdOrderByOrderIndexAsc(productId).stream()
                .map(process -> new ProductDtos.ProductProcessResponse(
                        process.getId(),
                        process.getStage().name(),
                        process.getDescription(),
                        process.getResultType(),
                        process.getOrderIndex(),
                        processMediaRepository.findByProcessId(process.getId()).stream()
                                .map(media -> new ProductDtos.ProcessMediaResponse(
                                        media.getId(),
                                        media.getMediaType().name(),
                                        media.getUrl(),
                                        media.getCaption()
                                ))
                                .toList()
                ))
                .toList();
    }

    private ProductDtos.ProductCatalogItemResponse toCatalogItem(Product product) {
        return new ProductDtos.ProductCatalogItemResponse(
                product.getId(),
                product.getProducerProfile().getId(),
                product.getProducerProfile().getBrandName(),
                product.getProducerProfile().getLocationText(),
                product.getFarm().getId(),
                product.getFarm().getName(),
                product.getName(),
                product.getVariety(),
                product.getPricePerKg(),
                product.getAvailableKg(),
                product.getDescription(),
                product.getMainImageUrl(),
                product.getStatus().name(),
                product.getCreatedAt()
        );
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private boolean containsIgnoreCase(String source, String fragment) {
        return source != null && source.toLowerCase().contains(fragment.toLowerCase());
    }

    private Farm resolveFarm(ProducerProfile profile, Long farmId) {
        return producerProfileService.getFarmForProducer(profile.getId(), farmId);
    }
}
