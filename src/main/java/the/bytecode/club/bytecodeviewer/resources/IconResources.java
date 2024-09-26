/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Konloch - Konloch.com / BytecodeViewer.com           *
 *                                                                         *
 * This program is free software: you can redistribute it and/or modify    *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation, either version 3 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 ***************************************************************************/

package the.bytecode.club.bytecodeviewer.resources;

import com.github.weisj.darklaf.iconset.AllIcons;
import com.github.weisj.darklaf.properties.icons.IconLoader;
import com.github.weisj.darklaf.properties.icons.IconResolver;
import org.imgscalr.Scalr;
import the.bytecode.club.bytecodeviewer.BytecodeViewer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Any resources loaded by disc or by memory.
 *
 * @author Konloch
 */

public class IconResources
{
    static protected final int SIZE = 9;

    public static final List<BufferedImage> iconList;
    public static final BufferedImage icon;
    public static final Icon add;
    public static final Icon remove;
    public static final Icon nextIcon;
    public static final Icon prevIcon;
    public static final Icon busyIcon;
    public static final Icon batIcon;
    public static final Icon shIcon;
    public static final Icon csharpIcon;
    public static final Icon cplusplusIcon;
    public static final Icon configIcon;
    public static final Icon jarIcon;
    public static final Icon zipIcon;
    public static final Icon packagesIcon;
    public static final Icon folderIcon;
    public static final Icon androidIcon;
    public static final Icon unknownFileIcon;
    public static final Icon textIcon;
    public static final Icon classIcon;
    public static final Icon imageIcon;
    public static final Icon decodedIcon;
    public static final Icon javaIcon;

    static
    {
        IconResolver iconResolver = IconLoader.get();
        icon = loadImageFromResource("gui/bcv_icon.png");
        add = AllIcons.Action.Add.get();
        remove = AllIcons.Action.Remove.get();
        nextIcon = iconResolver.getIcon("gui/next.svg", true);
        prevIcon = iconResolver.getIcon("gui/previous.svg", true);
        busyIcon = AllIcons.Misc.Progress.get();
        batIcon = iconResolver.getIcon("gui/bat.svg", true);
        shIcon = batIcon;
        csharpIcon = iconResolver.getIcon("gui/cs.svg", true);
        cplusplusIcon = iconResolver.getIcon("gui/cpp.svg", true);
        configIcon = iconResolver.getIcon("gui/config.svg", true);
        jarIcon = iconResolver.getIcon("gui/jarDirectory.svg", true);
        zipIcon = iconResolver.getIcon("gui/archive.svg", true);
        packagesIcon = iconResolver.getIcon("gui/package.svg", true);
        folderIcon = AllIcons.Files.Folder.get();
        androidIcon = iconResolver.getIcon("gui/android.svg");
        unknownFileIcon = AllIcons.Files.General.get();
        textIcon = AllIcons.Files.Text.get();
        classIcon = iconResolver.getIcon("gui/javaClass.svg", true);
        imageIcon = AllIcons.Files.Image.get();
        decodedIcon = iconResolver.getIcon("gui/decodedResource.svg", true);
        javaIcon = iconResolver.getIcon("gui/java.svg", true);

        iconList = new ArrayList<>();
        int size = 16;
        for (int i = 0; i < 24; i++)
        {
            iconList.add(resize(icon, size, size));
            size += 2;
        }
    }

    private static BufferedImage resize(BufferedImage image, int width, int height)
    {
        return Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, width, height);
    }

    private static BufferedImage loadImageFromResource(String imageLocation)
    {
        try
        {
            return ImageIO.read(Objects.requireNonNull(IconResources.class.getResourceAsStream("/" + imageLocation)));
        }
        catch (IOException e)
        {
            BytecodeViewer.handleException(e);
        }
        return null;
    }
}
