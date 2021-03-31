.class public jasmin
.super java/lang/Object

.method public static main : ([Ljava/lang/String;)V
    .limit stack 10
    .limit locals 10

    getstatic java/lang/System/out Ljava/io/PrintStream;
    ldc "Hello World!"
    invokevirtual java/io/PrintStream println (Ljava/lang/Object;)V
    return
.end method