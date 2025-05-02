package com.gondo.map.domain.hist.dto;

import com.gondo.map.domain.hist.record.HistRecord;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(staticName = "of")
public class YearHistDto {

    private String yearLabel;
    private List<HistRecord> histRecords;

    public void addHistRecord(List<HistRecord> histRecords) {
        this.histRecords.addAll(histRecords);
    }
}
