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
import soot.jimple.internal.*;
import soot.dava.internal.SET.SETNodeLabel;
import java.lang.Object;

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
import soot.jimple.internal.JAddExpr;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JInstanceOfExpr;

public class CheckValue implements JimpleValueSwitch{

@Override
 public void caseAddExpr(AddExpr v){
 	Value v1 = v.getOp1();
 	Value v2 = v.getOp2();
 	Value tz0,tz1;
 	String name;
 	Type t;

 	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp1())){
	 	name = v1.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz0 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz0);
		InstanceFieldRef ref0 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
	 	Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz0,ref0));			//tz = Obj<r>
	}
	else
	{
		tz0 = v1;
	}

	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp2())){
	 	name = v2.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz1 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz1);
		InstanceFieldRef ref1 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
		Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz1,ref1));
	}
	else{
		tz1 = v2;
	}

 	JAddExpr expr = new JAddExpr(tz0,tz1); 
 	Container.getInstance().ret = expr;
 }

@Override           
 public void caseAndExpr(AndExpr v){
 	Value v1 = v.getOp1();
 	Value v2 = v.getOp2();
 	Value tz0,tz1;
 	String name;
 	Type t;

 	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp1())){
	 	name = v1.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz0 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz0);
		InstanceFieldRef ref0 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
	 	Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz0,ref0));			//tz = Obj<r>
	}
	else
	{
		tz0 = v1;
	}

	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp2())){
	 	name = v2.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz1 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz1);
		InstanceFieldRef ref1 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
		Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz1,ref1));
	}
	else{
		tz1 = v2;
	}

 	JAndExpr expr = new JAndExpr(tz0,tz1); 
 	Container.getInstance().ret = expr;
 }

@Override           
 public void caseCastExpr(CastExpr V){
 	String name = V.getOp().toString();
 	Type t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
 	Local tz = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
 	Container.getInstance().currMethod.getActiveBody().getLocals().add(tz);
 	InstanceFieldRef ref = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
 	Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz,ref));

 	JCastExpr expr = new JCastExpr(tz,V.getCastType());
 	Container.getInstance().ret = expr; 
 }
 
 @Override          
 public void caseCmpExpr(CmpExpr v){
 	Value v1 = v.getOp1();
 	Value v2 = v.getOp2();
 	Value tz0,tz1;
 	String name;
 	Type t;

 	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp1())){
	 	name = v1.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz0 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz0);
		InstanceFieldRef ref0 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
	 	Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz0,ref0));			//tz = Obj<r>
	}
	else
	{
		tz0 = v1;
	}

	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp2())){
	 	name = v2.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz1 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz1);
		InstanceFieldRef ref1 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
		Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz1,ref1));
	}
	else{
		tz1 = v2;
	}

 	JCmpExpr expr = new JCmpExpr(tz0,tz1); 
 	Container.getInstance().ret = expr;
 }

@Override           
 public void caseCmpgExpr(CmpgExpr v){
 	Value v1 = v.getOp1();
 	Value v2 = v.getOp2();
 	Value tz0,tz1;
 	String name;
 	Type t;

 	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp1())){
	 	name = v1.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz0 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz0);
		InstanceFieldRef ref0 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
	 	Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz0,ref0));			//tz = Obj<r>
	}
	else
	{
		tz0 = v1;
	}

	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp2())){
	 	name = v2.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz1 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz1);
		InstanceFieldRef ref1 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
		Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz1,ref1));
	}
	else{
		tz1 = v2;
	}

 	JCmpgExpr expr = new JCmpgExpr(tz0,tz1); 
 	Container.getInstance().ret = expr;
 }

