package the.bytecode.club.bytecodeviewer.classparser;

public enum ClassVersion {

    UNKNOWN(0, 0),

    JAVA_1_1(45, 3),
    JAVA_1_2(46, 0),
    JAVA_1_3(47, 0),
    JAVA_1_4(48, 0),
    JAVA_5(49, 0),
    JAVA_6(50, 0),
    JAVA_7(51, 0),
    JAVA_8(52, 0),
    JAVA_9(53, 0),
    JAVA_10(54, 0),
    JAVA_11(55, 0),
    JAVA_12(56, 0),
    JAVA_13(57, 0),
    JAVA_14(58, 0),
    JAVA_15(59, 0),
    JAVA_16(60, 0),
    JAVA_17(61, 0);

    public final int major;
    public final int minor;

    ClassVersion(int major, int minor) {
        this.major = major;
        this.minor = minor;
    }

    public static ClassVersion check(int major, int minor) {
        for (ClassVersion v : ClassVersion.values()) {
            if (v.major == major && v.minor == minor) {
                return v;
            }
        }
        return UNKNOWN;
    }
}
