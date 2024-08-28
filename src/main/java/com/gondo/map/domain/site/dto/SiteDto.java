package com.gondo.map.domain.site.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class SiteDto {
    private final String siteId;
    private final String siteNm;
    private final String siteLat;
    private final String siteLng;
    private final String siteLogoImgPath;
}
