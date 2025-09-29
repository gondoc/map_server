package com.gondo.map.component.util;

import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

@Component
public class RandomUtil {

    private final Random random = new Random();

    public String getRandomUUIDCd() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    public int getRandomIntByMax(int max) {
        random.setSeed(System.currentTimeMillis());
        return random.nextInt(max - 1);
    }

    public String getRandomIntValuesByLength(int length) {
        if (length <= 0) {
            return getSingleNumber();
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(getSingleNumber());
        }
        return result.toString();
    }

    private String getSingleNumber() {
        return String.valueOf(random.nextInt(10));
    }
}
