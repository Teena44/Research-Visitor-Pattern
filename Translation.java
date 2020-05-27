// import java.util.Map;
// import java.util.List;
// import java.util.ArrayList;
// import java.util.Iterator;
// import java.util.Collection;

import soot.*;
import java.util.*;
import javafx.util.Pair;
import soot.jimple.spark.*;
import soot.jimple.*;
import soot.options.Options;
import soot.util.*;
import java.io.*;
import java.*;
import java.lang.invoke.LambdaMetafactory;
import soot.jimple.Jimple;
import soot.dava.internal.SET.SETNodeLabel;

import soot.Body;
import soot.SootClass;
import soot.PackManager;
import soot.PatchingChain;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Transform;
import soot.Unit;
import soot.Type;
import soot.ArrayType;
import soot.PatchingChain;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.typing.fast.UseChecker;
import soot.jimple.toolkits.callgraph.Edge;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.tagkit.GenericAttribute;
import soot.jimple.InvokeExpr;
import soot.jimple.internal.JAssignStmt;
import soot.util.Chain;
import soot.jimple.internal.JGotoStmt;
import soot.jimple.internal.JTableSwitchStmt;
import soot.jimple.internal.JimpleLocal;
import soot.jimple.internal.JNopStmt;
import soot.jimple.internal.JDynamicInvokeExpr;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.AbstractInstanceInvokeExpr;
import soot.jimple.toolkits.annotation.logic.LoopFinder;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.toolkits.graph.LoopNestTree;
import soot.jimple.internal.JNewExpr;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JStaticInvokeExpr;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.internal.JArrayRef;
import soot.jimple.internal.JLtExpr;
import soot.jimple.internal.JGeExpr;
import soot.jimple.internal.JAddExpr;
import soot.jimple.internal.JNewMultiArrayExpr;
import soot.jimple.internal.JNewArrayExpr;
import soot.jimple.internal.JIfStmt;
import soot.jimple.internal.JReturnStmt;
import soot.jimple.internal.JIdentityStmt;

public class Translation extends SceneTransformer {
	static CallGraph cg;
	public HashMap<String,SootClass> acceptClass;
	public HashMap<String, SootClass> visitableClassCorressVisit;
	public List<SootMethod> visitMethods;
	public List<SootMethod> acceptMethods;
	public List<Unit> switchAcceptCalls;
	public SootClass itemInterface;
	public HashMap<Unit, SootMethod> switchCasesCorresVisit;
	public HashMap<SootMethod, Local> visitableObjCorresVisit;					//stores the visitable object corresponding to each visit method for local variables
	// public SootClass interfaceClass;
	public SootClass stackObjClass;
	// public SootClass acceptinterface;
	// public SootClass closureInterface;
	// public SootClass Translate;
	public SootMethod getMyIdAbstractMethod;
	// public Local closureObj;
	public List<SootClass> writeToFileClasses;						//all the classes that need to be written
	public int varIndex;

	@Override
	protected void internalTransform(String arg0, Map<String, String> arg1)  {
		cg = Scene.v().getCallGraph();

		// Container.getInstance().Globals = new ArrayList<SootField>(); 
		varIndex = 0;
		acceptClass = new HashMap<String, SootClass>();
		acceptMethods = new ArrayList<SootMethod>();
		acceptMethods = Container.getInstance().accept;
		visitableClassCorressVisit = new HashMap<String, SootClass>();
		switchCasesCorresVisit = new HashMap<Unit, SootMethod>();
		visitableObjCorresVisit = new HashMap<SootMethod, Local>();

		switchAcceptCalls = new ArrayList<Unit>();

		for(int i = 0; i < Container.getInstance().accept.size(); i++){
			if(!acceptClass.containsKey(Container.getInstance().accept.get(i).getDeclaringClass().getName())){
				acceptClass.put(Container.getInstance().accept.get(i).getDeclaringClass().getName(),Container.getInstance().accept.get(i).getDeclaringClass());
			}
		}

		// for(String v: acceptClass.keySet()){
		// 	System.out.println(v+" "+acceptClass.get(v));
		// }
		Scene.v().loadClassAndSupport("java.lang.Object");
        Scene.v().loadClassAndSupport("java.lang.System");

		visitMethods = new ArrayList<SootMethod>();
		visitMethods = Container.getInstance().visit;

		Container.getInstance().stmtMap = new HashMap<Stmt,Stmt>();
		Container.getInstance().ifStmtMap = new HashMap<Stmt,Stmt>();
		Container.getInstance().index = 0;
		Container.getInstance().translateClass = visitMethods.get(0).getDeclaringClass();

		writeToFileClasses = new ArrayList<SootClass>();

		//mapping the visit methods called from each visitable class
		for(SootMethod m: acceptMethods){
			if(!m.hasActiveBody()) continue;
			for(Unit u: m.getActiveBody().getUnits()){
				if(((soot.jimple.Stmt) u).containsInvokeExpr()) {
					visitableClassCorressVisit.put(((soot.jimple.Stmt) u).getInvokeExpr().getMethod().getSubSignature(), m.getDeclaringClass());
				}
			}
		}

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
		SootClass visitableinterface = identifyVisitableInterface();	//to identify the interface class that the visitable classes implement. Assumption: All the visitable classes implement only one interface.
		
		createStateClasses();						//to create classes that hold the state of the local variables in visit methods

		SootClass transformClass = createTransformClass();					//create the class for the Translation phase
		
		assignVisitableClassId();									//give ids to visitable classes and add the method getMyId()

		stackObjClass = createStackObjClass(visitableinterface);			//class for stack Obj. 

		buildMainMethod();					//build the main method and translation method. Identify the first accept call and the first node 

		translationPhase(transformClass, stackObjClass, visitMethods.get(0).getDeclaringClass().getMethodByName("translation"));													//build the translate method that contains the code for translation phase

		writeToFile();
	}

