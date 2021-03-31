// Originally created as a test for Krakatau (https://github.com/Storyyeller/Krakatau)
public class BoolizeTest{

	static void main(boolean x, int y) {}
	static void main(boolean[] x, byte[] y) {}

	public static void main(String[] args){
		main(false, 0);
		main(null, null);
		test(new byte[2][2]);
	}

	static void test(byte[][] bbb) {
		byte b = bbb[0][0];
		bbb[1][1] = b;
	}
}