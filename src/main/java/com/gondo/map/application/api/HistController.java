package com.gondo.map.application.api;

import com.gondo.map.domain.hist.dto.HistRecord;
import com.gondo.map.domain.hist.dto.HistSaveRecord;
import com.gondo.map.domain.hist.service.HistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hist")
public class HistController {

    private final HistService histService;

    @GetMapping
    public List<HistRecord> getItems() {
        return histService.getItems();
    }

    @PostMapping("item")
    public HistRecord addItem(@RequestBody HistSaveRecord saveRecord) {
        return histService.addItem(saveRecord);
    }
}
