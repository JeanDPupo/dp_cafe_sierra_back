package edu.unimagdalena.cowork.api;

import edu.unimagdalena.cowork.api.dto.ProductDtos;
import edu.unimagdalena.cowork.domain.services.ProductService;
import edu.unimagdalena.cowork.shared.security.SecurityUtils;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ProductDtos.ProductDetailResponse create(@Valid @RequestBody ProductDtos.ProductCreateRequest request) {
        return productService.create(SecurityUtils.currentUserId(), request);
    }

    @GetMapping
    public List<ProductDtos.ProductCatalogItemResponse> catalog(
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String variety
    ) {
        return productService.search(priceMin, priceMax, location, variety);
    }

    @GetMapping("/{id}")
    public ProductDtos.ProductDetailResponse getById(@PathVariable Long id) {
        return productService.getById(id);
    }

    @PatchMapping("/{id}")
    public ProductDtos.ProductDetailResponse update(@PathVariable Long id, @Valid @RequestBody ProductDtos.ProductUpdateRequest request) {
        return productService.update(SecurityUtils.currentUserId(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        productService.delete(SecurityUtils.currentUserId(), id);
    }
}
