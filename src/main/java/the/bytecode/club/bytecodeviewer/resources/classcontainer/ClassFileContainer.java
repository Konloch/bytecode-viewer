package the.bytecode.club.bytecodeviewer.resources.classcontainer;

import com.github.javaparser.*;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import the.bytecode.club.bytecodeviewer.decompilers.Decompiler;
import the.bytecode.club.bytecodeviewer.resources.ResourceContainer;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.locations.*;
import the.bytecode.club.bytecodeviewer.resources.classcontainer.parser.visitors.MyVoidVisitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This is a container for a specific class. The container name is based on the actual class name and the decompiler used.
 * <p>
 * Created by Bl3nd.
 * Date: 8/26/2024
 */
public class ClassFileContainer
{
    public transient NavigableMap<String, ArrayList<ClassFieldLocation>> fieldMembers = new TreeMap<>();
    public transient NavigableMap<String, ArrayList<ClassParameterLocation>> methodParameterMembers = new TreeMap<>();
    public transient NavigableMap<String, ArrayList<ClassLocalVariableLocation>> methodLocalMembers = new TreeMap<>();
    public transient NavigableMap<String, ArrayList<ClassMethodLocation>> methodMembers = new TreeMap<>();
    public transient NavigableMap<String, ArrayList<ClassReferenceLocation>> classReferences = new TreeMap<>();

    public boolean hasBeenParsed = false;
    public final String className;
    private final String content;
    private final String parentContainer;
    private final String path;

    public ClassFileContainer(String className, String content, ResourceContainer resourceContainer)
    {
        this.className = className;
        this.content = content;
        this.parentContainer = resourceContainer.name;
        this.path = resourceContainer.file.getAbsolutePath();
    }

    /**
     * Parse the class content with JavaParser.
     */
    public boolean parse()
    {
        try
        {
            if (shouldParse())
            {
                TypeSolver typeSolver = new CombinedTypeSolver(new ReflectionTypeSolver(true), new JarTypeSolver(path));
                JavaParser parser = new JavaParser();
                parser.getParserConfiguration().setSymbolResolver(new JavaSymbolSolver(typeSolver));
                ParseResult<CompilationUnit> parse = parser.parse(this.content);
                if (!parse.isSuccessful())
                {
                    System.err.println("Failed to parse: " + this.getName());
                    parse.getProblems().forEach(System.out::println);
                    return false;
                }

                CompilationUnit compilationUnit = parse.getResult().orElse(null);
                if (compilationUnit == null)
                    return false;

                compilationUnit.accept(new MyVoidVisitor(this, compilationUnit), null);
                return true;
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        return false;
    }

    public boolean shouldParse()
    {
        return !getDecompiler().equals(Decompiler.BYTECODE_DISASSEMBLER.getDecompilerName())
            && !getDecompiler().equals(Decompiler.KRAKATAU_DISASSEMBLER.getDecompilerName())
            && !getDecompiler().equals(Decompiler.JAVAP_DISASSEMBLER.getDecompilerName())
            && !getDecompiler().equals(Decompiler.SMALI_DISASSEMBLER.getDecompilerName())
            && !getDecompiler().equals(Decompiler.ASM_DISASSEMBLER.getDecompilerName())
            && !getDecompiler().equals(Decompiler.ASMIFIER_CODE_GEN.getDecompilerName());
    }

    public String getName()
    {
        if (this.className.contains("/"))
            return this.className.substring(this.className.lastIndexOf('/') + 1, this.className.lastIndexOf('.'));
        else
            return this.className.substring(0, this.className.lastIndexOf('.'));
    }

    public String getDecompiler()
    {
        return this.className.substring(this.className.lastIndexOf('-') + 1);
    }

    public String getParentContainer()
    {
        return this.parentContainer;
    }

    public void putField(String key, ClassFieldLocation value)
    {
        this.fieldMembers.computeIfAbsent(key, v -> new ArrayList<>()).add(value);
    }

    public List<ClassFieldLocation> getFieldLocationsFor(String fieldName)
    {
        return fieldMembers.getOrDefault(fieldName, new ArrayList<>());
    }

    public void putParameter(String key, ClassParameterLocation value)
    {
        this.methodParameterMembers.computeIfAbsent(key, v -> new ArrayList<>()).add(value);
    }

    public List<ClassParameterLocation> getParameterLocationsFor(String key)
    {
        return methodParameterMembers.getOrDefault(key, new ArrayList<>());
    }

    public void putLocalVariable(String key, ClassLocalVariableLocation value)
    {
        this.methodLocalMembers.computeIfAbsent(key, v -> new ArrayList<>()).add(value);
    }

    public List<ClassLocalVariableLocation> getLocalLocationsFor(String key)
    {
        return methodLocalMembers.getOrDefault(key, new ArrayList<>());
    }

    public void putMethod(String key, ClassMethodLocation value)
    {
        this.methodMembers.computeIfAbsent(key, v -> new ArrayList<>()).add(value);
    }

    public List<ClassMethodLocation> getMethodLocationsFor(String key)
    {
        return methodMembers.getOrDefault(key, new ArrayList<>());
    }

    public void putClassReference(String key, ClassReferenceLocation value)
    {
        this.classReferences.computeIfAbsent(key, v -> new ArrayList<>()).add(value);
    }

    public List<ClassReferenceLocation> getClassReferenceLocationsFor(String key)
    {
        return classReferences.getOrDefault(key, null);
    }

    public String getClassForField(String fieldName)
    {
        AtomicReference<String> className = new AtomicReference<>("");
        this.classReferences.forEach((s, v) ->
        {
            v.forEach(classReferenceLocation ->
            {
                if (classReferenceLocation.fieldName.equals(fieldName))
                {
                    className.set(classReferenceLocation.packagePath + "/" + s);
                }
            });
        });

        return className.get();
    }
}
