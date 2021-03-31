; Originally created as a test for Krakatau (https://github.com/Storyyeller/Krakatau)
.class public DoubleEdge
.super java/lang/Object

.method public static main : ([Ljava/lang/String;)V
    .limit locals 1111
    .limit stack 1111
    .catch [0] from L1 to L5 using L5

    aload 0
    bipush 0
L1:
	aaload
L5:
	getstatic java/lang/System out Ljava/io/PrintStream;
	swap
	invokevirtual java/io/PrintStream println (Ljava/lang/Object;)V
	return
.end method
