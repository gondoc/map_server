package com.gondo.map.domain.category.record;

import com.gondo.map.domain.category.entity.Category;

import java.util.UUID;

public record CategorySaveRecord (
        String name,
        String content
) {
    public Category toEntity() {
        return Category.of(
                UUID.randomUUID().toString(),
                this.name,
                this.content
        );
    }
}
