package party.komas19.velocityslots;


import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigUpdater {

    private static final LinkedHashMap<String, String[]> defaultsWithComments = new LinkedHashMap<>();

    static {
        defaultsWithComments.put("max-slots-mode", new String[]{
                "# ----------------------------------------------------------",
                "# VelocitySlots Configuration File",
                "# ----------------------------------------------------------",
                "# Controls the 'max slots' shown in your server list (MOTD)",
                "# without affecting the real online player count.",
                "#",
                "# Options:",
                "#   STATIC   -> always show a fixed number",
                "#   DYNAMIC  -> show online + offset",
                "#",
                "# Note: This only fakes the max slots. Real limits still apply.",
                "# ----------------------------------------------------------",
                "#",
                "# ------------------------",
                "# MAX SLOTS MODE",
                "# ------------------------",
                "# How max slots appear:",
                "#   DYNAMIC -> online + offset",
                "#   STATIC  -> fixed static-slots value"
        });
        defaultsWithComments.put("offset", new String[]{
                "# ------------------------",
                "# DYNAMIC MODE SETTINGS",
                "# ------------------------",
                "# Used only if max-slots-mode = DYNAMIC",
                "# Shows: online + offset",
                "# Example: 50 online + 10 offset => 50/60 shown"
        });
        defaultsWithComments.put("static-slots", new String[]{
                "# ------------------------",
                "# STATIC MODE SETTINGS",
                "# ------------------------",
                "# Used only if max-slots-mode = STATIC",
                "# Always shows this number as max slots",
                "# Example: 20 online with static-slots: 1 => 20/1 shown"
        });
    }

    public static void update(Path configFile, Map<String, Object> defaults) throws IOException {
        List<String> lines = Files.exists(configFile)
                ? Files.readAllLines(configFile)
                : new ArrayList<>();

        Set<String> existingKeys = lines.stream()
                .map(line -> line.split(":", 2)[0].trim())
                .collect(Collectors.toSet());

        try (BufferedWriter writer = Files.newBufferedWriter(configFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            for (String key : defaults.keySet()) {
                if (!existingKeys.contains(key)) {
                    // add comment first
                    String[] comments = defaultsWithComments.getOrDefault(key, new String[0]);
                    for (String commentLine : comments) {
                        writer.write(commentLine);
                        writer.newLine();
                    }
                    // add key=value
                    writer.write(key + ": " + defaults.get(key));
                    writer.newLine();
                }
            }
        }
    }
}

