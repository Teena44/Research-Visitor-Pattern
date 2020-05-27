import java.util.*;

interface Item{
	// Item next;
	// public long cost = 0;
	// public Item next =  null;
	public int id = 0;
	// public void accept(visitor V);
	public int getmyId();
	// public Item getNext();
	// public long getCost();
}

class Vegetable implements Item{
	public final static int id = 12;
	public long cost;
	public Item next;
	public Vegetable(){
		// id = 6;
		cost = 1;
		next = new Nil();
	}
	// public void accept(visitor v){
	// 	v.visit(this);
	// }
	public long getCost(){
		return this.cost;
	}
	public int getmyId(){return id;}
}

class Fruit implements Item{
	public static final int id = 11;
	public long cost;
	public Item next;
	public Fruit(){
		// id = 5;
		cost = 1;
		next = new Nil();
	}
	// public void accept(visitor v){
	// 	v.visit(this);
	// }
	public long getCost(){
		return this.cost;
	}
	public int getmyId(){return id;}
}

class Food implements Item{
	public static final int id = 8;
	public Fruit f0;
	public Vegetable f1;
	public Item next;
	public Food(){
		// id = 2;
		f0 = new Fruit();
		f1 = new Vegetable();
		next = new Nil();
	}
	// public void accept(visitor v){
	// 	v.visit(this);
	// }
	public int getmyId(){return id;}
	// public long getCost(){
	// 	return this.cost;
	// }
}

class Shirt implements Item{
	public static final int id = 13;
	public long cost;
	public Item next;
	public Shirt(){
		// id = 7;
		cost = 1;
		next = new Nil();
	}
	// public void accept(visitor v){
	// 	v.visit(this);
	// }
	public long getCost(){
		return this.cost;
	}
	public int getmyId(){return id;}
}

class Pant implements Item{
	public static final int id = 14;
	public long cost;
	public Item next;
	public Pant(){
		// id = 8;
		cost = 1;
		next = new Nil();
	}
	// public void accept(visitor v){
	// 	v.visit(this);
	// }
	public long getCost(){
		return this.cost;
	}
	public int getmyId(){return id;}
}

class Apparel implements Item{
	public static final int id = 9;
	public Shirt f0;
	public Pant f1;
	public Item next;
	public Apparel(){
		// id = 3;
		f0 = new Shirt();
		f1 = new Pant();
		next = new Nil();
	}
	// public void accept(visitor v){
	// 	v.visit(this);
	// }

	public int getmyId(){return id;}
}

class Hat implements Item{
	public static final int id = 15;
	public long cost;
	public Item next;
	public Hat(){
		// id = 9;
		cost = 1;
		next = new Nil();
	}
	// public void accept(visitor v){
	// 	v.visit(this);
	// }
	public long getCost(){
		return this.cost;
	}
	public int getmyId(){return id;}
}

class Bag implements Item{
	public static final int id = 16;
	public long cost;
	public Item next;
	public Bag(){
		// id = 10;
		cost = 1;
		next = new Nil();
	}
	// public void accept(visitor v){
	// 	v.visit(this);
	// }
	public long getCost(){
		return this.cost;
	}
	public int getmyId(){return id;}
}

class Accessor implements Item{
	public static final int id = 10;
	public Hat f0;
	public Bag f1;
	public Item next;
	public Accessor(){
		// id = 4;
		f0 = new Hat();
		f1 = new Bag();
		next = new Nil();
	}
	// public void accept(visitor v){
	// 	v.visit(this);
	// }
	public Item getNext(){
		return this.next;
	}
	public int getmyId(){return id;}
	// public long getCost(){
	// 	return this.cost;
	// }
}

class Shop implements Item{
	public final static int id = 7;
	public Food f0;
	public Apparel f1;
	public Accessor f2;
	public Item next;
	public Shop(){
		// id = 1;
		f0 = new Food();
		f1 = new Apparel();
		f2 = new Accessor();
		next = new Nil();
		// System.out.println("creating Shop");
	}
	// public void accept(visitor v){
	// 	v.visit(this);
	// }
	public Item getNext(){
		return this.next;
	}
	public int getmyId(){return id;}
}

