; Originally created as a test for Krakatau (https://github.com/Storyyeller/Krakatau)
.version 49 0
.class public SwapLoopTest
.super java/lang/Object

.field static test I = 9

.method public static main : ([Ljava/lang/String;)V
    .limit stack 10
    .limit locals 10

    aload_0
    iconst_0
    aaload
    aload_0
    iconst_1
    aaload

LBEGIN:
	swap
	dup2
	invokestatic SwapLoopTest print (Ljava/lang/String;)V
	invokestatic SwapLoopTest print (Ljava/lang/String;)V
	goto LBEGIN

.end method


.method public static print : (Ljava/lang/String;)V
    .limit stack 10
    .limit locals 10

    iconst_m1
    getstatic SwapLoopTest test I
    dup2
    idiv
    pop
    iadd
    putstatic SwapLoopTest test I

    getstatic java/lang/System out Ljava/io/PrintStream;
    aload_0
    invokevirtual java/io/PrintStream println (Ljava/lang/Object;)V
    return
.end method
