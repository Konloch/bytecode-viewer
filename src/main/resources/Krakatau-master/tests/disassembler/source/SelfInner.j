.version 49 0
.class public super SelfInner
.super java/lang/Object

.method public static main : ([Ljava/lang/String;)V
    .code stack 2 locals 5
L0:     ldc Class SelfInner
L2:     invokevirtual Method java/lang/Class getDeclaredClasses ()[Ljava/lang/Class;
L5:     astore_1
L6:     aload_1
L7:     arraylength
L8:     istore_2
L9:     iconst_0
L10:    istore_3
L11:    iload_3
L12:    iload_2
L13:    if_icmpge L35
L16:    aload_1
L17:    iload_3
L18:    aaload
L19:    astore 4
L21:    getstatic Field java/lang/System out Ljava/io/PrintStream;
L24:    aload 4
L26:    invokevirtual Method java/io/PrintStream println (Ljava/lang/Object;)V
L29:    iinc 3 1
L32:    goto L11
L35:    return
L36:
    .end code
.end method

.const [1] = Class SelfInner
.innerclasses
	[1] SelfInner Foo public
.end innerclasses


.attribute SourceDebugExtension b''
.end class
