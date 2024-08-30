package com.gondo.map.domain.hist.dto;

import com.gondo.map.domain.hist.entity.History;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public record HistSaveRecord(
        String histNm, Integer staffCnt,
        String categoryId,
        String siteId
) {
    public History toHistory() {
        return History.of(
                UUID.randomUUID().toString(),
                this.histNm,
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                this.staffCnt,
                this.categoryId,
                this.siteId
        );
    }
}
