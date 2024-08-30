package com.gondo.map.domain.site.service;

import com.gondo.map.domain.site.record.SiteRecord;
import com.gondo.map.domain.site.record.SiteSaveRecord;
import com.gondo.map.domain.site.entity.Site;
import com.gondo.map.domain.site.repository.SiteRepository;
import com.gondo.map.domain.site.repository.query.SiteQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;
    private final SiteQuery siteQuery;

    public SiteRecord findItem(String id) {
        return siteQuery.findItem(id).toDto();
    }

    public List<SiteRecord> getAllSites() {
        return siteRepository.findAll().stream().map(Site::toDto).toList();
    }

    public SiteRecord addItem(SiteSaveRecord insertDto) {
        return siteRepository.save(insertDto.toSiteEntity()).toDto();
    }


}
