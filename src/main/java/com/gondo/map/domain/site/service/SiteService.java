package com.gondo.map.domain.site.service;

import com.gondo.map.component.FileManager;
import com.gondo.map.domain.common.CommException;
import com.gondo.map.domain.site.entity.Site;
import com.gondo.map.domain.site.record.SiteRecord;
import com.gondo.map.domain.site.record.SiteSaveRecord;
import com.gondo.map.domain.site.record.SiteUpdateRecord;
import com.gondo.map.domain.site.repository.SiteRepository;
import com.gondo.map.domain.site.repository.query.SiteQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;
    private final SiteQuery siteQuery;
    private final FileManager fileManager;

    public SiteRecord findItem(String id) {
        return siteQuery.findItem(id).toRecord();
    }

    public List<SiteRecord> getAllSites() {
        return siteRepository.getAllByUseYn().stream().map(Site::toRecord).toList();
    }

    public SiteRecord addItem(SiteSaveRecord insertReqDto, MultipartFile uploadFile) {
        try {
            String fileName = uploadFile != null ? uploadFile.getOriginalFilename() : null;
            Site savedEntity = siteRepository.save(insertReqDto.toSiteEntity(fileName));
            SiteRecord dto = savedEntity.toRecord();
            fileManager.storeFile(dto.id(), uploadFile);
            return dto;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public SiteRecord modItem(SiteUpdateRecord updateReqDto, MultipartFile uploadFile) throws CommException {
        Site find = siteRepository.findCategoryBy(updateReqDto.siteId()).orElse(null);
        if (find == null) {
            throw new CommException(404, "수정 대상이 없습니다.");
        }

        if (find.findIsLockEntity()) {
            throw new CommException(405, "관리자의 잠금 설정으로 인해 수정이 불가합니다.");
        }
        Site updateEntity = find.toUpdateEntity(updateReqDto, (uploadFile != null && !uploadFile.isEmpty()) ? uploadFile.getOriginalFilename() : null);
        Site updatedEntity = siteRepository.save(updateEntity);
        SiteRecord updatedDto = updatedEntity.toRecord();
        fileManager.storeFile(updatedDto.id(), uploadFile);
        return updatedDto;
    }

    public boolean delItem(String siteId) throws CommException {
        Site find = siteRepository.findById(siteId).orElse(null);
        if (find == null) {
            throw new CommException(404, "삭제 대상이 없습니다.");
        }
        if (find.findIsLockEntity()) {
            throw new CommException(405, "관리자의 잠금 설정으로 인해 삭제가 불가합니다.");
        }
        int row = siteRepository.deleteBySiteId(siteId);
        if (row > 0) {
            fileManager.deleteFile(siteId);
            return true;
        } else {
            return false;
        }
    }

}
