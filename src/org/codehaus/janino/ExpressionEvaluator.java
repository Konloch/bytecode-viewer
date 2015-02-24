
/*
 * Janino - An embedded Java[TM] compiler
 *
 * Copyright (c) 2001-2010, Arno Unkrig
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *       following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *       following disclaimer in the documentation and/or other materials provided with the distribution.
 *    3. The name of the author may not be used to endorse or promote products derived from this software without
 *       specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.codehaus.janino;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.commons.compiler.CompileException;
import org.codehaus.commons.compiler.CompilerFactoryFactory;
import org.codehaus.commons.compiler.Cookable;
import org.codehaus.commons.compiler.IClassBodyEvaluator;
import org.codehaus.commons.compiler.ICompilerFactory;
import org.codehaus.commons.compiler.ICookable;
import org.codehaus.commons.compiler.IExpressionEvaluator;
import org.codehaus.commons.compiler.IScriptEvaluator;
import org.codehaus.commons.compiler.ISimpleCompiler;
import org.codehaus.commons.compiler.PrimitiveWrapper;
import org.codehaus.janino.Java.AmbiguousName;
import org.codehaus.janino.Java.BlockStatement;
import org.codehaus.janino.Java.Rvalue;
import org.codehaus.janino.Visitor.RvalueVisitor;
import org.codehaus.janino.util.Traverser;

/**
 * This {@link IExpressionEvaluator} is implemented by creating and compiling a temporary
 * compilation unit defining one class with one static method with one RETURN statement.
 * <p>
 * A number of "convenience constructors" exist that execute the set-up steps described for {@link
 * IExpressionEvaluator} instantly.
 * <p>
 * If the parameter and return types of the expression are known at compile time, then a "fast"
 * expression evaluator can be instantiated through
 * {@link #createFastExpressionEvaluator(String, Class, String[], ClassLoader)}. Expression
 * evaluation is faster than through {@link #evaluate(Object[])}, because it is not done through
 * reflection but through direct method invocation.
 * <p>
 * Example:
 * <pre>
 * public interface Foo {
 *     int bar(int a, int b);
 * }
 * ...
 * Foo f = (Foo) ExpressionEvaluator.createFastExpressionEvaluator(
 *     "a + b",                    // expression to evaluate
 *     Foo.class,                  // interface that describes the expression's signature
 *     new String[] { "a", "b" },  // the parameters' names
 *     (ClassLoader) null          // Use current thread's context class loader
 * );
 * System.out.println("1 + 2 = " + f.bar(1, 2)); // Evaluate the expression
 * </pre>
 * Notice: The <code>interfaceToImplement</code> must either be declared <code>public</code>,
 * or with package scope in the root package (i.e. "no" package).
 * <p>
 * On my system (Intel P4, 2 GHz, MS Windows XP, JDK 1.4.1), expression "x + 1"
 * evaluates as follows:
 * <table>
 *   <tr><td></td><th>Server JVM</th><th>Client JVM</th></td></tr>
 *   <tr><td>Normal EE</td><td>23.7 ns</td><td>64.0 ns</td></tr>
 *   <tr><td>Fast EE</td><td>31.2 ns</td><td>42.2 ns</td></tr>
 * </table>
 * (How can it be that interface method invocation is slower than reflection for
 * the server JVM?)
 */
@SuppressWarnings({ "rawtypes", "unchecked" }) public
class ExpressionEvaluator extends ScriptEvaluator implements IExpressionEvaluator {

    private Class[] optionalExpressionTypes;

    /**
     * Equivalent to<pre>
     * ExpressionEvaluator ee = new ExpressionEvaluator();
     * ee.setExpressionType(expressionType);
     * ee.setParameters(parameterNames, parameterTypes);
     * ee.cook(expression);</pre>
     *
     * @see #ExpressionEvaluator()
     * @see ExpressionEvaluator#setExpressionType(Class)
     * @see ScriptEvaluator#setParameters(String[], Class[])
     * @see Cookable#cook(String)
     */
    public
    ExpressionEvaluator(
        String   expression,
        Class    expressionType,
        String[] parameterNames,
        Class[]  parameterTypes
    ) throws CompileException {
        this.setExpressionType(expressionType);
        this.setParameters(parameterNames, parameterTypes);
        this.cook(expression);
    }

