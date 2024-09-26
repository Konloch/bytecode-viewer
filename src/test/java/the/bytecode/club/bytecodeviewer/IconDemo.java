package the.bytecode.club.bytecodeviewer;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.properties.icons.IconLoader;
import the.bytecode.club.bytecodeviewer.gui.theme.LAFTheme;
import the.bytecode.club.bytecodeviewer.resources.IconResources;
import the.bytecode.club.bytecodeviewer.translation.components.TranslatedJRadioButtonMenuItem;

import javax.swing.*;
import java.awt.*;

public class IconDemo
{

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() ->
        {
            switchToLaf(LAFTheme.SYSTEM);
            JFrame frame = new JFrame("Icon Demo");
            JMenuBar menuBar = new JMenuBar();
            frame.setJMenuBar(menuBar);

            JMenu menu = menuBar.add(new JMenu("Theme"));
            ButtonGroup lafGroup = new ButtonGroup();
            for (LAFTheme theme : LAFTheme.values())
            {
                JRadioButtonMenuItem item = new TranslatedJRadioButtonMenuItem(theme.getReadableName(), theme.getTranslation());
                if (LAFTheme.SYSTEM.equals(theme))
                    item.setSelected(true);
                lafGroup.add(item);
                item.addActionListener(e -> switchToLaf(theme));
                menu.add(item);
            }

            IconEntry[] iconEntries = new IconEntry[] {
                new IconEntry("Next", IconResources.nextIcon),
                new IconEntry("Previous", IconResources.prevIcon),
                new IconEntry("Busy", IconResources.busyIcon),
                new IconEntry(".bat", IconResources.batIcon),
                new IconEntry(".sh", IconResources.shIcon),
                new IconEntry(".cs", IconResources.csharpIcon),
                new IconEntry(".cpp", IconResources.cplusplusIcon),
                new IconEntry(".Config", IconResources.configIcon),
                new IconEntry(".jar", IconResources.jarIcon),
                new IconEntry(".zip", IconResources.zipIcon),
                new IconEntry("Package", IconResources.packagesIcon),
                new IconEntry("Folder", IconResources.folderIcon),
                new IconEntry("Android", IconResources.androidIcon),
                new IconEntry("Unknown File", IconResources.unknownFileIcon),
                new IconEntry("Text", IconResources.textIcon),
                new IconEntry(".class", IconResources.classIcon),
                new IconEntry("Image", IconResources.imageIcon),
                new IconEntry("Decoded", IconResources.decodedIcon),
                new IconEntry(".java", IconResources.javaIcon),
            };

            JList<IconEntry> iconList = new JList<>(iconEntries);
            iconList.setCellRenderer(new DefaultListCellRenderer()
            {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
                {
                    super.getListCellRendererComponent(list, ((IconEntry) value).name, index, isSelected, cellHasFocus);
                    setIcon(((IconEntry) value).icon);
                    return this;
                }
            });
            JComponent content = new JScrollPane(iconList);
            content.setPreferredSize(new Dimension(200, 400));
            frame.setContentPane(content);
            frame.pack();

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static void switchToLaf(LAFTheme theme)
    {
        try
        {
            theme.setLAF();
            LafManager.updateLaf();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private static class IconEntry
    {
        private static final int DISPLAY_SIZE = 50;

        private final String name;
        private final Icon icon;

        private IconEntry(String name, Icon icon)
        {
            this.name = name;
            this.icon = IconLoader.createDerivedIcon(icon, DISPLAY_SIZE, DISPLAY_SIZE);
        }
    }
}
