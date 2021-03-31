// Originally created as a test for Krakatau (https://github.com/Storyyeller/Krakatau)
public class WhileLoops {
	static int x;

	public static void main(String[] a)
	{
		boolean y = a.length > 0;
		boolean z = a.length < 2;
		x = 42;

	    while(1==1){
	    	x++;

			if (x <= 127){
				if (y ^ z){
					//x = y ? 4 : z ? x%7 : 127;
					x = a.length ^ x;
					break;
				}
				else{
					y = y & true;
					z = z | false;
					continue;
				}
			}
			x = ~(~x) >>> 3L;
			break;
		}

		System.out.println(x);
		System.out.println(y);
		System.out.println(z);
		try{
			main(a[0]);
		} catch (IllegalArgumentException x) {}

		viod();
	}

	static int i,i2;

	private static int foo(){
		--i;
		return 0x1111;
	}


	private static void main(String a)
	{
		int xx = java.lang.Integer.valueOf(a);
		while(true){
			if (i2 < 0) {
				if (xx > 1111 && i > foo()) {
					continue;
				}

				++i;
				break;
			}
			else {
				L0: {
					L1: {
						i = xx;
						if (++i == 10) {break L0;}
						if (++i == 20) {break L1;}
						if (++i == 30) {break L0;}
						if (++i == 50) {break L1;}
					}
					i2 = i - (i * i);
					continue;
				}
				break;
			}
		}

		System.out.println(i);
	}

	protected static void viod() {
		int x = 1337;
		int y = 0;

		while(x >= 0) {
			if(x == 0) {
				y = 1;
				x = 0;
			}
			if (y != 0) {
				if (x == 0) {x = 0;} else {y = 0;}
				System.out.println(y + ',' + y);
				x = -40;
			}

			x--;
		}
	}
}