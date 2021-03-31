import java.lang.reflect.*;

public class MethodParametersTest {
    public static void main(String[] args) throws Throwable  {
        (new Object() {
            String ident(String foo, final double d, String[] bar)[] throws Throwable  {
                class Nested {
                    void main3(final int foo, String... args) throws Throwable {
                        System.out.println(args.length == bar.length);
                        Method m = Nested.class.getDeclaredMethods()[0];

                        while (m != null) {
                            System.out.println(m.getName());
                            System.out.println(m.getParameterCount());
                            for (Parameter p: m.getParameters()) {
                                System.out.println(p);
                            }

                            m = m.getDeclaringClass().getEnclosingMethod();
                        }
                    }
                }

                new Nested().main3(0, args);
                return bar;
            }
        }).ident("Test", 5.666, args);
    }
}