    /**
     * Equivalent to<pre>
     * ExpressionEvaluator ee = new ExpressionEvaluator();
     * ee.setExpressionType(expressionType);
     * ee.setParameters(parameterNames, parameterTypes);
     * ee.setThrownExceptions(thrownExceptions);
     * ee.setParentClassLoader(optionalParentClassLoader);
     * ee.cook(expression);</pre>
     *
     * @see #ExpressionEvaluator()
     * @see ExpressionEvaluator#setExpressionType(Class)
     * @see ScriptEvaluator#setParameters(String[], Class[])
     * @see ScriptEvaluator#setThrownExceptions(Class[])
     * @see SimpleCompiler#setParentClassLoader(ClassLoader)
     * @see Cookable#cook(String)
     */
    public
    ExpressionEvaluator(
        String      expression,
        Class       expressionType,
        String[]    parameterNames,
        Class[]     parameterTypes,
        Class[]     thrownExceptions,
        ClassLoader optionalParentClassLoader
    ) throws CompileException {
        this.setExpressionType(expressionType);
        this.setParameters(parameterNames, parameterTypes);
        this.setThrownExceptions(thrownExceptions);
        this.setParentClassLoader(optionalParentClassLoader);
        this.cook(expression);
    }

    /**
     * Equivalent to<pre>
     * ExpressionEvaluator ee = new ExpressionEvaluator();
     * ee.setExpressionType(expressionType);
     * ee.setParameters(parameterNames, parameterTypes);
     * ee.setThrownExceptions(thrownExceptions);
     * ee.setExtendedType(optionalExtendedType);
     * ee.setImplementedTypes(implementedTypes);
     * ee.setParentClassLoader(optionalParentClassLoader);
     * ee.cook(expression);</pre>
     *
     * @see #ExpressionEvaluator()
     * @see ExpressionEvaluator#setExpressionType(Class)
     * @see ScriptEvaluator#setParameters(String[], Class[])
     * @see ScriptEvaluator#setThrownExceptions(Class[])
     * @see ClassBodyEvaluator#setExtendedClass(Class)
     * @see ClassBodyEvaluator#setImplementedInterfaces(Class[])
     * @see SimpleCompiler#setParentClassLoader(ClassLoader)
     * @see Cookable#cook(String)
     */
    public
    ExpressionEvaluator(
        String      expression,
        Class       expressionType,
        String[]    parameterNames,
        Class[]     parameterTypes,
        Class[]     thrownExceptions,
        Class       optionalExtendedType,
        Class[]     implementedTypes,
        ClassLoader optionalParentClassLoader
    ) throws CompileException {
        this.setExpressionType(expressionType);
        this.setParameters(parameterNames, parameterTypes);
        this.setThrownExceptions(thrownExceptions);
        this.setExtendedClass(optionalExtendedType);
        this.setImplementedInterfaces(implementedTypes);
        this.setParentClassLoader(optionalParentClassLoader);
        this.cook(expression);
    }

    /**
     * Equivalent to<pre>
     * ExpressionEvaluator ee = new ExpressionEvaluator();
     * ee.setClassName(className);
     * ee.setExtendedType(optionalExtendedType);
     * ee.setImplementedTypes(implementedTypes);
     * ee.setStaticMethod(staticMethod);
     * ee.setExpressionType(expressionType);
     * ee.setMethodName(methodName);
     * ee.setParameters(parameterNames, parameterTypes);
     * ee.setThrownExceptions(thrownExceptions);
     * ee.setParentClassLoader(optionalParentClassLoader);
     * ee.cook(scanner);
     *
     * @see IExpressionEvaluator
     * @see IClassBodyEvaluator#setClassName(String)
     * @see IClassBodyEvaluator#setExtendedClass(Class)
     * @see IClassBodyEvaluator#setImplementedInterfaces(Class[])
     * @see IScriptEvaluator#setStaticMethod(boolean)
     * @see IExpressionEvaluator#setExpressionType(Class)
     * @see IScriptEvaluator#setMethodName(String)
     * @see IScriptEvaluator#setParameters(String[], Class[])
     * @see IScriptEvaluator#setThrownExceptions(Class[])
     * @see ISimpleCompiler#setParentClassLoader(ClassLoader)
     * @see ICookable#cook(Reader)
     */
    public
    ExpressionEvaluator(
        Scanner     scanner,
        String      className,
        Class       optionalExtendedType,
        Class[]     implementedTypes,
        boolean     staticMethod,
        Class       expressionType,
        String      methodName,
        String[]    parameterNames,
        Class[]     parameterTypes,
        Class[]     thrownExceptions,
        ClassLoader optionalParentClassLoader
    ) throws CompileException, IOException {
        this.setClassName(className);
        this.setExtendedClass(optionalExtendedType);
        this.setImplementedInterfaces(implementedTypes);
        this.setStaticMethod(staticMethod);
        this.setExpressionType(expressionType);
        this.setMethodName(methodName);
        this.setParameters(parameterNames, parameterTypes);
        this.setThrownExceptions(thrownExceptions);
        this.setParentClassLoader(optionalParentClassLoader);
        this.cook(scanner);
    }

