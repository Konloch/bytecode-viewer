package the.bytecode.club.bytecodeviewer.resources.importing.impl;

import com.android.tools.build.bundletool.commands.BuildApksCommand;
import the.bytecode.club.bytecodeviewer.resources.importing.Importer;
import the.bytecode.club.bytecodeviewer.util.MiscUtils;

import java.io.File;
import java.nio.file.Path;

import static the.bytecode.club.bytecodeviewer.Constants.FS;
import static the.bytecode.club.bytecodeviewer.Constants.TEMP_DIRECTORY;

public class AABResourceImporter implements Importer
{

    @Override
    public void open(File file) throws Exception
    {
        String randomStr = MiscUtils.randomString(32);
        Path universalApksZipPath = Path.of(TEMP_DIRECTORY, randomStr + ".apks");

        BuildApksCommand.builder()
            .setApkBuildMode(BuildApksCommand.ApkBuildMode.UNIVERSAL)
            .setGenerateOnlyForConnectedDevice(false)
            .setBundlePath(file.toPath())
            .setOutputFile(universalApksZipPath)
            .setOutputPrintStream(System.out)
            .build()
            .execute();

        File universalApk = new File(TEMP_DIRECTORY + FS + randomStr + "_universal.apk");

        MiscUtils.extractFileFromZip(universalApksZipPath, "universal.apk", universalApk.toPath());

        APKResourceImporter.openImpl(universalApk, file.getName());
    }
}
