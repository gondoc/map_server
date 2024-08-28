package com.gondo.map.domain.site.entity;

import com.gondo.map.domain.site.dto.SiteDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Table(name = "tbl_project_site", schema = "project")
@Entity
public class SiteEntity {

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

    public SiteDto toDto() {
        return SiteDto.of(this.siteId, this.siteNm, this.siteLat, this.siteLng, this.siteLogoImgPath);
    }

}
