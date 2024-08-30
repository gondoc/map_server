package com.gondo.map.application.api;

import com.gondo.map.domain.category.record.CategoryRecord;
import com.gondo.map.domain.category.record.CategorySaveRecord;
import com.gondo.map.domain.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/items")
    public List<CategoryRecord> getItems() {
        return categoryService.getItems();
    }

    @PostMapping("/item")
    public CategoryRecord createItem(@RequestBody CategorySaveRecord saveRecord) {
        return categoryService.addItem(saveRecord);
    }
}
