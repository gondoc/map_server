package com.gondo.map.application.api;

import com.gondo.map.domain.site.record.SiteRecord;
import com.gondo.map.domain.site.record.SiteSaveRecord;
import com.gondo.map.domain.site.service.SiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/site")
public class SiteController {

    private final SiteService siteService;

    @GetMapping("/items")
    public List<SiteRecord> getItems() {
        return siteService.getAllSites();
    }

    @PostMapping("/item")
    public SiteRecord addItem(@RequestBody SiteSaveRecord insertDto) {
        return siteService.addItem(insertDto);
    }
}
