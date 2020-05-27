import soot.PackManager;
import soot.Transform;
import java.lang.CharSequence;
import soot.SootMethod;
import soot.SootClass;
import java.util.List;
import soot.util.Chain;
import java.util.ArrayList;
import soot.Body;
import soot.Local;
import soot.SootField;
import soot.Unit;
import soot.Value;
import soot.jimple.internal.JIfStmt;
import soot.jimple.internal.JNopStmt;
import soot.jimple.*;
import java.util.*;

class Container{

  public List<SootMethod> accept;
  public List<SootMethod> visit;
  public String objClass;
  public SootMethod currMethod;
  public SootMethod visitMeth;
  public Local obj;
  public int index;
  public boolean left,right;
  public Unit unit;
  public Value ret;
  public Value val;
  public Map<Stmt, Stmt> stmtMap;
  public Map<Stmt, Stmt> ifStmtMap;
  public int labelIndex;
  public JimpleBody currBody;
  public int genIndex;
  public List<SootMethod> closureMethods;
  public List<SootClass> closureClass;
  public Chain<SootField> Globals;
  public SootClass translateClass;

  private static Container instance = null;
  private void Container(){

  }
  public static Container getInstance(){
    if(instance==null){
       instance = new Container();
      }
      return instance;
  }
}

public class MainC {

	public static void main(String[] args) {
		String classPath = ".";

		// Container.getInstance().x = 3;

		String[] sootArgs = {
			"-cp", classPath,"-w", 
			"-f", "J",
			"-keep-line-number",
			"-no-bodies-for-excluded",
			// "-p", "jb", "use-original-names",
			// "-p","cg","enabled:true",
		   //enable Spark
			"-p","cg.spark","enabled:true",
      "-process-dir", "ApplicationClasses"
			// "-main-class","proc3","proc3","SumVisitor","SumVisitor","Nil","Nil","Cons","Cons"
		};

		PackManager.v().getPack("wjtp").add(new Transform("wjtp.sample", new Identification()));
		PackManager.v().getPack("wjtp").add(new Transform("wjtp.sample2", new Translation()));		
		// PackManager.v().getPack("wjtp").add(new Transform("wjtp.sample2", new CT()));

		soot.Main.main(sootArgs);
	}
}

//"ShoppingCartClient","ShoppingCartClient","Book","Book","Fruit","Fruit","ShoppingCartVisitorImpl","ShoppingCartVisitorImpl"
//"proc3","proc3","SumVisitor","SumVisitor","Nil","Nil","Cons","Cons"