    public ExpressionEvaluator() {}

    @Override public void
    setExpressionType(Class expressionType) { this.setExpressionTypes(new Class[] { expressionType }); }

    @Override public void
    setExpressionTypes(Class[] expressionTypes) {
        this.assertNotCooked();
        this.optionalExpressionTypes = expressionTypes;

        Class[] returnTypes = new Class[expressionTypes.length];
        for (int i = 0; i < returnTypes.length; ++i) {
            Class et = expressionTypes[i];
            returnTypes[i] = et == IExpressionEvaluator.ANY_TYPE ? Object.class : et;
        }
        super.setReturnTypes(returnTypes);
    }

    /** @deprecated {@link #setExpressionType(Class)} should be called instead. */
    @Override @Deprecated public final void
    setReturnType(Class returnType) {
        throw new AssertionError("Must not be used on an ExpressionEvaluator; use 'setExpressionType()' instead");
    }

    /** @deprecated {@link #setExpressionTypes(Class[])} should be called instead. */
    @Override @Deprecated public final void
    setReturnTypes(Class[] returnTypes) {
        throw new AssertionError("Must not be used on an ExpressionEvaluator; use 'setExpressionTypes()' instead");
    }

    @Override protected Class
    getDefaultReturnType() { return Object.class; }

    @Override protected List<BlockStatement>
    makeStatements(int idx, Parser parser) throws CompileException, IOException {
        List<BlockStatement> statements = new ArrayList();

        // Parse the expression.
        Rvalue value = parser.parseExpression().toRvalueOrCompileException();

        Class et = (
            this.optionalExpressionTypes == null
            ? IExpressionEvaluator.ANY_TYPE
            : this.optionalExpressionTypes[idx]
        );
        if (et == void.class) {

            // ExpressionEvaluator with an expression type "void" is a simple expression statement.
            statements.add(new Java.ExpressionStatement(value));
        } else {

            // Special case: Expression type "ANY_TYPE" means return type "Object" and automatic
            // wrapping of primitive types.
            if (et == IExpressionEvaluator.ANY_TYPE) {
                value = new Java.MethodInvocation(
                    parser.location(),          // location
                    new Java.ReferenceType(     // optionalTarget
                        parser.location(), // location
                        new String[] {     // identifiers
                            "org", "codehaus", "commons", "compiler", "PrimitiveWrapper"
                        },
                        null               // optionalTypeArguments
                    ),
                    "wrap",                     // methodName
                    new Java.Rvalue[] { value } // arguments
                );

                // Make sure "PrimitiveWrapper" is compiled.
                PrimitiveWrapper.wrap(99);

                // Verify that "PrimitiveWrapper" is loadable.
                this.classToType(null, PrimitiveWrapper.class);
            }

            // Add a return statement.
            statements.add(new Java.ReturnStatement(parser.location(), value));
        }
        if (!parser.peekEof()) {
            throw new CompileException("Unexpected token \"" + parser.peek() + "\"", parser.location());
        }

        return statements;
    }

    /**
     * <pre>
     * {@link IExpressionEvaluator} ee = {@link CompilerFactoryFactory}.{@link
     * CompilerFactoryFactory#getDefaultCompilerFactory() getDefaultCompilerFactory}().{@link
     * ICompilerFactory#newExpressionEvaluator() newExpressionEvaluator}();
     * ee.setParentClassLoader(optionalParentClassLoader);
     * return ee.{@link #createFastEvaluator createFastEvaluator}(expression, interfaceToImplement, parameterNames);
     * </pre>
     *
     * @deprecated Use {@link #createFastEvaluator(String, Class, String[])} instead:
     */
    @Deprecated public static Object
    createFastExpressionEvaluator(
        String      expression,
        Class       interfaceToImplement,
        String[]    parameterNames,
        ClassLoader optionalParentClassLoader
    ) throws CompileException {
        IExpressionEvaluator ee = new ExpressionEvaluator();
        ee.setParentClassLoader(optionalParentClassLoader);
        return ee.createFastEvaluator(expression, interfaceToImplement, parameterNames);
    }

