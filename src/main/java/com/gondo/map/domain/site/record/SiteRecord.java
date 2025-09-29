package com.gondo.map.domain.site.record;

import com.gondo.map.domain.site.entity.Site;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record SiteRecord(
        String id,
        String nm,
        String lat,
        String lng,
        String logoImgPath,
        boolean useYn,
        String createDtm,
        String updateDtm,
        boolean isLock
) {

    public static SiteRecord of(String siteId, String siteNm, String siteLat, String siteLng, String siteLogoImgPath, boolean useYn,
                                String createDtm, String updateDtm, boolean isLock) {
        return new SiteRecord(siteId, siteNm, siteLat, siteLng, siteLogoImgPath, useYn, createDtm, updateDtm, isLock);
    }

    public Site toSiteEntity() {
        return Site.of(
                this.id,
                this.nm,
                this.lat,
                this.lng,
                this.logoImgPath,
                this.useYn,
                LocalDateTime.parse(this.createDtm, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                this.updateDtm != null ? LocalDateTime.parse(this.updateDtm, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null,
                this.isLock
        );
    }
}
