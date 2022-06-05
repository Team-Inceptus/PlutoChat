package us.teaminceptus.plutochat.language;

import org.bukkit.ChatColor;
import us.teaminceptus.plutochat.PlutoChat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

public enum Language {

    ENGLISH(""),
    SPANISH("_es"),
    GERMAN("_de"),
    FRENCH("_fr"),
    PORTUGUESE("_pt")
    ;

    private final String id;

    Language(String id) {
        this.id = id;
    }

    public String getIdentifier() {
        return this.id.replace("_", "");
    }

    public String getMessage(String key) {
        if (key == null) return null;

        try {
            Properties p = new Properties();
            p.load(Files.newInputStream(new File(PlutoChat.getPlugin().getDataFolder(), "plutochat" + id + ".properties").toPath()));

            return ChatColor.translateAlternateColorCodes('&', p.getProperty(key, "Unknown Value"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Language getById(String id) {
        if (id.equalsIgnoreCase("en")) return ENGLISH;
        for (Language l : values()) {
            if (l.getIdentifier().equalsIgnoreCase(id)) return l;
        }

        return null;
    }



}