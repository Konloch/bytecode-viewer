
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

/**
 * This class defines constants and convenience methods for the handling of modifiers as defined by the JVM.
 * <p>
 * Notice: This class should be named <code>IClass.IModifier</code>, but changing the name would break existing client
 * code. Thus it won't be renamed until there's a really good reason to do it (maybe with a major design change).
 */
public final
class Mod {
    private Mod() {} // Don't instantiate me!

    /** An alias for '0' -- <i>no</i> modifiers. */
    public static final short NONE = 0x0000;

    /**
     * The flag indicating 'public accessibility' of the modified element. Methods of interfaces are always {@link
     * #PUBLIC}.
     *
     * @see #PPP
     * @see #isPublicAccess(short)
     */
    public static final short PUBLIC = 0x0001;

    /** @return Whether the given modifier symbolizes {@link #PUBLIC} accessibility */
    public static boolean isPublicAccess(short sh) { return (sh & Mod.PPP) == Mod.PUBLIC; }

    /**
     * The flag indicating 'private accessibility' of the modified element.
     *
     * @see #PPP
     * @see #isPrivateAccess(short)
     */
    public static final short PRIVATE = 0x0002;

    /** @return Whether the given modifier symbolizes {@link #PRIVATE} accessibility */
    public static boolean isPrivateAccess(short sh) { return (sh & Mod.PPP) == Mod.PRIVATE; }

    /**
     * The flag indicating 'protected accessibility' of the modified element.
     *
     * @see #PPP
     * @see #isProtectedAccess(short)
     */
    public static final short PROTECTED = 0x0004;

    /** @return Whether the given modifier symbolizes {@link #PROTECTED} accessibility */
    public static boolean isProtectedAccess(short sh) { return (sh & Mod.PPP) == Mod.PROTECTED; }

    /**
     * The flag indicating 'default accessibility' a.k.a. 'package accessibility' of the modified element.
     *
     * @see #PPP
     * @see #isPackageAccess(short)
     */
    public static final short PACKAGE = 0x0000;

    /** @return Whether the given modifier symbolizes {@link #PACKAGE} (a.k.a. 'default') accessibility */
    public static boolean isPackageAccess(short sh) { return (sh & Mod.PPP) == Mod.PACKAGE; }

    /** The mask to select the accessibility flags from modifiers. */
    public static final short PPP = 0x0007;

    /** @return The given {@code modifiers}, but with the accessibility part changed to {@code newAccess} */
    public static short
    changeAccess(short modifiers, short newAccess) { return (short) ((modifiers & ~Mod.PPP) | newAccess); }

    /**
     * This flag is set on class or interface initialization methods, STATIC class fields, all interface fields, STATIC
     * methods, and STATIC nested classes.
     */
    public static final short STATIC = 0x0008;

    /** @return Whether the given modifier includes {@link #STATIC} */
    public static boolean isStatic(short sh) { return (sh & Mod.STATIC) != 0; }

    /**
     * This flag is set on FINAL classes, FINAL fields and FINAL methods, and is mutually exclusive with {@link
     * #VOLATILE} and {@link #ABSTRACT}.
     */
    public static final short FINAL = 0x0010;

    /** @return Whether the given modifier includes {@link #INTERFACE} */
    public static boolean isFinal(short sh) { return (sh & Mod.FINAL) != 0; }

    /**
     * This flag is always set on classes, and never set on any other element. Notice that it has the same value as
     * {@link #SYNCHRONIZED}, which is OK because {@link #SYNCHRONIZED} is for methods and {@link #SUPER} for classes.
     */
    public static final short SUPER = 0x0020;

    /** @return Whether the given modifier includes {@link #SUPER} */
    public static boolean isSuper(short sh) { return (sh & Mod.SUPER) != 0; }

    /**
     * This flag is set on SYNCHRONIZED methods. Notice that it has the same value as {@link #SUPER}, which is OK
     * because {@link #SYNCHRONIZED} is for methods and {@link #SUPER} for classes.
     */
    public static final short SYNCHRONIZED = 0x0020;

    /** @return Whether the given modifier includes {@link #SYNCHRONIZED} */
    public static boolean isSynchronized(short sh) { return (sh & Mod.SYNCHRONIZED) != 0; }

    /**
     * This flag is set on VOLATILE fields and is mutually exclusive with {@link #FINAL}. Notice that it has the same
     * value as {@link #BRIDGE}, which is OK because {@link #BRIDGE} is for methods and {@link #VOLATILE} for fields.
     */
    public static final short VOLATILE = 0x0040;

    /** @return Whether the given modifier includes {@link #VOLATILE} */
    public static boolean isVolatile(short sh) { return (sh & Mod.VOLATILE) != 0; }

    /**
     * This flag is set on 'bridge methods' generated by the compiler. Notice that it has the same value as {@link
     * #VOLATILE}, which is OK because {@link #BRIDGE} is for methods and {@link #VOLATILE} for fields.
     */
    public static final short BRIDGE = 0x0040;

    /** @return Whether the given modifier includes {@link #BRIDGE} */
    public static boolean isBridge(short sh) { return (sh & Mod.BRIDGE) != 0; }

    /**
     * This flag is set on TRANSIENT fields. Notice that it has the same value as {@link #VARARGS}, which is OK because
     * {@link #VARARGS} is for methods and {@link #TRANSIENT} for fields.
     */
    public static final short TRANSIENT = 0x0080;

    /** @return Whether the given modifier includes {@link #TRANSIENT} */
    public static boolean isTransient(short sh) { return (sh & Mod.TRANSIENT) != 0; }

