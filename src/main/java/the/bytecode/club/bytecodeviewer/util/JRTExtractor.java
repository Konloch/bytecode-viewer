package the.bytecode.club.bytecodeviewer.util;

// Copyright 2017 Robert Grosse

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at

//    http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class JRTExtractor {
    public static void extractRT(String path) throws Throwable {
        FileSystem fs = FileSystems.getFileSystem(URI.create("jrt:/"));

        try (ZipOutputStream zipStream = new ZipOutputStream(Files.newOutputStream(Paths.get(path)));
			Stream<Path> stream =  Files.walk(fs.getPath("/"))) {
            stream.forEach(p -> {
                if (!Files.isRegularFile(p)) {
                    return;
                }

                try {
                    byte[] data = Files.readAllBytes(p);

                    List<String> list = new ArrayList<>();
                    p.iterator().forEachRemaining(p2 -> list.add(p2.toString()));
                    assert list.remove(0).equals("modules");

                    if (!list.get(list.size() - 1).equals("module-info.class")) {
                        list.remove(0);
                    }

                    list.remove(0);
                    String outPath = String.join("/", list);

                    if (!outPath.endsWith("module-info.class")) {
                        ZipEntry ze = new ZipEntry(outPath);
                        zipStream.putNextEntry(ze);
                        zipStream.write(data);
                    }
                } catch (Throwable t) {
                    throw new RuntimeException(t);
                }
            });
        }
    }
}
