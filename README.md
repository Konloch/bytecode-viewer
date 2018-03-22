# Bytecode Viewer

Bytecode Viewer is an Advanced Lightweight Java Bytecode Viewer, GUI Java Decompiler, GUI Bytecode Editor, GUI Smali, GUI Baksmali, GUI APK Editor, GUI Dex Editor, GUI APK Decompiler, GUI DEX Decompiler, GUI Procyon Java Decompiler, GUI Krakatau, GUI CFR Java Decompiler, GUI FernFlower Java Decompiler, GUI DEX2Jar, GUI Jar2DEX, GUI Jar-Jar, Hex Viewer, Code Searcher, Debugger and more.
It's written completely in Java, and it's open sourced. It's currently being maintained and developed by Konloch.

There is also a plugin system that will allow you to interact with the loaded classfiles, for example you can write a String deobfuscator, a malicious code searcher, or something else you can think of.
You can either use one of the pre-written plugins, or write your own. It supports groovy scripting. Once a plugin is activated, it will execute the plugin with a ClassNode ArrayList of every single class loaded in BCV, this allows the user to handle it completely using ASM.

Code from various projects has been used, including but not limited to:
* J-RET by WaterWolf
* JHexPane by Sam Koivu
* RSynaxPane by Robert Futrell
* Commons IO by Apache
* ASM by OW2
* FernFlower by Stiver
* Procyon by Mstrobel
* CFR by Lee Benfield
* CFIDE by Bibl
* Smali by JesusFreke
* Dex2Jar by pxb1..?
* Krakatau by Storyyeller
* JD GUI/JD Core by The Java-Decompiler Team
* Enjarify by Storyyeller

Contributors:
* Konloch
* Bibl
* Fluke
* Righteous
* sahitya-pavurala
* priav03
* Afffsdd
* Szperak
* Zooty
* samczsun
* ItzSomebody
* If I missed you, please feel free to contact me @Konloch or konloch@gmail.com

Website: https://bytecodeviewer.com

Source Code: https://github.com/konloch/bytecode-viewer

Bin/Archive: https://github.com/konloch/bytecode-viewer/releases

Java Docs: https://the.bytecode.club/docs/bytecode-viewer/

License (Copyleft): https://raw.githubusercontent.com/Konloch/bytecode-viewer/master/LICENSE

Report Bugs (or below): https://github.com/Konloch/bytecode-viewer/issues

Discussion Forum: https://the.bytecode.club/forumdisplay.php?fid=69

Key Features:
* Krakatau Integration for Bytecode assembly/disassembly.
* Smali/BakSmali Integration - You can now edit class files/dex files via smali!
* APK/DEX Support - Using Dex2Jar and Jar2Dex it's able to load and save APKs with ease!
* Java Decompiler - It utilizes FernFlower, Procyon and CFR for decompilation.
* Bytecode Decompiler - A modified version of CFIDE's.
* Hex Viewer - Powered by JHexPane.
* Each Decompiler/Editor/Viewer is toggleable, you can also select what will display on each pane.
* Fully Featured Search System - Search through strings, functions, variables and more!
* A Plugin System With Built In Plugins - (Show All Strings, Malicious Code Scanner, String Decrypters, etc)
* Fully Featured Scripting System That Supports Groovy.
* EZ-Inject - Graphically insert hooks and debugging code, invoke main and start the program.
* Recent Files & Recent Plugins.
* And more! Give it a try for yourself!

Command Line Input:
```
	-help                         Displays the help menu
	-list                         Displays the available decompilers
	-decompiler <decompiler>      Selects the decompiler, procyon by default
	-i <input file>               Selects the input file (Jar, Class, APK, ZIP, DEX all work automatically)
	-o <output file>              Selects the output file (Java or Java-Bytecode)
	-t <target classname>         Must either be the fully qualified classname or "all" to decompile all as zip
	-nowait                       Doesn't wait for the user to read the CLI messages
```

Are you a Java Reverse Engineer? Do you want to learn?

Join The Bytecode Club Today!

https://the.bytecode.club