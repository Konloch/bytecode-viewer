from . import objtypes

# common exception types
Arithmetic = objtypes.TypeTT('java/lang/ArithmeticException', 0)
ArrayOOB = objtypes.TypeTT('java/lang/ArrayIndexOutOfBoundsException', 0)
ArrayStore = objtypes.TypeTT('java/lang/ArrayStoreException', 0)
ClassCast = objtypes.TypeTT('java/lang/ClassCastException', 0)
MonState = objtypes.TypeTT('java/lang/IllegalMonitorStateException', 0)
NegArrSize = objtypes.TypeTT('java/lang/NegativeArraySizeException', 0)
NullPtr = objtypes.TypeTT('java/lang/NullPointerException', 0)
OOM = objtypes.TypeTT('java/lang/OutOfMemoryError', 0)
