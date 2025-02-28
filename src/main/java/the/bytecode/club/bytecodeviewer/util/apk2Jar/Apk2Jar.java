package the.bytecode.club.bytecodeviewer.util.apk2Jar;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.gui.MainViewerGUI;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainerImporter;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

import static the.bytecode.club.bytecodeviewer.Constants.FS;
import static the.bytecode.club.bytecodeviewer.Constants.TEMP_DIRECTORY;

public abstract class Apk2Jar
{

    private static final Object workLock = new Object();

    final public ResourceContainer resourceContainerFromApk(File inputApk) throws IOException {
        synchronized (workLock) {
            return resourceContainerFromApkImpl(inputApk);
        }
    }

    protected abstract ResourceContainer resourceContainerFromApkImpl(File inputApk) throws IOException;

    final protected File createTempJarFile()
    {
        String name = MiscUtils.getRandomizedName() + ".jar";
        return new File(TEMP_DIRECTORY + FS + name);
    }

    final protected File createTempFolder()
    {
        String name = MiscUtils.getRandomizedName();
        File folder = new File(TEMP_DIRECTORY + FS + name);
        if (!folder.mkdir())
        {
            throw new RuntimeException("Failed to create temp folder: " + folder.getAbsolutePath());
        }
        return folder;
    }

    final protected ResourceContainer createResourceContainerFromJar(File output) throws IOException
    {
        return new ResourceContainerImporter(new ResourceContainer(output)).importAsZip().getContainer();
    }

    final protected ResourceContainer createResourceContainerFromFolder(File output) throws IOException
    {
        return new ResourceContainerImporter(new ResourceContainer(output)).importAsFolder().getContainer();
    }

    /**
     * Translates dex classes from an apk to a folder
     *
     * @param input The apk file
     * @return Folder with the .class files
     */
    final public File apk2Folder(File input) {
        File folder = createTempFolder();
        apk2FolderImpl(input, folder);
        return folder;
    }

    /**
     * Translates and repackage dex classes from an apk to a jar
     *
     * @param input The apk file
     * @return The jar file
     */
    final public File apk2Jar(File input) {
        File output = createTempJarFile();
        synchronized (workLock) {
            apk2JarImpl(input, output);
        }
        return output;
    }

    protected abstract void apk2JarImpl(File input, File output);
    protected abstract void apk2FolderImpl(File input, File output);

    public static Apk2Jar obtainImpl() {
        MainViewerGUI viewer = BytecodeViewer.viewer;
        ButtonGroup apkConversionGroup = viewer.apkConversionGroup;

        if (apkConversionGroup.isSelected(viewer.apkConversionDex.getModel()))
            return new Dex2Jar();
        else if (apkConversionGroup.isSelected(viewer.apkConversionEnjarify.getModel()))
            return new Enjarify();

        throw new RuntimeException("Unknown implementation");
    }
}
