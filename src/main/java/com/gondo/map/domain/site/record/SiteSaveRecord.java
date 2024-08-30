package com.gondo.map.domain.site.record;

import com.gondo.map.domain.site.entity.Site;

import java.util.UUID;

public record SiteSaveRecord(String siteNm, String siteLat, String siteLng, String fileNm) {

    public Site toSiteEntity() {
        return Site.of(UUID.randomUUID().toString(),
                this.siteNm,
                this.siteLat,
                this.siteLng,
                this.fileNm
        );
    }

}