    /**
     * Notice: This method is not declared in {@link IExpressionEvaluator}, and is hence only available in <i>this</i>
     * implementation of <code>org.codehaus.commons.compiler</code>. To be independent from this particular
     * implementation, try to switch to {@link #createFastEvaluator(Reader, Class, String[])}.
     *
     * @deprecated Use {@link #createFastEvaluator(Reader, Class, String[])} instead
     */
    @Deprecated public static Object
    createFastExpressionEvaluator(
        Scanner     scanner,
        String      className,
        Class       optionalExtendedType,
        Class       interfaceToImplement,
        String[]    parameterNames,
        ClassLoader optionalParentClassLoader
    ) throws CompileException, IOException {
        ExpressionEvaluator ee = new ExpressionEvaluator();
        ee.setClassName(className);
        ee.setExtendedClass(optionalExtendedType);
        ee.setParentClassLoader(optionalParentClassLoader);
        return ee.createFastEvaluator(scanner, interfaceToImplement, parameterNames);
    }

    /**
     * Notice: This method is not declared in {@link IExpressionEvaluator}, and is hence only available in <i>this</i>
     * implementation of <code>org.codehaus.commons.compiler</code>. To be independent from this particular
     * implementation, try to switch to {@link #createFastEvaluator(Reader, Class, String[])}.
     *
     * @deprecated Use {@link #createFastEvaluator(Reader, Class, String[])} instead
     */
    @Deprecated public static Object
    createFastExpressionEvaluator(
        Scanner     scanner,
        String[]    optionalDefaultImports,
        String      className,
        Class       optionalExtendedType,
        Class       interfaceToImplement,
        String[]    parameterNames,
        ClassLoader optionalParentClassLoader
    ) throws CompileException, IOException {
        ExpressionEvaluator ee = new ExpressionEvaluator();
        ee.setClassName(className);
        ee.setExtendedClass(optionalExtendedType);
        ee.setDefaultImports(optionalDefaultImports);
        ee.setParentClassLoader(optionalParentClassLoader);
        return ee.createFastEvaluator(scanner, interfaceToImplement, parameterNames);
    }

    /**
     * Guess the names of the parameters used in the given expression. The strategy is to look
     * at all "ambiguous names" in the expression (e.g. in "a.b.c.d()", the ambiguous name
     * is "a.b.c"), and then at the first components of the ambiguous name.
     * <ul>
     *   <li>If any component starts with an upper-case letter, then ambiguous name is assumed to
     *       be a type name.
     *   <li>Otherwise, it is assumed to be a parameter name.
     * </ul>
     *
     * @see Scanner#Scanner(String, Reader)
     */
    public static String[]
    guessParameterNames(Scanner scanner) throws CompileException, IOException {
        Parser parser = new Parser(scanner);

        // Eat optional leading import declarations.
        while (parser.peek("import")) parser.parseImportDeclaration();

        // Parse the expression.
        Rvalue rvalue = parser.parseExpression().toRvalueOrCompileException();
        if (!parser.peekEof()) {
            throw new CompileException("Unexpected token \"" + parser.peek() + "\"", scanner.location());
        }

        // Traverse the expression for ambiguous names and guess which of them are parameter names.
        final Set<String> parameterNames = new HashSet();
        rvalue.accept((RvalueVisitor) new Traverser() {

            @Override public void
            traverseAmbiguousName(AmbiguousName an) {

                // If any of the components starts with an upper-case letter, then the ambiguous
                // name is most probably a type name, e.g. "System.out" or "java.lang.System.out".
                for (String identifier : an.identifiers) {
                    if (Character.isUpperCase(identifier.charAt(0))) return;
                }

                // It's most probably a parameter name (although it could be a field name as well).
                parameterNames.add(an.identifiers[0]);
            }
        }.comprehensiveVisitor());

        return (String[]) parameterNames.toArray(new String[parameterNames.size()]);
    }
}
