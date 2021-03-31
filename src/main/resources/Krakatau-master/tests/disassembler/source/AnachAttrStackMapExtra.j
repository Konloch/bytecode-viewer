.version 50 0
.class public super AnachAttrStackMapExtra
.super java/lang/Object

.method public static main : ([Ljava/lang/String;)V
    .code stack 2 locals 5


L21:    getstatic Field java/lang/System out Ljava/io/PrintStream;
        ldc "stackmap +++"
L26:    invokevirtual Method java/io/PrintStream println (Ljava/lang/Object;)V


        nop
        nop
        nop
L35:    return
LEND:

        .attribute StackMapTable b'\x00\x01@\x08\x00\x0b'
    .end code
.end method


.end class
