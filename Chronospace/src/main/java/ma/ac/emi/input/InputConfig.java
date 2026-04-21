package ma.ac.emi.input;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class InputConfig {

    private static final String CONFIG_PATH = "src/main/configs/resources/input_config.json";
    private static InputConfig instance;

    public int moveUp    = KeyEvent.VK_W;
    public int moveDown  = KeyEvent.VK_S;
    public int moveLeft  = KeyEvent.VK_A;
    public int moveRight = KeyEvent.VK_D;

    public int switchWeapon = KeyEvent.VK_E;
    public int reload = KeyEvent.VK_R;
    public int pause        = KeyEvent.VK_ESCAPE;

    public int aimUp    = KeyEvent.VK_UP;
    public int aimDown  = KeyEvent.VK_DOWN;
    public int aimLeft  = KeyEvent.VK_LEFT;
    public int aimRight = KeyEvent.VK_RIGHT;

    public boolean keyboardAimMode = false;

    public static InputConfig getInstance() {
        if (instance == null) instance = load();
        return instance;
    }

    private static InputConfig load() {
        try {
            Path p = Paths.get(CONFIG_PATH);
            if (Files.exists(p)) {
                String json = Files.readString(p);
                InputConfig cfg = new Gson().fromJson(json, InputConfig.class);
                if (cfg != null) return cfg;
            }
        } catch (Exception e) {
            System.err.println("InputConfig: could not load, using defaults. " + e.getMessage());
        }
        return new InputConfig();
    }

    public void save() {
        try {
            Path p = Paths.get(CONFIG_PATH);
            Files.createDirectories(p.getParent());
            String json = new GsonBuilder().setPrettyPrinting().create().toJson(this);
            Files.writeString(p, json);
        } catch (Exception e) {
            System.err.println("InputConfig: could not save. " + e.getMessage());
        }
    }

    public static String keyName(int vk) {
        String name = KeyEvent.getKeyText(vk);
        return (name == null || name.isBlank()) ? "?" : name.toUpperCase();
    }
}