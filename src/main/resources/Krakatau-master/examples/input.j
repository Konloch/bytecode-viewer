.class public input
.super java/lang/Object

.method public static main : ([Ljava/lang/String;)V
    .limit stack 10
    .limit locals 10
                                                                                        ; <empty>
    getstatic java/lang/System out Ljava/io/PrintStream;                                ; System
    
    ldc "Hello "                                                                        ; System, "Hello "
    aload_0                                                                             ; System, "Hello ", args
    iconst_0                                                                            ; System, "Hello ", args, 0
    aaload                                                                              ; System, "Hello ", name 
    ldc "!"                                                                             ; System, "Hello ", name, "!"

    invokevirtual java/lang/String concat (Ljava/lang/String;)Ljava/lang/String;        ; System, "Hello ", "name!"
    invokevirtual java/lang/String concat (Ljava/lang/String;)Ljava/lang/String;        ; System, "Hello name!"

    invokevirtual java/io/PrintStream println (Ljava/lang/Object;)V                     ; <empty>
    return
.end method