package com.gondo.map.domain.category.service;

import com.gondo.map.domain.category.record.CategoryRecord;
import com.gondo.map.domain.category.entity.Category;
import com.gondo.map.domain.category.record.CategorySaveRecord;
import com.gondo.map.domain.category.repository.CategoryRepository;
import com.gondo.map.domain.category.repository.query.CategoryQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryQuery categoryQuery;

    public CategoryRecord findItem(String id) {
        return categoryQuery.findItem(id).toRecord();
    }

    public List<CategoryRecord> getItems() {
        return categoryRepository.findAll().stream().map(Category::toRecord).toList();
    }

    public CategoryRecord addItem(CategorySaveRecord saveRecord) {
        return categoryRepository.save(saveRecord.toEntity()).toRecord();
    }
}
