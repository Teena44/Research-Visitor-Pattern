import java.util.*;

interface Item{
	// Item next;
	// public long cost = 0;
	// public Item next =  null;
	public void accept(visitor V);
	public Item getNext();
	// public long getCost();
}

class Vegetable implements Item{
	public long cost;
	public Item next;
	public Vegetable(){
		cost = 1;
		next = new Nil();
	}
	public void accept(visitor v){
		v.visit(this);
	}
	public long getCost(){
		return this.cost;
	}
	public Item getNext(){
		return this.next;
	}
}

class Fruit implements Item{
	public long cost;
	public Item next;
	public Fruit(){
		cost = 1;
		// System.out.println("creating Fruit");
		next = new Nil();
	}
	public void accept(visitor v){
		v.visit(this);
	}
	public long getCost(){
		return this.cost;
	}
	public Item getNext(){
		return this.next;
	}
}

class Food implements Item{
	public Fruit f0;
	public Vegetable f1;
	public Item next;
	public Food(){
		f0 = new Fruit();
		f1 = new Vegetable();
		next = new Nil();
		// System.out.println("creating Food");
	}
	public void accept(visitor v){
		v.visit(this);
	}
	public Item getNext(){
		return this.next;
	}
	// public long getCost(){
	// 	return this.cost;
	// }
}

class Shirt implements Item{
	public long cost;
	public Item next;
	public Shirt(){
		cost = 1;
		next = new Nil();
	}
	public void accept(visitor v){
		v.visit(this);
	}
	public long getCost(){
		return this.cost;
	}
	public Item getNext(){
		return this.next;
	}
}

class Pant implements Item{
	public long cost;
	public Item next;
	public Pant(){
		cost = 1;
		next = new Nil();
	}
	public void accept(visitor v){
		v.visit(this);
	}
	public long getCost(){
		return this.cost;
	}
	public Item getNext(){
		return this.next;
	}
}

class Apparel implements Item{
	public Shirt f0;
	public Pant f1;
	public Item next;
	public Apparel(){
		f0 = new Shirt();
		f1 = new Pant();
		next = new Nil();
	}
	public void accept(visitor v){
		v.visit(this);
	}
	public Item getNext(){
		return this.next;
	}
	// public long getCost(){
	// 	return this.cost;
	// }
}

class Hat implements Item{
	public long cost;
	public Item next;
	public Hat(){
		cost = 1;
		next = new Nil();
	}
	public void accept(visitor v){
		v.visit(this);
	}
	public long getCost(){
		return this.cost;
	}
	public Item getNext(){
		return this.next;
	}
}

class Bag implements Item{
	public long cost;
	public Item next;
	public Bag(){
		cost = 1;
		next = new Nil();
	}
	public void accept(visitor v){
		v.visit(this);
	}
	public long getCost(){
		return this.cost;
	}
	public Item getNext(){
		return this.next;
	}
}

class Accessor implements Item{
	public Hat f0;
	public Bag f1;
	public Item next;
	public Accessor(){
		f0 = new Hat();
		f1 = new Bag();
		next = new Nil();
	}
	public void accept(visitor v){
		v.visit(this);
	}
	public Item getNext(){
		return this.next;
	}
	// public long getCost(){
	// 	return this.cost;
	// }
}

class Shop implements Item{
	public Food f0;
	public Apparel f1;
	public Accessor f2;
	public Item next;
	public Shop(){
		f0 = new Food();
		f1 = new Apparel();
		f2 = new Accessor();
		next = new Nil();
		// System.out.println("creating Shop");
	}
	public void accept(visitor v){
		v.visit(this);
	}
	public Item getNext(){
		return this.next;
	}
	// public long getCost(){
	// 	return this.cost;
	// }
}

class Nil implements Item{
	public Item next;
	public void accept(visitor v){
		v.visit(this);
	}
	public Item getNext(){
		return this.next;
	}
	// public long getCost(){
	// 	return this.cost;
	// }
}

interface visitor{
	public void visit(Shop n);
	public void visit(Food n);
	public void visit(Fruit n);
	public void visit(Vegetable n);
	public void visit(Apparel n);
	public void visit(Shirt n);
	public void visit(Pant n);
	public void visit(Accessor n);
	public void visit(Hat n);
	public void visit(Bag n);
	public void visit(Nil n);
}

