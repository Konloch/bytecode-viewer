import java.lang.annotation.*;
import java.util.function.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Target({
	ElementType.ANNOTATION_TYPE,
	ElementType.CONSTRUCTOR,
	ElementType.FIELD,
	ElementType.LOCAL_VARIABLE,
	ElementType.METHOD,
	ElementType.PACKAGE,
	ElementType.PARAMETER,
	ElementType.TYPE,
	ElementType.TYPE_PARAMETER,
	ElementType.TYPE_USE
})
@Retention(RetentionPolicy.RUNTIME)
@interface A {
	double value() default 0.0/0.0;
}

@Target({
	ElementType.TYPE_USE
})
@A(0)
@interface B {
	@A(1) A value() default @A(2);
}

@A(3)
interface test<@A(4)T extends @A(5)Cloneable, @A(6)U> extends java.util.@A(7)Map<@A(8)U, @A(9)T> {
}

@A(10)
public class AnnotationsTest<@A(11) T> extends java.util.Stack<@A(12) T> implements @A(13) Cloneable, java.io.@A(14) Serializable {

	@A(15)
	public class Inner {
		@A(16)public Inner() {}
		@A(17)public <@A(18)T> Inner(@A(19)T t) {}
	}

	@A(20)
	static class Inner2 {
		@A(21)public Inner2() {}
		@A(22)public <@A(23)T2> Inner2(@A(24)T2 t) {}
	}

	@A(25)
	String foo() throws @A(26)RuntimeException, @A(27)Error {
		@A(28)String p = "::";
		@A(29)Object x = new @A(30)AnnotationsTest();
		@A(31)Object[] y = new @A(32)AnnotationsTest[0];
		@A(33)Map.@A(34)Entry<@A(35)String,@A(36)A> z = null;

		x = this.new @A(37)Inner();
		x = x instanceof @A(38)String;

		return (@A(39)String)p;
	}

	static <@A(40)T2> void foo(@A(41)T2 x) {}

	@A(43)
	static public void main(@A(44)String... args) {
		new @A(Double.NEGATIVE_INFINITY)AnnotationsTest().foo();
		java.util.Arrays.stream(args).forEach(@A(45)AnnotationsTest::<@A(46)String>foo);

		Function<@B() String, @A(47)AutoCloseable> newsr = @A(48)java.io.StringReader::new;
		try (
				@A(49)java.io.StringReader sr = new java.io.@A(50)StringReader("");
				@A(51)AutoCloseable sr2 = newsr.apply("");
			)
		{
			Object x;
			x = new @A(52)AnnotationsTest.@A(53)Inner[0];
			x = new AnnotationsTest.@A(54)Inner2();
			{ @A(55)Map.Entry<@A(56)?, @A(57)?> y = null; }
			{ @A(58)Map.Entry<Map.Entry<@A(59)?, @A(60)?>, @A(61)?> y = null; }
			{ @A(62)Map.Entry<Map.Entry<@A(63)?, AnnotationsTest.@A(64)Inner2>, AnnotationsTest.@A(65)Inner> y = null; }

		} catch (@A(66)Exception | @A(67)Error e) {} finally {
			@B(@A(-0.0)) Object x = adfs$adf;
			java.lang.System.out.println(x);
		}
	}

	final @A(68)Object adfsadf = "Hello!";
	static final @A(69)Object adfs$adf = "\"Hello!\"";

	public @A(70)String toString() {return null;}
	public @A(71)test<@A(72)Cloneable, @A(Double.NaN)test> toString2() {return null;}

}
