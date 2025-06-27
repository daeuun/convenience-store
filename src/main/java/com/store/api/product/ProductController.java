package com.store.api.product;

import com.store.api.product.model.ProductListResponse;
import com.store.api.product.model.ProductResponse;
import com.store.api.product.model.ProductSearchParam;
import com.store.api.product.model.ProductUpdateRequest;
import com.store.api.product.model.StockResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Product", description = "상품 API v1")
@Validated
@RequestMapping("/api/v1/products")
@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "상품 목록 조회", description = "검색 조건에 따른 상품 목록을 조회")
    @GetMapping
    public ProductListResponse findAllProducts(ProductSearchParam productSearchParam) {
        return productService.getProducts(productSearchParam);
    }

    @Operation(summary = "상품 상세 조회", description = "상품 상세 정보를 조회")
    @GetMapping("/{productId}")
    public ProductResponse findProduct(@PathVariable Long productId) {
        return productService.getProduct(productId);
    }

    @Operation(summary = "상품 재고 조회", description = "구매 가능한 상품의 재고를 조회")
    @GetMapping("/stock/{productId}")
    public StockResponse getStock(@PathVariable Long productId) {
        return productService.getStocks(productId);
    }

    @Operation(summary = "상품 수정", description = "상품 상세 정보를 변경")
    @PutMapping("/{productId}")
    public ProductResponse updateProduct(@PathVariable Long productId,
                                         @RequestBody ProductUpdateRequest request) {
        return productService.updateProduct(productId, request);
    }

    @Operation(summary = "상품 삭제")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok().build();
    }

}
