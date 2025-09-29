package com.gondo.map.domain.site.repository;

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
public interface SiteRepository extends JpaRepository<Site, String> {

    @Query("""
            select s from Site s where 1=1 
            and s.siteUseYn = true 
            and s.siteId = ?1 
            """)
    Optional<Site> findCategoryBy(String id);

    @Query("""
            select s from Site s where s.siteUseYn = true
                order by COALESCE(s.siteUpdateDtm, s.siteCreateDtm) DESC
            """)
    List<Site> getAllByUseYn();

    @Transactional
    @Modifying
    @Query("""
            update Site s set s.siteUseYn = false where s.siteId = :siteId
            """)
    int deleteBySiteId(@Param("siteId") String siteId);
}
