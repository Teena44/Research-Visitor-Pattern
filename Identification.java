import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;

import soot.*;
import java.util.*;
import soot.jimple.spark.*;

import soot.Body;
import soot.SootClass;
import soot.PackManager;
import soot.PatchingChain;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.jimple.Stmt;
import soot.Type;
import soot.PatchingChain;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.jimple.InvokeExpr;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JDynamicInvokeExpr;
import soot.util.Chain;
import soot.jimple.toolkits.annotation.logic.LoopFinder;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.toolkits.graph.LoopNestTree;

public class Identification extends SceneTransformer {
	static CallGraph cg;
	static PointsToAnalysis analysis;
	public List<SootMethod> accept;
	public List<SootMethod> visit;
	public boolean vis;
	public int visitor;
	public List<SootMethod> dummy;

	@Override
	protected void internalTransform(String arg0, Map<String, String> arg1) {

		//set the PointsToAnalysis with phase arg1
		// loadClass("proc3",false);
		// loadClass("Cons",false);
		// loadClass("Nil",false);
		// loadClass("SumVisitor",false);

		// loadClass("ShoppingCartClient",false);
		// loadClass("Book",false);
		// loadClass("Fruit",false);
		// loadClass("ShoppingCartVisitorImpl",false);

		HashMap options = new HashMap();
		options.put("verbose", "true");
		options.put("propagator", "worklist");
		options.put("simple-edges-bidirectional", "false");
		options.put("on-fly-cg", "true");
		options.put("set-impl", "hybrid");
		options.put("double-set-old", "hybrid");
		options.put("double-set-new", "hybrid");
		SparkTransformer.v().transform("",options);
		analysis = Scene.v().getPointsToAnalysis();


		cg = Scene.v().getCallGraph();

		List<SootMethod> entryPoints = Scene.v().getEntryPoints();
		List<SootMethod> allMethods = methodsOfApplicationClasses();
		accept = new ArrayList();
		visit = new ArrayList();
		this.vis = false;
		this.visitor = 0;

		for( Iterator clIt = Scene.v().getApplicationClasses().iterator(); clIt.hasNext(); ) {
            final SootClass cl = (SootClass) clIt.next();
            for( Iterator mIt = cl.getMethods().iterator(); mIt.hasNext(); ) {
                final SootMethod m = (SootMethod) mIt.next();

                if( m.isConcrete() && m.getParameterCount() > 0 ) {	// check if the number of parameters of the function is more than 0
                	
                	Body body = m.getActiveBody();
                	UnitGraph G = new BriefUnitGraph(body);
					PatchingChain<Unit> units = body.getUnits();

					for (Unit u : units) {
						if (((soot.jimple.Stmt) u).containsInvokeExpr()) {
							if(check(u,m)){	
								if(!isVisit(body, m, u) && !m.getSubSignature().equals("void main(java.lang.String[])") && !accept.contains(m)){					//if the method m has an invoke statement
									
									accept.add(m);
								}
								else if(isVisit(body, m, u) && !m.getSubSignature().equals("void main(java.lang.String[])") && !visit.contains(m)){					//if the method m has an invoke statement
									
									visit.add(m);
								}
								
							}
						}
						vis = false;
					}

                }

            }
        }

        // for(int k = 0;k< accept.size();k++)
        // 	System.out.println(k+" "+accept.get(k));

        // for(int k = 0;k< visit.size();k++)
        // 	System.out.println(k+" "+visit.get(k));


        // List<SootMethod> finalMethods

        addInterfaceAcceptMethods();
        visit = methodOverloading(visit);

        System.out.println("ACCEPT METHODS");
        for(int k = 0;k< accept.size();k++)
        	System.out.println(k+" "+accept.get(k));

        System.out.println("VISIT METHODS");
        for(int k = 0;k< visit.size();k++)
        	System.out.println(k+" "+visit.get(k));

		System.out.println("The End");

		Container.getInstance().accept = accept;
		Container.getInstance().visit = visit;		
	}