class Nil implements Item{
	final static public int id = 17;
	public Item next;
	// public void accept(visitor v){
	// 	v.visit(this);
	// }
	public Nil(){
		// id = 11;
	}
	public Item getNext(){
		return this.next;
	}
	public int getmyId(){return id;}
	// public long getCost(){
	// 	return this.cost;
	// }
}

interface ItemObj {}

class ShopObj implements ItemObj{}
class FoodObj implements ItemObj{}
class AccessorObj implements ItemObj{}
class ApparelObj implements ItemObj{}
class FruitObj implements ItemObj{}
class VegetableObj implements ItemObj{}
class ShirtObj implements ItemObj{}
class PantObj implements ItemObj{}
class HatObj implements ItemObj{}
class BagObj implements ItemObj{}
class NilObj implements ItemObj{}

class stackObj{
	Item x;
	int task;
	ItemObj O;
}

interface visitor{
	// public void visit(Shop n);
	// public void visit(Food n);
	// public void visit(Fruit n);
	// public void visit(Vegetable n);
	// public void visit(Apparel n);
	// public void visit(Shirt n);
	// public void visit(Pant n);
	// public void visit(Accessor n);
	// public void visit(Hat n);
	// public void visit(Bag n);
	// public void visit(Nil n);
}

class SumVisitor implements visitor{
	long total = 0;
	

