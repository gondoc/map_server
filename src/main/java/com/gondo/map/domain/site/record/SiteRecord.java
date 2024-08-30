package com.gondo.map.domain.site.record;

import com.gondo.map.domain.site.entity.Site;

public record SiteRecord(
        String id,
        String nm,
        String lat,
        String lng,
        String logoImgPath
) {

    public static SiteRecord of(String siteId, String siteNm, String siteLat, String siteLng, String siteLogoImgPath) {
        return new SiteRecord(siteId, siteNm, siteLat, siteLng, siteLogoImgPath);
    }

    public Site toSiteEntity() {
        return Site.of(this.id, this.nm, this.lat, this.lng, this.logoImgPath);
    }
}