	public void createStateClasses(){	//to create classes that hold the state of the local variables in visit methods; The fields of the classes are the local variables of the corresponding visit methods
		itemInterface = new SootClass("Item_Obj", Modifier.INTERFACE);						//one of the fields of Stack
		itemInterface.setSuperclass(Scene.v().getSootClass("java.lang.Object"));
        Scene.v().addClass(itemInterface);
        writeToFileClasses.add(itemInterface);

		for(int i=0;i<visitMethods.size();i++){
			String className = visitableClassCorressVisit.get(visitMethods.get(i).getSubSignature()).getName();
			SootClass itemClass = new SootClass(className+"_Obj", Modifier.PUBLIC );
			itemClass.setSuperclass(Scene.v().getSootClass("java.lang.Object"));
			itemClass.addInterface(itemInterface);
        	Scene.v().addClass(itemClass);
        	//Add locals of this visit method as fields of this class
        	Chain<Local> locals = visitMethods.get(i).getActiveBody().getLocals();
			for(Iterator lt = locals.iterator(); lt.hasNext();){
				final Local local = (Local) lt.next();

				SootField newField = new SootField(local.getName(), local.getType(), Modifier.PUBLIC);
				itemClass.addField(newField);
			}

			//global parameters
			Chain<SootField> globals = visitMethods.get(i).getDeclaringClass().getFields();
    		for( Iterator lIt = globals.iterator(); lIt.hasNext();){
    			final SootField g = (SootField) lIt.next();
    			SootField f = new SootField(g.getName(), g.getType(), Modifier.PUBLIC);
    			itemClass.addField(f);
    		}

			// List<Type> paramTypes = visitMethods.get(i).getParameterTypes();

			for(int p = 0;p < visitMethods.get(i).getParameterCount();p++){
				SootField newField = new SootField("param"+p, visitMethods.get(i).getParameterType(p), Modifier.PUBLIC);
				itemClass.addField(newField);
			}

        	writeToFileClasses.add(itemClass);
		}	
	}

	public SootClass identifyVisitableInterface(){						//to identify the interface class that the visitable classes implement. Assumption: All the visitable classes implement only one interface.
		SootClass visitableinterface;
		visitableinterface = acceptMethods.get(0).getDeclaringClass().getInterfaces().getFirst();
		return visitableinterface;
	}

	public SootClass createTransformClass(){						//create the class for the Translation phase 
		
		SootClass sootClass = new SootClass("Translate",Modifier.PUBLIC);
		sootClass.setSuperclass(Scene.v().getSootClass("java.lang.Object"));
       	Scene.v().addClass(sootClass);

       	writeToFileClasses.add(sootClass);
       	return sootClass;
	}

	public void assignVisitableClassId(){							//give ids to visitable classes and add the method getMyId()
		int id = 1;
		for(String v: acceptClass.keySet()){
			SootClass aClass = acceptClass.get(v);
			if(aClass.isAbstract()){
				getMyIdAbstractMethod = new SootMethod("getMyId", null, IntType.v(), Modifier.PUBLIC+Modifier.ABSTRACT);
				aClass.addMethod(getMyIdAbstractMethod);
				continue;
			}
			SootField field = new SootField("visitableId",IntType.v(), Modifier.PUBLIC);	//create field of type int for Id
			aClass.addField(field);
			//create method to return visitableId
			SootMethod constructorMethod = aClass.getMethodByName("<init>");
			SootMethod getMyIdMethod = new SootMethod("getMyId", null, IntType.v(), Modifier.PUBLIC);
			JimpleBody getMyIdBody = Jimple.v().newBody(getMyIdMethod);
			JimpleLocal l = new JimpleLocal("r0", aClass.getType());
			getMyIdBody.getLocals().add(l);
			getMyIdBody.getUnits().add(Jimple.v().newIdentityStmt(l, Jimple.v().newThisRef(aClass.getType())));
			JReturnStmt retStmt = new JReturnStmt(IntConstant.v(id++));
			getMyIdBody.getUnits().add(retStmt);
			getMyIdMethod.setActiveBody(getMyIdBody);
			aClass.addMethod(getMyIdMethod);

			writeToFileClasses.add(aClass);
		}
	}

