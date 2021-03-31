// Originally created as a test for Krakatau (https://github.com/Storyyeller/Krakatau)
import java.nio.channels.*;
import java.net.*;
import java.io.*;

public class TryCatchTest {
    static volatile boolean i2 = true;

    public static void main(String[] args)
    {
        try{
            int x = args.length;

            try{
                if (args[0].equals("bad") && i2){
                    throw new MalformedURLException(args[1] + args[1]);
                }

                if (args[0].equals("good") || ++x == 3){
                    throw new FileLockInterruptionException();
                }
            } catch (final MalformedURLException e) {
                throw e;
            } catch (Exception e) {
                Throwable t = new MalformedURLException(e.getClass().getName());
                Throwable t2 = e.initCause(t);
                throw (MalformedURLException)t;
            }

            System.out.println(x);
        } catch (IOException e){
            System.out.println(e);
        }

        test2(54); test2(0);
        test3(args);
        test3b(args);
        System.out.println(i);
        test4(args);
        try{ test5(); }catch(ClassCastException e) {}
    }

    static String x; static int i;
    public static void test2(int i) {
        String[] x = null;
        try {
            TryCatchTest.i = 54/i;
            TryCatchTest.x = x[0];
            System.out.println(x);}
        catch (RuntimeException e) {}
        catch (Throwable e) {x = new String[0];}

        try {
            TryCatchTest.i = 54/i;
            TryCatchTest.x = x[0] = "";
            System.out.println(x);}
        catch (RuntimeException e) {}
        catch (Throwable e) {x = new String[-1];}
    }

    Object z;
    public static void test3(Object x) {
        long j = 0;
        try {
            (new TryCatchTest()).z = x;
            i = 123456;
            i = (int)(123456L/j);
        }
        catch (Throwable e) {}
    }

    public static void test3b(Object[] x) {
        int i = 0;
        try {
            x[i] = x[i++];
            x[i] = x[i++];
            x[i] = x[i++];
            x[i] = x[i++];
            x[i] = x[i++];
            x[i] = x[i++];
            x[i] = x[i++];
            x[i] = x[i++];
            x[i] = x[i++];
            x[i] = x[i++];
            x[i] = x[i++];
        }
        catch (Throwable e) {}
        System.out.println(i);
    }

    public static void test4(Object x) {
        int y = 1;
        try{x = new int[-1];}catch(Throwable t){
        try{y = 52 % 0;}catch(Throwable t2){
        try{y = ((String)null).length();}catch(Throwable t3){
            System.out.println("test4 passed");
            return;
        }
        }
        }
        System.out.println("fail!");
    }

    public static void test5() {
        Number n = new Long(-1);
        if (n != null) {
            System.out.println((Integer)n);
        }
    }

    // This function was added because it breaks Procyon 0.5.25
    public static int bar()
    {
        while(true) {
            ltry:
            try {
                main(null);
                return 0;
            } catch (Throwable t) {
                t.printStackTrace();
                continue;
            } finally {
                int x = 0;
                break ltry;
            }
        }
    }
}