package com.project.demo.rest.product;


import com.project.demo.logic.entity.product.Product;
import com.project.demo.logic.entity.category.Category;
import com.project.demo.logic.entity.category.CategoryRepository;
import com.project.demo.logic.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductRestController {

    @Autowired
    private com.project.demo.logic.entity.product.ProductRepository ProductRepository;

    @Autowired
    private com.project.demo.logic.entity.category.CategoryRepository CategoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','SUPER_ADMIN_ROLE')")
    public List<Product> getAllProducts() {
        return ProductRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public Product addProduct(@RequestBody Product product) {

        if (product == null || product.getCategory() == null || product.getCategory().getId() == null) {
            throw new IllegalArgumentException("Product or category cannot be null");
        }

        Long categoryId = product.getCategory().getId();
        Category category = CategoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + categoryId));

        product.setCategory(category);

        return ProductRepository.save(product);
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return ProductRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    @GetMapping("/filterByName/{name}")
    public List<Product> getProductById(@PathVariable String name) {
        return ProductRepository.findProductsWithCharacterInName(name);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return ProductRepository.findById(id)
                .map(existingProduct -> {
                    existingProduct.setName(product.getName());
                    existingProduct.setDescription(product.getDescription());
                    existingProduct.setPrice(product.getPrice());
                    existingProduct.setQuantityInStock(product.getQuantityInStock());
                    return ProductRepository.save(existingProduct);
                })
                .orElseGet(() -> {
                    product.setId(id);
                    return ProductRepository.save(product);
                });
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public void deleteUser(@PathVariable Long id) {
        ProductRepository.deleteById(id);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public User authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}
