Bytecode Viewer is a Java Bytecode Viewer, GUI Procyon Java Decompiler, GUI CFR Java Decompiler, GUI FernFlower Java Decompiler, GUI Jar-Jar, Hex Viewer, Code Searcher, Debugger and more.
It's written completely in Java, and it's open sourced. It's currently being maintained and developed by Konloch.

There is also a plugin system that will allow you to interact with the loaded classfiles, for example you can write a String deobfuscator, a malicious code searcher, or something else you can think of.
You can either use one of the pre-written plugins, or write your own. It supports groovy, python and ruby scripting. Once a plugin is activated, it will send a ClassNode ArrayList of every single class loaded in the file system to the execute function, this allows the user to handle it completely using ASM.

Code from various projects has been used, including but not limited to:
J-RET by WaterWolf
JHexPane by Sam Koivu
JSynaxPane by Ayman Al
Commons IO by Apache
ASM by OW2
FernFlower by Stiver
Procyon by Mstrobel
CFR by Lee Benfield

Video of Beta 1.0 (Outdated): https://mediacru.sh/RJUXfW9wd2Tu/direct

Features:
    Java Decompiler - It uses a modified version of FernFlower, Procyon and CFR.
    Bytecode Decompiler - A modified version of J-RET's.
    Hex Viewer - Powered by JHexPane.
    Each Decompiler/Viewer is toggleable.
    Fully Featured Search System.
    A Plugin System With Built In Plugins. (Show All Strings, Malicious Code Scanner, String Decrypters, etc)
    Fully Featured Scripting System That Supports Groovy, Python And Ruby.
    Recent Files & Recent Plugins.
    And more! Give it a try for yourself!

Are you a Java Reverse Engineer? Do you want to learn?
Join The Bytecode Club Today!
http://the.bytecode.club

Changelog:
--- Beta 1.0 ---:
10/4/2014 - Designed a POC GUI, still needs a lot of work.
10/4/2014 - Started importing J-RET's backend.
10/5/2014 - Finished importing J-RET's backend.
10/6/2014 - Started modifying J-RET's UI.
10/6/2014 - Added several FernFlower options.
10/6/2014 - Fixed the class search function so it doesn't require exact class names.
10/6/2014 - Added save as, it'll save all of the loaded classes into one jar file (GUI Jar-Jar now).
10/6/2014 - Centered the select jar text inside of the file navigator.
10/6/2014 - Properly threaded the open jar function, now fernflower/bytecode decompiler runs in the background.
10/6/2014 - Added a hex viewer (Instead of using Re-Java's, I've decided to use a modified version of JHexEditor).
10/6/2014 - Made all of the viewer (Sourcecode, Bytecode & Hexcode toggleable).
10/7/2014 - Fixed the search function.
10/7/2014 - You can now add new files without it creating a new workspace.
10/7/2014 - Added new workspace button underneath File, this will reset the workspace.
10/7/2014 - Renamed File>Open.. to File>Add..
10/7/2014 - Added recent files.
10/7/2014 - Did some bitch work, the project has no warnings now.
10/7/2014 - Added waiting cursors to anything that will require waiting or loading.
10/8/2014 - Searching now runs in a background thread.
10/8/2014 - Added File>About.
10/8/2014 - The main GUI now starts in the middle of your screen, same with the about window.
10/8/2014 - Made the File Navigator Pane, Workspace Pane & Search Pane a little sexier.
10/9/2014 - Started on a Plugin system
10/9/2014 - Added a malicious code scanner plugin, based off of the one from J-RET, this searches for a multitude of classes/packages that can be used for malicious purposes.
10/9/2014 - Added a show all strings plugin, this grabs all the declared strings and displays them in a nice little window.
10/9/2014 - Fixed a bug with Bytecode Decompiler, where it would it display \r and \n as return carriages.
10/9/2014 - Fixed the Bytecode Decompiler>Debug Instructions option.
10/9/2014 - Save Class Files As is now renamed to Save Files As.
10/9/2014 - Save Files As now saves jar resources, not just classfiles.
10/9/2014 - Added an 'Are you sure' pane when you click on File>New Workspace.
10/9/2014 - Save Files As is no longer dependent on the File System, now if you're on windows and you have a file called AA, and one called Aa, you're fine.
10/11/2014 - Modified the FernFlower library, it no longer spits out System.out.println's while processing a method, this has sped it up quite a lot.
10/12/2014 - Fix an issue when resizing.
10/12/2014 - Modified the core slighty to no longer have a modularized decompiling system (since there are only 2 decompilers anyways).
10/12/2014 - Fixed an issue with decompiling multiple files at once.
10/12/2014 - The Plugin Console now shows the plugin's name on the title.
10/12/2014 - Debug Helpers will now debug all jump instructions by showing what instruction is on the line it's suppose to goto, example: 90. goto 120 // line 120 is PUTFIELD Animable_Sub4.anInt1593 : I
10/12/2014 - Now when you select an already opened file, it will automatically go to that opened pane.
10/14/2014 - Added the option 'exact' to the class finder.
10/14/2014 - Added the option 'exact' to the searcher, now it'll search for .contains when unselected.
10/14/2014 - Stopped the use of StringBuffer, replaced all instances with StringBuilder.
10/14/2014 - Added Labels and Try-Catch blocks to the Bytecode Decompiler.
10/14/2014 - For panes that are not selected, the corresponding decompiler will not execute.
10/14/2014 - Added plugin Show Main Methods, this will show every single public static void main(String[]).
10/14/2014 - Plugins can no longer be ran when there is no loaded classes.
10/14/2014 - The Malicious Code Scanner now has gui option pane before you run it.
10/14/2014 - Added a java/io option to the Malicious Code Scanner.
10/14/2014 - Added save Java files as.
10/15/2014 - Added save as Jar file. (Export as Jar)
10/15/2014 - Added the option to ASCII only strings in the Bytecode Decompiler.
10/15/2014 - External plugins are now fully functional, same with recent plugins.
10/16/2014 - Removed all refences of 'ClassContainer'.
10/16/2014 - Rewrote the tempfile system.
10/16/2014 - Moved the file import to BytecodeViewer.class.
10/16/2014 - Fixed a jTree updating issue.
10/16/2014 - Now if you try search with an empty string, it won't search.
10/16/2014 - Added Replace Strings plugin.
10/16/2014 - Added a loading icon that displays whenever a background task is being executed.
--- Beta 1.1 ---:
10/19/2014 - Fixed harcoded \\.
--- Beta 1.2 ---:
10/19/2014 - Started importing Procyon and CFR decompilers.
10/19/2014 - Partially finished importing Procyon and CFR, just need to finish export java files as zip.
--- Beta 1.3 ---:
10/22/2014 - Imported Bibl's Bytecode Decompiler from CFIDE.
10/22/2014 - Did some changes to the Bytecode Decompiler.
10/23/2014 - Added CFR settings.
10/23/2014 - Updated FernFlower to Intellij's Open Sourced version of FernFlower.
10/24/2014 - Fixed FernFlower save Java files as zip.
10/29/2014 - Added version checker.
10/29/2014 - Added Procyon settings.
10/29/2014 - When saving as jars or zips, it'll automatically append the file extension if it's not added.
10/29/2014 - All the built in plugins no longer set the cursor to busy.
10/29/2014 - Tried to fix the issue with JSyntaxPane by making it create the object in a background thread, it still freezes the UI. Changes kept for later implementation of another syntax highlighter.
10/29/2014 - Sped up start up time