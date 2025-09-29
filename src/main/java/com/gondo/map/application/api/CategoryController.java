package com.gondo.map.application.api;

import com.gondo.map.domain.category.record.CategoryRecord;
import com.gondo.map.domain.category.record.CategorySaveRecord;
import com.gondo.map.domain.category.record.CategoryUpdateRecord;
import com.gondo.map.domain.category.service.CategoryService;
import com.gondo.map.domain.common.CommException;
import com.gondo.map.domain.common.CommResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    @Value("${api.use}")
    private String apiUse;

    @GetMapping("/items")
    public CommResponse<List<CategoryRecord>> getItems() {
        return CommResponse.response200(categoryService.getItems());
    }

    @PostMapping("/item")
    public CommResponse<CategoryRecord> createItem(@RequestBody CategorySaveRecord saveRecord) {
        return Boolean.parseBoolean(apiUse) ? CommResponse.response200(categoryService.addItem(saveRecord)) :
                CommResponse.response405(null);
    }

    @PatchMapping("/item")
    public ResponseEntity<Object> modifyItem(@RequestBody CategoryUpdateRecord updateRecord) {
        try {
            if (!Boolean.parseBoolean(apiUse)) {
                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(categoryService.modItem(updateRecord));
        } catch (CommException e) {
            return ResponseEntity.status(e.getCode()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

    @DeleteMapping("/item/{id}")
    public ResponseEntity<Object> deleteItem(@PathVariable("id") String id) {
        try {
            if (!Boolean.parseBoolean(apiUse)) {
                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(categoryService.delItem(id));
        } catch (CommException e) {
            return ResponseEntity.status(e.getCode()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/stat")
    public ResponseEntity<Object> getStat() {
        try {
            if (!Boolean.parseBoolean(apiUse)) {
                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(categoryService.findHomeStat());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }
}
