package com.gondo.map.domain.hist.record;

import com.gondo.map.domain.hist.entity.History;

import java.util.UUID;

public record HistSaveRecord(
        String histNm,
        Integer staffCnt,
        String categoryId,
        String siteId,
        String startDtm,
        String endDtm
) {
    public History toHistory() {
        return History.of(
                UUID.randomUUID().toString(),
                this.histNm,
                this.staffCnt,
                this.categoryId,
                this.siteId,
                this.startDtm,
                this.endDtm
        );
    }
}
