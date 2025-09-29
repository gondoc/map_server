package com.gondo.map.component.util;

import java.util.regex.Pattern;

public class EmailUtil {

    private static final Pattern BASIC_EMAIL_REGEX =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public static boolean isValid(String email) {
        if (email == null) return false;
        email = email.trim();

        // 전체 길이 제한 (RFC 권장: 254)
        if (email.isEmpty() || email.length() > 254) return false;

        // 정규식 빠른 검사
        if (!BASIC_EMAIL_REGEX.matcher(email).matches()) return false;

        // split local@domain
        int atIndex = email.lastIndexOf('@');
        if (atIndex <= 0 || atIndex == email.length() - 1) return false;

        String local = email.substring(0, atIndex);
        String domain = email.substring(atIndex + 1);

        // 로컬파트 길이 제한 (RFC: max 64)
        if (local.length() > 64) return false;

        // 연속된 점 금지
        if (local.contains("..") || domain.contains("..")) return false;

        // 로컬의 시작/끝 점 금지
        if (local.startsWith(".") || local.endsWith(".")) return false;

        // 라벨(부분)별 검사: 각 라벨은 1~63자, 하이픈으로 시작/끝 불가
        String[] labels = domain.split("\\.");
        if (labels.length < 2) return false; // 최상위 도메인 하나는 필요
        for (String label : labels) {
            if (label.isEmpty() || label.length() > 63) return false;
            if (label.startsWith("-") || label.endsWith("-")) return false;
            // 라벨은 영숫자와 하이픈만 허용 (IDN 변환 후)
            if (!label.matches("^[A-Za-z0-9-]+$")) return false;
        }

        return true;
    }

    public static String maskingEmail(String email) {
        String result = "";

        if (email == null || !email.contains("@")) {
            return null; // 유효하지 않은 이메일인 경우 원본 반환
        }

        // @ 기준으로 분리
        String[] parts = email.split("@");
        String localPart = parts[0];  // @ 앞부분 (사용자명)
        String domainPart = parts[1]; // @ 뒷부분 (도메인)

        // 로컬 파트가 너무 짧은 경우 처리
        if (localPart.length() <= 2) {
            result = localPart.charAt(0) + "*@" + domainPart;
        } else if (localPart.length() <= 4) {
            // 4글자 이하인 경우: 첫글자 + * + 마지막글자
            result = localPart.charAt(0) + "*" + localPart.charAt(localPart.length() - 1) + "@" + domainPart;
        } else if (localPart.length() == 5) {
            // 5글자인 경우: 첫 1글자 + 마스킹 + 마지막 1글자 (원래 길이 유지)
            String prefix = localPart.substring(0, 1);
            String suffix = localPart.substring(localPart.length() - 1);
            int maskLength = localPart.length() - 2; // 전체 길이에서 앞뒤 2글자씩 제외
            String mask = "*".repeat(maskLength);
            result = prefix + mask + suffix + "@" + domainPart;
        } else {
            // 5글자 이상인 경우: 첫 2글자 + 마스킹 + 마지막 2글자 (원래 길이 유지)
            String prefix = localPart.substring(0, 2);
            String suffix = localPart.substring(localPart.length() - 2);
            int maskLength = localPart.length() - 4; // 전체 길이에서 앞뒤 2글자씩 제외
            String mask = "*".repeat(maskLength);
            result = prefix + mask + suffix + "@" + domainPart;
        }

        return result;
    }
}
