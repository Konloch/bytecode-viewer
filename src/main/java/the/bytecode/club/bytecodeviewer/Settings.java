package the.bytecode.club.bytecodeviewer;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.ParseException;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

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

    public static final Settings<String> PYTHON2_LOCATION = new Settings<>("python2location");
    public static final Settings<String> PYTHON3_LOCATION = new Settings<>("python3location");
    public static final Settings<String> JAVAC_LOCATION = new Settings<>("javaclocation");
    public static final Settings<String> JAVA_LOCATION = new Settings<>("javalocation");
    public static final Settings<String> RT_LOCATION = new Settings<>("rtlocation");
    public static final Settings<String> PATH = new Settings<>("path");

    private String key;
    private T value;

    public Settings(String key) {
        this.key = key;
        ALL_SETTINGS.put(this.key, this);
    }

    public T get() {
        return this.value;
    }

    public void set(T value) {
        this.value = value;
    }

    public boolean isEmpty() {
        return this.value == null || (this.value instanceof String && ((String) this.value).isEmpty());
    }

    public static void saveGUI() {
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
                    rootSettings.add(setting.getKey(), setting.getValue().get().toString());
                }
            }
            FileOutputStream out = new FileOutputStream(BytecodeViewer.settingsFile);
            out.write(settings.toString().getBytes("UTF-8"));
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadGUI() {
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
                        setting.getValue().set(rootSettings.get(setting.getKey()).asString());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}