	public void addInterfaceAcceptMethods(){
		int size = accept.size();
		for(int i=0;i<size;i++){
			SootClass currClass = accept.get(i).getDeclaringClass();
			for(SootClass c: currClass.getInterfaces()){
				if(c.declaresMethodByName(accept.get(i).getName()))
					if(!accept.contains(c.getMethodByName(accept.get(i).getName())))
						accept.add(c.getMethodByName(accept.get(i).getName()));
			}
		}
	}

	public boolean check(Unit u, SootMethod M){
		boolean flag = false;
		List<Type> params = M.getParameterTypes();			//all the parameters of the accept method
		for(Type t : params){
			for( Iterator clIt = Scene.v().getApplicationClasses().iterator(); clIt.hasNext(); ) {
            	final SootClass cl = (SootClass) clIt.next();
            	//iterate through all the classes
            	if(cl.getInterfaceCount() > 0){

            		Chain<SootClass> l = cl.getInterfaces();
            		for( Iterator lIt = l.iterator(); lIt.hasNext();){
            			final SootClass classT = (SootClass) lIt.next();	
            			if(classT.toString().equals(t.toString())){
            				
            				List<SootMethod> methodsofInterface = classT.getMethods();

            				InvokeExpr E = ((soot.jimple.Stmt) u).getInvokeExpr();
            				if(methodsofInterface.contains(E.getMethod())){
            					for( Iterator mIt = cl.getMethods().iterator(); mIt.hasNext(); ) {	//get all the methods of that class
					                final SootMethod m = (SootMethod) mIt.next();
					                if( m.isConcrete() ){		
					                				//isConcrete() returns true if method is not phantom or abstract 
					                	if((E.getMethod().getName().equals(m.getName()))&&((l.contains(E.getMethod().getDeclaringClass()))||(cl.getName().equals(E.getMethod().getDeclaringClass())))){		//checks if the method invoked in the accept function is a method of any of its parameters
					                		Body body = M.getActiveBody();

					                		if(thisParameter(M,body,E,u)){
					                			if(checkInvokedInvokes(M,u,m)){
					                				if(classT.isInterface()){
					                					this.vis = true;
					                				}
													return true;
					                			}
					                		}
					                	}
					                }
					        	}
            				}


            				flag = true;
            				break;
            			}
            		}
            			
            		if(t.toString().equals(cl.getName())){	//if a parameter type and a class match ; also change it to subclass/interface too
	            		for( Iterator mIt = cl.getMethods().iterator(); mIt.hasNext(); ) {	//get all the methods of that class
			                final SootMethod m = (SootMethod) mIt.next();
			                if( m.isConcrete() ){						//isConcrete() returns true if method is not phantom or abstract 
			                	
			                	InvokeExpr E = ((soot.jimple.Stmt) u).getInvokeExpr();		//get the invoke expression from u
			                	if((E.getMethod().getName().equals(m.getName()))&&((l.contains(E.getMethod().getDeclaringClass()))||(cl.getName().equals(E.getMethod().getDeclaringClass())))){		//checks if the method invoked in the accept function is a method of any of its parameters
			                		Body body = M.getActiveBody();
			                		// System.out.println(E);
			                	// System.out.println(E.getMethod().getDeclaringClass()+" "+l);

			                		if(thisParameter(M,body,E,u)){

			                			// System.out.println(M);
										if(checkInvokedInvokes(M,u,m)){
											if(cl.isInterface()){
												this.vis = true;
											}

			                				return true;
			                			}
			                		}
			                	}
			                	else{		//checks if the method invoked in the accept function is a method of any of its parameters
			                		Body body = M.getActiveBody();
			                		Chain<SootClass> intClass = E.getMethod().getDeclaringClass().getInterfaces();
			                		for( Iterator iter = intClass.iterator(); iter.hasNext();){
            							final SootClass fclass = (SootClass) iter.next();
            							if(l.contains(fclass)){
            								List<SootMethod> fclassMethods = new ArrayList();
            								fclassMethods = fclass.getMethods();
            								for(int sig = 0; sig< fclass.getMethods().size();sig++){
            									// System.out.println(fclassMethods.get(sig).getSubSignature()+" "+fclassMethods.get(sig).getReturnType()+" "+E.getMethod().getSubSignature()+" "+E.getMethod().getReturnType());
            									if(fclassMethods.get(sig).getSubSignature().equals(E.getMethod().getSubSignature()) && fclassMethods.get(sig).getReturnType().equals(E.getMethod().getReturnType())){
			                						if(thisParameter(M,body,E,u)){

						                			// System.out.println(M);
														if(checkInvokedInvokes(M,u,m)){
			                						// System.out.println(E.getMethod());

															if(cl.isInterface()){
																this.vis = true;
															}

							                				return true;
							                			}
						                			}
            									}
            								}

            							}
            						}

								}
			                }
			        	}
	            	}
            	}
            	else
            	{
            		if(t.toString().equals(cl.getName())){	//if a parameter type and a class match ; also change it to subclass/interface too
	            		
	            		for( Iterator mIt = cl.getMethods().iterator(); mIt.hasNext(); ) {	//get all the methods of that class
			                final SootMethod m = (SootMethod) mIt.next();
			                if( m.isConcrete() ){						//isConcrete() returns true if method is not phantom or abstract 
			                	InvokeExpr E = ((soot.jimple.Stmt) u).getInvokeExpr();		//get the invoke expression from u
			                	
			                	if((E.getMethod().getName().equals(m.getName()))&&(cl.getName().equals(E.getMethod().getDeclaringClass()))){			//checks if the method invoked in the accept function is a method of any of its parameters
			                		Body body = M.getActiveBody();
					                if(thisParameter(M,body,E,u))
			                			if(checkInvokedInvokes(M,u,m)){
			                				if(cl.isInterface()){
			                					this.vis = true;
			                				}
			                				return true;
			                			}
			                	}
			                }
			            }
	            	}
            	}
            	
        	}

		}
		return false;
	}

