import java.util.*;

class proc3{
	public static void main(String args[]){
		List l = new Cons();      // The List-object
		long i;
		Cons n = new Cons();
		n = (Cons) l;
		for(i=0;i<5;i++)
		{
			n.head = i;
			n.tail = new Cons();
			n = (Cons) n.tail;
		}
		n.tail = new Nil();
		n = (Cons) l;
		long startTime = System.currentTimeMillis();
		SumVisitor sv = new SumVisitor();
		n.accept(sv);
		// sv.visit(n);
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println(sv.sum+" "+totalTime);
	}
}

interface List {
	void accept(Visitor v);
}

interface Visitor {
	void visit(Nil x);
	void visit(Cons x);
}

class SumVisitor implements Visitor {
	long sum = 0;
	long diff = 0;
	public void visit(Nil x) {}
	public void visit(Cons x) {
		sum = sum + x.head;
		x.tail.accept(this);
	}
}

class Nil implements List {
	public void accept(Visitor v) {
		Nil n = new Nil();
		n = this;
		v.visit(this);
	}
}

class Cons implements List {
	long head;
	List tail;
	public void accept(Visitor v) {
		v.visit(this);
	}

	// public void accept(Visitor v,Object o){
	// }

	// public void dumy2(Object o){}
}