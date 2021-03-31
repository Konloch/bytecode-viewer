// Originally created as a test for Krakatau (https://github.com/Storyyeller/Krakatau)
abstract public class floattest {
	static final float e = 2.7182818459f;
	static double d = 2.7182818459;

	static double x = -1./.0;
	static double y = -5e-324d;
	static float z = 700649232162408535461864791644958065640130970938257885878534141944895541342930300743319094181060791015626E-150f;

	final static float Float = 0X.0P0f/-0X0p-0f;
	static float __ = 5.25E1F;

	strictfp public static void main(String[] args)
	{
		floattest self = null; x++;

		double t = (double)(1L << 53);
		double x = t*t;
		double y = (double)(-1L >>> 11);
		double z = x % y;
	    System.out.println(z);
	    System.out.println(1.0 == z);
	    System.out.println(z*e);
	    System.out.println(z*d);

	    System.out.println(self.x);
	    System.out.println(self.y);
	    System.out.println(self.z);
	    System.out.println((double)self.z);

	    x = 1.23;
	    System.out.println(Float);
	    System.out.println(-.0F);
	    System.out.println(-.0);
	    System.out.println(1e29f);
	    System.out.println(129f);
	    System.out.println(__);
	    System.out.println(x);
	    System.out.println(x == (__ = (float)x));
	    System.out.println(3.14159f);
	    double __ = __ = 0x1.8ap50;
	    double floattest = -1.0/.0;

	    if ((__ >= 0x1.8ap50) & (__ <= 0x1.8ap50)) {
	    	System.out.println((self.x%x));
	    }

	    System.out.println(-floattest);
	    floattest = -0.0;
	    System.out.println(-floattest);
	    System.out.println(__);
	    System.out.println(self.__);
	    System.out.println((float)(null==self?1.00000017881393421514957253748434595763683319091796875001f:.0f));
	    System.out.println((float)(null==self?1.00000017881393421514957253748434595763683319091796875001d:.0d));
	}

	abstract void classIsAbstract();
}