	public List<SootMethod> methodOverloading(List<SootMethod> visit){
		List<SootMethod> tempList = new ArrayList();

		for (SootMethod item : visit) {
			tempList.add(item);
		}

		for(Iterator it = tempList.iterator(); it.hasNext();){
			SootMethod foundM = (SootMethod) it.next();

			SootClass foundC = foundM.getDeclaringClass();
			Chain<SootClass> foundInter = foundC.getInterfaces();

			for(Iterator clit = foundInter.iterator(); clit.hasNext();){
				final SootClass cit = (SootClass) clit.next();

				if(cit.declaresMethod(foundM.getName(),foundM.getParameterTypes())){
					List<SootClass> subClasses = getSubClass(cit);

					for(Iterator scit = subClasses.iterator(); scit.hasNext();){
						SootClass sc = (SootClass) scit.next();
						if(sc.declaresMethodByName(foundM.getName())){
							for(Iterator mit = sc.methodIterator(); mit.hasNext();){
								SootMethod newMethod = (SootMethod) mit.next();
								if(newMethod.getName().equals(foundM.getName()))
									if(!visit.contains(newMethod))
										visit.add(newMethod);
							}
						}
					}
				}
			}
		}
		return visit;
	}

	public List<SootClass> getSubClass (SootClass supClass){
		List<SootClass> subClasses = new ArrayList();

		for( Iterator clIt = Scene.v().getApplicationClasses().iterator(); clIt.hasNext(); ) {
            final SootClass cl = (SootClass) clIt.next();
            if(cl.getInterfaces().contains(supClass))
            	subClasses.add(cl);
        }

        return subClasses;

	}

