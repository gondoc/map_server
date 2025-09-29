package com.gondo.map.domain.hist.record;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record HistRecord(
        String id,
        String histNm,
        String startDtm,
        String endDtm,
        Integer staffCnt,
        boolean isLock,
//        LocalDateTime createDtm,
        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime lastUpdateDtm,
        String categoryNm,
        String categoryContent,
        String siteId,
        String siteNm,
        String lat,
        String lng,
        String logoImgPath
) {
    public static HistRecord of(
            String id,
            String histNm,
            String startDtm,
            String endDtm,
            Integer staffCnt,
            boolean isLock,
//            LocalDateTime createDtm,
            LocalDateTime lastUpdateDtm,
            String categoryNm,
            String categoryContent,
            String siteId,
            String siteNm,
            String lat,
            String lng,
            String logoImgPath
    ) {
        return new HistRecord(
                id,
                histNm,
                startDtm,
                endDtm,
                staffCnt,
                isLock,
//                createDtm,
                lastUpdateDtm,
                categoryNm,
                categoryContent,
                siteId,
                siteNm,
                lat,
                lng,
                logoImgPath
        );
    }
}
