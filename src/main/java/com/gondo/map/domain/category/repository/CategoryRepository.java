package com.gondo.map.domain.category.repository;

import com.gondo.map.domain.category.entity.Category;
import com.gondo.map.domain.site.entity.Site;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    @Query("""
            select c from Category c where 1=1 
            and c.categoryUseYn = true 
            and c.categoryId = ?1 
            """)
    Optional<Category> findCategoryBy(String id);

    @Query("""
            select c from Category c where c.categoryUseYn = true
                order by COALESCE(c.categoryUpdateDtm, c.categoryCreateDtm) DESC
            """)
    List<Category> getAllByUseYn();

    @Transactional
    @Modifying
    @Query("""
            update Category c set c.categoryUseYn = false where c.categoryId = :categoryId
            """)
    int deleteBySiteId(@Param("categoryId") String categoryId);
}
