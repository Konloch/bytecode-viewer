// Originally created as a test for Krakatau (https://github.com/Storyyeller/Krakatau)

public class Synchronized {
	public static int x;

	public static void main(String[] a)
	{
	    try{
			synchronized(a){
				x = (1<<-1)/a.length;
			}
		} catch (Throwable t){
			x = 1;
		}

		int y = x+2;
		System.out.println(y);
	}
}