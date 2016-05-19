package com.yoosal.common;


public abstract class JdkVersion {

    public static final int JAVA_13 = 0;

    public static final int JAVA_14 = 1;

    public static final int JAVA_15 = 2;

    public static final int JAVA_16 = 3;

    public static final int JAVA_17 = 4;

    private static final String javaVersion;

    private static final int majorJavaVersion;

    static {
        javaVersion = System.getProperty("java.version");
        if (javaVersion.contains("1.7.")) {
            majorJavaVersion = JAVA_17;
        } else if (javaVersion.contains("1.6.")) {
            majorJavaVersion = JAVA_16;
        } else {
            majorJavaVersion = JAVA_15;
        }
    }

    public static String getJavaVersion() {
        return javaVersion;
    }

    public static int getMajorJavaVersion() {
        return majorJavaVersion;
    }

    @Deprecated
    public static boolean isAtLeastJava14() {
        return true;
    }

    @Deprecated
    public static boolean isAtLeastJava15() {
        return true;
    }

    @Deprecated
    public static boolean isAtLeastJava16() {
        return (majorJavaVersion >= JAVA_16);
    }

}
