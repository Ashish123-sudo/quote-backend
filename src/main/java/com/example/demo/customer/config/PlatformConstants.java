package com.example.demo.customer.config;

import java.util.UUID;

public class PlatformConstants {

    // This is the super admin org — matches the fallback in SecurityHelper
    public static final UUID PLATFORM_ORG_ID =
            UUID.fromString("10000000-0000-0000-0000-000000000001");

    private PlatformConstants() {}

    public static boolean isPlatformOrg(UUID orgId) {
        return PLATFORM_ORG_ID.equals(orgId);
    }
}