class SumVisitor implements visitor{
	long total = 0;

	public void visitDummy(){
		Nil[] stack = new Nil[100];
		int i = 0;
		do{
			stack[i] = new Nil();
			i++;
		}while(i>10);

		switch(i){
			case 0:{
				System.out.println("0");
				break;
			}
			case 1:{
				System.out.println("1");
				break;
			}
			case 2:{
				System.out.println("2");
				break;
			}
		}
	}

	public void visit(Shop n){
		// System.out.println(n.f0+" is here");
		n.f0.accept(this);
		n.f1.accept(this);
		n.f2.accept(this);
		n.next.accept(this);
		System.out.println("Shop");
	}

	public void visit(Food n){
		n.f0.accept(this);
		n.f1.accept(this);
		n.next.accept(this);
		System.out.println("Food");
	}

	public void visit(Fruit n){
		total = total+n.getCost();
		n.next.accept(this);
		System.out.println("Fruit");
	}

	public void visit(Vegetable n){
		total = total+n.getCost();
		// System.out.println("there "+total);
		n.next.accept(this);
		System.out.println("Vegetable");
	}

	public void visit(Apparel n){
		// System.out.println("Am in Apparel");

		n.f0.accept(this);
		n.f1.accept(this);
		n.next.accept(this);
		System.out.println("Apparel");
	}

	public void visit(Shirt n){
		// System.out.println("Am in Shirt");

		total = total+n.getCost();
		n.next.accept(this);
		System.out.println("Shirt");
	}

	public void visit(Pant n){
		// System.out.println("Am in Pant");

		total = total+n.getCost();
		n.next.accept(this);
		System.out.println("Pant");
	}

	public void visit(Accessor n){
		// System.out.println("Am in Accessor");

		n.f0.accept(this);
		n.f1.accept(this);
		n.next.accept(this);
		System.out.println("Accessor");
	}

	public void visit(Hat n){
		// System.out.println("Am in Hat");

		total = total+n.getCost();
		n.next.accept(this);
		System.out.println("Hat");
	}

	public void visit(Bag n){
		// System.out.println("Am in Bag");
		total = total+n.getCost();
		n.next.accept(this);
		System.out.println("Bag");
	}

	public void visit(Nil n){
		// System.out.println("Shop done");
	}
}

class Shopping{
	public static void main(String args[]){
		Item l = new Shop();      
		long i;
		// Item n = new Shop();
		long total = 0;

		Item n = new Shop();
		n = (Shop) l;
		total = total+6;
		// n = (Shop) n.next;
		// Item d;
		// d = n;
		// n = new Item();
		boolean flag = false;
		for(i=2;i<=1900000;i++)
		{
			if(!flag){
				((Shop) n).next = new Shop();
				total = total+6;
				n = ((Shop) n).next;
			}
			else{
				((Bag) n).next = new Shop();
				total = total+6;
				n = ((Bag) n).next;
			}

			((Shop) n).next = new Food();
			total = total+2;
			n = ((Shop) n).next;

			((Food) n).next = new Fruit();
			total++;
			n = ((Food) n).next;

			((Fruit) n).next = new Vegetable();
			total++;
			n = ((Fruit) n).next;

			((Vegetable) n).next = new Apparel();
			total = total+2;
			n = ((Vegetable) n).next;

			((Apparel) n).next = new Shirt();
			total++;
			n = ((Apparel) n).next;

			((Shirt) n).next = new Pant();
			total++;
			n = ((Shirt) n).next;

			((Pant) n).next = new Accessor();
			total = total+2;
			n = ((Pant) n).next;

			((Accessor) n).next = new Hat();
			total++;
			n = ((Accessor) n).next;

			((Hat) n).next = new Bag();
			total++;
			n = ((Hat) n).next;

			flag = true;
		}

		((Bag) n).next = new Nil();
		Shop d = (Shop) l;
		System.out.println("Expected: "+total);
		long startTime = System.currentTimeMillis();
		SumVisitor sv = new SumVisitor();
		d.accept(sv);
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Calculated: "+sv.total);
		System.out.println(totalTime);
	}
}