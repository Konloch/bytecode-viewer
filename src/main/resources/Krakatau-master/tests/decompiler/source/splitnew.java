// Originally created as a test for Krakatau (https://github.com/Storyyeller/Krakatau)
public class splitnew {
    public static void main(String... args){
    	Number x = null;
    	int y = args.length;
    	while (y --> 0){
        	if (y%3 == 2){
    			break;
    		}
    	}
    	x = new Long(args[0]);
    	System.out.println(x);
    	System.out.println((byte)y);
	}
}