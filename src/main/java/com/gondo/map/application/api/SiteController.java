package com.gondo.map.application.api;

import com.gondo.map.domain.common.CommException;
import com.gondo.map.domain.common.CommResponse;
import com.gondo.map.domain.site.record.SiteRecord;
import com.gondo.map.domain.site.record.SiteSaveRecord;
import com.gondo.map.domain.site.record.SiteUpdateRecord;
import com.gondo.map.domain.site.service.SiteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/site")
public class SiteController {

    private final SiteService siteService;

    @Value("${api.use}")
    private String apiUse;

    @GetMapping("/items")
    public CommResponse<List<SiteRecord>> getItems() {
        return CommResponse.response200(siteService.getAllSites());
    }

    @PostMapping(value = "/item")
    public CommResponse<SiteRecord> addItem(
            @ModelAttribute SiteSaveRecord insertDto,
            @RequestParam(value = "logoFile", required = false) MultipartFile logoFile
    ) {
        return Boolean.parseBoolean(apiUse) ? CommResponse.response200(siteService.addItem(insertDto, logoFile)) :
                CommResponse.response405(null);
    }

    @PatchMapping(value = "/item")
    public ResponseEntity<?> modItem(
            @ModelAttribute SiteUpdateRecord updateDto,
            @RequestParam(value = "logoFile", required = false) MultipartFile logoFile
    ) {
        try {
            if (!Boolean.parseBoolean(apiUse)) {
                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(siteService.modItem(updateDto, logoFile));
        } catch (CommException e) {
            return ResponseEntity.status(e.getCode()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping(value = "/item/{id}")
    public ResponseEntity<?> delItem(@PathVariable("id") String id) {
        try {
            if (!Boolean.parseBoolean(apiUse)) {
                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
            }
            boolean result = siteService.delItem(id);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (CommException e) {
            return ResponseEntity.status(e.getCode()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
