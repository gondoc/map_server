package com.gondo.map.domain.site.record;

import com.gondo.map.domain.site.entity.Site;

import java.time.LocalDateTime;
import java.util.UUID;

public record SiteSaveRecord(String siteNm, String siteLat, String siteLng) {

    public Site toSiteEntity(String fileNm) {
        return Site.of(UUID.randomUUID().toString(),
                this.siteNm,
                this.siteLat,
                this.siteLng,
                fileNm,
                true,
                LocalDateTime.now(),
                null,
                false
        );
    }

}