@Override           
 public void caseCmplExpr(CmplExpr v){
 	Value v1 = v.getOp1();
 	Value v2 = v.getOp2();
 	Value tz0,tz1;
 	String name;
 	Type t;

 	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp1())){
	 	name = v1.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz0 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz0);
		InstanceFieldRef ref0 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
	 	Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz0,ref0));			//tz = Obj<r>
	}
	else
	{
		tz0 = v1;
	}

	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp2())){
	 	name = v2.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz1 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz1);
		InstanceFieldRef ref1 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
		Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz1,ref1));
	}
	else{
		tz1 = v2;
	}

 	JCmplExpr expr = new JCmplExpr(tz0,tz1); 
 	Container.getInstance().ret = expr;
 }
 
 @Override          
 public void caseDivExpr(DivExpr v){
 	Value v1 = v.getOp1();
 	Value v2 = v.getOp2();
 	Value tz0,tz1;
 	String name;
 	Type t;

 	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp1())){
	 	name = v1.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz0 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz0);
		InstanceFieldRef ref0 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
	 	Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz0,ref0));			//tz = Obj<r>
	}
	else
	{
		tz0 = v1;
	}

	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp2())){
	 	name = v2.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz1 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz1);
		InstanceFieldRef ref1 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
		Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz1,ref1));
	}
	else{
		tz1 = v2;
	}

 	JDivExpr expr = new JDivExpr(tz0,tz1); 
 	Container.getInstance().ret = expr;
 }
  
  @Override         
 public void caseDynamicInvokeExpr(DynamicInvokeExpr v){}
 
 @Override          
 public void caseEqExpr(EqExpr v){Value v1 = v.getOp1();
 	Value v2 = v.getOp2();
 	Value tz0,tz1;
 	String name;
 	Type t;
 	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp1())){
	 	name = v1.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz0 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz0);
		InstanceFieldRef ref0 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
	 	Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz0,ref0));			//tz = Obj<r>
	}
	else
	{
		tz0 = v1;
	}

	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp2())){
	 	name = v2.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz1 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz1);
		InstanceFieldRef ref1 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
		Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz1,ref1));
	}
	else{
		tz1 = v2;
	}

 	JEqExpr expr = new JEqExpr(tz0,tz1); 
 	Container.getInstance().ret = expr;
 }
 
 @Override          
 public void caseGeExpr(GeExpr v){
 	Value v1 = v.getOp1();
 	Value v2 = v.getOp2();
 	Value tz0,tz1;
 	String name;
 	Type t;

 	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp1())){
	 	name = v1.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz0 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz0);
		InstanceFieldRef ref0 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
	 	Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz0,ref0));			//tz = Obj<r>
	}
	else
	{
		tz0 = v1;
	}

	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp2())){
	 	name = v2.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz1 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz1);
		InstanceFieldRef ref1 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
		Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz1,ref1));
	}
	else{
		tz1 = v2;
	}

 	JGeExpr expr = new JGeExpr(tz0,tz1); 
 	Container.getInstance().ret = expr;
 }
 
 @Override          
 public void caseGtExpr(GtExpr v){
 	Value v1 = v.getOp1();
 	Value v2 = v.getOp2();
 	Value tz0,tz1;
 	String name;
 	Type t;

 	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp1())){
	 	name = v1.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz0 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz0);
		InstanceFieldRef ref0 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
	 	Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz0,ref0));			//tz = Obj<r>
	}
	else
	{
		tz0 = v1;
	}

	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp2())){
	 	name = v2.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz1 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz1);
		InstanceFieldRef ref1 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
		Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz1,ref1));
	}
	else{
		tz1 = v2;
	}

 	JGtExpr expr = new JGtExpr(tz0,tz1); 
 	Container.getInstance().ret = expr;
 }
 
 @Override          
 public void caseInstanceOfExpr(InstanceOfExpr v){
 	String name;
 	Type t;
 	Value v1 = v.getOp();
 	Value tz0;

 	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp())){
	 	name = v1.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz0 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz0);
		InstanceFieldRef ref0 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
	 	Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz0,ref0));			//tz = Obj<r>
	}
	else
	{
		tz0 = v1;
	}

	JInstanceOfExpr expr = new JInstanceOfExpr(tz0,v.getCheckType()); 
 	Container.getInstance().ret = expr;
 }
 
 @Override          
 public void caseInterfaceInvokeExpr(InterfaceInvokeExpr V){
 	String name = V.getBase().toString();
 	Type t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
 	Local tz = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
 	Container.getInstance().currMethod.getActiveBody().getLocals().add(tz);
 	InstanceFieldRef ref = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
	Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz,ref));
 	
 	List<Value> args = V.getArgs(); 
 	List<Value> newArgs = new ArrayList<Value>();
 	Value tz0;

 	for(int i = 0;i <args.size(); i++){
 		if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(args.get(i))){
 			name = args.get(i).toString();
 			t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
 			tz0 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
 			Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz0);
 			InstanceFieldRef ref0 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
			Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz0,ref0));
 		}
 		else{
 			tz0 = args.get(i);
 		}
 		newArgs.add(tz0);
 	}	
 	JInterfaceInvokeExpr Expr = new JInterfaceInvokeExpr((Local) tz, V.getMethodRef(), newArgs);
 	Container.getInstance().ret = Expr;
 }
 
 @Override          
 public void caseLeExpr(LeExpr v){
 	Value v1 = v.getOp1();
 	Value v2 = v.getOp2();
 	Value tz0,tz1;
 	String name;
 	Type t;

 	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp1())){
	 	name = v1.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz0 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz0);
		InstanceFieldRef ref0 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
	 	Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz0,ref0));			//tz = Obj<r>
	}
	else
	{
		tz0 = v1;
	}

	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp2())){
	 	name = v2.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz1 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz1);
		InstanceFieldRef ref1 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
		Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz1,ref1));
	}
	else{
		tz1 = v2;
	}

 	JLeExpr expr = new JLeExpr(tz0,tz1); 
 	Container.getInstance().ret = expr;
 }
 
 @Override          
 public void caseLengthExpr(LengthExpr v){
 	String name;
 	Type t;
 	Value v1 = v.getOp();
 	Value tz0;
 	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp())){
	 	name = v1.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz0 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz0);
		InstanceFieldRef ref0 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
	 	Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz0,ref0));			//tz = Obj<r>
	}
	else
	{
		tz0 = v1;
	}

	JLengthExpr expr = new JLengthExpr(tz0); 
 	Container.getInstance().ret = expr;
 }
 
 @Override          
 public void caseLtExpr(LtExpr v){
 	Value v1 = v.getOp1();
 	Value v2 = v.getOp2();
 	Value tz0,tz1;
 	String name;
 	Type t;

 	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp1())){
	 	name = v1.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz0 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz0);
		InstanceFieldRef ref0 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
	 	Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz0,ref0));			//tz = Obj<r>
	}
	else
	{
		tz0 = v1;
	}

	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp2())){
	 	name = v2.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz1 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz1);
		InstanceFieldRef ref1 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
		Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz1,ref1));
	}
	else{
		tz1 = v2;
	}

 	JLeExpr expr = new JLeExpr(tz0,tz1); 
 	Container.getInstance().ret = expr;
 }
 
 @Override          
 public void caseMulExpr(MulExpr v){
 	Value v1 = v.getOp1();
 	Value v2 = v.getOp2();
 	Value tz0,tz1;
 	String name;
 	Type t;

 	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp1())){
	 	name = v1.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz0 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz0);
		InstanceFieldRef ref0 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
	 	Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz0,ref0));			//tz = Obj<r>
	}
	else
	{
		tz0 = v1;
	}

	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp2())){
	 	name = v2.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz1 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz1);
		InstanceFieldRef ref1 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
		Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz1,ref1));
	}
	else{
		tz1 = v2;
	}

 	JMulExpr expr = new JMulExpr(tz0,tz1); 
 	Container.getInstance().ret = expr;
 }
 
 @Override          
 public void caseNeExpr(NeExpr v){
 	Value v1 = v.getOp1();
 	Value v2 = v.getOp2();
 	Value tz0,tz1;
 	String name;
 	Type t;

 	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp1())){
	 	name = v1.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz0 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz0);
		InstanceFieldRef ref0 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
	 	Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz0,ref0));			//tz = Obj<r>
	}
	else
	{
		tz0 = v1;
	}

	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp2())){
	 	name = v2.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz1 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz1);
		InstanceFieldRef ref1 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
		Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz1,ref1));
	}
	else{
		tz1 = v2;
	}

 	JNeExpr expr = new JNeExpr(tz0,tz1); 
 	Container.getInstance().ret = expr;
 }

