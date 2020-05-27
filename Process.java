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

public class Process {

	public static void main(String[] args) {
		String classPath = ".";

		// Container.getInstance().x = 3;

		String[] sootArgs = {
			"-cp", classPath,"-w", 
			"-f", "class",     //the output format
			"-keep-line-number",
			"-no-bodies-for-excluded",
			// "-p", "jb", "use-original-names",
			// "-p","cg","enabled:true",
		   //enable Spark
      "-src-prec","jimple",   //the type of files to process
			"-p","cg.spark","enabled:true",
      "-process-dir", "sootOutput"
			// "-main-class","proc3","proc3","SumVisitor","SumVisitor","Nil","Nil","Cons","Cons"
		};

		// PackManager.v().getPack("wjtp").add(new Transform("wjtp.sample", new Identification()));
		// PackManager.v().getPack("wjtp").add(new Transform("wjtp.sample2", new Translation()));		

		soot.Main.main(sootArgs);
	}
}

//"ShoppingCartClient","ShoppingCartClient","Book","Book","Fruit","Fruit","ShoppingCartVisitorImpl","ShoppingCartVisitorImpl"
//"proc3","proc3","SumVisitor","SumVisitor","Nil","Nil","Cons","Cons"