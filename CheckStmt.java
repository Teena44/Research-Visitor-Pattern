import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;

import soot.*;
import java.util.*;
import soot.jimple.spark.*;
import soot.jimple.*;
import soot.options.Options;
import soot.util.*;
import java.io.*;
import java.*;
import soot.jimple.Jimple;
import soot.dava.internal.SET.SETNodeLabel;
import java.lang.Object;
import soot.jimple.internal.*;

import soot.Body;
import soot.SootClass;
import soot.jimple.FieldRef;
import soot.jimple.ThisRef;
import soot.jimple.Jimple;
import soot.PackManager;
import soot.PatchingChain;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Transform;
import soot.Unit;
import soot.Type;
import soot.PatchingChain;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.toolkits.graph.BriefUnitGraph;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.AbstractInstanceInvokeExpr;
import soot.jimple.internal.AbstractInstanceFieldRef;
import soot.jimple.StaticFieldRef;
import soot.toolkits.graph.UnitGraph;
import soot.tagkit.GenericAttribute;
import soot.jimple.InvokeExpr;
import soot.jimple.internal.JAssignStmt;
import soot.util.Chain;
import soot.jimple.StaticFieldRef;

public class CheckStmt implements StmtSwitch{

	@Override
	public void caseInvokeStmt(InvokeStmt stmt){
		JNopStmt label = new JNopStmt(); 
		if(stmt.getBoxesPointingToThis().size() > 0){
			Container.getInstance().currMethod.getActiveBody().getUnits().add(label);
		}

		CheckValue V = new CheckValue();
		stmt.getInvokeExpr().apply(V);

		JInvokeStmt jstmt = new JInvokeStmt(Container.getInstance().ret);
		Container.getInstance().currMethod.getActiveBody().getUnits().add(jstmt);

		if(stmt.getBoxesPointingToThis().size()>0){
			if(!Container.getInstance().stmtMap.containsKey(stmt))
				Container.getInstance().stmtMap.put(stmt,label);

			if(Container.getInstance().ifStmtMap.containsKey(stmt)){
				if(Container.getInstance().ifStmtMap.get(stmt) instanceof JIfStmt)
					((JIfStmt) Container.getInstance().ifStmtMap.get(stmt)).setTarget(label);
				if(Container.getInstance().ifStmtMap.get(stmt) instanceof JGotoStmt)
					((JGotoStmt) Container.getInstance().ifStmtMap.get(stmt)).setTarget(label);
			}
		}
		// the idea is to enter the statements that are jumped to by if and goto stmts for later reference
		//1. check if statement is jumped at. 
		//2. If yes, add it to the map as the value of the original statement
		//3. This way, when an if condition is met with, we can lookup the stmt into map and we will find it
		//4. But if the statement to jump to occurs after the if condition, then enter the if condition as a value against the original stmt in the IfStmtMap map
		//5. This way, if later a statement is encountered and looked up into IfStmtMap, then we can replace the target of that if statement with this new stmt 
	}

	@Override
	public void caseAssignStmt(AssignStmt stmt){
		CheckValue V = new CheckValue();
		Value rightOp = null;

		JNopStmt label = new JNopStmt();
		if(stmt.getBoxesPointingToThis().size() > 0){
			Container.getInstance().currMethod.getActiveBody().getUnits().add(label);
		}

		if(stmt.getRightOp() instanceof soot.jimple.internal.JimpleLocal){
			if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(((JimpleLocal) stmt.getRightOp()))){
				stmt.getRightOp().apply(V);

				rightOp = Container.getInstance().ret;
			}
		}

