package the.bytecode.club.bytecodeviewer;

import com.eclipsesource.json.JsonObject;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DecompilerSettings {
    private Decompiler decompiler;

    public DecompilerSettings(Decompiler decompiler) {
        this.decompiler = decompiler;
    }

    private Map<Setting, JCheckBoxMenuItem> menuItems = new HashMap<>();
    private List<Setting> registrationOrder = new ArrayList<>();

    public void registerSetting(Setting setting) {
        if (!menuItems.containsKey(setting)) {
            registrationOrder.add(setting);
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(setting.getText());
            if (setting.isDefaultOn()) {
                item.setSelected(true);
            }
            menuItems.put(setting, item);
        }
    }

    public boolean isSelected(Setting setting) {
        return menuItems.get(setting).isSelected();
    }

    public JCheckBoxMenuItem getMenuItem(Setting setting) {
        return menuItems.get(setting);
    }

    public int size() {
        return registrationOrder.size();
    }

    public void loadFrom(JsonObject rootSettings) {
        if (rootSettings.get("decompilers") != null) {
            JsonObject decompilerSection = rootSettings.get("decompilers").asObject();
            if (decompilerSection.get(decompiler.getName()) != null) {
                JsonObject thisDecompiler = decompilerSection.get(decompiler.getName()).asObject();
                for (Map.Entry<Setting, JCheckBoxMenuItem> entry : menuItems.entrySet()) {
                    if (thisDecompiler.get(entry.getKey().getParam()) != null) {
                        entry.getValue().setSelected(thisDecompiler.get(entry.getKey().getParam()).asBoolean());
                    }
                }
            }
        }
    }

    public void saveTo(JsonObject rootSettings) {
        if (rootSettings.get("decompilers") == null) {
            rootSettings.add("decompilers", new JsonObject());
        }
        JsonObject decompilerSection = rootSettings.get("decompilers").asObject();
        if (decompilerSection.get(decompiler.getName()) == null) {
            decompilerSection.add(decompiler.getName(), new JsonObject());
        }
        JsonObject thisDecompiler = decompilerSection.get(decompiler.getName()).asObject();
        for (Map.Entry<Setting, JCheckBoxMenuItem> entry : menuItems.entrySet()) {
            thisDecompiler.add(entry.getKey().getParam(), entry.getValue().isSelected());
        }
    }

    public interface Setting {
        String getText();

        String getParam();

        boolean isDefaultOn();
    }
}
