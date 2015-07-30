Bytecode Viewer is an Advanced Lightweight Java Bytecode Viewer, GUI Java Decompiler, GUI Bytecode Editor, GUI Smali, GUI Baksmali, GUI APK Editor, GUI Dex Editor, GUI APK Decompiler, GUI DEX Decompiler, GUI Procyon Java Decompiler, GUI Krakatau, GUI CFR Java Decompiler, GUI FernFlower Java Decompiler, GUI DEX2Jar, GUI Jar2DEX, GUI Jar-Jar, Hex Viewer, Code Searcher, Debugger and more.
It's written completely in Java, and it's open sourced. It's currently being maintained and developed by Konloch.

There is also a plugin system that will allow you to interact with the loaded classfiles, for example you can write a String deobfuscator, a malicious code searcher, or something else you can think of.
You can either use one of the pre-written plugins, or write your own. It supports groovy scripting. Once a plugin is activated, it will execute the plugin with a ClassNode ArrayList of every single class loaded in BCV, this allows the user to handle it completely using ASM.

Code from various projects has been used, including but not limited to:
    J-RET by WaterWolf
    JHexPane by Sam Koivu
    RSynaxPane by Robert Futrell
    Commons IO by Apache
    ASM by OW2
    FernFlower by Stiver
    Procyon by Mstrobel
    CFR by Lee Benfield
    CFIDE by Bibl
    Smali by JesusFreke
    Dex2Jar by pxb1..?
    Krakatau by Storyyeller
    JD GUI/JD Core by The Java-Decompiler Team
    Enjarify by Storyyeller

Contributors:
    Konloch
    Bibl
    Fluke
    Righteous
    sahitya-pavurala
    priav03
    Afffsdd
    If I missed you, please feel free to contact me @Konloch or konloch@gmail.com

