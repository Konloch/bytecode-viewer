// Originally created as a test for Krakatau (https://github.com/Storyyeller/Krakatau)
public class NullInference {
    public static void main(String args[]) throws Throwable{
    	String s = null;
    	String s2 = null;

        try{
        	try{
	        	s2 = args[0];

				if (args == null){
					throw (Exception)(Object)args;
				}

				s = args[1];
			} catch (ArrayIndexOutOfBoundsException e){
			}

		} catch (Throwable t){
			System.out.println(t instanceof NullPointerException);
			throw t;
		}

		System.out.println(s2);
		System.out.println(s);
    }
}