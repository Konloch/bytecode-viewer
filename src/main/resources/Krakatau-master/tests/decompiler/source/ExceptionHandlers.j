; Originally created as a test for Krakatau (https://github.com/Storyyeller/Krakatau)
.class public ExceptionHandlers
.super java/util/UnknownFormatFlagsException

.method public static main : ([Ljava/lang/String;)V
	.limit stack 111
	.limit locals 111

	aload_0
	iconst_0
	aaload

	invokestatic java/lang/Integer parseInt (Ljava/lang/String;)I
	dup
	dup_x1

	invokestatic ExceptionHandlers test (I)V
	invokestatic ExceptionHandlers test2 (I)V
	invokestatic ExceptionHandlers test3 (I)V

	return
.end method


.method public static test : (I)V
	.limit stack 111
	.limit locals 111

	.catch ExceptionHandlers from LBEGIN to LBEGIN2 using L7
	.catch java/util/UnknownFormatFlagsException from LBEGIN to LBEGIN2 using L6
	.catch java/util/IllegalFormatException from LBEGIN to LBEGIN2 using L5
	.catch java/lang/IllegalArgumentException from LBEGIN to LBEGIN2 using L4
	.catch java/lang/RuntimeException from LBEGIN to LBEGIN2 using L3
	.catch java/lang/Exception from LBEGIN to LBEGIN2 using L2
	.catch java/lang/Throwable from LBEGIN to LBEGIN2 using L1

LBEGIN:
	iload 0
	invokestatic ExceptionHandlers _throw (I)V
	return
LBEGIN2:

L1:
	ldc '1'
	invokestatic ExceptionHandlers _print (Ljava/lang/Object;)V
	goto LEND
L2:
	ldc '2'
	invokestatic ExceptionHandlers _print (Ljava/lang/Object;)V
	goto LEND
L3:
	ldc '3'
	invokestatic ExceptionHandlers _print (Ljava/lang/Object;)V
	goto LEND
L4:
	ldc '4'
	invokestatic ExceptionHandlers _print (Ljava/lang/Object;)V
	goto LEND
L5:
	ldc '5'
	invokestatic ExceptionHandlers _print (Ljava/lang/Object;)V
	goto LEND
L6:
	ldc '6'
	invokestatic ExceptionHandlers _print (Ljava/lang/Object;)V
	goto LEND
L7:
	ldc '7'
	invokestatic ExceptionHandlers _print (Ljava/lang/Object;)V

LEND:
	invokestatic ExceptionHandlers _print (Ljava/lang/Object;)V
	return
.end method

.method public static test2 : (I)V
	.limit stack 111
	.limit locals 111

	.catch ExceptionHandlers from LBEGIN to LBEGIN2 using L1
	.catch java/util/UnknownFormatFlagsException from LBEGIN to LBEGIN2 using L2
	.catch java/util/IllegalFormatException from LBEGIN to LBEGIN2 using L1
	.catch java/lang/IllegalArgumentException from LBEGIN to LBEGIN2 using L2
	.catch java/lang/RuntimeException from LBEGIN to LBEGIN2 using L1
	.catch java/lang/Exception from LBEGIN to LBEGIN2 using L2
	.catch java/lang/Throwable from LBEGIN to LBEGIN2 using L1

LBEGIN:
	iload 0
	invokestatic ExceptionHandlers _throw (I)V
	return
LBEGIN2:

L1:
	ldc '1'
	invokestatic ExceptionHandlers _print (Ljava/lang/Object;)V
	goto LEND
L2:
	ldc '2'
	invokestatic ExceptionHandlers _print (Ljava/lang/Object;)V

LEND:
	invokestatic ExceptionHandlers _print (Ljava/lang/Object;)V
	return
.end method

.method public static test3 : (I)V
	.limit stack 111
	.limit locals 111

	.catch ExceptionHandlers from LBEGIN to LBEGIN2 using L4
	.catch java/util/UnknownFormatFlagsException from LBEGIN to LBEGIN2 using L6
	.catch java/util/UnknownFormatFlagsException from LBEGIN to LBEGIN2 using L6
	.catch java/lang/RuntimeException from LBEGIN to LBEGIN2 using L3
	.catch java/util/IllegalFormatException from LBEGIN to LBEGIN2 using L5
	.catch java/lang/IllegalArgumentException from LBEGIN to LBEGIN2 using L4
	.catch java/lang/RuntimeException from LBEGIN to LBEGIN2 using L3
	.catch java/lang/Exception from LBEGIN to LBEGIN2 using L2
	.catch [0] from LBEGIN to LBEGIN2 using L2
	.catch java/lang/Throwable from LBEGIN to LBEGIN2 using L1
	.catch java/util/IllegalFormatException from LBEGIN to LBEGIN2 using L2
	.catch [0] from LBEGIN to LBEGIN2 using LEND

LBEGIN:
	iload 0
	invokestatic ExceptionHandlers _throw (I)V
	return
LBEGIN2:

L1:
	ldc '1'
	invokestatic ExceptionHandlers _print (Ljava/lang/Object;)V
	goto LEND
L2:
	ldc '2'
	invokestatic ExceptionHandlers _print (Ljava/lang/Object;)V
	goto LEND
L3:
	ldc '3'
	invokestatic ExceptionHandlers _print (Ljava/lang/Object;)V
	goto LEND
L4:
	ldc '4'
	invokestatic ExceptionHandlers _print (Ljava/lang/Object;)V
	goto LEND
L5:
	ldc '5'
	invokestatic ExceptionHandlers _print (Ljava/lang/Object;)V
	goto LEND
L6:
	ldc '6'
	invokestatic ExceptionHandlers _print (Ljava/lang/Object;)V

LEND:
	invokestatic ExceptionHandlers _print (Ljava/lang/Object;)V
	return
.end method



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

.method public <init> : ()V
	.limit stack 111
	.limit locals 111

	aload_0
	ldc ''
	invokespecial java/util/UnknownFormatFlagsException <init> (Ljava/lang/String;)V
	return
.end method

.method public static _throw : (I)V
.throws java/lang/Throwable
	.limit stack 111
	.limit locals 111

	iload_0
	tableswitch 0
		L1
		L2
		L2b
		L3
		L3b
		L4
		L4b
		L5b
		L6
		L6b
		L7
		default : LDEF

L1:
    new java/lang/Throwable
    dup
    invokespecial java/lang/Throwable <init> ()V
    goto LEND
L2:
    new java/lang/Exception
    dup
    invokespecial java/lang/Exception <init> ()V
    goto LEND
L3:
    new java/lang/RuntimeException
    dup
    invokespecial java/lang/RuntimeException <init> ()V
    goto LEND
L4:
    new java/lang/IllegalArgumentException
    dup
    invokespecial java/lang/IllegalArgumentException <init> ()V
    goto LEND
L6:
    new java/util/UnknownFormatFlagsException
    dup
    ldc ''
    invokespecial java/util/UnknownFormatFlagsException <init> (Ljava/lang/String;)V
    goto LEND
L2b:
    new java/lang/Error
    dup
    invokespecial java/lang/Error <init> ()V
    goto LEND
L3b:
    new java/io/IOException
    dup
    invokespecial java/io/IOException <init> ()V
    goto LEND
L4b:
    new java/lang/ArrayStoreException
    dup
    invokespecial java/lang/ArrayStoreException <init> ()V
    goto LEND
L5b:
    new java/nio/charset/UnsupportedCharsetException
    dup
    ldc ''
    invokespecial java/nio/charset/UnsupportedCharsetException <init> (Ljava/lang/String;)V
    goto LEND
L6b:
    new java/util/DuplicateFormatFlagsException
    dup
    ldc ''
    invokespecial java/util/DuplicateFormatFlagsException <init> (Ljava/lang/String;)V
    goto LEND
L7:
    new ExceptionHandlers
    dup
    invokespecial ExceptionHandlers <init> ()V
    goto LEND
LDEF:
	aconst_null
	goto LEND

LEND:
	athrow
.end method



.method static _print : (Ljava/lang/Object;)V
	.limit stack 111
	.limit locals 111

    getstatic java/lang/System out Ljava/io/PrintStream;
    aload_0
    invokevirtual java/io/PrintStream println (Ljava/lang/Object;)V
	return
.end method
