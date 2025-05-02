package com.gondo.map.application.api;

import com.gondo.map.domain.hist.record.HistRecord;
import com.gondo.map.domain.hist.record.HistSaveRecord;
import com.gondo.map.domain.hist.dto.YearHistDto;
import com.gondo.map.domain.hist.service.HistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hist")
public class HistController {

    private final HistService histService;

    @GetMapping("/items")
    public List<HistRecord> getItems() {
        return histService.getItems();
    }

    @GetMapping("/year-items")
    public List<YearHistDto> getItemsBy() {
        return histService.getYearItems();
    }

    @PostMapping("/item")
    public HistRecord addItem(@RequestBody HistSaveRecord saveRecord) {
        return histService.addItem(saveRecord);
    }
}
