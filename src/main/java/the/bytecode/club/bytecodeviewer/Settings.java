package the.bytecode.club.bytecodeviewer;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.ParseException;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;

import javax.swing.*;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.*;

/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Kalen 'Konloch' Kinloch - http://bytecodeviewer.com  *
 * *
 * This program is free software: you can redistribute it and/or modify    *
 * it under the terms of the GNU General Public License as published by  *
 * the Free Software Foundation, either version 3 of the License, or     *
 * (at your option) any later version.                                   *
 * *
 * This program is distributed in the hope that it will be useful,       *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 * GNU General Public License for more details.                          *
 * *
 * You should have received a copy of the GNU General Public License     *
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 ***************************************************************************/

/**
 * Used to handle loading/saving the GUI (options).
 *
 * @author Konloch
 */
public class Settings<T> {
    private static final Map<String, Settings> ALL_SETTINGS = new HashMap<>();

    public static final Settings<String> PYTHON2_LOCATION = new Settings<>("python2location", "");
    public static final Settings<String> PYTHON3_LOCATION = new Settings<>("python3location", "");
    public static final Settings<String> JAVAC_LOCATION = new Settings<>("javaclocation", "");
    public static final Settings<String> JAVA_LOCATION = new Settings<>("javalocation", "");
    public static final Settings<String> RT_LOCATION = new Settings<>("rtlocation", "");
    public static final Settings<String> PATH = new Settings<>("path", "");

    public static final Settings<List<Decompiler>> PANES = new Settings<>("panes",
            Arrays.asList(Decompiler.FERNFLOWER, Decompiler.BYTECODE, null));

    public static final Settings<Boolean> COMPILE_ON_SAVE = new Settings<>("compileOnSave", true);
    public static final Settings<Boolean> COMPILE_ON_REFRESH = new Settings<>("compileOnRefresh", true);
    public static final Settings<Boolean> REFRESH_ON_CHANGE = new Settings<>("refreshOnChange", false);
    public static final Settings<Boolean> DECODE_APK_RESOURCES = new Settings<>("decodeAPKResources", true);
    public static final Settings<Boolean> UPDATE_CHECK = new Settings<>("updateCheck", true);
    public static final Settings<Boolean> DELETE_OUTDATED_LIBS = new Settings<>("deleteOutdatedLibs", true);
    public static final Settings<Integer> GUI_WIDTH = new Settings<>("guiWidth", 800);
    public static final Settings<Integer> GUI_HEIGHT = new Settings<>("guiHeight", 400);
    public static final Settings<Integer> FILE_NAVIGATION_PANE_WIDTH = new Settings<>("fileNavigationPaneWidth", 200);
    public static final Settings<Integer> FILE_NAVIGATION_PANE_HEIGHT = new Settings<>("fileNavigationPaneHeight", 50);
    public static final Settings<Integer> SEARCHING_PANE_WIDTH = new Settings<>("searchingPaneWidth", 200);
    public static final Settings<Integer> SEARCHING_PANE_HEIGHT = new Settings<>("searchingPaneHeight", 50);
    public static final Settings<Boolean> SYNCHRONIZE_VIEWING = new Settings<>("synchronizeViewing", false);
    public static final Settings<Boolean> SHOW_METHODS_LIST = new Settings<>("showMethodsList", false);

    private String key;
    private T value;

    public Settings(String key, T value) {
        this.key = key;
        this.value = value;
        ALL_SETTINGS.put(this.key, this);
    }

    public T get() {
        if (this.isEmpty()) {
            return null;
        }
        return this.value;
    }

    public String getType() {
        if (this.value != null) {
            return this.value.getClass().getSimpleName();
        }
        return "";
    }

    public void set(T value) {
        this.value = value;
    }

    public boolean isEmpty() {
        return this.value == null ||
                (this.value instanceof String && ((String) this.value).isEmpty()) ||
                (this.value instanceof Integer && ((Integer) this.value) == 0) ||
                (this.value instanceof ArrayList && ((ArrayList) this.value).isEmpty());
    }

