; Originally created as a test for Krakatau (https://github.com/Storyyeller/Krakatau)
.version 49 49
.class public ControlFlow
.super java/lang/Object

.method public static main : ([Ljava/lang/String;)V
    .limit locals 11
    .limit stack 11

    ; Test unused iinc results
    iconst_3
    iconst_3
    istore 3
    istore 4
    iconst_4
    iconst_4
    if_icmpgt Lendiinc
    iinc 4 -44
Lendiinc:
    iinc 3 33

LSTART:
    aload_0
    dup
    arraylength
    istore_0

    dup
    iconst_0
    aaload
    dup
    invokestatic ControlFlow dsm (Ljava/lang/String;)V


    invokevirtual java/lang/String hashCode ()I
    invokestatic ControlFlow switchtest2 (I)I
    getstatic java/lang/System out Ljava/io/PrintStream;
    swap
    invokevirtual java/io/PrintStream print (I)V



    iconst_1
    aaload
    invokestatic java/lang/Integer decode (Ljava/lang/String;)Ljava/lang/Integer;
    invokevirtual java/lang/Integer intValue ()I
    dup

LDEC:
	iload_0
	iconst_m1
	i2c
	if_icmple LDEC2
		iinc 0 -113
		goto LDEC
LDEC2:

    iinc 0 1
    ifne LIF
LIF:
	iload_0
	dup2
	ixor
	istore_0

	iconst_m1
	if_icmpeq LIF

	tableswitch -2
		LS1
		LS2
		LS1
		default : LS2

LS1:
	iinc 0 4
LS2:
	wide iinc 0 -1289

	iload_0
	i2l
	invokestatic java/lang/Long valueOf (J)Ljava/lang/Long;

LPRINT:
    getstatic java/lang/System out Ljava/io/PrintStream;
    swap
    invokevirtual java/io/PrintStream println (Ljava/lang/Object;)V
    return

LEX:
	checkcast java/lang/ClassCastException
LEX2:
	goto LPRINT

.catch java/lang/IndexOutOfBoundsException from LSTART to LS1 using LEX
.catch java/lang/RuntimeException from LSTART to LS2 using LEX2
.catch java/lang/NumberFormatException from LSTART to LS2 using LEX
.catch java/lang/Throwable from LSTART to LS1 using LEX2

.catch [0] from LSTART to LS2 using LEX
.catch [0] from LEX to LEX2 using LEX
.catch [0] from LEX to LEX2 using LEX2
.end method

.method static switchtest2 : (I)I
    .limit locals 65535
    .limit stack 65535
	iload_0
	iload_0
	iload_0

	iconst_2
	irem

	tableswitch 0
		LSWITCHA
		LSWITCHB
		default : LSWITCHC
LSWITCHC:
	imul
	ireturn
LSWITCHA:
	iconst_5
	goto LMERGE
LSWITCHB:
	bipush -42
LMERGE:
	swap
	pop
	dup2
	if_icmple LSWITCHC
	ixor
	ireturn
.end method

.method static dsm : (Ljava/lang/String;)V
    .limit locals 11
    .limit stack 11

    aload_0
    invokevirtual java/lang/String toCharArray ()[C

    bipush 127
    newarray char
    astore_0
    iconst_m1
    istore_2
    bipush 32

LS0:
	bipush 64
LSUB_BEGIN_0:
	ixor
	aconst_null
	iinc 2 1
	swap
	aload_0
	swap
	iload_2
	swap
	castore
	astore_3
	dup
	iload_2
	caload
	dup
	iconst_5
	irem
LSUB_END_0:
	lookupswitch
		0 : LS0
		1 : LS1
		2 : LS2
		default : LS3

LS1:
	bipush 32
LSUB_BEGIN_1:
	ixor
	aconst_null
	iinc 2 1
	swap
	aload_0
	swap
	iload_2
	swap
	castore
	astore_3
	dup
	iload_2
	caload
	dup
	iconst_5
	irem
LSUB_END_1:
	lookupswitch
		0 : LS1
		3 : LS2
		4 : LS0
		default : LS3

LS2:
	bipush 16
LSUB_BEGIN_2:
	ixor
	aconst_null
	iinc 2 1
	swap
	aload_0
	swap
	iload_2
	swap
	castore
	astore_3
	dup
	iload_2
	caload
	dup
	iconst_5
	irem
LSUB_END_2:
	lookupswitch
		0 : LS3
		1 : LS0
		2 : LS1
		4 : LS1
		default : LS0

LS3:
	bipush 8
LSUB_BEGIN_3:
	ixor
	aconst_null
	iinc 2 1
	swap
	aload_0
	swap
	iload_2
	swap
	castore
	astore_3
	dup
	iload_2
	caload
	dup
	iconst_5
	irem
LSUB_END_3:
	lookupswitch
		0 : LS0
		1 : LS1
		2 : LS3
		default : LS2

.catch java/lang/IndexOutOfBoundsException from LSUB_BEGIN_0 to LSUB_END_0 using LEND
.catch java/lang/IndexOutOfBoundsException from LSUB_BEGIN_1 to LSUB_END_1 using LEND
.catch java/lang/IndexOutOfBoundsException from LSUB_BEGIN_2 to LSUB_END_2 using LEND
.catch java/lang/IndexOutOfBoundsException from LSUB_BEGIN_3 to LSUB_END_3 using LEND

LEND:
    getstatic java/lang/System out Ljava/io/PrintStream;

    new java/lang/String
    dup
    aload_0
    iconst_0
    iload_2
    invokespecial java/lang/String <init> ([CII)V

    invokevirtual java/io/PrintStream println (Ljava/lang/Object;)V

	return
.end method
