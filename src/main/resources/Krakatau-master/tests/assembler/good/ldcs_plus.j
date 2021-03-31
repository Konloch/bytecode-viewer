.version +50 0x0
.class public LdcTest
.super java/lang/Object

.method public static print : (F)V
    .code stack 910 locals 10
        ldc 0
        ldc -0
        ldc +0
        ldc +1
        ldc +0x1
        ldc -0x1
        ldc -10e1f
        ldc +10e1f
        ldc +0.0f
        ldc -0.0f
        ldc -0x80000000
        ldc -0x7FFFFFFF
        ldc +0x7FFFFFFF

        ldc2_w 0L
        ldc2_w -0L
        ldc2_w +0L
        ldc2_w +1L
        ldc2_w +0x1L
        ldc2_w -0x1L
        ldc2_w -10e1
        ldc2_w +10e1
        ldc2_w +0.0
        ldc2_w -0.0
        ldc2_w -0x8000000000000000L
        ldc2_w -0x7FFFFFFFffffffffL
        ldc2_w +0x7FFFFFFFffffffffL


        return
    .end code
.end method
.end class