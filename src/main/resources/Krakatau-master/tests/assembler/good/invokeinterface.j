.version 47 0
.class public super invokeinterface
.super java/lang/Object

.method public static varargs main : ([Ljava/lang/String;)V
    .code stack 94 locals 2
        aconst_null
        aconst_null
        dup2
        dup2
        dup2
        dup2
        dup2

        invokeinterface InterfaceMethod java/util/function/LongUnaryOperator applyAsLong (J)J
        invokeinterface InterfaceMethod [luo] applyAsLong (J)J

        invokeinterface InterfaceMethod java/util/function/LongUnaryOperator applyAsLong [jj] 3
        invokeinterface InterfaceMethod [luo] [nat] 3
        invokeinterface [im] 3


        return
    .end code
.end method

.const [desc] = Utf8 (J)J
.const [jj] = [desc]
.const [luo] = Class java/util/function/LongUnaryOperator
.const [aal] = Utf8 applyAsLong
.const [nat] = NameAndType applyAsLong (J)J
.const [im] = InterfaceMethod [luo] [nat]

