package com.gondo.map.domain.hist.dto;

public record HistRecord(String id, String histNm, String dtm, Integer staffCnt,
                         String categoryNm, String categoryContent,
                         String siteNm, String lat, String lng, String logoImgPath) {

    public static HistRecord of(String id, String histNm, String dtm, Integer staffCnt,
                                String categoryNm, String categoryContent,
                                String siteNm, String lat, String lng, String logoImgPath) {
        return new HistRecord(id, histNm, dtm, staffCnt, categoryNm, categoryContent, siteNm, lat, lng, logoImgPath);
    }
}
