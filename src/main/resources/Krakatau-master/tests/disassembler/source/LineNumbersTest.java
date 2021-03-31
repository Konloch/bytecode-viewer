import java.util.*;

public class LineNumbersTest extends LinkedList<Object>{
    public LineNumbersTest(int x) {
        super((x & 0) == 1 ?
            new LinkedList<Object>((x & 1) == x++ ? new ArrayList<Object>() : new HashSet<Object>())
            :new HashSet<Object>());
        super.add(x = getLineNo());
        this.add(x = getLineNo());
    }

    static int getLineNo() {
        return new Throwable().fillInStackTrace().getStackTrace()[1].getLineNumber();
    }

    public static void main(String[] args) {
        System.out.println(getLineNo());
        System.out.println(new Throwable().fillInStackTrace().getStackTrace()[0].getFileName());
        System.out.println(getLineNo());

        System.out.println(new LineNumbersTest(2));
        List<Object> foo = new ArrayList<>();
        System.out.println(foo.addAll(foo));
    }
}
