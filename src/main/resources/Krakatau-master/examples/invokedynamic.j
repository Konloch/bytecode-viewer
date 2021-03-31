.version 51 0
.class public invokedynamic
.super java/lang/Object

.const [mycls] = Class invokedynamic
.const [mymeth] = Method [mycls] mybsm (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;

.const [mydyn] = InvokeDynamic invokeStatic [mymeth] : whatever (I)V
.const [mydyn2] = InvokeDynamic invokeStatic [mymeth] : whatever (Ljava/lang/Integer;)V

.method public static print : (Ljava/lang/Integer;)V
    .limit stack 10
    .limit locals 10
    getstatic java/lang/System out Ljava/io/PrintStream;
    aload_0
    invokevirtual java/io/PrintStream println (Ljava/lang/Object;)V
    return
.end method

.const [intclass] = Class java/lang/Integer

.method public static mybsm : (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
    .limit stack 10
    .limit locals 10

    new java/lang/invoke/ConstantCallSite
    dup

    aload_0
    ldc [mycls]
    ldc "print"

    getstatic java/lang/Void TYPE Ljava/lang/Class;
    ldc [intclass]
    invokestatic java/lang/invoke/MethodType methodType (Ljava/lang/Class;Ljava/lang/Class;)Ljava/lang/invoke/MethodType;
    
    invokevirtual java/lang/invoke/MethodHandles$Lookup findStatic (Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;

    ;Adapter to box int arg
    aload_2
    invokevirtual java/lang/invoke/MethodHandle asType (Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;

    invokespecial java/lang/invoke/ConstantCallSite <init> (Ljava/lang/invoke/MethodHandle;)V
    areturn
.end method

.method static public main : ([Ljava/lang/String;)V
	.limit stack 10
	.limit locals 10

    iconst_m1
    invokedynamic [mydyn]

    ldc "#ABC"
    invokestatic [intclass] decode (Ljava/lang/String;)Ljava/lang/Integer;    
    invokedynamic [mydyn2]

	return
.end method
