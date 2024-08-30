package com.gondo.map.domain.category.entity;

import com.gondo.map.domain.category.record.CategoryRecord;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

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

    public CategoryRecord toRecord() {
        return CategoryRecord.of(this.categoryId, this.categoryNm, this.categoryContent);
    }
}