    /**
     * This flag is set on 'variable arity' (a.k.a. 'varargs') methods and constructors. Notice that it has the same
     * value as {@link #TRANSIENT}, which is OK because {@link #VARARGS} is for methods and {@link #TRANSIENT} for
     * fields.
     */
    public static final short VARARGS = 0x0080;

    /** @return Whether the given modifier includes {@link #VARARGS} */
    public static boolean isVarargs(short sh) { return (sh & Mod.VARARGS) != 0; }

    /** This flag is set on NATIVE methods, and is mutually exclusive with {@link #ABSTRACT}. */
    public static final short NATIVE = 0x0100;

    /** @return Whether the given modifier includes {@link #NATIVE} */
    public static boolean isNative(short sh) { return (sh & Mod.NATIVE) != 0; }

    /**
     * This flag is set on interfaces (including nested interfaces), and requires that {@link #ABSTRACT} must also be
     * set. {@link #INTERFACE} is mutually exclusive with {@link #FINAL}, {@link #SUPER} and {@link #ENUM}.
     */
    public static final short INTERFACE = 0x0200;

    /** @return Whether the given modifier includes {@link #INTERFACE} */
    public static boolean isInterface(short sh) { return (sh & Mod.INTERFACE) != 0; }

    /**
     * This flag is set on all interfaces, ABSTRACT classes and ABSTRACT methods, and is mutually exclusive with
     * {@link #FINAL}, {@link #NATIVE}, {@link #PRIVATE}, {@link #STATIC} and {@link #SYNCHRONIZED}.
     */
    public static final short ABSTRACT = 0x0400;

    /** @return Whether the given modifier includes {@link #ABSTRACT} */
    public static boolean isAbstract(short sh) { return (sh & Mod.ABSTRACT) != 0; }

    /** This flag is set on STRICTFP methods, and is mutually exclusive with {@link #ABSTRACT}. */
    public static final short STRICTFP = 0x0800;

    /** @return Whether the given modifier includes {@link #STRICTFP} */
    public static boolean isStrictfp(short sh) { return (sh & Mod.STRICTFP) != 0; }

    // Poorly documented JDK 1.5 modifiers:

    /**
     * This flag is set on classes, methods and fields that were generated by the compiler and do not appear in the
     * source code.
     */
    public static final short SYNTHETIC = 0x1000;

    /** @return Whether the given modifier includes {@link #SYNTHETIC} */
    public static boolean isSynthetic(short sh) { return (sh & Mod.SYNTHETIC) != 0; }

    /**
     * This flag is set on annotation types (including nested annotation types), and requires that {@link #INTERFACE}
     * is also set.
     */
    public static final short ANNOTATION = 0x2000;

    /** @return Whether the given modifier includes {@link #ANNOTATION} */
    public static boolean isAnnotation(short sh) { return (sh & Mod.ANNOTATION) != 0; }

    /**
     * This flag is set on enumerated types (including nested enumerated types) and enumerated types' elements, and is
     * mutually exclusive with {@link #INTERFACE}.
     */
    public static final short ENUM = 0x4000;

    /** @return Whether the given modifier includes {@link #ENUM} */
    public static boolean isEnum(short sh) { return (sh & Mod.ENUM) != 0; }

    /**
     * Composes and returns a string that maps the given modifier as follows:
     * <ul>
     *   <li>Value zero is mapped to "".
     *   <li>Non-zero values are mapped to a sequence of words, separated with blanks.
     *   <li>{@link #VARARGS} is mapped to "transient", because the two flags have the same value
     *   <li>{@link #SUPER} is mapped to "synchronized", because the two flags have the same value
     *   <li>{@link #BRIDGE} is mapped to "volatile", because the two flags have the same value
     * </ul>
     */
    public static String
    shortToString(short sh) {
        if (sh == 0) return "";
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < Mod.MAPPINGS.length; i += 2) {
            if ((sh & ((Short) Mod.MAPPINGS[i + 1]).shortValue()) == 0) continue;
            if (res.length() > 0) res.append(' ');
            res.append((String) Mod.MAPPINGS[i]);
        }
        return res.toString();
    }

    private static final Object[] MAPPINGS = {
        "public",       new Short(Mod.PUBLIC),
        "private",      new Short(Mod.PRIVATE),
        "protected",    new Short(Mod.PROTECTED),
//      "???",          new Short(Mod.PACKAGE),
        "static",       new Short(Mod.STATIC),
        "final",        new Short(Mod.FINAL),
        "synchronized", new Short(Mod.SYNCHRONIZED), // Has the same value as SUPER
//      "super",        new Short(Mod.SUPER),        // Has the same value as SYNCHRONIZED
        "volatile",     new Short(Mod.VOLATILE), // Has the same value as BRIDGE
//      "bridge",       new Short(Mod.BRIDGE),   // Has the same value as VOLATILE
        "transient",    new Short(Mod.TRANSIENT), // Has the same value as VARARGS
//      "varargs",      new Short(Mod.VARARGS),   // Has the same value as TRANSIENT
        "native",       new Short(Mod.NATIVE),
        "interface",    new Short(Mod.INTERFACE),
        "abstract",     new Short(Mod.ABSTRACT),
        "strictfp",     new Short(Mod.STRICTFP),
        "enum",         new Short(Mod.ENUM),
        "synthetic",    new Short(Mod.SYNTHETIC),
        "@",            new Short(Mod.ANNOTATION),
    };
}