@Override           
 public void caseNegExpr(NegExpr v){
 	String name;
 	Type t;
 	Value v1 = v.getOp();
 	Value tz0;
 	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp())){
	 	name = v1.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz0 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz0);
		InstanceFieldRef ref0 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
	 	Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz0,ref0));			//tz = Obj<r>
	}
	else
	{
		tz0 = v1;
	}

	JNegExpr expr = new JNegExpr(tz0); 
 	Container.getInstance().ret = expr;
 }
 
 @Override          
 public void caseNewArrayExpr(NewArrayExpr v){
 	Container.getInstance().ret = v;
 }
 
 @Override          
 public void caseNewExpr(NewExpr V){
 	Container.getInstance().ret = V;
 }
 
 @Override          
 public void caseNewMultiArrayExpr(NewMultiArrayExpr v){
 	Container.getInstance().ret = v;
 }
 
 @Override          
 public void caseOrExpr(OrExpr v){
 	Value v1 = v.getOp1();
 	Value v2 = v.getOp2();
 	Value tz0,tz1;
 	String name;
 	Type t;

 	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp1())){
	 	name = v1.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz0 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz0);
		InstanceFieldRef ref0 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
	 	Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz0,ref0));			//tz = Obj<r>
	}
	else
	{
		tz0 = v1;
	}

	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp2())){
	 	name = v2.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz1 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz1);
		InstanceFieldRef ref1 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
		Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz1,ref1));
	}
	else{
		tz1 = v2;
	}

 	JOrExpr expr = new JOrExpr(tz0,tz1); 
 	Container.getInstance().ret = expr;
 }
 
 @Override          
 public void caseRemExpr(RemExpr v){
 	Value v1 = v.getOp1();
 	Value v2 = v.getOp2();
 	Value tz0,tz1;
 	String name;
 	Type t;

 	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp1())){
	 	name = v1.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz0 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz0);
		InstanceFieldRef ref0 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
	 	Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz0,ref0));			//tz = Obj<r>
	}
	else
	{
		tz0 = v1;
	}

	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp2())){
	 	name = v2.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz1 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz1);
		InstanceFieldRef ref1 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
		Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz1,ref1));
	}
	else{
		tz1 = v2;
	}

 	JRemExpr expr = new JRemExpr(tz0,tz1); 
 	Container.getInstance().ret = expr;
 }
 
 @Override          
 public void caseShlExpr(ShlExpr v){
 	Value v1 = v.getOp1();
 	Value v2 = v.getOp2();
 	Value tz0,tz1;
 	String name;
 	Type t;

 	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp1())){
	 	name = v1.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz0 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz0);
		InstanceFieldRef ref0 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
	 	Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz0,ref0));			//tz = Obj<r>
	}
	else
	{
		tz0 = v1;
	}

	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp2())){
	 	name = v2.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz1 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz1);
		InstanceFieldRef ref1 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
		Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz1,ref1));
	}
	else{
		tz1 = v2;
	}

 	JShlExpr expr = new JShlExpr(tz0,tz1); 
 	Container.getInstance().ret = expr;
 }
  
  @Override         
 public void caseShrExpr(ShrExpr v){
 	Value v1 = v.getOp1();
 	Value v2 = v.getOp2();
 	Value tz0,tz1;
 	String name;
 	Type t;

 	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp1())){
	 	name = v1.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz0 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz0);
		InstanceFieldRef ref0 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
	 	Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz0,ref0));			//tz = Obj<r>
	}
	else
	{
		tz0 = v1;
	}

	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp2())){
	 	name = v2.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz1 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz1);
		InstanceFieldRef ref1 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
		Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz1,ref1));
	}
	else{
		tz1 = v2;
	}

 	JShrExpr expr = new JShrExpr(tz0,tz1); 
 	Container.getInstance().ret = expr;
 }
 
 @Override          
 public void caseSpecialInvokeExpr(SpecialInvokeExpr V){
 	String name = V.getBase().toString();
 	Type t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
 	Local tz = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
 	Container.getInstance().currMethod.getActiveBody().getLocals().add(tz);
 	InstanceFieldRef ref = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
	Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz,ref));
 	
 	List<Value> args = V.getArgs(); 
 	List<Value> newArgs = new ArrayList<Value>();
 	Value tz0;
 	for(int i = 0;i <args.size(); i++){
 		if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(args.get(i))){
 			name = args.get(i).toString();
 			t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
 			tz0 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
 			Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz0);
 			InstanceFieldRef ref0 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
			Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz0,ref0));
 		}
 		else{
 			tz0 = args.get(i);
 		}
 		newArgs.add(tz0);
 	}	
 	JSpecialInvokeExpr Expr = new JSpecialInvokeExpr((Local) tz, V.getMethodRef(), newArgs);
 	Container.getInstance().ret = Expr;
 }
 
 @Override          
 public void caseStaticInvokeExpr(StaticInvokeExpr V){
 	String name;
 	Type t;

 	List<Value> args = V.getArgs(); 
 	List<Value> newArgs = new ArrayList<Value>();
 	Value tz0;
 	for(int i = 0;i <args.size(); i++){
 		if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(args.get(i))){
 			name = args.get(i).toString();
 			t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
 			tz0 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
 			Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz0);
 			InstanceFieldRef ref0 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
			Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz0,ref0));
 		}
 		else{
 			tz0 = args.get(i);
 		}
 		newArgs.add(tz0);
 	}	
 	JStaticInvokeExpr Expr = new JStaticInvokeExpr(V.getMethodRef(), newArgs);
 	Container.getInstance().ret = Expr;
 }
 
 @Override          
 public void caseSubExpr(SubExpr v){
 	Value v1 = v.getOp1();
 	Value v2 = v.getOp2();
 	Value tz0,tz1;
 	String name;
 	Type t;

 	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp1())){
	 	name = v1.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz0 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz0);
		InstanceFieldRef ref0 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
	 	Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz0,ref0));			//tz = Obj<r>
	}
	else
	{
		tz0 = v1;
	}

	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp2())){
	 	name = v2.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz1 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz1);
		InstanceFieldRef ref1 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
		Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz1,ref1));
	}
	else{
		tz1 = v2;
	}

 	JSubExpr expr = new JSubExpr(tz0,tz1); 
 	Container.getInstance().ret = expr;
 }
 
 @Override          
 public void caseUshrExpr(UshrExpr v){
 	Value v1 = v.getOp1();
 	Value v2 = v.getOp2();
 	Value tz0,tz1;
 	String name;
 	Type t;

 	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp1())){
	 	name = v1.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz0 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz0);
		InstanceFieldRef ref0 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
	 	Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz0,ref0));			//tz = Obj<r>
	}
	else
	{
		tz0 = v1;
	}

	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp2())){
	 	name = v2.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz1 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz1);
		InstanceFieldRef ref1 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
		Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz1,ref1));
	}
	else{
		tz1 = v2;
	}

 	JUshrExpr expr = new JUshrExpr(tz0,tz1); 
 	Container.getInstance().ret = expr;
 }
 
 @Override          
 public void caseVirtualInvokeExpr(VirtualInvokeExpr V){
 	String name = V.getBase().toString();
 	Type t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
 	Local tz = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
 	Container.getInstance().currMethod.getActiveBody().getLocals().add(tz);
 	InstanceFieldRef ref = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
	Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz,ref));
 	
 	List<Value> args = V.getArgs(); 
 	List<Value> newArgs = new ArrayList<Value>();
 	Value tz0;
 	for(int i = 0;i <args.size(); i++){
 		if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(args.get(i))){
 			name = args.get(i).toString();
 			t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
 			tz0 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
 			Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz0);
 			InstanceFieldRef ref0 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
			Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz0,ref0));
 		}
 		else{
 			tz0 = args.get(i);
 		}
 		newArgs.add(tz0);
 	}	
 	JVirtualInvokeExpr Expr = new JVirtualInvokeExpr((Local) tz, V.getMethodRef(), newArgs);
 	Container.getInstance().ret = Expr;
 }
 
 @Override          
 public void caseXorExpr(XorExpr v){
 	Value v1 = v.getOp1();
 	Value v2 = v.getOp2();
 	Value tz0,tz1;
 	String name;
 	Type t;

 	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp1())){
	 	name = v1.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz0 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz0);
		InstanceFieldRef ref0 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
	 	Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz0,ref0));			//tz = Obj<r>
	}
	else
	{
		tz0 = v1;
	}

	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getOp2())){
	 	name = v2.toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz1 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz1);
		InstanceFieldRef ref1 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
		Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz1,ref1));
	}
	else{
		tz1 = v2;
	}

 	JXorExpr expr = new JXorExpr(tz0,tz1); 
 	Container.getInstance().ret = expr;
 }

 @Override
 public void caseLocal(Local l){
 	String name = l.toString();
 	Type t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
 	Local tz0 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz0);
	InstanceFieldRef ref0 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
	Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz0,ref0));

	Container.getInstance().ret = tz0;			
 }

