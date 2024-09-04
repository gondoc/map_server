package com.gondo.map.domain.hist.dto;

public record HistRecord(String id, String histNm, String startDtm, String endDtm, Integer staffCnt,
                         String categoryNm, String categoryContent,
                         String siteNm, String lat, String lng, String logoImgPath) {

    public static HistRecord of(String id, String histNm, String startDtm, String endDtm, Integer staffCnt,
                                String categoryNm, String categoryContent,
                                String siteNm, String lat, String lng, String logoImgPath) {
        return new HistRecord(id, histNm, startDtm, endDtm, staffCnt, categoryNm, categoryContent, siteNm, lat, lng, logoImgPath);
    }
}
