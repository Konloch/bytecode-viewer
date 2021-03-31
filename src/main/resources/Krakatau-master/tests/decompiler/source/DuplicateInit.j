; Originally created as a test for Krakatau (https://github.com/Storyyeller/Krakatau)
.class abstract super public DuplicateInit
.super java/lang/Object
.implements java/lang/CharSequence
.implements java/lang/Cloneable

.method static public synchronized main : ([Ljava/lang/String;)V
	.limit stack 19
	.limit locals 2

	iconst_0
	invokestatic Method DuplicateInit withCast (Z)V
	invokestatic Method DuplicateInit ifnonnull ()V

	; Test dead code
	goto LREALSTART
	nop
	ifnull LREALSTART
	nop

LREALSTART:

	new java/lang/Integer
	dup
LFOO:
	dup2
	astore_1
	;ifnull LFOO
	pop

	aload_0
	dup
	arraylength
	sipush 2
	if_icmpne LINT

	iconst_m1
	dup
	iushr

	aaload
	invokespecial java/lang/Integer <init> (Ljava/lang/String;)V
	goto_w LFINAL

LINT:
	arraylength
	iconst_5
	ishl

	invokespecial java/lang/Integer <init> (I)V

LFINAL:

	getstatic java/lang/System out Ljava/io/PrintStream;
	dup_x1
	aload_1
	invokevirtual java/io/PrintStream println (Ljava/lang/Object;)V
	invokevirtual java/io/PrintStream println (Ljava/lang/Object;)V

	return
.end method

.method static public withCast : (Z)V
	.code stack 3 locals 1
		new java/lang/Integer
		iload_0
		ifeq LLL
        dup
        ldc 2
        invokespecial Method java/lang/Integer <init> (I)V
        checkcast java/lang/System
        return

	LLL:
        dup
        ldc -1
        invokespecial Method java/lang/Integer <init> (I)V
		getstatic java/lang/System out Ljava/io/PrintStream;
		swap
		invokevirtual java/io/PrintStream println (Ljava/lang/Object;)V
        return

	.end code
.end method

.method public static ifnonnull : ()V
    .code stack 10 locals 1

        new java/lang/Integer
Ltemp:
        iconst_0
        ifeq Lrest
        goto Ltemp

Lrest:
        ifnonnull Lbranch
        ldc 'bad'
        astore_0
        goto Lend
Lbranch:
        ldc 'good'
        astore_0
Lend:
        getstatic Field java/lang/System out Ljava/io/PrintStream;
        aload_0
        invokevirtual Method java/io/PrintStream println (Ljava/lang/Object;)V
        return

    .end code
.end method

.end class
