import java.util.*;

class A{
	int x;
}

public class exp {

	public static void main(String[] args) {
		A a = new A();
		A b = new A();
		a = b;

		b.x = 5;
		System.out.println(a.x+" "+b.x);
	}
}