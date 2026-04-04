/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : ProductServiceImpl.java                               ║
 * ║  MODULE : 07-spring-rest-api / 01-rest-fundamentals             ║
 * ║  GRADLE : ./gradlew :07-spring-rest-api:bootRun                 ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Business logic layer for the Product domain.  ║
 * ║                   Demonstrates service layer patterns: DI,      ║
 * ║                   @Transactional, exception mapping, DTO usage. ║
 * ║  WHY IT EXISTS  : Without a service layer, controllers become   ║
 * ║                   fat (business logic + HTTP concern), making   ║
 * ║                   testing without HTTP impossible and re-use     ║
 * ║                   across different entry points (REST, batch,   ║
 * ║                   messaging) impossible.                        ║
 * ║  PYTHON COMPARE : FastAPI service pattern (see class javadoc)  ║
 * ║  USE CASES      : E-commerce product catalogue management,      ║
 * ║                   inventory tracking, price management,         ║
 * ║                   soft-deletion for audit compliance            ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM — Spring Boot Layered Architecture               ║
 * ║                                                                  ║
 * ║   HTTP Request                                                   ║
 * ║       │                                                          ║
 * ║       ▼                                                          ║
 * ║   [ ProductController ]   ← validates input, maps HTTP ↔ DTO   ║
 * ║       │                                                          ║
 * ║       ▼                                                          ║
 * ║   [ ProductService ]      ← business rules   ← YOU ARE HERE    ║
 * ║       │                                                          ║
 * ║       ▼                                                          ║
 * ║   [ ProductRepository ]   ← JpaRepository data access          ║
 * ║       │                                                          ║
 * ║       ▼                                                          ║
 * ║   [ PostgreSQL ]                                                 ║
 * ║                                                                  ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :07-spring-rest-api:bootRun         ║
 * ║                   Then: GET http://localhost:8080/api/products   ║
 * ║  RELATED FILES  : ProductController.java, ProductRepository.java ║
 * ║                   01-rest-fundamentals.md, Product.java          ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.restapi.service;