	public SootClass createStackObjClass(SootClass visitableinterface){//class for stack Obj. 
		SootClass stackObjClass = new SootClass("StackObj", Modifier.PUBLIC);
		//init Method construction
		SootMethod initMethod = new SootMethod("<init>", null, VoidType.v());
		JimpleBody initBody = Jimple.v().newBody(initMethod);
		initMethod.setActiveBody(initBody);
		stackObjClass.addMethod(initMethod);
		JimpleLocal thisVar = new JimpleLocal("r0", stackObjClass.getType());
		initMethod.getActiveBody().getLocals().add(thisVar);
		initMethod.getActiveBody().getUnits().add(Jimple.v().newIdentityStmt(thisVar, Jimple.v().newThisRef(stackObjClass.getType())));
		initMethod.getActiveBody().getUnits().add(Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr(thisVar, Scene.v().getMethod("<java.lang.Object: void <init>()>").makeRef())));	
		initMethod.getActiveBody().getUnits().add(Jimple.v().newReturnVoidStmt());
		// add fields to stackObjClass
		SootField item = new SootField("stateObj", itemInterface.getType(), Modifier.PUBLIC);
		SootField task = new SootField("task", IntType.v(), Modifier.PUBLIC);
		SootField visitableObj = new SootField("x", visitableinterface.getType(), Modifier.PUBLIC);
		stackObjClass.addField(item);								//item: to store the state of the visit methods while switching between various cases
		stackObjClass.addField(task);								//task: to navigate through the visit methods
		stackObjClass.addField(visitableObj);						//visitableObj: the visitable object on which visit methods are called
		Scene.v().addClass(stackObjClass);

		writeToFileClasses.add(stackObjClass);