@Override
 public void caseArrayRef(ArrayRef v){
 	//x[i]
 	//tz0 = x;
 	String name = v.getBase().toString();
 	Type t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
 	Local tz0 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz0);
	InstanceFieldRef ref0 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
	Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz0,ref0));
	//tz1 = i;
	Value tz1;
	if(Container.getInstance().visitMeth.getActiveBody().getLocals().contains(v.getIndex())){
	 	name = v.getIndex().toString();
	 	t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
	 	tz1 = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
	 	Container.getInstance().currMethod.getActiveBody().getLocals().add((Local) tz1);
		InstanceFieldRef ref1 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
		Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz1,ref1));
	}
	else{
		tz1 = v.getIndex();
	}

	JArrayRef expr = new JArrayRef(tz0,tz1);
	Container.getInstance().ret = expr;
 }

@Override           
 public void caseCaughtExceptionRef(CaughtExceptionRef v){}
 
 @Override          
 public void caseInstanceFieldRef(InstanceFieldRef V){
 	
 		if(Container.getInstance().right){
			String name = V.getBase().toString();
			InstanceFieldRef ref1 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
			Type t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
			Local tz = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);
			Container.getInstance().currMethod.getActiveBody().getLocals().add(tz);
			InstanceFieldRef ref = Jimple.v().newInstanceFieldRef(tz,V.getFieldRef());

			Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz,ref1));
			Container.getInstance().ret = ref;
			//Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(stmt.getLeftOp(),ref));

		}
		if(Container.getInstance().left){
			String name = V.getBase().toString();
			Type t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
			Local tz = Jimple.v().newLocal("tz"+Container.getInstance().index++,t);

			InstanceFieldRef ref0 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
			Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz,ref0));

			InstanceFieldRef ref = Jimple.v().newInstanceFieldRef(tz,V.getFieldRef());
			Container.getInstance().ret = ref;
			Container.getInstance().currMethod.getActiveBody().getLocals().add(tz);
			
			// Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(ref, stmt.getRightOp()));				

			// InstanceFieldRef ref1 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj,Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());

			// Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(ref1,tz));
		}
	// }
 }
 
 @Override          
 public void caseParameterRef(ParameterRef V){
 	String name = "param"+V.getIndex();
 	// Type t = Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).getType();
 	// Local tz = Jimple.v().newLocal("tz"+Container.getInstance().index++, t);
 	// Container.getInstance().currMethod.getActiveBody().getLocals().add(tz);

 	InstanceFieldRef ref0 = Jimple.v().newInstanceFieldRef(Container.getInstance().obj, Scene.v().getSootClass(Container.getInstance().objClass+"_Obj").getFieldByName(name).makeRef());
 	// Container.getInstance().currMethod.getActiveBody().getUnits().add(Jimple.v().newAssignStmt(tz, ref0));

 	Container.getInstance().ret = ref0;
 }

@Override           
 public void caseStaticFieldRef(StaticFieldRef v){
 	Container.getInstance().ret = v;
 }

@Override           
 public void caseThisRef(ThisRef v){
 	Container.getInstance().ret = v;
 }

@Override
 public void caseClassConstant(ClassConstant v){}

@Override           
 public void caseDoubleConstant(DoubleConstant v){
 	Container.getInstance().ret = v;
 }

@Override           
 public void caseFloatConstant(FloatConstant v){
 	Container.getInstance().ret = v;
 }

@Override           
 public void caseIntConstant(IntConstant v){
 	Container.getInstance().ret = v;
 }

@Override           
 public void caseLongConstant(LongConstant v){
 	Container.getInstance().ret = v;
 }

@Override           
 public void caseNullConstant(NullConstant v){
 	Container.getInstance().ret = v;
 }

@Override           
 public void caseStringConstant(StringConstant v){
 	Container.getInstance().ret = v;
 }

 @Override
 public void caseMethodHandle(MethodHandle m){}

@Override           
 public void defaultCase(Object object){}
           
}