	public void Translate(Shop N){
		int maxArr = 0;
		stackObj[] stack = new stackObj[50];
		int index = 0;
		// long startTime = System.currentTimeMillis();
		for(int i=0;i<50;i++)
			stack[i] = new stackObj();	//potential 
		// Stack<stackObj> stack = new Stack<stackObj>();
		// long endTime = System.currentTimeMillis();
		// long totalTime = endTime - startTime;
		// System.out.println("while loop "+totalTime);
		stackObj s = new stackObj();
		stack[index].x = N;
		stack[index].task = 0;
		stack[index].O = null;
		// stack.push(s);
		// index++;
		maxArr = index+1;
		s.x = stack[index].x;
		boolean flag = true;

		do{
			// boolean flag = true;
			// do{
				int switchId = (s.task) | ((s.x.getmyId()) & ((s.task==0)?Integer.MAX_VALUE:0)) ;
				// System.out.println(switchId);
//((1000+s.x.getmyId()) & (~s.task)) | (s.task);
				switch(switchId){
					
					case 1:{
						Fruit x = (Fruit) s.x;
						total = total + (x).getCost();
						//s.task = 0;
						s.x = (x).next;
						break;
					}
					case 2:{
						Vegetable x = (Vegetable) s.x;
						total = total + (x).getCost();
						//s.task = 0;
						s.x = (x).next;
						break;
					}
					case 3:{
						Shirt x = (Shirt) s.x;
						total = total + (x).getCost();
						//s.task = 0;
						s.x = (x).next;
						break;
					}
					case 4:{
						Pant x = (Pant) s.x;
						total = total + (x).getCost();
						//s.task = 0;
						s.x = (x).next;
						break;
					}
					case 5:{
						Hat x = (Hat) s.x;
						total = total + (x).getCost();
						//s.task = 0;
						s.x = (x).next;
						break;
					}
					case 6:{
						Bag x = (Bag) s.x;
						total = total + (x).getCost();
						//s.task = 0;
						s.x = (x).next;
						break;
					}
					case 7:{	//Shop
						// ShopObj O = new ShopObj();

						// stackObj p = new stackObj();
						Shop t = (Shop) s.x;
						stack[index++].x = t.next;
						// p.task = 0;
						// p.O = null;
						// stack.push(p);
						// stackObj q = new stackObj();
						stack[index++].x = t.f2;
						// q.task = 0;
						// q.O = null;
						// stack.push(q);
						// stackObj r = new stackObj();
						stack[index++].x = t.f1;
						// r.task = 0;
						// r.O = null;
						// stack.push(r);


						// stackObj u = new stackObj();
						s.x = t.f0;				//avoiding the last push i.e., the first call is not pushed but directly called
						//s.task = 0;
						// s.O = null;
						// stack.push(u);

						// flag = true;
						// if(maxArr < index)
						// 	maxArr = index;
						break;
					}
					case 8:{//Food
						// FoodObj O = new FoodObj();

						// stackObj p = new stackObj();
						Food t = (Food) s.x;
						stack[index++].x = t.next;
						// p.task = 0;
						// p.O = null;
						// stack.push(p);
						// stackObj q = new stackObj();
						stack[index++].x = t.f1;
						// q.task = 0;
						// q.O = null;
						// stack.push(q);
						
						// stackObj r = new stackObj();

						s.x = t.f0;
						//s.task = 0;
						// s.O = null;
						// stack.push(r);

						// flag = true;
						// if(maxArr < index)
						// 	maxArr = index;
						break;
					}
					case 9:{//Apparel
						// ApparelObj O = new ApparelObj();

						// stackObj p = new stackObj();
						Apparel t = (Apparel) s.x;
						stack[index++].x = t.next;//accessor
						// p.task = 0;
						// p.O = null;
						// stack.push(p);
						// stackObj q = new stackObj();
						stack[index++].x = t.f1;
						// q.task = 0;
						// q.O = null;
						// stack.push(q);
						
						// stackObj r = new stackObj();
						s.x = t.f0;
						//s.task = 0;
						// s.O = null;
						// stack.push(r);

						// flag = true;
						// if(maxArr < index)
						// 	maxArr = index;
						break;
					}
					case 10:{
						// AccessorObj O = new AccessorObj();

						// stackObj p = new stackObj();
						Accessor t = (Accessor) s.x;
						stack[index++].x = (t).next;
						// p.task = 0;
						// p.O = null;
						// stack.push(p);
						// stackObj q = new stackObj();
						stack[index++].x = t.f1;
						// q.task = 0;
						// q.O = null;
						// stack.push(q);
						
						// stackObj r = new stackObj();
						s.x = t.f0;
						//s.task = 0;
						// s.O = null;
						// stack.push(r);

						// flag = true;
						// if(maxArr < index)
						// 	maxArr = index;
						break;
					}
					case 11:{
						// FruitObj O = new FruitObj();
						
						Fruit x = (Fruit) s.x;					//inlined the code of case 1 of outer switch
						total = total + (x).getCost();
						//s.task = 0;
						s.x = (x).next;

						// s.x = s.x;
						// s.task = 1;
						// s.O = O;
						// stack.push(q);

						// flag = true;

						break;
					}
					case 12:{
						// VegetableObj O = new VegetableObj();

						Vegetable x = (Vegetable) s.x;			//inlined the code of case 2 of outer switch
						total = total + (x).getCost();
						//s.task = 0;
						s.x = (x).next;

						// s.x = s.x;
						// s.task = 2;
						// s.O = O;
						// stack.push(q);

						// flag = true;
						break;
					}
					case 13:{
						// ShirtObj O = new ShirtObj();

						Shirt x = (Shirt) s.x;
						total = total + (x).getCost();
						//s.task = 0;
						s.x = (x).next;
						
						// s.x = s.x;
						// s.task = 3;
						// s.O = O;
						// stack.push(q);

						// flag = true;
						break;
					}
					case 14:{
						// PantObj O = new PantObj();

						Pant x = (Pant) s.x;
						total = total + (x).getCost();
						//s.task = 0;
						s.x = (x).next;

						// s.x = s.x;
						// s.task = 4;
						// s.O = O;
						// stack.push(q);

						// flag = true;
						break;
					}
					case 15:{
						// HatObj O = new HatObj();

						Hat x = (Hat) s.x;
						total = total + (x).getCost();
						//s.task = 0;
						s.x = (x).next;

						// s.x = s.x;
						// s.task = 5;
						// s.O = O;
						// stack.push(q);

						// flag = true;
						break;
					}
					case 16:{
						// BagObj O = new BagObj();

						Bag x = (Bag) s.x;
						total = total + (x).getCost();
						//s.task = 0;
						s.x = (x).next;
						// s.x = s.x;
						// s.task = 6;
						// s.O = O;
						// stack.push(q);

						// flag = true;
						break;
					}
					case 17:{
						flag = true;
						if(index<=0)
							flag = false;
						else s.x = stack[--index].x;
						break;
					}
				}
			// }while(flag);
		}while(flag);
		// System.out.println(maxArr);
	}
}

