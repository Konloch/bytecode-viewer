.class public NaNTest
.super java/lang/Object

.method public static print : (D)V
    .code stack 10 locals 10
        getstatic java/lang/System out Ljava/io/PrintStream;
        dload_0
        invokevirtual Method java/io/PrintStream println (D)V
        return
    .end code
.end method

.method public static print : (F)V
    .code stack 10 locals 10
        getstatic java/lang/System out Ljava/io/PrintStream;
        fload_0
        invokevirtual Method java/io/PrintStream println (F)V
        return
    .end code
.end method

.method static public main : ([Ljava/lang/String;)V
    .code stack 10 locals 10

        ldc2_w +Infinity
        invokestatic Method NaNTest print (D)V
        ldc2_w -Infinity
        invokestatic Method NaNTest print (D)V
        ldc2_w +NaN
        invokestatic Method NaNTest print (D)V
        ldc2_w -NaN
        invokestatic Method NaNTest print (D)V

        ldc2_w +NaN<0x7ff0000000000001>
        invokestatic Method NaNTest print (D)V
        ldc2_w +NaN<0x7ff0123456789abc>
        invokestatic Method NaNTest print (D)V
        ldc2_w +NaN<0x7fffffffffffffff>
        invokestatic Method NaNTest print (D)V
        ldc2_w +NaN<0xFff0000000000001>
        invokestatic Method NaNTest print (D)V
        ldc2_w +NaN<0xFff0123456789abc>
        invokestatic Method NaNTest print (D)V
        ldc2_w +NaN<0xFfffffffffffffff>
        invokestatic Method NaNTest print (D)V

        ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

        ldc +Infinityf
        invokestatic Method NaNTest print (F)V
        ldc -Infinityf
        invokestatic Method NaNTest print (F)V
        ldc +NaNf
        invokestatic Method NaNTest print (F)V
        ldc -NaNf
        invokestatic Method NaNTest print (F)V

        ldc +NaN<0x7f800001>F
        invokestatic Method NaNTest print (F)V
        ldc +NaN<0x7f801234>F
        invokestatic Method NaNTest print (F)V
        ldc +NaN<0x7fffffff>f
        invokestatic Method NaNTest print (F)V
        ldc +NaN<0xff800001>F
        invokestatic Method NaNTest print (F)V
        ldc +NaN<0xff801234>F
        invokestatic Method NaNTest print (F)V
        ldc +NaN<0xFfffffff>f
        invokestatic Method NaNTest print (F)V

        return

    .end code
.end method
.end class