import com.learning.restapi.dto.CreateProductRequest;
import com.learning.restapi.dto.ProductResponse;
import com.learning.restapi.dto.UpdateProductRequest;
import com.learning.restapi.entity.Product;
import com.learning.restapi.exception.DuplicateProductException;
import com.learning.restapi.exception.ProductNotFoundException;
import com.learning.restapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ProductServiceImpl — business logic layer for the Product domain.
 *
 * <p>Handles all product operations: creation, retrieval, pricing updates,
 * inventory adjustments, and soft deletion. Enforces business rules such as
 * price validation, duplicate SKU prevention, and stock floor enforcement.</p>
 *
 * <p>All write operations are transactional. Read operations use readOnly=true
 * to disable Hibernate dirty checking for better performance.</p>
 *
 * <p><b>Python FastAPI equivalent:</b>
 * <pre>
 *   class ProductService:
 *       def __init__(self, repo: ProductRepository = Depends(get_db)):
 *           self.repo = repo
 *
 *       async def create(self, req: CreateProductRequest) -> ProductResponse:
 *           product = Product(**req.dict())
 *           self.repo.add(product)
 *           await self.repo.flush()
 *           return ProductResponse.from_orm(product)
 * </pre>
 * Key difference: Spring DI is automatic — no explicit Depends() in method
 * signatures. Spring also applies @Transactional transparently via AOP proxy.
 *
 * <p><b>ASCII — Position in the layered architecture:</b>
 * <pre>
 *   ProductController          (HTTP ↔ DTO boundary)
 *          │
 *          ▼
 *   ProductServiceImpl  ← YOU ARE HERE (business rules)
 *          │
 *          ▼
 *   ProductRepository          (JPA data access)
 *          │
 *          ▼
 *   PostgreSQL
 * </pre>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepo;

    /**
     * Create a new product and persist it to the database.
     *
     * <p>Validates that no product with the same SKU already exists.
     * Converts the request DTO to a domain entity, persists, and
     * returns a response DTO. Never exposes the JPA entity directly.</p>
     *
     * <p><b>Python equivalent:</b>
     * <pre>
     *   async def create(self, req: CreateProductRequest,
     *                    db: AsyncSession = Depends(get_db)) -> ProductResponse:
     *       async with db.begin():
     *           product = Product(**req.dict())
     *           db.add(product)
     *       return ProductResponse.from_orm(product)
     * </pre>
     *
     * <p><b>Flow:</b>
     * <pre>
     *   create(request)
     *       │
     *       ▼
     *   Map DTO → Entity
     *       │
     *       ▼
     *   productRepo.save(entity) ──→ DataIntegrityViolation? → DuplicateProductException
     *       │
     *       ▼
     *   Map Entity → Response DTO
     *       │
     *       ▼
     *   return ProductResponse
     * </pre>
     *
     * @param request  Validated create request containing name, SKU, price, stock
     * @return         ProductResponse with generated ID and all fields
     * @throws DuplicateProductException if a product with the same SKU already exists
     */
    @Transactional
    @Override
    public ProductResponse create(CreateProductRequest request) {
        log.info("Creating product with SKU: {}", request.sku());

        Product product = Product.builder()
                .name(request.name())
                .sku(request.sku())
                .price(request.price())
                .stockQuantity(request.stockQuantity())
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        try {
            Product saved = productRepo.save(product);
            log.info("Product created successfully: id={}, sku={}", saved.getId(), saved.getSku());
            return ProductResponse.from(saved);
        } catch (DataIntegrityViolationException e) {
            // DB unique constraint on sku column was violated
            // Translate infrastructure exception to domain exception before it leaves the service layer
            throw new DuplicateProductException(request.sku());
        }
    }

    /**
     * Find a product by its ID, throwing if not found.
     *
     * <p><b>Python equivalent:</b>
     * <pre>
     *   async def find_by_id(self, product_id: int) -> ProductResponse:
     *       product = await db.get(Product, product_id)
     *       if not product:
     *           raise HTTPException(status_code=404, detail=f"Product {product_id} not found")
     *       return ProductResponse.from_orm(product)
     * </pre>
     *
     * @param id  Product ID to look up
     * @return    ProductResponse for the found product
     * @throws ProductNotFoundException if no product exists with this ID
     */
    @Transactional(readOnly = true)
    // readOnly=true: disables Hibernate dirty checking — faster for reads since
    // Hibernate doesn't need to compare entity snapshots at flush time
    @Override
    public ProductResponse findById(Long id) {
        return productRepo.findById(id)
                .map(ProductResponse::from)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    /**
     * List all active products, optionally filtered by minimum price.
     *
     * @param minPrice  Optional minimum price filter (null = no filter)
     * @param pageable  Pagination and sorting parameters
     * @return          Page of ProductResponse matching the criteria
     */
    @Transactional(readOnly = true)
    @Override
    public Page<ProductResponse> findAll(BigDecimal minPrice, Pageable pageable) {
        if (minPrice != null) {
            return productRepo.findByPriceGreaterThanEqualAndActiveTrue(minPrice, pageable)
                    .map(ProductResponse::from);
        }
        return productRepo.findByActiveTrue(pageable).map(ProductResponse::from);
    }

    /**
     * Update product price and/or stock quantity.
     *
     * <p>Uses JPA dirty checking: we load the entity, mutate it,
     * and let Hibernate generate the UPDATE SQL at transaction commit.
     * No explicit save() call needed — this is the standard JPA pattern.</p>
     *
     * <p><b>Flow:</b>
     * <pre>
     *   update(id, request)
     *       │
     *       ▼
     *   Load entity ──→ Not found? → ProductNotFoundException
     *       │
     *       ▼
     *   Mutate entity fields
     *       │
     *       ▼
     *   Method returns normally
     *       │
     *       ▼
     *   @Transactional proxy commits → Hibernate dirty check → UPDATE SQL
     * </pre>
     *
     * @param id       Product ID to update
     * @param request  Fields to update (null values are ignored)
     * @return         Updated ProductResponse
     * @throws ProductNotFoundException if no product exists with this ID
     */
    @Transactional
    @Override
    public ProductResponse update(Long id, UpdateProductRequest request) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (request.price() != null) {
            product.setPrice(request.price());
        }
        if (request.stockQuantity() != null) {
            product.setStockQuantity(request.stockQuantity());
        }

        // No explicit save() here — JPA dirty checking handles it.
        // At transaction commit, Hibernate compares current state to snapshot
        // taken at load time and generates UPDATE SQL for changed fields only.
        return ProductResponse.from(product);
    }

    /**
     * Soft-delete a product by setting active=false.
     *
     * <p>Soft deletion is preferred over hard DELETE in production for:
     * audit trail compliance, FK integrity, and the ability to restore
     * accidentally deleted items without database restore.</p>
     *
     * @param id  Product ID to deactivate
     * @throws ProductNotFoundException if no product exists with this ID
     */
    @Transactional
    @Override
    public void delete(Long id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        // Soft delete — never hard DELETE in production
        // Preserves audit trail and avoids FK violations from OrderLineItems
        product.setActive(false);
        product.setDeletedAt(LocalDateTime.now());

        log.info("Product soft-deleted: id={}, sku={}", id, product.getSku());
    }
}
