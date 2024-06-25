package com.project.demo.rest.category;

import com.project.demo.logic.entity.category.CategoryRepository;
import com.project.demo.logic.entity.category.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("categories")
public class CategoryRestController {
    @Autowired
    private com.project.demo.logic.entity.category.CategoryRepository CategoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','SUPER_ADMIN_ROLE')")
    public List<Category> getAllCategory() {
        return CategoryRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public Category addCategory(@RequestBody Category category) {
        String categoryName = category.getName();
        if (CategoryRepository.existsByName(categoryName)) {
            throw new IllegalArgumentException("Category with name '" + categoryName + "' already exists");
        }

        return CategoryRepository.save(category);
    }

    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable Long id) {
        return CategoryRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    @GetMapping("/filterByName/{name}")
    public List<Category> getCategoryById(@PathVariable String name) {
        return CategoryRepository.findCategoriesWithCharacterInName(name);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public Category updateCategory(@PathVariable Long id, @RequestBody Category category) {
        return CategoryRepository.findById(id)
                .map(existingCategory -> {
                    existingCategory.setName(category.getName());
                    existingCategory.setDescription(category.getDescription());
                    return CategoryRepository.save(existingCategory);
                })
                .orElseGet(() -> {
                    category.setId(id);
                    return CategoryRepository.save(category);
                });
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN_ROLE')")
    public void deleteCategory(@PathVariable Long id) {
        CategoryRepository.deleteById(id);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public Category authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Category) authentication.getPrincipal();
    }

}