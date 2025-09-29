package com.gondo.map.domain.category.record;

public record CategoryRecord(
        String id,
        String nm,
        String content,
        String createDtm,
        String updateDtm,
        boolean isLock
) {
    public static CategoryRecord of(String id, String nm, String content, String createDtm, String updateDtm, boolean isLock) {
        return new CategoryRecord(id, nm, content, createDtm, updateDtm, isLock);
    }
}
