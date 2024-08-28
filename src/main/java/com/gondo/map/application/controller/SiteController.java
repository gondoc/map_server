package com.gondo.map.application.controller;

import com.gondo.map.domain.site.dto.SiteDto;
import com.gondo.map.domain.site.service.SiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/site")
public class SiteController {

    private final SiteService siteService;

    @GetMapping("/items")
    public List<SiteDto> getItems() {
        return siteService.getAllSites();
    }
}
