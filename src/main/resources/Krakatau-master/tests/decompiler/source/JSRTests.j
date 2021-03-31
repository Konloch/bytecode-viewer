; Originally created as a test for Krakatau (https://github.com/Storyyeller/Krakatau)

.class public JSRTests
.super java/lang/Object

.method public static main : ([Ljava/lang/String;)V
    .limit locals 11
    .limit stack 11

    aload_0
    dup
    invokestatic JSRTests skipJSR ([Ljava/lang/String;)V
    invokestatic JSRTests nestedJSRs ([Ljava/lang/String;)V
    invokestatic JSRTests doubleJumpRet ()V
    invokestatic JSRTests retWithStack ()V
    return
.end method

.method public static nestedJSRs : ([Ljava/lang/String;)V
    .limit locals 11
    .limit stack 11

    ; JSR as goto
    jsr_w L1
L1: jsr L2
L2: pop2

    ; Test a non-regular loop inside nested subprocedures
    aload_0
    arraylength
    istore_0

    sipush 1337
    istore_2

    iload_0
    jsr LSUB1
    iconst_0
    jsr LSUB1
    bipush 12
    jsr LSUB1
    return

LSUB1:
    swap
    istore_1
    jsr LSUB2
    astore_1
    ret 1

LSUB2:
    jsr LSUB3
    iconst_5
    istore_1
    jsr LSUB3
    astore_1
    ret 1

LSUB3:
    iload_1
    dup
    iconst_3
    irem

    ifeq LLOOP_ENTRY_2
LLOOP_ENTRY_1:
    iinc 2 -27
    jsr LPRINT

    dup
    iconst_2
    irem
    ifne LLOOP_TAIL

LLOOP_ENTRY_2:
    jsr LPRINT
    iinc 2 -7
    dup
    iflt LLOOP_EXIT

LLOOP_TAIL:
    iconst_m1
    iadd
    goto_w LLOOP_ENTRY_1
LLOOP_EXIT:
    swap
    wide astore 1
    pop
    ret 1

LPRINT:
    iinc 0 17
    getstatic java/lang/System out Ljava/io/PrintStream;
    wide iload 0
    iload_2
    imul
    invokevirtual java/io/PrintStream println (I)V
    astore_1
    ret 1

.end method

.method public static skipJSR : ([Ljava/lang/String;)V
    .limit locals 1111
    .limit stack 11

    iconst_1
    istore_1
    jsr LSUB

    iconst_1
    newarray double
    dup
    astore_2
    iconst_0
    iload_1
    i2d
    dastore
    iinc 1 1

    jsr LSUB
    jsr LSUB
    aload_2
    iconst_0
    daload
    iload_1
    i2d
    ddiv
    dstore_0

    getstatic java/lang/System out Ljava/io/PrintStream;
    dload_0
    invokevirtual java/io/PrintStream println (D)V
    return

LS_2:
    arraylength
    iadd
    istore_1
    wide ret 333

LSUB:
    wide astore 333
    aload_0
    iload_1
    dup_x1
    lookupswitch
        default : LS_2
.end method

.method public static jsrStack : ([Ljava/lang/String;)V
    .limit locals 11
    .limit stack 11
    ; Test for returning from JSR which isn't top of the stack

    aload_0
    arraylength
    istore_0

    jsr LTOP
        ldc 'TM'
        invokestatic JSRTests print (Ljava/lang/String;)V
    jsr LTOP
        ldc 'TR'
        invokestatic JSRTests print (Ljava/lang/String;)V
    return

LTOP:
    astore_1
    jsr LSUB
        ldc 'SM'
        invokestatic JSRTests print (Ljava/lang/String;)V
    jsr LSUB
        ldc 'SR'
        invokestatic JSRTests print (Ljava/lang/String;)V
    return

LSUB:
    astore_2
    iinc 0 -1
    iload_0
    lookupswitch
        -1 : LBRANCH1
        default : LBRANCH2

    ; return either from this or from the caller
LBRANCH1:
    ret 1
LBRANCH2:
    ret 2
.end method

.method public static print : (Ljava/lang/String;)V
    .limit locals 1
    .limit stack 2

    getstatic java/lang/System out Ljava/io/PrintStream;
    aload_0
    invokevirtual java/io/PrintStream println (Ljava/lang/Object;)V
    return
.end method

.method public static doubleJumpRet : ()V
    .attribute "Code" .code stack 2 locals 3
        iconst_2
        istore_2
Lproc0:
        jsr Lproc1
        ldc 'Pass'
        invokestatic JSRTests print (Ljava/lang/String;)V
        return
Lproc2:
        pop
        iload_2
        ifeq Lproc1
        astore_0
        ret 0
Lproc1:
        jsr Lproc2
        ldc 'Fail'
        invokestatic JSRTests print (Ljava/lang/String;)V
        return
    .end code
.end method

.method public static retWithStack : ()V
    .code stack 21 locals 3
        jsr Lproc
        invokestatic JSRTests print (Ljava/lang/String;)V
        invokestatic JSRTests print (Ljava/lang/String;)V
        invokestatic JSRTests print (Ljava/lang/String;)V
        return
Lproc:
        dup
        astore_0
        ldc 'str 1'
        ldc 'str 2'
        ldc 'str 3'
        ret 0
    .end code
.end method


