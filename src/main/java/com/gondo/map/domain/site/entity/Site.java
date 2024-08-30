package com.gondo.map.domain.site.entity;

import com.gondo.map.domain.site.record.SiteRecord;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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

    public SiteRecord toDto() {
        return SiteRecord.of(this.siteId, this.siteNm, this.siteLat, this.siteLng, this.siteLogoImgPath);
    }

}
