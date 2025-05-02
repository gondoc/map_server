package com.gondo.map.domain.category.record;

public record CategoryRecord(
        String id,
        String nm,
        String content
) {
    public static CategoryRecord of(String id, String nm, String content) {
        return new CategoryRecord(id, nm, content);
    }
}
