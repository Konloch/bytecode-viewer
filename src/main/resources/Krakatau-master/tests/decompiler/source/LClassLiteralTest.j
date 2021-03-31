; Originally created as a test for Krakatau (https://github.com/Storyyeller/Krakatau)
.version 49 0
.class public abstract LClassLiteralTest ; Make class name start with L to test descriptor parsing
.super java/util/AbstractList


.const [this_c1] = Class LClassLiteralTest
.const [this_c2] = Class LLClassLiteralTest;
.const [obj_c1] = Class java/lang/Object
.const [obj_c2] = Class Ljava/lang/Object;
.const [iarr_c] = Class [I
.const [aarr_c] = Class [Ljava/lang/Object;
.const [longarr_c] = Class [[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[D


.method public static main : ([Ljava/lang/String;)V
    .limit stack 12
    .limit locals 1

    ldc [obj_c1]
    ldc [obj_c2]
    dup2
    ldc [this_c1]
    ldc [this_c2]
    dup2_x2
    ldc [iarr_c]
    ldc [aarr_c]
    ldc [longarr_c]
    invokestatic LClassLiteralTest print (Ljava/lang/Object;)V
    invokestatic LClassLiteralTest print (Ljava/lang/Object;)V
    invokestatic LClassLiteralTest print (Ljava/lang/Object;)V
    invokestatic LClassLiteralTest print (Ljava/lang/Object;)V
    invokestatic LClassLiteralTest print (Ljava/lang/Object;)V
    invokestatic LClassLiteralTest print (Ljava/lang/Object;)V
    invokestatic LClassLiteralTest print (Ljava/lang/Object;)V


    new Ljava/lang/String;
    dup
    ldc_w "Lwtf;"
    invokespecial Ljava/lang/String; <init> (Ljava/lang/String;)V
    invokestatic LLClassLiteralTest; print (Ljava/lang/Object;)V


    if_acmpne LENDIF1
        ldc "Equal1"
        invokestatic LClassLiteralTest print (Ljava/lang/Object;)V
LENDIF1:

    if_acmpne LENDIF2
        ldc "Equal2"
        invokestatic LClassLiteralTest print (Ljava/lang/Object;)V
LENDIF2:

    return
.end method


.method public static print : (Ljava/lang/Object;)V
    .limit stack 2
    .limit locals 1

    aload 0
    checkcast java/lang/Object
    checkcast Ljava/lang/Object;
    astore 0

    getstatic java/lang/System out Ljava/io/PrintStream;
    aload_0
    invokevirtual java/lang/Object toString ()Ljava/lang/String;
    invokevirtual java/io/PrintStream println (Ljava/lang/Object;)V
    return
.end method
