package com.project.demo.logic.entity.category;

import com.project.demo.logic.entity.product.Product;
import com.project.demo.logic.entity.rol.Role;
import com.project.demo.logic.entity.rol.RoleEnum;
import com.project.demo.logic.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE %?1%")
    List<Category> findCategoriesWithCharacterInName(String character);

    @Query("SELECT c FROM Category c WHERE c.name = ?1")
    Optional<Category> findByName(String name);

    boolean existsByName(String name);
}
