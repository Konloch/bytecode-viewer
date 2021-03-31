Krakatau Bytecode Tools
Copyright (C) 2012-18 Robert Grosse

=== Introduction ===

Krakatau currently contains three tools - a decompiler and disassembler for
Java classfiles and an assembler to create classfiles.

=== Requirements ===

The Krakatau decompiler requires Python 2.7 while the assembler and disassembler
support both Python 2.7 and Python 3. Note that if you want to do decompilation,
you'll probably want an installation of the JDK as well. For assembly and
disassembly, a Java installation is not strictly necessary, but it is still
useful for testing the resulting classes.

=== Decompilation ===

Usage:
python Krakatau\decompile.py [-nauto] [-path PATH] [-out OUT] [-r] [-skip]
    target

PATH : An optional list of directories, jars, or zipfiles to search for
    classes in. Krakatau will attempt to automatically detect and add the
    jar containing core language classes, but you can disable this with
    the -nauto option. For multiple jars, you can either pass a semicolon
    seperated list of jars or pass the -path option multiple times.

OUT : Directory name where source files are to be written. Defaults to the
    current directory. If the name ends in .zip or .jar, the output will be a
    zipfile instead.

-r : Decompiles all .class files found in the directory target (recursively)

-skip : Continue upon errors. If an error occurs while decompiling a specific
    method, the traceback will be printed as comments in the source file. If the
    error occurs while decompiling at the class level, no source file will be
    emitted and an error message will be printed to the console.

target : Class name or jar name to decompile. If a jar is specified, all
    classes in the jar will be decompiled. If -r is specified, this should
    be a directory.

The Krakatau decompiler takes a different approach to most Java decompilers.
It can be thought of more as a compiler whose input language is Java bytecode
and whose target language happens to be Java source code. Krakatau takes in
arbitrary bytecode, and attempts to transform it to equivalent Java code. This
makes it robust to minor obfuscation, though it has the drawback of not
reconstructing the "original" source, leading to less readable output than a
pattern matching decompiler would produce for unobfuscated Java classes.

However, it will not always produce valid Java since there are some things
that are difficult or impossible to decompile at all thanks to the limitations
of the Java language. In most cases, Krakatau will try to at least produce
readable pseudocode, but sometimes it may just throw an exception.

Warning: Output on Windows uses UNC-style paths, which means that depending on
    the input class name, it may create files which are difficult or impossible
    to access through Windows Explorer or other non-UNC aware tools.

Note that the decompiler does not currently support Java 8 or invokedynamic,
although the assembler and disassembler do.

=== Assembly ===

Usage:
python Krakatau\assemble.py [-out OUT] [-r] [-q] target

OUT : Directory name where class files are to be written. Defaults to the
    current directory. If the name ends in .zip or .jar, the output will be a
    zipfile instead.

-r : Assembles all .j files found in the directory target (recursively)

-q : Quiet mode (only display warnings and errors)

target : Name of file to assemble. If -r is specified, this should be a
    directory.

The Krakatau assembler uses a syntax similar to Jasmin, but with many new
features, most importantly the ability to directly specify constant pool
references. For more information about the syntax look in the Documentation
folder.

=== Disassembly ===

Usage:
python Krakatau\disassemble.py [-out OUT] [-r] [-roundtrip] target

OUT : File or directory name where source files are to be written. Defaults
    to the current directory. If the name ends in .zip or .jar, the output will
    be a zipfile instead.

-r : Disassembles all .class files found in the directory target (recursively)

-roundtrip : Creates an assembly file that will assemble back to the exact
    binary file that was disassembled.

    Without -roundtrip, it will assemble to a classfile which is equivalent to
    the original and has the same behavior, but may differ in binary encoding
    details such as the precise ordering of constant pool entries. Reproducing
    the exact original classfile requires outputting such low level information
    in the assembly file, which makes the assembly harder for humans to read and
    edit. Therefore this option is disabled by default.

target : Filename or jar name to disassemble. If a jar is specified, all
    classes in the jar will be disassembled. If -r is specified, this should
    be a directory.

This takes a classfile and converts it into a human readable assembly format.
Unlike Javap, this can handle even pathological classes, and the output can
be reassembled. Together with the Krakatau assembler, this tool can roundtrip
any class through assembly and back into an equivalent class. If the -roundtrip
option is passed, then it can roundtrip any valid class through assembly and
exactly reproduce the original binary classfile.

Note: If you find a valid class which Krakatau cannot disassemble, please file
an issue at https://github.com/Storyyeller/Krakatau/issues.

=== Java 10 ===

The assembler and disassembler fully support Java 10, while the decompiler only
supports Java 7. In particular, decompilation of lambdas is not supported.

=== Performance ===

You can disable the internal debugging checks by passing -O to Python. This
will make Krakatau slightly faster, so it is recommended for normal usage.

=== Pypy ===

If you want to use Pypy, you'll need a build from 15 Feb 2016 or later.
Previous versions of Pypy have a bug that causes a segfault when running
Krakatau.