		return stackObjClass;
	}

	public void buildMainMethod(){
		// get the first accept method called from main
		// SootMethod mainMethod = Scene.v().getMainMethod();
  //   	Body mainBody = mainMethod.getActiveBody();
		// PatchingChain<Unit> units = mainBody.getUnits();

		// List<Unit> acceptCalls = new ArrayList<Unit>();

		Unit acceptUnit = null;
		for (Unit u : Scene.v().getMainMethod().getActiveBody().getUnits()) {
			if (((soot.jimple.Stmt) u).containsInvokeExpr()) {
            	InvokeExpr E = ((soot.jimple.Stmt) u).getInvokeExpr();
				SootMethod m = E.getMethod();

				if(acceptMethods.contains(m)){
					// acceptCalls.add(u);						//add all the accept methods called from main here
					// System.out.println("++++++++++++++++++++++++++++++++++"+u);
					acceptUnit = u;
					break;
				}
			}
		}



		List<Type> params = new ArrayList<Type>();
		params.add(RefType.v("java.lang.String[]"));
		SootMethod mainMethod = new SootMethod("main",params, VoidType.v(), Modifier.PUBLIC+ Modifier.STATIC);
		JimpleBody body = Jimple.v().newBody(mainMethod);
		mainMethod.setActiveBody(body);
		SootClass visitorClass = visitMethods.get(0).getDeclaringClass();			//the visitor class 
		visitorClass.addMethod(mainMethod);
		// Scene.v().setMainClass(Scene.v().getSootClass("Translate"));

		// Scene.v().setMainClass(Translate);
		for(Local l: Scene.v().getMainMethod().getActiveBody().getLocals()){
			mainMethod.getActiveBody().getLocals().add(l);
		}
		for(Unit units: Scene.v().getMainMethod().getActiveBody().getUnits()){
			if(acceptUnit != units){
				mainMethod.getActiveBody().getUnits().add(units);
			}
			else{
				List<Type> parameters = new ArrayList<Type>();
				parameters.add(((JVirtualInvokeExpr) ((JInvokeStmt)units).getInvokeExpr()).getBase().getType());
				SootMethod translate = new SootMethod("translation", parameters, VoidType.v(), Modifier.PUBLIC);
				visitorClass.addMethod(translate);
				JimpleBody translateBody = Jimple.v().newBody(translate);
				translate.setActiveBody(translateBody);

				List<ValueBox> uses = new ArrayList<ValueBox>();
				uses = ((JInvokeStmt)units).getInvokeExpr().getUseBoxes();
				for(int i=0;i<uses.size();i++){
					if(!((JInvokeStmt)units).getInvokeExpr().getArgs().contains(uses.get(i).getValue())){
						List<Value> args = new ArrayList<Value>();
						args.add(uses.get(i).getValue());
					
						Local l = new JimpleLocal("tz"+Container.getInstance().index++,Container.getInstance().translateClass.getType());
						mainMethod.getActiveBody().getLocals().add(l);
						mainMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(l, Jimple.v().newNewExpr(Container.getInstance().translateClass.getType())));
						mainMethod.getActiveBody().getUnits().add(Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr(l, Container.getInstance().translateClass.getMethodByName("<init>").makeRef())));
						JVirtualInvokeExpr expr = new JVirtualInvokeExpr(l,translate.makeRef(),args);
						mainMethod.getActiveBody().getUnits().add(Jimple.v().newInvokeStmt(expr));
				// System.out.println("++++++++++++++++++++++++");
						break;
					}
				}
			}
		}
		Scene.v().setMainClass(visitorClass);
	}

	public void translationPhase(SootClass translationClass, SootClass stackObjClass, SootMethod translate){	//build the translation method that contains the code for translation phase					
		
		//add global variables of visitor class as local variables in translation method
		SootClass visitorClass = visitMethods.get(0).getDeclaringClass();			//

		Chain<SootField> globals = visitorClass.getFields();
		Container.getInstance().Globals = globals;
		for( Iterator lIt = globals.iterator(); lIt.hasNext();){
			final SootField g = (SootField) lIt.next();
			JimpleLocal globalLocal = new JimpleLocal(g.getName(), g.getType());
			translate.getActiveBody().getLocals().add(globalLocal);
		}
		//start Translation phase

		//start with r0 = @this:SumVisitor
		JimpleLocal this0 = new JimpleLocal("r0", Container.getInstance().translateClass.getType());
		translate.getActiveBody().getLocals().add(this0);
		translate.getActiveBody().getUnits().add(Jimple.v().newIdentityStmt(this0, Jimple.v().newThisRef(Container.getInstance().translateClass.getType())));
		//node = parameter0
		JimpleLocal p0 = new JimpleLocal("node", translate.getParameterType(0));
		JimpleLocal par0 = new JimpleLocal("r1", translate.getParameterType(0));
		translate.getActiveBody().getLocals().add(par0);
		translate.getActiveBody().getLocals().add(p0);
		translate.getActiveBody().getUnits().add(Jimple.v().newIdentityStmt(par0, Jimple.v().newParameterRef(p0.getType(), 0)));
		translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(p0, par0));
		//create a new array to simulate stack
		ArrayType arrayType = ArrayType.v(stackObjClass.getType(), 1);
		JimpleLocal array = new JimpleLocal("stack", arrayType);
		translate.getActiveBody().getLocals().add(array);
		//StackObj[] stack = new stackObj[100];	Assumption: the array is given a capacity of 100.

		JNewArrayExpr newArray = new JNewArrayExpr(stackObjClass.getType(), IntConstant.v(100));
		JAssignStmt assignNewStack = new JAssignStmt(array, newArray);
		translate.getActiveBody().getUnits().add(assignNewStack);

		//stackIterator = 0;
		JimpleLocal stackIterator = new JimpleLocal("stackIterator", IntType.v());
		translate.getActiveBody().getLocals().add(stackIterator);
		JAssignStmt stackIteratorInitiate = new JAssignStmt(stackIterator, IntConstant.v(0));
		translate.getActiveBody().getUnits().add(stackIteratorInitiate);
		//for(stackIterator = 0; stackIterator < 100; ++)
		//allocate stackObj
		NopStmt stackIteratorStart = Jimple.v().newNopStmt();
		JGeExpr stackIteratorExpr = new JGeExpr(stackIterator, IntConstant.v(100));
		JIfStmt stackIteratorCondition = new JIfStmt(stackIteratorExpr, (Unit) null);
		NopStmt stackIteratorEnd = Jimple.v().newNopStmt();
		stackIteratorCondition.setTarget(stackIteratorEnd);
		translate.getActiveBody().getUnits().add(stackIteratorStart);
		translate.getActiveBody().getUnits().add(stackIteratorCondition);
		JNewExpr newStackObj = new JNewExpr(RefType.v(stackObjClass.toString()));
		JArrayRef arrayRef = new JArrayRef(array, stackIterator);
		JimpleLocal allocVariable = new JimpleLocal("allocVariable",stackObjClass.getType());
		translate.getActiveBody().getLocals().add(allocVariable);
		JAssignStmt assignArrayRef = new JAssignStmt(allocVariable, newStackObj);
		translate.getActiveBody().getUnits().add(assignArrayRef);
		translate.getActiveBody().getUnits().add(Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr(allocVariable, stackObjClass.getMethodByName("<init>").makeRef())));
		translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(arrayRef, allocVariable));

		JAddExpr stackIteratorIncrement = new JAddExpr(IntConstant.v(1), stackIterator);
		JAssignStmt stackIteratorIncrementStmt = new JAssignStmt(stackIterator, stackIteratorIncrement);
		translate.getActiveBody().getUnits().add(stackIteratorIncrementStmt);
		translate.getActiveBody().getUnits().add(Jimple.v().newGotoStmt(stackIteratorStart));
		translate.getActiveBody().getUnits().add(stackIteratorEnd);

		//index = 0;
		JimpleLocal index = new JimpleLocal("index", IntType.v());
		translate.getActiveBody().getLocals().add(index);
		translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(index, IntConstant.v(0)));

		JimpleLocal r3 = new JimpleLocal("var"+(varIndex++), stackObjClass.getType());
		translate.getActiveBody().getLocals().add(r3);
		translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(r3, Jimple.v().newArrayRef(array, index)));
		//stack[index].x = N;
		translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(r3, stackObjClass.getFieldByName("x").makeRef()), p0));

		//stack[index].task = 0;
		translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(r3, stackObjClass.getFieldByName("task").makeRef()), IntConstant.v(0)));

		//boolean flag = true;
		JimpleLocal flag = new JimpleLocal("flag", BooleanType.v());
		translate.getActiveBody().getLocals().add(flag);
		translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(flag, IntConstant.v(1)));

		//stackObj s = new stackObj
		//s.x = stack[index].x;
		JimpleLocal s = new JimpleLocal("s", stackObjClass.getType());
		translate.getActiveBody().getLocals().add(s);
		translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(s, Jimple.v().newNewExpr(stackObjClass.getType())));
		translate.getActiveBody().getUnits().add(Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr(s, Container.getInstance().translateClass.getMethodByName("<init>").makeRef())));
		JimpleLocal r0 = new JimpleLocal("var"+(varIndex++), stackObjClass.getType());
		translate.getActiveBody().getLocals().add(r0);
		translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(r0, Jimple.v().newArrayRef(array, index)));
		JInstanceFieldRef ref = new JInstanceFieldRef(r0,stackObjClass.getFieldByName("x").makeRef());
		JimpleLocal r1 = new JimpleLocal("var"+(varIndex++), stackObjClass.getFieldByName("x").getType());
		translate.getActiveBody().getLocals().add(r1);
		translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(r1, ref));
		translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(s, stackObjClass.getFieldByName("x").makeRef()),r1));
		//s.task = stack[index].task;
		JInstanceFieldRef ref0 = new JInstanceFieldRef(r0,stackObjClass.getFieldByName("task").makeRef());
		JimpleLocal r2 = new JimpleLocal("var"+(varIndex++), stackObjClass.getFieldByName("task").getType());
		translate.getActiveBody().getLocals().add(r2);
		translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(r2, ref0));
		translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(s, stackObjClass.getFieldByName("task").makeRef()),r2));

		//do while
		NopStmt beginWhile = Jimple.v().newNopStmt();
		translate.getActiveBody().getUnits().add(beginWhile);

		HashMap<SootMethod, Integer> cases = getSwitchCases();
		// System.out.println(cases);
		//create switch table
		NopStmt labelSwitch = new JNopStmt();
		translate.getActiveBody().getUnits().add(Jimple.v().newGotoStmt(labelSwitch));
		createSwitchTable(cases, translate, index, array, s, flag, labelSwitch);

		JIfStmt whileCondition = new JIfStmt(Jimple.v().newEqExpr(flag, IntConstant.v(1)), beginWhile);
		translate.getActiveBody().getUnits().add(whileCondition);
		translate.getActiveBody().getUnits().add(Jimple.v().newReturnVoidStmt());
	}

	public void createSwitchTable(HashMap<SootMethod, Integer> cases, SootMethod translate, JimpleLocal index, JimpleLocal stack, JimpleLocal s, JimpleLocal flag, Unit labelSwitch){
		JimpleLocal switchId = new JimpleLocal("switchId", IntType.v());
		translate.getActiveBody().getLocals().add(switchId);
		List<Unit> switchTargetList = new ArrayList<Unit>();
		for(int i = 0;i<visitMethods.size();i++){
			switchTargetList.add(Jimple.v().newNopStmt());

			Local o = Jimple.v().newLocal(visitableClassCorressVisit.get(visitMethods.get(i).getSubSignature()).getName()+"O",Scene.v().getSootClass(visitableClassCorressVisit.get(visitMethods.get(i).getSubSignature()).getType()+"_Obj").getType());
			translate.getActiveBody().getLocals().add(o);		//adding a class_obj for each visit method
			visitableObjCorresVisit.put(visitMethods.get(i), o);
		}
		NopStmt defaultSwitchCase = Jimple.v().newNopStmt();

		NopStmt endSwitch = Jimple.v().newNopStmt();//acts as break
		//first add visit bodies in the cases
		Map<Unit, ArrayList<Unit> > nonVisitCases = new HashMap<Unit, ArrayList<Unit> >();
		Pair<Map<Unit, ArrayList<Unit> >, List<Unit> > p = addVisitCases(cases, switchTargetList, translate, endSwitch, index, stack, s);
		nonVisitCases = p.getKey();
		switchTargetList = p.getValue();

		CheckStmt convert = new CheckStmt();
		Container.getInstance().currMethod = translate;
		
		for(Unit u: nonVisitCases.keySet()){
			translate.getActiveBody().getUnits().add(u);
			Container.getInstance().visitMeth = switchCasesCorresVisit.get(u);
			Container.getInstance().objClass = visitableClassCorressVisit.get(Container.getInstance().visitMeth.getSubSignature()).getName();
			Container.getInstance().obj = visitableObjCorresVisit.get(Container.getInstance().visitMeth);
			Container.getInstance().Globals = switchCasesCorresVisit.get(u).getDeclaringClass().getFields();

			//obj = s.state_Obj
			translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(Container.getInstance().obj, Jimple.v().newInstanceFieldRef(s, stackObjClass.getFieldByName("stateObj").makeRef())));
			for(Unit v: nonVisitCases.get(u))
				v.apply(convert);
				// translate.getActiveBody().getUnits().add(v);

			//flag condition: if(index<=0)flag = false;else { s.x = stack[--index].x;s.task = stack[index].task;}
			JIfStmt flagIfCondition = new JIfStmt(Jimple.v().newGtExpr(index, IntConstant.v(0)),(Unit) null);
			translate.getActiveBody().getUnits().add(flagIfCondition);
			translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(flag, IntConstant.v(0)));
			translate.getActiveBody().getUnits().add(Jimple.v().newGotoStmt(endSwitch));
			JNopStmt flagElse = new JNopStmt();
			translate.getActiveBody().getUnits().add(flagElse);
			flagIfCondition.setTarget(flagElse);
			translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(index, Jimple.v().newSubExpr(index, IntConstant.v(1))));
			JimpleLocal l0 = new JimpleLocal("var"+(varIndex++), stackObjClass.getType());
			translate.getActiveBody().getLocals().add(l0);
			translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(l0, Jimple.v().newArrayRef(stack, index)));
			JimpleLocal l1 = new JimpleLocal("var"+(varIndex++), stackObjClass.getFieldByName("x").getType());
			JimpleLocal l2 = new JimpleLocal("var"+(varIndex++), stackObjClass.getFieldByName("task").getType());
			JimpleLocal l3 = new JimpleLocal("var"+(varIndex++), stackObjClass.getFieldByName("stateObj").getType());
			translate.getActiveBody().getLocals().add(l1);
			translate.getActiveBody().getLocals().add(l2);
			translate.getActiveBody().getLocals().add(l3);
			translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(l1, Jimple.v().newInstanceFieldRef(l0, stackObjClass.getFieldByName("x").makeRef())));
			translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(s, stackObjClass.getFieldByName("x").makeRef()), l1));
			translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(l2, Jimple.v().newInstanceFieldRef(l0, stackObjClass.getFieldByName("task").makeRef())));
			translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(s, stackObjClass.getFieldByName("task").makeRef()), l2));
			translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(l3, Jimple.v().newInstanceFieldRef(l0, stackObjClass.getFieldByName("stateObj").makeRef())));
			translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(s, stackObjClass.getFieldByName("stateObj").makeRef()), l3));

			translate.getActiveBody().getUnits().add(Jimple.v().newGotoStmt(endSwitch));
		}

		translate.getActiveBody().getUnits().add(defaultSwitchCase);
		translate.getActiveBody().getUnits().add(Jimple.v().newGotoStmt(endSwitch));

		translate.getActiveBody().getUnits().add(labelSwitch);
		//l4 = stackObj.task;
		//l5 = stackObj.x;
		//l6 = l5.getMyId();
		JimpleLocal l4 = new JimpleLocal("var"+(varIndex++), IntType.v());
		JimpleLocal l5 = new JimpleLocal("var"+(varIndex++), stackObjClass.getFieldByName("x").getType());
		JimpleLocal l6 = new JimpleLocal("var"+(varIndex++), IntType.v());
		translate.getActiveBody().getLocals().add(l4);
		translate.getActiveBody().getLocals().add(l5);
		translate.getActiveBody().getLocals().add(l6);
		translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(l4, Jimple.v().newInstanceFieldRef(s, stackObjClass.getFieldByName("task").makeRef())));
		translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(l5, Jimple.v().newInstanceFieldRef(s, stackObjClass.getFieldByName("x").makeRef())));
		translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(l6, Jimple.v().newInterfaceInvokeExpr(l5, getMyIdAbstractMethod.makeRef(), new ArrayList<Value>())));
		//if(l4!=0) goto label0; l7 = INT_MAX; goto label1; 
		JNopStmt switchIdLabel0 = new JNopStmt();
		JNopStmt switchIdLabel1 = new JNopStmt();
		JIfStmt switchIdIf = new JIfStmt(Jimple.v().newNeExpr(l4, IntConstant.v(0)), switchIdLabel0);
		translate.getActiveBody().getUnits().add(switchIdIf);
		JimpleLocal l7 = new JimpleLocal("var"+(varIndex++), IntType.v());
		translate.getActiveBody().getLocals().add(l7);
		translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(l7, IntConstant.v(Integer.MAX_VALUE)));
		translate.getActiveBody().getUnits().add(Jimple.v().newGotoStmt(switchIdLabel1));
		//label0; l7 = 0; label1: l8 = l6 & l7; switchId = l4 | l8;
		translate.getActiveBody().getUnits().add(switchIdLabel0);
		JimpleLocal l8 = new JimpleLocal("var"+(varIndex++), IntType.v());
		translate.getActiveBody().getLocals().add(l8);
		translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(l7, IntConstant.v(0)));
		translate.getActiveBody().getUnits().add(switchIdLabel1);
		translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(l8, Jimple.v().newAndExpr(l6, l7)));
		translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(switchId, Jimple.v().newOrExpr(l4, l8)));

		JTableSwitchStmt switchStmt = new JTableSwitchStmt(switchId, 1, switchTargetList.size(), switchTargetList, (Unit) defaultSwitchCase);
		translate.getActiveBody().getUnits().add(switchStmt);

		translate.getActiveBody().getUnits().add(endSwitch);
	}

	public Pair< Map<Unit, ArrayList<Unit> >, List<Unit> > addVisitCases(HashMap<SootMethod, Integer> cases, List<Unit> switchTargetList, SootMethod translate, Unit endSwitch, JimpleLocal index, JimpleLocal stack, JimpleLocal s){
		List<Unit> reverseListofUnits;
		Map<Unit, ArrayList<Unit> > switchCasesMap = new HashMap<Unit, ArrayList<Unit> >();
		int switchIndex = visitMethods.size();

		for(int i=0;i<visitMethods.size();i++){
			reverseListofUnits = new ArrayList<Unit>();
			ArrayList<Unit> visitUnitList = new ArrayList<Unit>();
			ArrayList<Unit> caseBody = new ArrayList<Unit>();
			boolean firstAccept = false;
			boolean lastAccept = true;

			CheckStmt convert = new CheckStmt();
			Container.getInstance().currMethod = translate;

			Container.getInstance().visitMeth = visitMethods.get(i);
			Container.getInstance().objClass = visitableClassCorressVisit.get(Container.getInstance().visitMeth.getSubSignature()).getName();
			Container.getInstance().obj = visitableObjCorresVisit.get(Container.getInstance().visitMeth);
			Container.getInstance().Globals = visitMethods.get(i).getDeclaringClass().getFields();

			for(Unit u: visitMethods.get(i).getActiveBody().getUnits()){
				reverseListofUnits.add(0,u);
			}
			//begin the switch case
			translate.getActiveBody().getUnits().add(switchTargetList.get(i));
			translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(Container.getInstance().obj, Jimple.v().newNewExpr(Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getType())));

			//add the parameter allocation in the beginning of the case body
			if(visitMethods.get(i).getParameterCount()>0){
				for(int j=0;j<visitMethods.get(i).getParameterCount()+1;j++){
					reverseListofUnits.get(reverseListofUnits.size()-j-1).apply(convert);
				}
			}


			//v0 = s.x;
			//v1 = (visitableClass) v0;
			JimpleLocal l0 = new JimpleLocal("var"+(varIndex++), stackObjClass.getFieldByName("x").getType());//v0
			JimpleLocal l1 = new JimpleLocal("var"+(varIndex++), visitableClassCorressVisit.get(visitMethods.get(i).getSubSignature()).getType());//v1
			translate.getActiveBody().getLocals().add(l0);
			translate.getActiveBody().getLocals().add(l1);
			// visitUnitList.add(Jimple.v().newAssignStmt(l0, Jimple.v().newInstanceFieldRef(s, stackObjClass.getFieldByName("x").makeRef())));
			// visitUnitList.add(Jimple.v().newAssignStmt(l1, Jimple.v().newCastExpr(l0, visitableClassCorressVisit.get(visitMethods.get(i).getSubSignature()).getType())));

			translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(l0, Jimple.v().newInstanceFieldRef(s, stackObjClass.getFieldByName("x").makeRef())));
			translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(l1, Jimple.v().newCastExpr(l0, visitableClassCorressVisit.get(visitMethods.get(i).getSubSignature()).getType())));			

			int acceptNo = 0;			//keeps track of the number of accept calls in the current visit method

			for(int j=0; j<reverseListofUnits.size(); j++){
				Unit u = reverseListofUnits.get(j);
				if(!isAccept(u)){
					caseBody.add(0,u);
				}
				else{
					if(caseBody.size()!=0){													//if there is any code chunk between two accept calls
						JNopStmt label = new JNopStmt();
						switchTargetList.add(label);
						switchCasesMap.put(label, caseBody);
						switchCasesCorresVisit.put(label, visitMethods.get(i));			// which visit does the switch case belong to
						caseBody = new ArrayList<Unit>();
						//var = stack[index]
						JimpleLocal l3 = new JimpleLocal("var"+(varIndex++), stackObjClass.getType());
						translate.getActiveBody().getLocals().add(l3);
						translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(l3, Jimple.v().newArrayRef(stack, index)));
						//var.task = caseNumber
						translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(l3, stackObjClass.getFieldByName("task").makeRef()), IntConstant.v(++switchIndex)));
						//var.x = s.x(l1)
						translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(l3, stackObjClass.getFieldByName("x").makeRef()), l1));
						//var.statObj = obj
						translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(l3, stackObjClass.getFieldByName("stateObj").makeRef()), Container.getInstance().obj));
						//index = index + 1
						translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(index, Jimple.v().newAddExpr(index, IntConstant.v(1))));
					}

					//$r9 = r1.<Shop: Apparel f1>;
					// tz4 = ShopO.<Shop_Obj: Shop r1>;

			  //       tz5 = tz4.<Shop: Apparel f1>;

			  //       ShopO.<Shop_Obj: Apparel $r9> = tz5;

					Unit nextNode = reverseListofUnits.get(++j);
					JimpleLocal v0 = new JimpleLocal("var"+(varIndex++), ((JInstanceFieldRef)((JAssignStmt) nextNode).getRightOp()).getBase().getType());
					translate.getActiveBody().getLocals().add(v0);
					translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(v0, Jimple.v().newInstanceFieldRef(Container.getInstance().obj, Scene.v().getSootClass(visitableClassCorressVisit.get(Container.getInstance().visitMeth.getSubSignature())+"_Obj").getFieldByName(((JInstanceFieldRef)((JAssignStmt) nextNode).getRightOp()).getBase().toString()).makeRef())));
					JimpleLocal v1 = new JimpleLocal("var"+(varIndex++), ((JAssignStmt) nextNode).getLeftOp().getType());
					translate.getActiveBody().getLocals().add(v1);
					translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(v1, Jimple.v().newInstanceFieldRef(v0, ((JInstanceFieldRef)((JAssignStmt) nextNode).getRightOp()).getField().makeRef())));
					translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(Container.getInstance().obj, Scene.v().getSootClass(visitableClassCorressVisit.get(Container.getInstance().visitMeth.getSubSignature())+"_Obj").getFieldByName(((JAssignStmt) nextNode).getLeftOp().toString()).makeRef()), v1));

					acceptNo++;
					if(cases.get(visitMethods.get(i))!=acceptNo){
						JimpleLocal l2 = new JimpleLocal("var"+(varIndex++), stackObjClass.getType());
						translate.getActiveBody().getLocals().add(l2);
						//l2 = stack[index]
						translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(l2, Jimple.v().newArrayRef(stack, index)));
						//l2.x = s.x
						translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(l2, stackObjClass.getFieldByName("x").makeRef()),v1));
						//l2.task = 0; for accept calls task = 0
						translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(l2, stackObjClass.getFieldByName("task").makeRef()), IntConstant.v(0)));
						//l2.stateObj = Obj
						translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(l2, stackObjClass.getFieldByName("stateObj").makeRef()), Container.getInstance().obj));
						//index++;
						translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(index, Jimple.v().newAddExpr(index, IntConstant.v(1))));
					}
					else{				//then this is the first accept call
						//s.x = x.f0;
						translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(s, stackObjClass.getFieldByName("x").makeRef()),v1));
						//s.task = 0; fpr accept calls
						translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(s, stackObjClass.getFieldByName("task").makeRef()), IntConstant.v(0)));
						//s.obj = obj
						translate.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(s, stackObjClass.getFieldByName("stateObj").makeRef()), Container.getInstance().obj));

					}
					
				}
			}
			if(caseBody.size()!=0){
				
				for(Unit v: caseBody){
					v.apply(convert);
				}
			}

			translate.getActiveBody().getUnits().add(Jimple.v().newGotoStmt(endSwitch));

			// translate.getActiveBody().getUnits().add(switchTargetList.get(i));

			// for(Unit v: visitUnitList)
			// 	translate.getActiveBody().getUnits().add(v);
		}
		Pair<Map<Unit, ArrayList<Unit> >, List<Unit> > ret = new Pair<Map<Unit, ArrayList<Unit> >, List<Unit> >(switchCasesMap, switchTargetList);
		return ret;
	}

	public HashMap<SootMethod, Integer> getSwitchCases(){
		int caseNumber = 0;
		HashMap<SootMethod, Integer> cases = new HashMap<SootMethod, Integer>();
		ListIterator<SootMethod> iterator = visitMethods.listIterator();
		while(iterator.hasNext()){
			int acceptCall = 0;
			SootMethod visitM = iterator.next();
			for(Unit u: visitM.getActiveBody().getUnits()){
				if(isAccept(u)){
					acceptCall++;
				}
			}
			cases.put(visitM, acceptCall);
		}
		return cases;
	}

	public ArrayList<ArrayList<Unit> > getSwitchCases2(){
		int caseNumber = 0;
		ArrayList<ArrayList<Unit> > cases = new ArrayList<ArrayList<Unit> >();
		ListIterator<SootMethod> iterator = visitMethods.listIterator();
		while(iterator.hasNext()){
			ArrayList<Unit> caseBody = new ArrayList<Unit>();
			boolean lastAccept = false;
			boolean firstAccept = true;

			for(Unit u: iterator.next().getActiveBody().getUnits()){
				if(!isAccept(u)){
					// System.out.println("isnt accept "+u);
					caseBody.add(u);
					lastAccept = false;
				}
				else{
					switchAcceptCalls.add(u);
					if(firstAccept){
						// cases.add(caseBody);
						caseBody = new ArrayList<Unit>();
						firstAccept = false;
						lastAccept = true;
						continue;
					}
					if(caseBody.size()!=0){
						cases.add(caseBody);
						caseBody = new ArrayList<Unit>();
						caseNumber++;
					}
					lastAccept = true;
				}
			}

			if(!lastAccept){
				cases.add(caseBody);
				caseNumber++;
				// System.out.println(u);
			}
		}
		// for(int i=0;i<cases.size();i++)
		// 	System.out.println(i+" "+cases.get(i));
		return cases;
	}

	public boolean isAccept(Unit u){
		if(!((soot.jimple.Stmt) u).containsInvokeExpr())
			return false;
		SootMethod m = ((soot.jimple.Stmt) u).getInvokeExpr().getMethod();
		if(acceptMethods.contains(m))
			return true;
		return false;
	}

	public void writeToFile(){
		try{

			for(int i = 0;i<writeToFileClasses.size();i++){
				String fileName = SourceLocator.v().getFileNameFor(writeToFileClasses.get(i), Options.output_format_jimple);
				OutputStream streamOut = new FileOutputStream(fileName);
				PrintWriter writerOut = new PrintWriter(new OutputStreamWriter(streamOut));
				Printer.v().printTo(writeToFileClasses.get(i), writerOut);
				writerOut.flush();
				streamOut.close();
			}
		}
		catch(FileNotFoundException e){
			System.out.println(e);
		}
		catch(IOException f){
			System.out.println(f);
		}
	}
}