		else if(stmt.getRightOp() instanceof soot.jimple.internal.JInstanceFieldRef){
			//global variables

	 		if(Container.getInstance().Globals.toString().contains(((JInstanceFieldRef) stmt.getRightOp()).getField().getName())){
	 			// System.out.println(Container.getInstance().Globals);
	 			Chain<Local> locals = Container.getInstance().translateClass.getMethodByName("translation").getActiveBody().getLocals();
	 			for(Iterator it = locals.iterator();it.hasNext();){
					final Local l = (Local) it.next();
					if(l.toString()==((JInstanceFieldRef) stmt.getRightOp()).getField().getName().toString()){
						rightOp = l;
					}
				}
	 		}
			//r0.<x> into t0 = Obj<r0>; t0.<x>
			else if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(((InstanceFieldRef) stmt.getRightOp()).getBase())){
				Container.getInstance().right = true;
				stmt.getRightOp().apply(V);

				rightOp = Container.getInstance().ret;
				// Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(stmt.getLeftOp(),(InstanceFieldRef) Container.getInstance().ret));
				Container.getInstance().right = false;
			}
		}

		else if(stmt.getRightOp() instanceof soot.jimple.internal.JArrayRef){
			//x = r[i] into tz = Obj.r; x = tz[i]
			if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(((ArrayRef) stmt.getRightOp()).getBase())){
				stmt.getRightOp().apply(V);
				// Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(stmt.getLeftOp(),Container.getInstance().ret));
				rightOp = Container.getInstance().ret;
			}
		}

		else if(stmt.getRightOp() instanceof soot.jimple.BinopExpr){
			//r0 = r1 + r1 into tz = Obj.r0 ; tz1 = Obj.r1; tz2 = Obj.r2; tz = tz1+tz2; Obj.r0 = tz;
			Container.getInstance().val = stmt.getLeftOp();
			//tz1 = Obj.r1; tz2 = Obj.r2;
			stmt.getRightOp().apply(V);
			rightOp = Container.getInstance().ret;
			//tz = Obj.r0
			// String name = stmt.getLeftOp().toString();
			// InstanceFieldRef ref1 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
			// Type t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
			// Local tz = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
			// Container.getInstance().currMethod.getActiveBody().getLocals().add(tz);
			// Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz,ref1));
			// //tz = tz1+tz2;
			// Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz,Container.getInstance().ret));
			// //Obj.r0 = tz;
			// Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(ref1,tz));
		}
		else if(stmt.getRightOp() instanceof soot.jimple.internal.JCastExpr){
			//r0 = (type) r1 into tz = Obj.r0; tz2 = Obj.r1; tz = (type) tz2; Obj.r0 = tz0;
			//tz2 = Obj.r1;
			stmt.getRightOp().apply(V);
			rightOp = Container.getInstance().ret;
			//tz = Obj.r0
			// String name = stmt.getLeftOp().toString();
			// InstanceFieldRef ref1 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
			// Type t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
			// Local tz = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
			// Container.getInstance().currMethod.getActiveBody().getLocals().add(tz);
			// Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz,ref1));
			// //tz = (type) tz2;
			// Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz,Container.getInstance().ret));
			// //Obj.r0 = tz;
			// Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(ref1,tz));
		}

		else if(stmt.getRightOp() instanceof soot.jimple.internal.JNewExpr){
			// r0 = new x() into tz = Obj.r0; tz = new x(); Obj.r0 = tz
			stmt.getRightOp().apply(V);
			rightOp = Container.getInstance().ret;
			//tz = Obj.r0
			// String name = stmt.getLeftOp().toString();
			// InstanceFieldRef ref1 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
			// Type t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
			// Local tz = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
			// Container.getInstance().currMethod.getActiveBody().getLocals().add(tz);
			// Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz,ref1));
			// //tz = new x();
			// Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz,Container.getInstance().ret));
			// //Obj.r0 = tz;
			// Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(ref1,tz));

		}
		else if(stmt.getRightOp() instanceof soot.jimple.internal.JNewArrayExpr){
			// r0 = newarray (int)[10]; into tz = Obj.r0; tz = newarray (int)[10]; Obj.r0 = tz;
			stmt.getRightOp().apply(V);
			rightOp = Container.getInstance().ret;
			//tz = Obj.r0
			// String name = stmt.getLeftOp().toString();
			// InstanceFieldRef ref1 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
			// Type t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
			// Local tz = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
			// Container.getInstance().currMethod.getActiveBody().getLocals().add(tz);
			// Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz,ref1));
			// //tz = newarray (int)[10];
			// Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz,Container.getInstance().ret));
			// //Obj.r0 = tz;
			// Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(ref1,tz));

		}
		else{
			stmt.getRightOp().apply(V);
			rightOp = Container.getInstance().ret;
		}

		if(stmt.getLeftOp() instanceof soot.jimple.internal.JimpleLocal){
			if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(((JimpleLocal) stmt.getLeftOp()))){
				stmt.getLeftOp().apply(V);

				// if(Container.getInstance().ret == null)
				// 	System.out.println("111111111111111111111111111111111111");
				// if(rightOp == null)
				// 	System.out.println("222222222222222222222222222222222222");
				Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(Container.getInstance().ret, rightOp));

				String name = stmt.getLeftOp().toString();
				InstanceFieldRef ref2 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj, Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
				Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(ref2,((JimpleLocal) Container.getInstance().ret)));
				
				if(stmt.getBoxesPointingToThis().size()>0){
					if(!Container.getInstance().stmtMap.containsKey(stmt))
						Container.getInstance().stmtMap.put(stmt,label);

					if(Container.getInstance().ifStmtMap.containsKey(stmt)){
						if(Container.getInstance().ifStmtMap.get(stmt) instanceof JIfStmt)
							((JIfStmt) Container.getInstance().ifStmtMap.get(stmt)).setTarget(label);
						if(Container.getInstance().ifStmtMap.get(stmt) instanceof JGotoStmt)
							((JGotoStmt) Container.getInstance().ifStmtMap.get(stmt)).setTarget(label);
					}
				}
			}
		}
		
		
		if(stmt.getLeftOp() instanceof soot.jimple.internal.JInstanceFieldRef){
			//global variable
			// System.out.println(V.getField().getName()+" "+V.getField().getSignature()+" "+V.getField().getSubSignature());
	 		if(Container.getInstance().Globals.toString().contains(((JInstanceFieldRef) stmt.getLeftOp()).getField().getName())){
	 			Chain<Local> locals = Container.getInstance().translateClass.getMethodByName("translation").getActiveBody().getLocals();
	 			for(Iterator it = locals.iterator();it.hasNext();){
					final Local l = (Local) it.next();
					if(l.toString()==((JInstanceFieldRef) stmt.getLeftOp()).getField().getName()){
						Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(l,rightOp));
					}
				}
	 		}
			//r0.<x> = p; into t0.<x> = p; Obj<r0> = t0;
			else if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(((InstanceFieldRef) stmt.getLeftOp()).getBase())){
				Container.getInstance().left = true;
				stmt.getLeftOp().apply(V);
				String name = ((InstanceFieldRef) stmt.getLeftOp()).getBase().toString();
				Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(Container.getInstance().ret, rightOp));				
				InstanceFieldRef ref1 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
				Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(ref1,((InstanceFieldRef) Container.getInstance().ret).getBase()));

				Container.getInstance().left = false;

				if(stmt.getBoxesPointingToThis().size()>0){
					if(!Container.getInstance().stmtMap.containsKey(stmt))
						Container.getInstance().stmtMap.put(stmt,label);

					if(Container.getInstance().ifStmtMap.containsKey(stmt)){
						if(Container.getInstance().ifStmtMap.get(stmt) instanceof JIfStmt)
							((JIfStmt) Container.getInstance().ifStmtMap.get(stmt)).setTarget(label);
						if(Container.getInstance().ifStmtMap.get(stmt) instanceof JGotoStmt)
							((JGotoStmt) Container.getInstance().ifStmtMap.get(stmt)).setTarget(label);
					}
				}
			}
		}
		//ArrayRef
		
		if(stmt.getLeftOp() instanceof soot.jimple.internal.JArrayRef){
			//r[i] = x into tz = Obj.r; tz[i] = x; Obj.r = tz; 
			if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(((ArrayRef) stmt.getLeftOp()).getBase())){
				String name = ((ArrayRef) stmt.getLeftOp()).getBase().toString();
				stmt.getLeftOp().apply(V);
				Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(Container.getInstance().ret,rightOp));

				InstanceFieldRef ref2 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj, Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
				Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(ref2,((JArrayRef) Container.getInstance().ret).getBase()));
				
				if(stmt.getBoxesPointingToThis().size()>0){
					if(!Container.getInstance().stmtMap.containsKey(stmt))
						Container.getInstance().stmtMap.put(stmt,label);

					if(Container.getInstance().ifStmtMap.containsKey(stmt)){
						if(Container.getInstance().ifStmtMap.get(stmt) instanceof JIfStmt)
							((JIfStmt) Container.getInstance().ifStmtMap.get(stmt)).setTarget(label);
						if(Container.getInstance().ifStmtMap.get(stmt) instanceof JGotoStmt)
							((JGotoStmt) Container.getInstance().ifStmtMap.get(stmt)).setTarget(label);
					}
				}
			}
		}

	}

	@Override
	public void caseIfStmt(IfStmt stmt){

		NopStmt label = new JNopStmt();
		if(stmt.getBoxesPointingToThis().size() > 0){
			Container.getInstance().currMethod.getActiveBody().getUnits().add(label);
		}

		CheckValue V = new CheckValue();
		stmt.getCondition().apply(V);
		Value ret = Container.getInstance().ret;
		JIfStmt ifStmt;

		if(Container.getInstance().stmtMap == null){				//if stmt is not present in map, then add this if stmt to map and then later check for the stmt
			ifStmt = new JIfStmt(ret, stmt.getTarget());
			Container.getInstance().currMethod.getActiveBody().getUnits().add(ifStmt);
			System.out.println("");		

			Container.getInstance().ifStmtMap.put(stmt.getTarget(),ifStmt);
		}
		else if(!Container.getInstance().stmtMap.containsKey(stmt.getTarget())){
			ifStmt = new JIfStmt(ret, stmt.getTarget());
			Container.getInstance().currMethod.getActiveBody().getUnits().add(ifStmt);

			Container.getInstance().ifStmtMap.put(stmt.getTarget(),ifStmt);	
		}
		else{
			ifStmt = new JIfStmt(ret, Container.getInstance().stmtMap.get(stmt.getTarget()));	
			// System.out.println(Container.getInstance().stmtMap.get(stmt.getTarget()));		
			Container.getInstance().currMethod.getActiveBody().getUnits().add(ifStmt);
		}

		if(stmt.getBoxesPointingToThis().size()>0){
			if(!Container.getInstance().stmtMap.containsKey(stmt))
				Container.getInstance().stmtMap.put(stmt,label);

			if(Container.getInstance().ifStmtMap.containsKey(stmt)){
				if(Container.getInstance().ifStmtMap.get(stmt) instanceof JIfStmt)
					((JIfStmt) Container.getInstance().ifStmtMap.get(stmt)).setTarget(label);
				if(Container.getInstance().ifStmtMap.get(stmt) instanceof JGotoStmt)
					((JGotoStmt) Container.getInstance().ifStmtMap.get(stmt)).setTarget(label);
			}
		}
	}

	@Override
	public void caseReturnVoidStmt(ReturnVoidStmt stmt){
		// Container.getInstance().currMethod.getActiveBody().getUnits().add(stmt);
	}

	@Override
	public void caseGotoStmt(GotoStmt stmt){
		
		NopStmt label = new JNopStmt();
		if(stmt.getBoxesPointingToThis().size() > 0){
			Container.getInstance().currMethod.getActiveBody().getUnits().add(label);
		}

		JGotoStmt gotoStmt; 

		if(Container.getInstance().stmtMap == null){				//if stmt is not present in map, then add this if stmt to map and then later check for the stmt
			gotoStmt = new JGotoStmt(stmt.getTarget());
			Container.getInstance().currMethod.getActiveBody().getUnits().add(gotoStmt);

			Container.getInstance().ifStmtMap.put((Stmt) stmt.getTarget(),gotoStmt);
		}
		else if(!Container.getInstance().stmtMap.containsKey(stmt.getTarget())){
			gotoStmt = new JGotoStmt(stmt.getTarget());
			Container.getInstance().currMethod.getActiveBody().getUnits().add(gotoStmt);

			Container.getInstance().ifStmtMap.put((Stmt) stmt.getTarget(),gotoStmt);	
		}
		else{
			gotoStmt = new JGotoStmt(Container.getInstance().stmtMap.get(stmt.getTarget()));
			// ifStmt = new JIfStmt(ret, Container.getInstance().stmtMap.get(stmt.getTarget()));	
			// System.out.println(Container.getInstance().stmtMap.get(stmt.getTarget()));		
			Container.getInstance().currMethod.getActiveBody().getUnits().add(gotoStmt);
		}



		if(stmt.getBoxesPointingToThis().size()>0){
			if(!Container.getInstance().stmtMap.containsKey(stmt))
				Container.getInstance().stmtMap.put(stmt,label);

			if(Container.getInstance().ifStmtMap.containsKey(stmt)){
				if(Container.getInstance().ifStmtMap.get(stmt) instanceof JIfStmt)
					((JIfStmt) Container.getInstance().ifStmtMap.get(stmt)).setTarget(label);
				if(Container.getInstance().ifStmtMap.get(stmt) instanceof JGotoStmt)
					((JGotoStmt) Container.getInstance().ifStmtMap.get(stmt)).setTarget(label);
			}
		}
				// System.out.println("__________________________");

	}

	@Override
	public void caseIdentityStmt(IdentityStmt stmt){
		NopStmt label = new JNopStmt();
		if(stmt.getBoxesPointingToThis().size() > 0){
			Container.getInstance().currMethod.getActiveBody().getUnits().add(label);
		}

		CheckValue V = new CheckValue();
		stmt.getRightOp().apply(V);
		Value right = Container.getInstance().ret;

		stmt.getLeftOp().apply(V);
		Value left = Container.getInstance().ret;

		if(stmt.getRightOp() instanceof ParameterRef){
			JAssignStmt idenStmt = new JAssignStmt(left, right);
			Container.getInstance().currMethod.getActiveBody().getUnits().add(idenStmt);
		}
		else{
			JIdentityStmt idenStmt = new JIdentityStmt(left, right);
			Container.getInstance().currMethod.getActiveBody().getUnits().add(idenStmt);
		}

		String name = stmt.getLeftOp().toString();
		InstanceFieldRef ref2 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj, Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
		Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(ref2,left));

		if(stmt.getBoxesPointingToThis().size()>0){
			if(!Container.getInstance().stmtMap.containsKey(stmt))
				Container.getInstance().stmtMap.put(stmt,label);

			if(Container.getInstance().ifStmtMap.containsKey(stmt)){
				if(Container.getInstance().ifStmtMap.get(stmt) instanceof JIfStmt)
					((JIfStmt) Container.getInstance().ifStmtMap.get(stmt)).setTarget(label);
				if(Container.getInstance().ifStmtMap.get(stmt) instanceof JGotoStmt)
					((JGotoStmt) Container.getInstance().ifStmtMap.get(stmt)).setTarget(label);
			}
		}
	}

	@Override
	public void caseBreakpointStmt(BreakpointStmt stmt){}

	@Override
	public void caseEnterMonitorStmt(EnterMonitorStmt stmt){}

	@Override
	public void caseExitMonitorStmt(ExitMonitorStmt stmt){}

	@Override
	public void caseLookupSwitchStmt(LookupSwitchStmt stmt){}

	@Override
	public void caseNopStmt(NopStmt stmt){}

	@Override
	public void caseRetStmt(RetStmt stmt){

	}

	@Override
	public void caseReturnStmt(ReturnStmt stmt){
		// CheckValue V = new CheckValue();
		// stmt.getOp().apply(V);
		// JReturnStmt retStmt = new JReturnStmt(Container.getInstance().ret);
		// Container.getInstance().currMethod.getActiveBody().getUnits().add(retStmt);
	}

	@Override
	public void caseTableSwitchStmt(TableSwitchStmt stmt){}

	@Override
	public void caseThrowStmt(ThrowStmt stmt){}

	@Override
	public void defaultCase(Object st){
	}

}