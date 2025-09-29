package com.gondo.map.domain.site.entity;

import com.gondo.map.domain.site.record.SiteRecord;
import com.gondo.map.domain.site.record.SiteUpdateRecord;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Table(name = "tbl_project_site", schema = "project")
@Entity
public class Site {

    @Id
    @Column(name = "site_id")
    private String siteId;

    @Column(name = "site_nm")
    private String siteNm;

    @Column(name = "site_lat")
    private String siteLat;

    @Column(name = "site_lng")
    private String siteLng;

    @Column(name = "site_logo_img_path")
    private String siteLogoImgPath;

    @Column(name = "site_use_yn")
    private Boolean siteUseYn;

    @Column(name = "site_create_dtm")
    private LocalDateTime siteCreateDtm;

    @Column(name = "site_update_dtm")
    private LocalDateTime siteUpdateDtm;

    @Column(name = "site_is_lock")
    private Boolean siteIsLock;

    public SiteRecord toRecord() {
        return SiteRecord.of(
                this.siteId,
                this.siteNm,
                this.siteLat,
                this.siteLng,
                this.siteLogoImgPath,
                this.siteUseYn,
                this.siteCreateDtm.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                this.siteUpdateDtm != null ? this.siteUpdateDtm.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null,
                this.siteIsLock
        );
    }

    public Site toUpdateEntity(SiteUpdateRecord updateReqDto, String fileName) {
        this.siteId = updateReqDto.siteId();
        this.siteNm = updateReqDto.siteNm();
        this.siteLat = updateReqDto.siteLat();
        this.siteLng = updateReqDto.siteLng();
        this.siteLogoImgPath = fileName;
        this.siteUpdateDtm = LocalDateTime.now();
        return this;
    }

    public boolean findIsLockEntity() {
        return this.siteIsLock;
    }
}