class Shopping_iterative{
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
		Random r = new Random();
		boolean even = true;
		for(i=0;i<=1500000;i++)
		{
			if(i%6 == 0){
				// if(!flag){
				((Shop) n).next = new Shop();
				total = total+6;
				n = ((Shop) n).next;
				//System.out.println("Shop");

				((Shop) n).next = new Food();
				total = total+2;
				n = ((Shop) n).next;
				//System.out.println("Food");

				((Food) n).next = new Fruit();
				total++;
				n = ((Food) n).next;
				//System.out.println("Fruit");

				((Fruit) n).next = new Vegetable();
				total++;
				n = ((Fruit) n).next;
				//System.out.println("Vegetable");

				((Vegetable) n).next = new Apparel();
				total = total+2;
				n = ((Vegetable) n).next;
				//System.out.println("Apparel");

				((Apparel) n).next = new Shirt();
				total++;
				n = ((Apparel) n).next;
				//System.out.println("Shirt");

				((Shirt) n).next = new Pant();
				total++;
				n = ((Shirt) n).next;
				//System.out.println("Pant");

				((Pant) n).next = new Accessor();
				total = total+2;
				n = ((Pant) n).next;
				//System.out.println("Accessor");

				((Accessor) n).next = new Hat();
				total++;
				n = ((Accessor) n).next;
				//System.out.println("Hat");

				((Hat) n).next = new Bag();
				total++;
				n = ((Hat) n).next;
				//System.out.println("Bag");

				even = false;
			}
			else if(i%6 == 1){
				((Bag) n).next = new Shop();
				total+=6;
				n = ((Bag) n).next;
				//System.out.println("Shop");

				((Shop) n).next = new Shirt();
				total++;
				n = ((Shop) n).next;
				//System.out.println("Shirt");

				((Shirt) n).next = new Food();
				total = total+2;
				n = ((Shirt) n).next;
				//System.out.println("Food");

				((Food) n).next = new Pant();
				total++;
				n = ((Food) n).next;
				//System.out.println("Pant");

				((Pant) n).next = new Fruit();
				total++;
				n = ((Pant) n).next;
				//System.out.println("Fruit");

				((Fruit) n).next = new Accessor();
				total+=2;
				n = ((Fruit) n).next;
				//System.out.println("Accessor");

				((Accessor) n).next = new Vegetable();
				total++;
				n = ((Accessor) n).next;
				//System.out.println("Vegetable");

				((Vegetable) n).next = new Hat();
				total++;
				n = ((Vegetable) n).next;
				//System.out.println("Hat");

				((Hat) n).next = new Apparel();
				total+=2;
				n = ((Hat) n).next;
				//System.out.println("Apparel");

				((Apparel) n).next = new Bag();
				total++;
				n = ((Apparel) n).next;
				//System.out.println("Bag");

				even = true;
			}
			else if(i%6 == 2){

				((Bag) n).next = new Shirt();
				total = total+1;
				n = ((Bag) n).next;
				//System.out.println("Shirt");

				((Shirt) n).next = new Shop();
				total+=6;
				n = ((Shirt) n).next;
				//System.out.println("Shop");

				((Shop) n).next = new Pant();
				total++;
				n = ((Shop) n).next;
				//System.out.println("Pant");

				((Pant) n).next = new Food();
				total = total+2;
				n = ((Pant) n).next;
				//System.out.println("Food");

				((Food) n).next = new Accessor();
				total+=2;
				n = ((Food) n).next;
				//System.out.println("Accessor");

				((Accessor) n).next = new Fruit();
				total++;
				n = ((Accessor) n).next;
				//System.out.println("Fruit");

				((Fruit) n).next = new Hat();
				total++;
				n = ((Fruit) n).next;
				//System.out.println("Hat");

				((Hat) n).next = new Vegetable();
				total++;
				n = ((Hat) n).next;
				//System.out.println("Vegetable");
								
				((Vegetable) n).next = new Bag();
				total = total+1;
				n = ((Vegetable) n).next;
				//System.out.println("Bag");

				((Bag) n).next = new Apparel();
				total+=2;
				n = ((Bag) n).next;		
				//System.out.println("Apparel");						
			}
			else if(i%6 == 3){

				((Apparel) n).next = new Pant();
				total = total+1;
				n = ((Apparel) n).next;
				//System.out.println("Pant");

				((Pant) n).next = new Hat();
				total++;
				n = ((Pant) n).next;
				//System.out.println("Hat");

				((Hat) n).next = new Shirt();
				total++;
				n = ((Hat) n).next;
				//System.out.println("Shirt");

				((Shirt) n).next = new Accessor();
				total+=2;
				n = ((Shirt) n).next;
				//System.out.println("Accessor");

				((Accessor) n).next = new Bag();
				total++;
				n = ((Accessor) n).next;
				//System.out.println("Bag");

				((Bag) n).next = new Food();
				total = total+2;
				n = ((Bag) n).next;
				//System.out.println("Food");

				((Food) n).next = new Vegetable();
				total++;
				n = ((Food) n).next;
				//System.out.println("Vegetable");

				((Vegetable) n).next = new Fruit();
				total = total+1;
				n = ((Vegetable) n).next;
				//System.out.println("Fruit");

				((Fruit) n).next = new Apparel();
				total+=2;
				n = ((Fruit) n).next;
				//System.out.println("Apparel");

				((Apparel) n).next = new Shop();
				total+=6;
				n = ((Apparel) n).next;		
				//System.out.println("Shop");

			}
			else if(i%6 == 4){

				((Shop) n).next = new Fruit();
				total++;
				n = ((Shop) n).next;		
				//System.out.println("Shop");

				((Fruit) n).next = new Bag();
				total++;
				n = ((Fruit) n).next;
				//System.out.println("Bag");

				((Bag) n).next = new Hat();
				total = total+1;
				n = ((Bag) n).next;
				//System.out.println("Hat");

				((Hat) n).next = new Pant();
				total++;
				n = ((Hat) n).next;
				//System.out.println("Pant");

				((Pant) n).next = new Apparel();
				total+=2;
				n = ((Pant) n).next;
				//System.out.println("Apparel");

				((Apparel) n).next = new Accessor();
				total = total+2;
				n = ((Apparel) n).next;
				//System.out.println("Accessor");

				((Accessor) n).next = new Shirt();
				total++;
				n = ((Accessor) n).next;
				//System.out.println("Shirt");

				((Shirt) n).next = new Vegetable();
				total++;
				n = ((Shirt) n).next;
				//System.out.println("Vegetable");

				((Vegetable) n).next = new Food();
				total = total+2;
				n = ((Vegetable) n).next;
				//System.out.println("Food");				

				((Food) n).next = new Hat();
				total++;
				n = ((Food) n).next;
				//System.out.println("Hat");
			}
			else if(i%6 == 5){

				((Hat) n).next = new Food();
				total+=2;
				n = ((Hat) n).next;
				//System.out.println("Food");

				((Food) n).next = new Apparel();
				total+=2;
				n = ((Food) n).next;
				//System.out.println("Apparel");

				((Apparel) n).next = new Fruit();
				total = total+1;
				n = ((Apparel) n).next;
				//System.out.println("Fruit");

				((Fruit) n).next = new Shop();
				total+=6;
				n = ((Fruit) n).next;
				//System.out.println("Shop");

				((Shop) n).next = new Vegetable();
				total+=1;
				n = ((Shop) n).next;		
				//System.out.println("Shop");

				((Vegetable) n).next = new Pant();
				total = total+1;
				n = ((Vegetable) n).next;
				//System.out.println("Pant");

				((Pant) n).next = new Shirt();
				total++;
				n = ((Pant) n).next;
				//System.out.println("Shirt");

				((Shirt) n).next = new Bag();
				total+=1;
				n = ((Shirt) n).next;
				//System.out.println("Bag");

				((Bag) n).next = new Accessor();
				total = total+2;
				n = ((Bag) n).next;
				//System.out.println("Accessor");

				((Accessor) n).next = new Shop();
				total+=6;
				n = ((Accessor) n).next;
				//System.out.println("Shop");			
			}

			flag = true;
		}

		((Bag) n).next = new Nil();
		Shop d = (Shop) l;
		System.out.println("Expected: "+total);
		 // try{Thread.sleep(20500);}catch(InterruptedException e){System.out.println(e);} 
		long startTime = System.currentTimeMillis();
		SumVisitor sv = new SumVisitor();
		sv.Translate(d);
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Calculated: "+sv.total);
		System.out.println(totalTime);
	}
}

class myStack{
	stackObj[] Stack = new stackObj[100];
	int index;
	int size;

	public void myStack(){
		index = 0;
		size = 0;
	}
}