    private static void savePanes(JsonObject rootSettings) {
        if (rootSettings.get("panes") == null) {
            rootSettings.add("panes", new JsonObject());
        }
        JsonObject panes = rootSettings.get("panes").asObject();
        for (int i = 0; i < BytecodeViewer.viewer.allPanes.size(); i++) {
            ButtonGroup group = BytecodeViewer.viewer.allPanes.get(i);
            for (Map.Entry<JRadioButtonMenuItem, Decompiler> entry : BytecodeViewer.viewer.allDecompilers.get(group).entrySet()) {
                if (group.isSelected(entry.getKey().getModel())) {
                    if (entry.getValue() != null) {
                        panes.add(Integer.toString(i), entry.getValue().getName());
                    }
                }
            }
        }
    }

    private static void loadPanes(JsonObject rootSettings) {
        if (rootSettings.get("panes") != null) {
            JsonObject panes = rootSettings.get("panes").asObject();
            List<Decompiler> decompilers = new ArrayList<>();
            for(JsonObject.Member value : panes) {
                decompilers.add(Decompiler.getByName(value.getValue().asString()));
            }
            Settings.PANES.set(decompilers);
        }
    }

    public static void saveSettings() {
        try {
            JsonObject settings = new JsonObject();
            Decompiler.CFR.getSettings().saveTo(settings);
            Decompiler.FERNFLOWER.getSettings().saveTo(settings);
            Decompiler.PROCYON.getSettings().saveTo(settings);
            Decompiler.BYTECODE.getSettings().saveTo(settings);
            if (settings.get("settings") == null) {
                settings.add("settings", new JsonObject());
            }
            JsonObject rootSettings = settings.get("settings").asObject();
            for (Map.Entry<String, Settings> setting : Settings.ALL_SETTINGS.entrySet()) {
                if (setting.getValue().get() != null) {
                    switch (setting.getValue().getType()) {
                        case "Integer":
                            rootSettings.add(setting.getKey(), (Integer) setting.getValue().get());
                            break;
                        case "Boolean":
                            rootSettings.add(setting.getKey(), (Boolean) setting.getValue().get());
                            break;
                        case "ArrayList":
                            if (setting.getKey().equals(Settings.PANES.key)) savePanes(rootSettings);
                            break;
                        case "String":
                        default:
                            rootSettings.add(setting.getKey(), setting.getValue().get().toString());
                    }
                }
            }
            FileOutputStream out = new FileOutputStream(BytecodeViewer.settingsFile);
            out.write(settings.toString().getBytes("UTF-8"));
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadSettings() {
        try {
            JsonObject settings = new JsonObject();
            try {
                settings = JsonObject.readFrom(new FileReader(BytecodeViewer.settingsFile));
            } catch (ParseException | UnsupportedOperationException e) {
            }
            Decompiler.CFR.getSettings().loadFrom(settings);
            Decompiler.FERNFLOWER.getSettings().loadFrom(settings);
            Decompiler.PROCYON.getSettings().loadFrom(settings);
            Decompiler.BYTECODE.getSettings().loadFrom(settings);
            if (settings.get("settings") != null) {
                JsonObject rootSettings = settings.get("settings").asObject();
                for (Map.Entry<String, Settings> setting : Settings.ALL_SETTINGS.entrySet()) {
                    if (rootSettings.get(setting.getKey()) != null) {
                        switch (setting.getValue().getType()) {
                            case "Integer":
                                setting.getValue().set(rootSettings.get(setting.getKey()).asInt());
                                break;
                            case "Boolean":
                                setting.getValue().set(rootSettings.get(setting.getKey()).asBoolean());
                                break;
                            case "ArrayList":
                                if (setting.getKey().equals(Settings.PANES.key)) loadPanes(rootSettings);
                                break;
                            case "String":
                            default:
                                setting.getValue().set(rootSettings.get(setting.getKey()).asString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}