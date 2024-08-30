package com.gondo.map.domain.category.record;

import lombok.AllArgsConstructor;
import lombok.Getter;

public record CategoryRecord(
        String id,
        String nm,
        String content
) {
    public static CategoryRecord of(String id, String nm, String content) {
        return new CategoryRecord(id, nm, content);
    }
}
