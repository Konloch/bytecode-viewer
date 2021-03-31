// Originally created as a test for Krakatau (https://github.com/Storyyeller/Krakatau)
public class Switch {
    strictfp public static void main(String args[]){
    	int x = -1;

    	x:switch(args.length % -5){
			case 3:
				x += 3;
			case 1:
				x--;

				switch(x) {
					case -2:
					if (x == -2){
						break x;
					}
				}
			case 0:
				switch((x == -1) ? 1 : 0) {
					default: break;
					case 1: System.out.println("Came from 0");
				}

				x += (x << x);
				break;
			case 2:
			default:
				x = x ^ (int)0xABCD000L;
			case 4:
				x *= 4;
				break;
		}

    	System.out.println(x);
    	System.out.println(i(args.length));
	}

	static public int i(int x){
		switch (x)	{
			case 2:
				x += 4;
			default:
				return -x;
			case 1: case 3:
				throw null;
		}
	}
}