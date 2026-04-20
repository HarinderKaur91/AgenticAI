package com.agenticAI.autonomousFramework.Utils;

import java.util.Random;

public class TestDataUtil {
    
    private static final Random random = new Random();
    
    public static String uniqueEmail() {
        return "testuser" + System.currentTimeMillis() + random.nextInt(1000) + "@example.com";
    }
    
    public static String uniqueName() {
        return "TestUser" + System.currentTimeMillis() + random.nextInt(1000);
    }

    public static String password() {
        return "Test@12345";
    }
}