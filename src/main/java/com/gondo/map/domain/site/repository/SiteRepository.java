package com.gondo.map.domain.site.repository;

import com.gondo.map.domain.site.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteRepository extends JpaRepository<Site, String> {


}
