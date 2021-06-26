# Bytecode Viewer

Bytecode Viewer - a lightweight user friendly Java Bytecode Viewer.

#### New Features
* Dark Mode
* Updated nearly all dependencies (incl. decompilers like CFR, JD-GUI etc.)
* Updated ASM library to version 9.1
* Added support to Java files compiled using JDK > 13
* Migrated to Maven

#### Links
* Website: https://bytecodeviewer.com
* Source Code: https://github.com/konloch/bytecode-viewer
* Bin/Archive: https://github.com/konloch/bytecode-viewer/releases
* Java Docs: https://the.bytecode.club/docs/bytecode-viewer/
* License (Copyleft): https://raw.githubusercontent.com/Konloch/bytecode-viewer/master/LICENSE
* Credits: https://github.com/Konloch/bytecode-viewer/blob/master/CREDITS.md
* Contributing: https://github.com/Konloch/bytecode-viewer/blob/master/CONTRIBUTING.md
* Report Bugs: https://github.com/Konloch/bytecode-viewer/issues
* Discussion Forum: https://the.bytecode.club/forumdisplay.php?fid=69

#### Key Features
* Krakatau Integration for Bytecode assembly/disassembly.
* Smali/BakSmali Integration - You can now edit class files/dex files via smali!
* APK/DEX Support - Using Dex2Jar and Jar2Dex it's able to load and save APKs with ease!
* Java Decompiler - It utilizes FernFlower, Procyon, CFR, JD & JADX for decompilation.
* Bytecode Decompiler - A modified version of CFIDE's.
* Hex Viewer - Powered by JHexPane.
* Each Decompiler/Editor/Viewer is toggleable, you can also select what will display on each pane.
* Fully Featured Search System - Search through strings, functions, variables and more!
* A Plugin System With Built In Plugins - (Show All Strings, Malicious Code Scanner, String Decrypters, etc)
* Fully Featured Scripting System That Supports Groovy, Java, Python, Ruby & Javascript.
* EZ-Inject - Graphically insert hooks and debugging code, invoke main and start the program.
* And more! Give it a try for yourself!

#### Command Line Input
```
	-help                         Displays the help menu
	-list                         Displays the available decompilers
	-decompiler <decompiler>      Selects the decompiler, procyon by default
	-i <input file>               Selects the input file (Jar, Class, APK, ZIP, DEX all work automatically)
	-o <output file>              Selects the output file (Java or Java-Bytecode)
	-t <target classname>         Must either be the fully qualified classname or "all" to decompile all as zip
	-nowait                       Doesn't wait for the user to read the CLI messages
```

## What is Bytecode Viewer?
Bytecode Viewer (BCV) is an Advanced Lightweight Java Bytecode Viewer, GUI Java Decompiler, GUI Bytecode Editor, GUI Smali, GUI Baksmali, GUI APK Editor, GUI Dex Editor, GUI APK Decompiler, GUI DEX Decompiler, GUI Procyon Java Decompiler, GUI Krakatau, GUI CFR Java Decompiler, GUI FernFlower Java Decompiler, GUI DEX2Jar, GUI Jar2DEX, GUI Jar-Jar, Hex Viewer, Code Searcher, Debugger and more.

It's written completely in Java, and it's open sourced. It's currently being maintained and developed by Konloch.

## How do I install BCV?
Download the latest version from https://github.com/konloch/bytecode-viewer/releases and run the Bytecode-Viewer-2.10.x.jar.
You may need to execute it via command line ```java -jar Bytecode-Viewer-2.10.x.jar``` (replace the X with the current minor version)

## How do I use BCV?
All you have to do is add a jar, class or APK file into the workspace. Then select the file you'd like to view from the workspace. BCV will automatically start decompiling the class in the background. When it's done it will show the Source code, Bytecode and Hexcode of the class file you chose (depending on the View panes you have selected). If you are trying to view a resource BCV will attempt to display it the best it can with code highlighting or by embedding the resources itself.

## How do the plugins work?
There is also a plugin system that will allow you to interact with the loaded classfiles. You could for example write a String deobfuscator, a malicious code searcher, or anything else you can think of.

You can either use one of the pre-written plugins, or write your own. The plugin system supports groovy, python, ruby, java and javascript scripting.

Once a plugin is activated, it will execute the plugin with a ClassNode ArrayList of every single class loaded in BCV, this allows the user to handle it completely using ASM.

## Instructions to compile

Just clone this repo and run ``mvn package``. It's that simple!

## Working on the source

Open the Maven project (e.g. in IntelliJ, open the ``pom.xml`` as a project file).

## Java Heap Space Issues
Start BCV with more RAM, e.g. `java -Xmx3G -jar BCV.jar`

#### Are you a Java Reverse Engineer? Do you want to learn?
Join The Bytecode Club Today! - https://the.bytecode.club
