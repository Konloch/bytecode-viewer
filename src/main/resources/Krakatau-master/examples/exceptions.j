.class public exceptions
.super java/lang/Object

.method public static main : ([Ljava/lang/String;)V
    .limit stack 10
    .limit locals 10


    ldc "Hello "                                                                        
    aload_0                                                                             
    iconst_0 

LTRY: 
    aaload 
LTRY2:
    .catch java/lang/ArrayIndexOutOfBoundsException from LTRY to LTRY2 using LCATCH

    ldc "!"                                                                             

    invokevirtual java/lang/String concat (Ljava/lang/String;)Ljava/lang/String;        
    invokevirtual java/lang/String concat (Ljava/lang/String;)Ljava/lang/String;      
    
    astore_1       
    goto LPRINT    

LCATCH:
    pop
    ldc "Please enter your name"
    astore_1

LPRINT:
    getstatic java/lang/System out Ljava/io/PrintStream;                                
    aload_1
    invokevirtual java/io/PrintStream println (Ljava/lang/Object;)V                     
    return
.end method