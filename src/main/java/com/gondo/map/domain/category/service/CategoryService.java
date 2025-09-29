package com.gondo.map.domain.category.service;

import com.gondo.map.domain.category.entity.Category;
import com.gondo.map.domain.category.record.CategoryRecord;
import com.gondo.map.domain.category.record.CategorySaveRecord;
import com.gondo.map.domain.category.record.CategoryUpdateRecord;
import com.gondo.map.domain.category.repository.CategoryRepository;
import com.gondo.map.domain.category.repository.query.CategoryQuery;
import com.gondo.map.domain.common.CommException;
import com.gondo.map.domain.common.CommHomeStat;
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
        return categoryRepository.getAllByUseYn().stream().map(Category::toRecord).toList();
    }

    public CategoryRecord addItem(CategorySaveRecord saveRecord) {
        return categoryRepository.save(saveRecord.toEntity()).toRecord();
    }

    public CategoryRecord modItem(CategoryUpdateRecord updateRecord) throws CommException {
        Category find = categoryRepository.findCategoryBy(updateRecord.id()).orElse(null);
        if (find == null) {
            throw new CommException(404, "수정 대상이 없습니다.");
        }

        if (find.findIsLockEntity()) {
            throw new CommException(405, "관리자의 잠금 설정으로 인해 수정이 불가합니다.");
        }
        Category updateEntity = find.toUpdateEntity(updateRecord);
        Category updatedEntity = categoryRepository.save(updateEntity);
        return updatedEntity.toRecord();
    }

    public boolean delItem(String categoryId) throws CommException {
        Category find = categoryRepository.findCategoryBy(categoryId).orElse(null);
        if (find == null) {
            throw new CommException(404, "삭제 대상이 없습니다.");
        }
        if (find.findIsLockEntity()) {
            throw new CommException(405, "관리자의 잠금 설정으로 인해 삭제가 불가합니다.");
        }
        int row = categoryRepository.deleteBySiteId(categoryId);
        return row > 0;
    }

    public CommHomeStat findHomeStat() {
        return categoryQuery.findCommHomeStat();
    }
}
