.version 52 0
.class public super LambdaTest1
.super java/lang/Object

.method public <init> : ()V
    .code stack 1 locals 1
        aload_0
        invokespecial Method java/lang/Object <init> ()V
        return
    .end code
.end method

.method public static varargs main : ([Ljava/lang/String;)V
    .code stack 4 locals 2
        invokedynamic InvokeDynamic invokeStatic Method java/lang/invoke/LambdaMetafactory metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; MethodType (J)J MethodHandle invokeStatic Method LambdaTest1 lambda$main$0 (J)J MethodType (J)J : applyAsLong ()Ljava/util/function/LongUnaryOperator;
        astore_1
        getstatic Field java/lang/System out Ljava/io/PrintStream;
        aload_1
        ldc2_w 42L
        invokeinterface InterfaceMethod java/util/function/LongUnaryOperator applyAsLong (J)J 3
        invokevirtual Method java/io/PrintStream println (J)V
        return
    .end code
.end method

.method private static synthetic lambda$main$0 : (J)J
    .code stack 4 locals 2
        lload_0
        lload_0
        l2i
        lshl
        lreturn
    .end code
.end method

.innerclasses
    java/lang/invoke/MethodHandles$Lookup java/lang/invoke/MethodHandles Lookup public static final
.end innerclasses

.end class
