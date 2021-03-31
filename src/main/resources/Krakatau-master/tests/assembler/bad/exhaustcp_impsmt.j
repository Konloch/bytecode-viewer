###preprocess###
.version 51 0
.class public [1]
.super [4]

.method public static [5] : [6]
    .attribute [7] .code stack 10 locals 10
L0:     getstatic [8]
L3:     aload_0
L4:     invokevirtual [9]
L7:     return
L8:
    .end code
.end method

.method public static [10] : [11]
    .attribute [7] .code stack 10 locals 10
L0:     new [12]
L3:     dup
L4:     aload_0
L5:     ldc [1]
L7:     ldc [2]
L9:     getstatic [13]
L12:    ldc [3]
L14:    invokestatic [14]
L17:    invokevirtual [15]
L20:    aload_2
L21:    invokevirtual [16]
L24:    invokespecial [17]
L27:    areturn
L28:
    .end code
.end method

.method public static [18] : [19]
    .attribute [7] .code stack 10 locals 10
    	.stack same
L0:     iconst_m1
L1:     invokedynamic [20]
L6:     return
L7:
    .end code
.end method
.attribute [21] .bootstrapmethods

.bootstrap [bs:0] = Bootstrap [22] :
.const [1] = Class [64]
.const [2] = String [5]
.const [3] = Class [63]
.const [4] = Class [62]
.const [5] = Utf8 print
.const [6] = Utf8 (Ljava/lang/Integer;)V
.const [7] = Utf8 Code
.const [8] = Field [57] [58]
.const [9] = Method [52] [53]
.const [10] = Utf8 mybsm
.const [11] = Utf8 (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
.const [12] = Class [51]
.const [13] = Field [46] [47]
.const [14] = Method [41] [42]
.const [15] = Method [36] [37]
.const [16] = Method [31] [32]
.const [17] = Method [12] [28]
.const [18] = Utf8 main
.const [19] = Utf8 ([Ljava/lang/String;)V
.const [20] = InvokeDynamic [bs:0] [25]
.const [21] = Utf8 BootstrapMethods
.const [22] = MethodHandle invokeStatic [23]
.const [23] = Method [1] [24]
.const [24] = NameAndType [10] [11]
.const [25] = NameAndType [26] [27]
.const [26] = Utf8 whatever
.const [27] = Utf8 (I)V
.const [28] = NameAndType [29] [30]
.const [29] = Utf8 <init>
.const [30] = Utf8 (Ljava/lang/invoke/MethodHandle;)V
.const [31] = Class [35]
.const [32] = NameAndType [33] [34]
.const [33] = Utf8 asType
.const [34] = Utf8 (Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;
.const [35] = Utf8 java/lang/invoke/MethodHandle
.const [36] = Class [40]
.const [37] = NameAndType [38] [39]
.const [38] = Utf8 findStatic
.const [39] = Utf8 (Ljava/lang/Class;Ljava/lang/String;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/MethodHandle;
.const [40] = Utf8 java/lang/invoke/MethodHandles$Lookup
.const [41] = Class [45]
.const [42] = NameAndType [43] [44]
.const [43] = Utf8 methodType
.const [44] = Utf8 (Ljava/lang/Class;Ljava/lang/Class;)Ljava/lang/invoke/MethodType;
.const [45] = Utf8 java/lang/invoke/MethodType
.const [46] = Class [50]
.const [47] = NameAndType [48] [49]
.const [48] = Utf8 TYPE
.const [49] = Utf8 Ljava/lang/Class;
.const [50] = Utf8 java/lang/Void
.const [51] = Utf8 java/lang/invoke/ConstantCallSite
.const [52] = Class [56]
.const [53] = NameAndType [54] [55]
.const [54] = Utf8 println
.const [55] = Utf8 (Ljava/lang/Object;)V
.const [56] = Utf8 java/io/PrintStream
.const [57] = Class [61]
.const [58] = NameAndType [59] [60]
.const [59] = Utf8 out
.const [60] = Utf8 Ljava/io/PrintStream;
.const [61] = Utf8 java/lang/System
.const [62] = Utf8 java/lang/Object
.const [63] = Utf8 java/lang/Integer
.const [64] = Utf8 exhaustion
###range(65, 65535):.const [{}] = Utf8 ''
###
.end class
