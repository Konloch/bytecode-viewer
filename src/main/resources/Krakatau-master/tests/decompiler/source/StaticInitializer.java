// Originally created as a test for Krakatau (https://github.com/Storyyeller/Krakatau)
public class StaticInitializer {
	public static final boolean b = true;

	static {
		for(char c = 66; c < 70; ++c){
			System.out.println((int)c);
			System.out.println(c);
		}
	}

	public static void main(String[] a)
	{
		System.out.println(b & b);
	}
}