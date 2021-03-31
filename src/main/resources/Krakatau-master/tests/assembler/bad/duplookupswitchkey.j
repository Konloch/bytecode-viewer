.class public whocares
.super java/lang/Object

.method public static print : (F)V
    .code stack 10 locals 10
L1: nop
L2:
        lookupswitch
            1 : L1
            2 : L2
            1 : L2
            default : LFOO
LFOO:
        return
    .end code
.end method
.end class
