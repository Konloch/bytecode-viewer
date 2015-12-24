package the.bytecode.club.bytecodeviewer.gui;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.Settings;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

public class FileChooser {
    private Settings<String> target;
    private String message;

    public FileChooser(Settings<String> target, String message) {
        this.target = target;
        this.message = message;
    }

    public void run() {
        File currentFile = new File(target.get() == null || target.get().isEmpty() ? System.getProperty("user.home") : target.get());
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return true;
            }

            @Override
            public String getDescription() {
                return message;
            }
        });
        if (currentFile.isDirectory()) {
            fc.setCurrentDirectory(currentFile);
        } else {
            fc.setSelectedFile(currentFile);
        }
        fc.setFileHidingEnabled(false);
        fc.setAcceptAllFileFilterUsed(false);

        int returnVal = fc.showOpenDialog(BytecodeViewer.viewer);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                target.set(fc.getSelectedFile().getAbsolutePath());
            } catch (Exception e1) {
                new the.bytecode.club.bytecodeviewer.api.ExceptionUI(e1);
            }
        }
    }
}
