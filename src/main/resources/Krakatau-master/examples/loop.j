.class public loop
.super java/lang/Object

.method public static main : ([Ljava/lang/String;)V
    .limit stack 10
    .limit locals 10

    iconst_1
    istore_0

LOOP_START:
    getstatic java/lang/System out Ljava/io/PrintStream;
    dup

    iload_0
    invokevirtual java/io/PrintStream print (I)V

    ldc " "
    invokevirtual java/io/PrintStream print (Ljava/lang/Object;)V

    iinc 0 1
    iload_0
    bipush 100
    if_icmple LOOP_START
    return
.end method