	public boolean thisParameter(SootMethod M, Body body, InvokeExpr E, Unit u){
		// System.out.println(M);
		Chain<Local> locals = body.getLocals();

		for( Iterator it = u.getUseBoxes().iterator(); it.hasNext();){
			ValueBox useB = (ValueBox) it.next();
			if(/*!useB.getValue().getType().toString().equals("void")*/!(useB.getValue() instanceof InvokeExpr) && analysis.reachingObjects(locals.getFirst()).hasNonEmptyIntersection(analysis.reachingObjects((Local) useB.getValue()))){
				if(E.getMethod().getParameterTypes().contains(useB.getValue().getType())){
						return true;					
				}

				for( Iterator clIt = Scene.v().getApplicationClasses().iterator(); clIt.hasNext(); ) {
            		final SootClass cl = (SootClass) clIt.next();
            		if(useB.getValue().getType().toString().equals(cl.getName())){
            			if(cl.getInterfaceCount()>0){
            				Chain<SootClass> l = cl.getInterfaces();
            				for( Iterator lIt = l.iterator(); lIt.hasNext();){
            					final SootClass classT = (SootClass) lIt.next();	
            					if(E.getMethod().getParameterTypes().toString().contains(classT.getName()))
            						return true;
            				}
            			}
            		}
            	}				
				
			}
        }
		return false;
	}

	public boolean checkInvokedInvokes(SootMethod M, Unit s, SootMethod m){ //if m invokes M

		if(m.hasActiveBody()){
		String phaseName;
			Body metBody = m.getActiveBody();
			UnitGraph gr = new BriefUnitGraph(metBody);
			PatchingChain<Unit> unitchain = metBody.getUnits();

			for (Unit ut : unitchain) {
				if (((soot.jimple.Stmt) ut).containsInvokeExpr()) {
					InvokeExpr ex = ((soot.jimple.Stmt) ut).getInvokeExpr();
					// System.out.println("here----"+M.getName()+" "+ex.getMethod().getName());
					List<Type> paramListM = M.getParameterTypes();
					List<Type> paramListm = ex.getMethod().getParameterTypes();
					if(M.getName().equals(ex.getMethod().getName()) && paramListM.equals(paramListm)){

						return true;
					}
					else{
						// System.out.println("check "+M.getName()+' '+ex.getMethod().getName());
					}
				}
			}
			return false;
		}
		else
		{
			// System.out.println("********************************************************************"+m);	
		}

		return false;
	}

	public boolean isVisit(Body B, SootMethod M, Unit U){
		int pc ;
		List<ValueBox> usedvalues = U.getUseBoxes();
		for( Iterator it = U.getUseBoxes().iterator(); it.hasNext();){
			ValueBox useB = (ValueBox) it.next();
			if(!(useB.getValue() instanceof InvokeExpr)){
				for(pc = 0; pc< M.getParameterCount();pc++){
					if(B.getParameterLocal(pc).equals(useB.getValue())){
						if(analysis.reachingObjects(B.getParameterLocal(pc)).hasNonEmptyIntersection(analysis.reachingObjects((Local) useB.getValue())))
							return false;
					}
				}
			}
		}

		return true;
	}

	public List methodsOfApplicationClasses() {
        List ret = new ArrayList();
        for( Iterator clIt = Scene.v().getApplicationClasses().iterator(); clIt.hasNext(); ) {
            final SootClass cl = (SootClass) clIt.next();
            for( Iterator mIt = cl.getMethods().iterator(); mIt.hasNext(); ) {
                final SootMethod m = (SootMethod) mIt.next();
                if( m.isConcrete() ) ret.add( m );
            }
        }
        return ret;
    }
	
	protected static void processCFG(SootMethod method) {
		if(!method.isJavaLibraryMethod()){
			Body body = method.getActiveBody();
			UnitGraph cfg = new BriefUnitGraph(body);
			PatchingChain<Unit> units = body.getUnits();
			
			
			for (Unit u : units) {
				if (u instanceof JAssignStmt) {
					// System.out.println("AssignStmt: " + u);
				}
			}	
			System.out.println();
	}
	}
	private static SootClass loadClass(String name, boolean main) {
		SootClass c = Scene.v().loadClassAndSupport(name);
		c.setApplicationClass();
		if (main) Scene.v().setMainClass(c);
		return c;
	}
}
