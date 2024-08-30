package com.gondo.map.domain.hist.service;

import com.gondo.map.domain.category.record.CategoryRecord;
import com.gondo.map.domain.category.service.CategoryService;
import com.gondo.map.domain.hist.dto.HistRecord;
import com.gondo.map.domain.hist.dto.HistSaveRecord;
import com.gondo.map.domain.hist.repository.HistRepository;
import com.gondo.map.domain.hist.repository.query.HistQuery;
import com.gondo.map.domain.site.record.SiteRecord;
import com.gondo.map.domain.site.service.SiteService;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class HistService {

    private final HistQuery histQuery;
    private final HistRepository histRepository;
    private final CategoryService categoryService;
    private final SiteService siteService;

    public List<HistRecord> getItems() {
        return histQuery.getHistItems();
    }

    public HistRecord addItem(HistSaveRecord saveRecord) {
        if (Strings.isNullOrEmpty(saveRecord.categoryId()) || Strings.isNullOrEmpty(saveRecord.siteId())) {
            return null;
        }

        CategoryRecord findCategoryRecord = findCategoryRecordById(saveRecord.categoryId());
        if (Objects.isNull(findCategoryRecord)) {
            return null;
        }

        SiteRecord findSiteRecord = findSiteRecordById(saveRecord.siteId());
        if (Objects.isNull(findSiteRecord)) {
            return null;
        }

        return histRepository.save(saveRecord.toHistory()).toDto(findCategoryRecord, findSiteRecord);
    }

    private CategoryRecord findCategoryRecordById(String id) {
        return categoryService.findItem(id);
    }

    private SiteRecord findSiteRecordById(String id) {
        return siteService.findItem(id);
    }


}
