###preprocess###
.class public whocares
.super java/lang/Object

.const [mycls] = Class invokedynamic
.const [mymeth] = Method [mycls] mybsm (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
.const [2] = InvokeDynamic invokeStatic [mymeth] : whatever (I)V

###range(65535,):.bootstrap [bs:{}] = Bootstrap [1] :
###
.end class
