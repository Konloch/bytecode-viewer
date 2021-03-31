
.class public super [cls]
.super java/lang/Object

.const [cls] = Class SamSunTests

.method public static main : ([Ljava/lang/String;)V
    .code stack 10 locals 10
        aload_0
        dup
        dup2
        invokestatic [cls] exceptionVerificationUsesOldLocalsState ([Ljava/lang/String;)V
        invokestatic [cls] castToInterface ([Ljava/lang/String;)V
        dup2
        invokestatic [cls] castFromInterface ([Ljava/lang/String;)V
        invokestatic [cls] castStringLiteral ([Ljava/lang/String;)V
        dup2
        invokestatic [cls] returnFromJSRWithSingleValueOnStack ([Ljava/lang/String;)V
        return
    .end code
.end method

.method public static exceptionVerificationUsesOldLocalsState : ([Ljava/lang/String;)V
    .code stack 10 locals 10
        .catch java/lang/Throwable from L0 to L1 using L0
        new java/lang/Integer
        astore_1
        aconst_null
L0:
        pop
        aload_1
        ifnonnull L3
        new java/lang/Long
        astore_1
L1:
        return
L3:
        aload_1
        dup
        iconst_0
        invokespecial Method java/lang/Integer <init> (I)V
        getstatic java/lang/System out Ljava/io/PrintStream;
        swap
        invokevirtual java/io/PrintStream println (Ljava/lang/Object;)V
        return
    .end code
.end method

.method public static num : ()I
    .code stack 10 locals 10
L0:     iconst_0
L1:     ireturn
L2:
    .end code
.end method

.method public static infiniteLoop : ([Ljava/lang/String;)V
    .code stack 10 locals 10
        invokestatic Method [cls] num ()I
L0:
        dup
        tableswitch 0
            L0
            default : L4
L4:
        return
    .end code
.end method

.method public static castToInterface : ([Ljava/lang/String;)V
    .code stack 10 locals 10
        iconst_2
        anewarray java/io/Serializable
        astore_0
        aload_0
        iconst_0
        new java/util/concurrent/atomic/AtomicReference
        dup
        aload_0
        invokespecial Method java/util/concurrent/atomic/AtomicReference <init> (Ljava/lang/Object;)V
        checkcast java/io/Serializable
        aastore
        aload_0
        iconst_1
        aconst_null
        aastore
        getstatic java/lang/System out Ljava/io/PrintStream;
        aload_0
        iconst_1
        aaload
        invokevirtual Method java/io/PrintStream println (Ljava/lang/Object;)V
        return
    .end code
.end method

.method public static castFromInterface : ([Ljava/lang/String;)V
    .code stack 10 locals 10
        .catch java/lang/Throwable from L0 to L1 using L2
        iconst_1
        anewarray java/io/Serializable
        astore_0
        aload_0
        iconst_0
        new java/lang/Exception
        dup
        invokespecial Method java/lang/Exception <init> ()V
        aastore
        aload_0
        iconst_0
        aaload
L0:
        checkcast java/lang/Exception
L1:
        astore_0
        aconst_null
L2:
        getstatic java/lang/System out Ljava/io/PrintStream;
        aload_0
        invokevirtual Method java/io/PrintStream println (Ljava/lang/Object;)V
        return
    .end code
.end method

.method public static castStringLiteral : ([Ljava/lang/String;)V
    .code stack 10 locals 10
    .catch java/lang/Exception from L1 to L2 using L3
    ldc "\n\n\n"
    getstatic java/lang/System out Ljava/io/PrintStream;
    swap
L1:
    checkcast java/lang/Exception
    goto L4
L2:
L3:
    getstatic java/lang/System out Ljava/io/PrintStream;
    swap
L4:
    invokevirtual Method java/io/PrintStream println (Ljava/lang/Object;)V
    return
    .end code
.end method

.method public static returnFromJSRWithSingleValueOnStack : ([Ljava/lang/String;)V
    .code stack 1024 locals 10
    jsr Lret
    return
Lret:
    astore 5
    ldc "Hi"
    invokevirtual Method java/lang/String toCharArray ()[C
    ret 5
    .end code
.end method

.method public static soleCatchOfNon$Non$NullThrow : ([Ljava/lang/String;)V
    .code stack 1024 locals 10
    .catch [0] from Lb to Lc using Ld
Lb:
        aconst_null
        athrow
Lc:
Ld:
        athrow
        return
    .end code
.end method
.end class

