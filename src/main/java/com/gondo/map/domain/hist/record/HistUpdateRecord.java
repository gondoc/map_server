package com.gondo.map.domain.hist.record;

import com.gondo.map.domain.hist.entity.History;

import java.time.LocalDateTime;
import java.util.UUID;

public record HistUpdateRecord(
        String histId,
        String histNm,
        Integer staffCnt,
        String categoryId,
        String siteId,
        String startDtm,
        String endDtm
) {
    public History toHistory() {
        return History.of(
                this.histId,
                this.histNm,
                this.staffCnt,
                this.categoryId,
                this.siteId,
                this.startDtm,
                this.endDtm,
                LocalDateTime.now(),
                null,
                false,
                true
        );
    }
}