Contribution Guide Lines/Coding Conventions:
    Packages must start with the.bytecode.club.bytecodeviewer
    If code you write can throw an exception, handle it using new the.bytecode.club.bytecodeviewer.ExceptionUI(exception, "authors@email.com")
	All variables must be at the start of each class.
	Brackets are meant to be on the same line, I.E. public void main(String[] args) { not (String[] args) <NEWLINE_BREAK> {

Website: https://bytecodeviewer.com
Source Code: https://github.com/konloch/bytecode-viewer
Bin/Archive: https://github.com/konloch/bytecode-viewer/releases
Java Docs: https://the.bytecode.club/docs/bytecode-viewer/
License (Copyleft): https://raw.githubusercontent.com/Konloch/bytecode-viewer/master/LICENSE
Report Bugs (or below): https://github.com/Konloch/bytecode-viewer/issues
Discussion Forum: https://the.bytecode.club/forumdisplay.php?fid=69

Key Features:
    Krakatau Integration for Bytecode assembly/disassembly. 
    Smali/BakSmali Integration - You can now edit class files/dex files via smali!
    APK/DEX Support - Using Dex2Jar and Jar2Dex it's able to load and save APKs with ease!
    Java Decompiler - It utilizes FernFlower, Procyon and CFR for decompilation.
    Bytecode Decompiler - A modified version of CFIDE's.
    Hex Viewer - Powered by JHexPane.
    Each Decompiler/Editor/Viewer is toggleable, you can also select what will display on each pane.
    Fully Featured Search System - Search through strings, functions, variables and more!
    A Plugin System With Built In Plugins - (Show All Strings, Malicious Code Scanner, String Decrypters, etc)
    Fully Featured Scripting System That Supports Groovy.
    EZ-Inject - Graphically insert hooks and debugging code, invoke main and start the program.
    Recent Files & Recent Plugins.
    And more! Give it a try for yourself!

Command Line Input:
	-help                         Displays the help menu
	-list                         Displays the available decompilers
	-decompiler <decompiler>      Selects the decompiler, procyon by default
	-i <input file>               Selects the input file (Jar, Class, APK, ZIP, DEX all work automatically)
	-o <output file>              Selects the output file (Java or Java-Bytecode)
	-t <target classname>         Must either be the fully qualified classname or "all" to decompile all as zip
	-nowait                       Doesn't wait for the user to read the CLI messages

Are you a Java Reverse Engineer? Do you want to learn?
Join The Bytecode Club Today!
https://the.bytecode.club

Changelog:
--- Beta 1.0.0 ---:
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
--- Beta 1.1.0 ---:
10/19/2014 - Fixed harcoded \\.
--- Beta 1.2.0 ---:
10/19/2014 - Started importing Procyon and CFR decompilers.
10/19/2014 - Partially finished importing Procyon and CFR, just need to finish export java files as zip.
--- Beta 1.3.0 ---:
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
--- Beta 1.3.1 ---:
10/29/2014 - Replaced JSyntaxPane with RSyntaxArea, this sadly removes the search feature inside of source/bytecode files, I'll implement a search function soon. (This also fixes the JRE 1.8 issue)
10/29/2014 - Added a new decompiler option to append brackets to labels.
10/31/2014 - Fixed an issue with the decompiler still running when the source code pane isn't toggled.
--- Beta 1.4.0 ---:
11/1/2014 - Fixed FernFlower save Java files on Unix.
11/1/2014 - FernFlower now uses the settings for save Java files.
11/1/2014 - Added Procyon save Java files (It uses the settings).
11/1/2014 - Updated CFR to cfr_0_89.
11/1/2014 - Added CFR save Java files (It uses the settings), however it relies on the file system, because of this if there is heavy name obfuscation, it could mess up for windows.
--- Beta 1.5.0 ---:
11/1/2014 - Updated and improved the search function, it now prints out more useful information.
11/1/2014 - Fixed a UI issue with the Replace All Strings plugin.
11/2/2014 - Added search function to the Class Viewer.
11/2/2014 - Updated Procyon to procyon-decompiler-0.5.27.
--- Beta 1.5.1 ---:
11/2/2014 - Fixed a CFR issue with packages.
--- Beta 1.5.2 ---:
11/3/2014 - Fixed Refresh Class.
--- Beta 1.5.3 ---:
11/3/2014 - Settings/Temp file are now in a global directory.
11/3/2014 - The GUI setttings now save.
11/3/2014 - Removed the option to disable syntax highlighting (since it's lightweight now).
11/3/2014 - About window now contains the version number and the BCV directory.
11/3/2014 - Added an option to toggle to outdated status.
--- 2.0.0 ---: //Out of beta, WOO
11/4/2014 - Officially been 1 month of development.
11/4/2014 - Replaced ""+ with String.valueOf (cheers bibl).
11/4/2014 - Changed how the temp directory was created.
11/4/2014 - Put a file.seperator  to the end of tempDirectory.
11/4/2014 - Made the exit button work.
11/4/2014 - Added a GUI for all Exception Stack Trace's.
11/4/2014 - The plugin system now shows a message instead of just printing to the console when it's not going to run a plugin.
11/4/2014 - Updated the search function, it's now perfect.
11/5/2014 - Made the Show All Strings plugin instant.
11/5/2014 - Kinda added middle mouse button closes tab (only if you click the exit button).
11/5/2014 - Improved the Malicious Code Scanner, also made it instant.
11/5/2014 - Added icons to the program (cheers Fluke).
--- 2.0.1 ---:
11/7/2014 - Fixed the search function.
11/7/2014 - Removed an unused package containing some unused classes.
--- 2.1.0 ---:
11/5/2014 - Started working on the EZ-Inject plugin.
11/6/2014 - Fixed the ClassNodeDecompiler creating unnessessary objects. (thanks bibl).
11/6/2014 - Finished an alpha version of EZ-Inject.
11/6/2014 - Started working on a basic obfuscator.
11/6/2014 - The Obfuscator now sucessfully renames all field names.
11/6/2014 - Updated CFR to cfr_0_90.
11/8/2014 - Started working on the API for BCV.
11/9/2014 - Decided to make a graphical reflection kit.
11/10/2014 - Made some progress with the obfuscator, almost finished EZ-Injection.
11/14/2014 - Been doing various updates to EZ-Injection, Obfucsation, Reflection Kit and the BCV API.
11/16/2014 - Added the option to launch BCV command line as java -jar bcv.jar C:/test.jar C:/example/whatever.jar
11/17/2014 - Fixed an issue with the out of date checking UI still activating when not selected.
11/19/2014 - Added annotatitons/local variables to the methodnode decompiler (Thanks Bibl).
11/21/2014 - Decided to release it with the obfuscator/reflection kit unfinished, they're currently disabled for future use.
--- 2.1.1 ---:
12/09/2014 - Upated CFR to cfr_0_91.
--- 2.2.0 ---:
12/09/2014 - Added a text search function to the plugin console.
12/09/2014 - When you press enter in the text search bar, it will now search.
12/13/2014 - The Bytecode Decompiler now shows the method's description in a comment.
12/13/2014 - Fixed an issue with the text search function.
12/13/2014 - Search results are now clickable.
--- 2.2.1 ---:
12/13/2014 - Fixed an issue with the Bytecode Decompiler. - Thanks bibl
--- 2.3.0 ---:
12/16/2014 - Started updating the class viewer.
12/18/2014 - Finished a basic concept of the new class viewer.
12/18/2014 - Fixed an error with importing some jars.
12/18/2014 - Fixed the about window.
12/18/2014 - Finished the final concept for the new class viewer.
12/18/2014 - Threaded save Java files as zip, it now runs in a background thread.
12/18/2014 - Save Java files as zip now prompts you to select a decompiler.
12/18/2014 - Removed the cursor waiting for save Java files as zip.
12/18/2014 - Wrapped the save Java files as zip around an exception handler, it will now safely show the exception if any is thrown.
12/18/2014 - Fixed not escaping the Java strings by default for the Bytecode decompiler. - http://i.imgur.com/YrRnZA7.png
12/18/2014 - Used Eclipse's code formatting tool and formatted the code
12/19/2014 - Priav03 fixed the quick class searcher.
--- 2.4.0 ---:
12/19/2014 - Afffsdd made the Bytecode Viewer directory hidden.
12/19/2014 - Added save Java file as, for singular class file decompilation (this is threaded).
12/19/2014 - Removed unused Bytecode Decompiler debug code.
12/20/2014 - Made a new outdated pane - http://i.imgur.com/xMxkwJ9.png
12/20/2014 - Added an expand/collapse the packages in the file navigator.
12/20/2014 - Moved all of the settings to the.bytecode.club.bytecodeviewer.Settings
12/20/2014 - If the class file does not start with CAFEBABE it won't be processed.
12/20/2014 - Properly handled file not found error.
12/21/2014 - Fixed the Refresh Class causing a dupe.
--- 2.5.0 ---:
12/28/2014 - Improved the outdated version pane by including an automatic downloader - http://i.imgur.com/4MXeBGb.png - http://i.imgur.com/v50Pghe.png - http://i.imgur.com/bVZqxZ2.png - http://i.imgur.com/l8nIMzD.png
12/28/2014 - Updated CFR to cfr_0.92.jar
12/31/2014 - Adrianherrera updated the Malicious Code Scanner to detect the security manager being set to null.
 **HAPPY NEW YEAR**
01/01/2015 - Added refresh class on decompiler/pane view change
01/01/2015 - Moved all of the settings into a settings pane
01/01/2015 - Added some debug code when you first start it up, it also includes how long it took to fully load up.
01/02/2015 - Cached the busy icon.
01/02/2015 - >> ADDED APK SUPPORT <<, had to downgrade to ASM 3.3, which means losing some annotation debugging for the Bytecode Decompiler.
01/03/2015 - Wrapped the search pane in a JScrollPane.
01/06/2015 - Added save as DEX and import .dex files.
--- 2.5.1 ---:
01/06/2015 - Silenced the error connecting to update server for offline mode.
01/06/2015 - Fixed a search function with Android APKs.
--- 2.5.2 ---:
01/06/2015 - Completely fixed the search function with Android APKs.
--- 2.6.0 ---:
01/06/2015 - Now saves if maximized or not.
01/07/2015 - For all save as functions, it will now append the correct extension if not added by the user.
01/07/2015 - You can no longer use use the save functions if no classes are loaded (fixes a crash issue).
01/07/2015 - Moved the Update Check to the Settings menu.
01/08/2015 - Added an extremely basic code sqeuence diagram plugin.
01/08/2015 - Updated CFR to CFR_0.93.jar
01/08/2015 - Threaded the Add files function.
01/08/2015 - Finally implemented Kontainer's HTTPRequest wrapper now that I've open sourced it.
01/08/2015 - Set the panes to be non-editable.
01/08/2015 - Sexified the view pane selection.
01/08/2015 - Started working on Smali Editing support, finished decompiler so far.
01/09/2015 - Fixed a bug with saving.
01/09/2015 - Added add entire directory.
01/09/2015 - Fixed import .DEX files.
01/10/2015 - Finished Smali Editing.
01/10/2015 - Fixed a class opening issue with synchronization.
01/11/2015 - Threaded all of the save functions.
01/11/2015 - Removed all instances of the setCursor to busy.
01/11/2015 - Added are you sure you wish to overwrite this existing file to all the other save functions.
01/11/2015 - All of the decompiling names are now randomly generated instead of a counting number.
01/11/2015 - Updated CFR to CFR_0.94.jar
01/11/2015 - Updated to the latest version of FernFlower.
01/11/2015 - Fixed an extension appending issue with save Java file.
--- 2.7.0 ---:
01/11/2015 - Improved the Refresh Class function to be used as the default compile function.
01/11/2015 - Implemented better error handling for decompiling class files.
01/15/2015 - CTRL + O will open the add file interface.
01/15/2015 - CTRL + N will open the net workspace interface.
01/15/2015 - It will now save the last directory you opened.
01/15/2015 - Some how the URL for the auto updater change log got changed, this has been fixed.
01/15/2015 - Slightly updated the change log display, it'll now show all the changes since your version.
01/16/2015 - Made EZ-Injection UI look a bit nicer.
01/27/2015 - Decided to scrap the  JVM Sandbox POC and use the Security Manager.
01/27/2015 - BCV now blocks exec and won't allow any ports to be bound.
--- 2.7.1 ---:
01/27/2015 - Fixed hide file.
--- 2.8.0 ---:
02/01/2015 - Updated CFR and Proycon to latest versions.
02/01/2015 - Started working on implementing Krakatau.
02/01/2015 - Sexifixed the security manager a little bit.
02/03/2015 - Fully added Krakatau Java decompiler, just disassembly/assembly left.
02/03/2015 - Updated the about window.
02/03/2015 - Dropped JRuby and Jython support (BCV is now roughly 16mb, was 45mb).
02/04/2015 - Added Krakatau Disassembly.
02/04/2015 - Added Krakatau Assembly.
--- 2.8.1 ---:
02/04/2015 - Fixed UI bug with Krakatau/Krakatau Editable view panes.
02/05/2015 - Added CTRL + F.
--- 2.9.0 ---:
02/11/2015 - Added ZStringArray String Decrypter. (Thanks Righteous)
02/20/2015 - Moved the decompilers/disassemblers around.
02/20/2015 - Fixed a resource leak with Krakatau Decompiler/Disassembler/Assembler.
02/21/2015 - Fixed regex searching if your regex search contained a syntax error.
02/21/2015 - Added the compiler/decompiler instances to the BytecodeViewer API class.
02/21/2015 - Sped up the decompilers, each view pane runs its own decompiler thread.
02/21/2015 - Added Janino compiler, you can now compile the decompiled source code inside of BCV.
02/21/2015 - Added the editable option for almost all of the decompilers/disassemblers.
02/21/2015 - Cached the next/previous icons and added a resources class for all resources.
01/21/2015 - Renamed EZ-Injection as File-Run, however kept the plugin named EZ-Injection.
02/21/2015 - Dropped Groovy support, added .Java plugin compilation instead (now only 10mb).
02/21/2015 - Added support for reading resources, including displaying images, detecting pure ascii files and more.
02/21/2015 - Fixed an issue with loading an already selected node in the file navigation pane.
02/22/2015 - Added an error console to the Java compiler
02/22/2015 - Ensured the spawned Python/Krakatau processes are killed when closing BCV.
02/22/2015 - Made it more beginner friendly.
02/22/2015 - Fixed? The file navigation search.
02/22/2015 - Added a shit ton more comments to non-api related classes.
02/23/2015 - Added APK resources.
02/23/2015 - MORE ANDROID LOVE! Added APKTool.jar's decode. (Takes a while so it's a setting, also pumped the jar back to 16MB)
02/23/2015 - Added close all but this tab menu.
02/23/2015 - Not really code related, but added _install.bat and _uninstall.bat for the exe version of BCV.
02/23/2015 - Back to ASM5, packed dex2jar in its own obfuscated jar.
02/23/2015 - Added the annotations back to the Bytecode Decompiler. (Once again, thanks Bibl)
02/23/2015 - It once again works with Java 8 Jars.
--- 2.9.1 ---:
02/24/2015 - Fixed the third pane window not showing the search buttons.
02/24/2015 - Fixed some issues with the compiler functionality.
--- 2.9.2 ---:
02/24/2015 - Actually fixed the compiler, LOL.
--- 2.9.3 ---:
02/28/2015 - Added drag and drop for any file.
02/28/2015 - Added ctrl + w to close the current opened tab.
02/28/2015 - Updated to CFR 0_97.jar
02/28/2015 - Fixed a concurrency issue with the decompilers.
02/28/2015 - Added image resize via scroll on mouse.
02/28/2015 - Added resource refreshing.
02/28/2015 - Im Frizzy started working on Obfuscation.
03/20/2015 - Updated Dex2Jar to 2.0.
03/20/2015 - Updated CFR to 0_98.jar
--- 2.9.4 ---:
04/19/2015 - Added -O to be passed for Krakatau Decompiler/Disassembler/Assembler. (Thanks Storyyeller).
04/19/2015 - Added -skip to be passed for Krakatau Decompiler. (Thanks Storyyeller).
04/19/2015 - Changed the warning window for Python to recommend PyPy. (Thanks Storyyeller).
04/20/2015 - Happy 2015 4/20 (Shoutout to @announce420 for being 2 years old).
04/21/2015 - Started reworking the View Panes.
04/21/2015 - Finished reworking the View Panes - http://i.imgur.com/SqIw4Vj.png - Cheers to whoever's idea this was (I forget sorry <3).
04/21/2015 - Updated CFR to 0_100.jar
04/21/2015 - Added CTRL + R for run.
04/21/2015 - Added CTRL + S for save files as.
04/21/2015 - Added CTRL + T for compile.
04/21/2015 - Added Krakatau optional library.
04/21/2015 - The about pane now provides a lot more up to date information.
04/21/2015 - Changed 'View Panes' to simply 'View'.
--- 2.9.5 ---:
05/01/2015 - Added 'pingback' for statistics (to track how many people globally use BCV)
--- 2.9.6 ---:
05/05/2015 - Fixed a typo in the about window
05/28/2015 - Started importing JD-GUI Decompiler.
05/28/2015 - Compile on refresh and compile on save are now enabled by default.
05/28/2015 - Renamed the File>Save As options to be much more informative.
06/24/2015 - Fixed a logic error with the Field & Method searchers.
06/26/2015 - Updated Procyon & CFR to their latest versions.
07/02/2015 - Added JD-GUI Decompiler. - Huge thanks to the guys behind JD-GUI! <3 (FIVE DECOMPILERS NOW LOL)
--- 2.9.7 ---:
07/02/2015 - Added ajustable font size.
07/05/2015 - Started working on the new Boot Screen.
07/06/2015 - Moved the font size to be under the view menu.
07/06/2015 - Fixed a bug with plugins not being able to grab the currently viewed class.
07/07/2015 - Started adding enjarify as an optional APK converter instead of Dex2Jar.
07/07/2015 - Finished the new Boot Screen
07/09/2015 - Fixed a process leak with krakatau decompiler.
07/09/2015 - Finished adding enjarify.
07/09/2015 - Supressed syntax exceptions due to JD-GUI.
07/09/2015 - Fixed refresh on non-refreshable resources.
07/09/2015 - Fixed opening a class and the name is so big, you cannot close because the [X] does not appear.
07/09/2015 - Added support for smaller screens for the boot screen.
07/16/2015 - Removed the FileFilter classes.
07/16/2015 - Updated the decompiler class to make more sense.
07/16/2015 - Started working on BCV CLI.
07/16/2015 - Finished BCV CLI.
--- 2.9.8 ---:
07/19/2015 - Fixed enjarify.
07/20/2015 - Bibl sexified the boot loading time.
07/20/2015 - Decode APK Resources is selected by default.
07/20/2015 - Made the security manager slightly safer, it can still be targeted but not as obviously now.
07/20/2015 - Added CLI to the boot page.
07/21/2015 - Added support for offline mode in case you cannot connect to github for some reason. (kicks in after 7 seconds)
07/21/2015 - Added fatjar option back, in case anyone wants a 100% portable version.
07/21/2015 - Made it so it now shows the decompiler it's using - http://i.imgur.com/yMEzXwv.png.
07/21/2015 - Rewrote the file system, it now shows the path of the jar it's got loaded.
07/21/2015 - Now it shows if the decompiler is in editable mode or not.
07/21/2015 - Fixed Enjarify bug from new security manager.
07/22/2015 - Fixed a typo (Thanks affffsdsd)
07/22/2015 - Finally added icons to the File Navigator, credits to http://famfamfam.com/lab/icons/silk/ for the icons.
07/22/2015 - JD-GUI is now the default decompiler for GUI.
07/22/2015 - Added Set Python 3.X to the UI.
07/22/2015 - Fixed krakatau/export as jar bug introduced by file system update.
07/22/2015 - Sped up krakatau decompiler/disassembler on big files.
07/22/2015 - Made it so when you press enter on the file navigation pane it opens the class.
07/22/2015 - The Quick file search now opens the files again.
07/23/2015 - Fixed opening single files and file folders into BCV
07/24/2015 - Added File>Reload Resources.
07/26/2015 - Fixed the view pane refresh after toggling a viewer, it's now flawless.
07/26/2015 - Fixed Krakatau Disassembler.
07/26/2015 - Mibbzz is gay once again.
07/30/2015 - Removed Janino Compiler & moved to Javac, it can now compile decompiled classes again.
07/30/2015 - Affssdd fixed the File Navigator Pane's Quick Class Search.
07/30/2015 - Fixed a process leak in KrakatauDisassembler.
07/30/2015 - Started working on converting all the decompilers to launch in their own process in an effort to reduce BCV resources (only for non-fatjar version).