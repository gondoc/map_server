package com.gondo.map.domain.category.entity;

import com.gondo.map.domain.category.record.CategoryRecord;
import com.gondo.map.domain.category.record.CategoryUpdateRecord;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Table(name = "tbl_project_category", schema = "project")
@Entity
public class Category {

    @Id
    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "category_nm")
    private String categoryNm;

    @Column(name = "category_content")
    private String categoryContent;

    @Column(name = "category_create_dtm")
    private LocalDateTime categoryCreateDtm;

    @Column(name = "category_update_dtm")
    private LocalDateTime categoryUpdateDtm;

    @Column(name = "category_is_lock")
    private Boolean categoryIsLock;

    @Column(name = "category_use_yn")
    private Boolean categoryUseYn;

    public CategoryRecord toRecord() {
        return CategoryRecord.of(this.categoryId,
                this.categoryNm,
                this.categoryContent,
                this.categoryCreateDtm.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                this.categoryUpdateDtm != null ? this.categoryUpdateDtm.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null,
                this.categoryIsLock
        );
    }

    public boolean findIsLockEntity() {
        return this.categoryIsLock;
    }

    public boolean findUseYnEntity() {
        return this.categoryUseYn;
    }

    public Category toUpdateEntity(CategoryUpdateRecord updateRecord) {
        this.categoryId = updateRecord.id();
        this.categoryNm = updateRecord.nm();
        this.categoryContent = updateRecord.content();
        this.categoryUpdateDtm = LocalDateTime.now();
        return this;
    }
}
