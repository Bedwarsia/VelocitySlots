package party.komas19.velocityslots.utils;

import org.slf4j.Logger;

public class JavaVersionChecker {

    public static int getMajorJavaVersion() {
        String version = System.getProperty("java.version");
        int dot = version.indexOf(".");
        return dot != -1 ? Integer.parseInt(version.substring(0, dot)) : Integer.parseInt(version);
    }

    public static void checkJavaVersion(Logger logger) {
        int javaVersion = getMajorJavaVersion();

        if (javaVersion >= 17 && javaVersion <= 20) {
            String border = "============================================================";
            String empty  = "=                                                          =";
            logger.warn(border);
            logger.warn(empty);
            logger.warn("=  WARNING: You are not running Java 21 or higher!         =");
            logger.warn(empty);
            logger.warn("=  Velocity recommends Java 21+ for best performance.      =");
            logger.warn("=  Please consider upgrading, as support for Java 17–20    =");
            logger.warn("=  may be removed in the future!                           =");
            logger.warn(empty);
            logger.warn(border);
        }
    }
}
