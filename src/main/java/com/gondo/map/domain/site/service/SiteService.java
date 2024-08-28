package com.gondo.map.domain.site.service;

import com.gondo.map.domain.site.dto.SiteDto;
import com.gondo.map.domain.site.entity.SiteEntity;
import com.gondo.map.domain.site.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;

    public List<SiteDto> getAllSites() {
        return siteRepository.findAll().stream().map(SiteEntity::toDto).toList();
    }
}
