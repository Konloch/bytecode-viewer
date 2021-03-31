// Originally created as a test for Krakatau (https://github.com/Storyyeller/Krakatau)
public class ConditionalTest{

	public static void main(String[] args){
		short s;
		float f = -4.2f;

		test(true);
		test(false);
		test((s = -42));
		test(f = f);

		testLabels(0, 0);
		testLabels(0, 2);
		testLabels(2, 2);
		testLabels(2, 3);
		testLabels(3, 3);
		testLabels(1, 1);
	}

	static void test(boolean b) {
		long j;
		int i, k;
		char c;
		byte bb;
		boolean z;
		double f,g = 5_5_5;

		if (b) {
			j = 77 * (i = 7 ^ (c = '1'));
			bb = (byte)(f = (float)j);
			g = 0x1234p567;
			z = true;
			k = 1;
		} else {
			j = 077 * (i = 07 ^ (c = '\1'));
			bb = (byte)(f = (float)j);
			g = 0xfdecbap-567;
			z = false;
			k = 0;
		}

		System.out.println(j);
		System.out.println(i);
		System.out.println(k);
		System.out.println(c);
		System.out.println(bb);
		System.out.println(z);
		System.out.println(f);
		System.out.println(g);
	}

	static void test(long j) {
		System.out.println(~j);
	}

	static void test(double j) {
		System.out.println(j - 0x1p-51);
		System.out.println(j - 0x1p-52);
	}

	static void testLabels(int arg1, int arg2) {
		System.out.print("x0");
		boolean b;
        label5: {
            label2: {
                label0: {
                    label1: {
                        label3: {
                            label4: {
                                if (arg1 >= 2)
                                {
                                    break label4;
                                }
                                if (foo(arg2, 2))
                                {
                                    break label3;
                                }
                                {
                                    break label2;
                                }
                            }
                            if (foo(arg2 & 1, 1))
                            {
                                break label1;
                            }
                            if (foo(arg1 & 1, 1))
                            {
                                break label0;
                            }
                        }
                        b = false;
                        break label5;
                    }
                    System.out.print("x1");
                    b = false;
                    break label5;
                }
                System.out.print("x2");
                b = true;
                break label5;
            }
            System.out.print("x3");
            b = true;
        }
        System.out.print("x4");
        System.out.println(b);
	}

	static boolean foo(int x, int y) {return x < y;}

}\u001a