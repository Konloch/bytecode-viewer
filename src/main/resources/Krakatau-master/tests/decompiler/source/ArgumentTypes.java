// Originally created as a test for Krakatau (https://github.com/Storyyeller/Krakatau)
import java.util.*;

public class ArgumentTypes{

	public static int main(boolean b){
		return b ? 1 : 0;
	}

	public static boolean main(int x){
		return x <= 42;
	}

	public static char main(char x){
		return x ^= '*';
	}

	public static String main(Object x){
		if (x instanceof boolean[]){
			return Arrays.toString((boolean[])x);
		}
		else if (x instanceof String[]) {
		}
		else if (x instanceof int[]) {
			return "" + ((int[])x)[0];
		}
		else {
			return java.util.Arrays.toString((byte[])x);
		}
		return null;
	}

	public static void main(java.lang.String[] a)
	{
		int x = Integer.decode(a[0]);
		boolean y = Boolean.valueOf(a[1]);

		System.out.println(main(x));
		System.out.println(main(y));

		byte[] z = {1,2,3,45,6};
		boolean[] w = {false, true, false};
		Object[] v = a;
		CharSequence[] u = a;

		println(main(u));
		println(main(v));
		println(main(w));
		println(main(z));

		char c = 'C';
		System.out.println(c);
		System.out.println((int)c);

		for(byte b=0; b<=2; ++b) {
			foo(b); foo2(b);
		}
	}

	public static byte[] main(byte[][] x){
		if (x.length > 0) {
			return x[0];
		}
		return null;
	}

	public static Object main(CharSequence[] x)[] {
		if (x == null) {return null;}
		CharSequence[] y = new CharSequence[x.length + 1];
		System.arraycopy(x, 0, y, 1, x.length);
		y[0] = new StringBuffer(45).append((long)y.length).append(45);
		return y;
	}

	public static void foo(byte b) {print(b);}
	public static void foo2(byte b) {print(b != 0 ? 1 : 0);}
	public static void print(int i) {System.out.println(i);}

	protected static void println(Object x) {System.out.println(x);}
	protected static void println(Object[] x) {println(java.util.Arrays.deepToString(x));}
}