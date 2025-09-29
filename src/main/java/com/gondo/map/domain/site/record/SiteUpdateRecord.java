package com.gondo.map.domain.site.record;

public record SiteUpdateRecord(
        String siteId,
        String siteNm,
        String siteLat,
        String siteLng
) {

}
