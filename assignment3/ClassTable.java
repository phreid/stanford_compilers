import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Enumeration;
import java.util.Set;

/** This class may be used to contain the semantic information such as
 * the inheritance graph.  You may use it or not as you like: it is only
 * here to provide a container for the supplied methods.  */
class ClassTable {
    private int semantErrors;
    private PrintStream errorStream;

    private HashMap<AbstractSymbol, class_c> classMap;
    private HashMap<AbstractSymbol, 
	HashMap<AbstractSymbol, ArrayList<AbstractSymbol>>> methodMap;
    private HashMap<AbstractSymbol, 
	HashMap<AbstractSymbol, AbstractSymbol>> attrMap;
    private HashMap<AbstractSymbol, ArrayList<AbstractSymbol>> iGraph;

    /** Creates data structures representing basic Cool classes (Object,
     * IO, Int, Bool, String).  Please note: as is this method does not
     * do anything useful; you will need to edit it to make if do what
     * you want.
     * */
    private void installBasicClasses() {
	AbstractSymbol filename 
	    = AbstractTable.stringtable.addString("<basic class>");
	
	// The following demonstrates how to create dummy parse trees to
	// refer to basic Cool classes.  There's no need for method
	// bodies -- these are already built into the runtime system.

	// IMPORTANT: The results of the following expressions are
	// stored in local variables.  You will want to do something
	// with those variables at the end of this method to make this
	// code meaningful.

	// The Object class has no parent class. Its methods are
	//        cool_abort() : Object    aborts the program
	//        type_name() : Str        returns a string representation 
	//                                 of class name
	//        copy() : SELF_TYPE       returns a copy of the object

	class_c Object_class = 
	    new class_c(0, 
		       TreeConstants.Object_, 
		       TreeConstants.No_class,
		       new Features(0)
			   .appendElement(new method(0, 
					      TreeConstants.cool_abort, 
					      new Formals(0), 
					      TreeConstants.Object_, 
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.type_name,
					      new Formals(0),
					      TreeConstants.Str,
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.copy,
					      new Formals(0),
					      TreeConstants.SELF_TYPE,
					      new no_expr(0))),
		       filename);
	
	// The IO class inherits from Object. Its methods are
	//        out_string(Str) : SELF_TYPE  writes a string to the output
	//        out_int(Int) : SELF_TYPE      "    an int    "  "     "
	//        in_string() : Str            reads a string from the input
	//        in_int() : Int                "   an int     "  "     "

	class_c IO_class = 
	    new class_c(0,
		       TreeConstants.IO,
		       TreeConstants.Object_,
		       new Features(0)
			   .appendElement(new method(0,
					      TreeConstants.out_string,
					      new Formals(0)
						  .appendElement(new formalc(0,
								     TreeConstants.arg,
								     TreeConstants.Str)),
					      TreeConstants.SELF_TYPE,
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.out_int,
					      new Formals(0)
						  .appendElement(new formalc(0,
								     TreeConstants.arg,
								     TreeConstants.Int)),
					      TreeConstants.SELF_TYPE,
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.in_string,
					      new Formals(0),
					      TreeConstants.Str,
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.in_int,
					      new Formals(0),
					      TreeConstants.Int,
					      new no_expr(0))),
		       filename);

	// The Int class has no methods and only a single attribute, the
	// "val" for the integer.

	class_c Int_class = 
	    new class_c(0,
		       TreeConstants.Int,
		       TreeConstants.Object_,
		       new Features(0)
			   .appendElement(new attr(0,
					    TreeConstants.val,
					    TreeConstants.prim_slot,
					    new no_expr(0))),
		       filename);

	// Bool also has only the "val" slot.
	class_c Bool_class = 
	    new class_c(0,
		       TreeConstants.Bool,
		       TreeConstants.Object_,
		       new Features(0)
			   .appendElement(new attr(0,
					    TreeConstants.val,
					    TreeConstants.prim_slot,
					    new no_expr(0))),
		       filename);

	// The class Str has a number of slots and operations:
	//       val                              the length of the string
	//       str_field                        the string itself
	//       length() : Int                   returns length of the string
	//       concat(arg: Str) : Str           performs string concatenation
	//       substr(arg: Int, arg2: Int): Str substring selection

	class_c Str_class =
	    new class_c(0,
		       TreeConstants.Str,
		       TreeConstants.Object_,
		       new Features(0)
			   .appendElement(new attr(0,
					    TreeConstants.val,
					    TreeConstants.Int,
					    new no_expr(0)))
			   .appendElement(new attr(0,
					    TreeConstants.str_field,
					    TreeConstants.prim_slot,
					    new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.length,
					      new Formals(0),
					      TreeConstants.Int,
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.concat,
					      new Formals(0)
						  .appendElement(new formalc(0,
								     TreeConstants.arg, 
								     TreeConstants.Str)),
					      TreeConstants.Str,
					      new no_expr(0)))
			   .appendElement(new method(0,
					      TreeConstants.substr,
					      new Formals(0)
						  .appendElement(new formalc(0,
								     TreeConstants.arg,
								     TreeConstants.Int))
						  .appendElement(new formalc(0,
								     TreeConstants.arg2,
								     TreeConstants.Int)),
					      TreeConstants.Str,
					      new no_expr(0))),
		       filename);

	/* Do somethind with Object_class, IO_class, Int_class,
           Bool_class, and Str_class here */
	classMap.put(Object_class.getName(), Object_class);
	classMap.put(IO_class.getName(), IO_class);
	classMap.put(Int_class.getName(), Int_class);
	classMap.put(Str_class.getName(), Str_class);
	classMap.put(Bool_class.getName(), Bool_class);

	for (AbstractSymbol name : classMap.keySet()) {
	    addClassFeatures(name);
	}
    }
	
    public ClassTable(Classes cls) {
	semantErrors = 0;
	errorStream = System.err;
	
	/* fill this in */
	classMap = new HashMap<AbstractSymbol, class_c>();
	methodMap = new HashMap<AbstractSymbol, 
	    HashMap<AbstractSymbol, ArrayList<AbstractSymbol>>>();
	attrMap = new HashMap<AbstractSymbol, 
	    HashMap<AbstractSymbol, AbstractSymbol>>();
	installBasicClasses();

	for (Enumeration e = cls.getElements(); e.hasMoreElements(); ) {
	    class_c c = (class_c) e.nextElement();
	    
	    AbstractSymbol parent = c.getParent();
	    if (parent.equals(TreeConstants.Str) ||
		parent.equals(TreeConstants.Int) ||
		parent.equals(TreeConstants.IO)) {
		errorStream = semantError(c);
		errorStream.println("Cannot inherit from basic class " 
				    + parent.toString());
	    }
	    
	    classMap.put(c.getName(), c);
	    addClassFeatures(c.getName());
	}
            
	buildInheritanceGraph();
    }

    private void addClassFeatures(AbstractSymbol name) {
	methodMap.put(name, 
		      new HashMap<AbstractSymbol,
		      ArrayList<AbstractSymbol>>());
	attrMap.put(name, 
		    new HashMap<AbstractSymbol, AbstractSymbol>());
	
	AbstractSymbol curName = name;
	Stack<class_c> stack = new Stack<class_c>();
	while (! curName.equals(TreeConstants.No_class)) {
	    class_c cls = classMap.get(curName);
	    stack.push(cls);
	    curName = cls.getParent();
	}

	while (! stack.isEmpty()) {
	    Features feats = stack.pop().getFeatures();
	    for (Enumeration e1 = feats.getElements(); e1.hasMoreElements(); ) {
		Feature feat = (Feature) e1.nextElement();
		if (feat instanceof method) {
		    method m = (method) feat;
		    methodMap.get(name).put(m.getName(),
					    new ArrayList<AbstractSymbol>());
		    
		    Formals formals = m.getFormals();
		    for (Enumeration e2 = formals.getElements(); 
			 e2.hasMoreElements(); ) {
			formalc formal = (formalc) e2.nextElement();
			methodMap.
			    get(name).
			    get(m.getName()).
			    add(formal.getType());
		    }
		    
		    methodMap.get(name).get(m.getName()).add(m.getReturnType());
		}

		if (feat instanceof attr) {
		    attr a = (attr) feat;
		    attrMap.get(name).put(a.getName(), a.getType());
		}
	    }
	}
	
    }

    private void buildInheritanceGraph() {
	iGraph = new HashMap<AbstractSymbol, 
	    ArrayList<AbstractSymbol>>();

	for (AbstractSymbol name : classMap.keySet()) {
	    AbstractSymbol parent = classMap.get(name).getParent();
	    
	    if (! classMap.containsKey(parent) &&
		! parent.equals(TreeConstants.No_class)) {
		errorStream = semantError(classMap.get(name));
		errorStream.println(name.toString() + " cannot inherit from" +
				    " undefined class " + parent.toString());
				   
	    }
	    
	    if (! iGraph.containsKey(parent)) {
		iGraph.put(parent, new ArrayList<AbstractSymbol>());
	    }

	    iGraph.get(parent).add(name);
	}

	checkCycles();
    }

    private boolean checkCycles() {
	HashSet<AbstractSymbol> white = new HashSet<AbstractSymbol>();
	HashSet<AbstractSymbol> grey = new HashSet<AbstractSymbol>();
       
	for (AbstractSymbol node : iGraph.keySet()) {
	    white.add(node);
	    for (AbstractSymbol child : iGraph.get(node)) {
		white.add(node);
	    }
	}

	for (AbstractSymbol node: iGraph.keySet()) {
	    if (white.contains(node)) {
		if (dfs(node, white, grey) == true) {
		    errorStream = semantError(classMap.get(node));
		    errorStream.println("Cyclic inheritance");
		    return true;
		}
	    }
	}

	return false;
    }

    private boolean dfs(AbstractSymbol node, 
			HashSet<AbstractSymbol> white, 
			HashSet<AbstractSymbol> grey) {
	white.remove(node);
	grey.add(node);

	for (AbstractSymbol child : iGraph.get(node)) {
	    if (grey.contains(child)) { return true; }
	    
	    if (white.contains(child) && 
		dfs(child, white, grey) == true) {
		return true;
	    }
	}

	grey.remove(node);
	return false;
    }

    public Set<AbstractSymbol> getNames() {
	return classMap.keySet();
    }

    public class_c get(AbstractSymbol name) {
	return classMap.get(name);
    }

    public boolean hasMethod(AbstractSymbol cName, AbstractSymbol mName) {
	return methodMap.get(cName).get(mName) == null;
    }

    public ArrayList<AbstractSymbol> getMethodSig(AbstractSymbol cName, 
						  AbstractSymbol mName) {
	class_c cls = classMap.get(cName);
	if (cls == null) { return null; }

	return methodMap.get(cName).get(mName);
    }

    public Set<AbstractSymbol> getAttributes(AbstractSymbol cName) {
	return attrMap.get(cName).keySet();
    }

    public AbstractSymbol getAttrType(AbstractSymbol cName, 
				      AbstractSymbol aName) {
	return attrMap.get(cName).get(aName);
    }

    public boolean isSubType(AbstractSymbol child, AbstractSymbol parent) {
	HashSet<AbstractSymbol> visited = new HashSet<AbstractSymbol>();
		
	return dfsTarget(parent, child, visited);
    }

    private boolean dfsTarget(AbstractSymbol node, AbstractSymbol target, 
			      HashSet<AbstractSymbol> visited) {
	visited.add(node);
		
	if (node.equals(target)) { return true; }
	
      	if (iGraph.containsKey(node)) {
	    for (AbstractSymbol child : iGraph.get(node)) {
		if (! visited.contains(child)) {
		    if (dfsTarget(child, target, visited)) { return true; }
		}
	    }
	}

	return false;
    }

    public AbstractSymbol lca(AbstractSymbol s1, AbstractSymbol s2) {
	HashSet<AbstractSymbol> parents = new HashSet<AbstractSymbol>();

	while (! s1.equals(TreeConstants.No_class)) {
	    parents.add(s1);
	    s1 = classMap.get(s1).getParent();  
	}
		
	while (! s2.equals(TreeConstants.No_class)) {
	    if (parents.contains(s2)) { return s2; }
	    s2 = classMap.get(s2).getParent();
	}

	return null;
    }

     /** Prints line number and file name of the given class.
     *
     * Also increments semantic error count.
     *
     * @param c the class
     * @return a print stream to which the rest of the error message is
     * to be printed.
     *
     * */
    public PrintStream semantError(class_c c) {
	return semantError(c.getFilename(), c);
    }

    /** Prints the file name and the line number of the given tree node.
     *
     * Also increments semantic error count.
     *
     * @param filename the file name
     * @param t the tree node
     * @return a print stream to which the rest of the error message is
     * to be printed.
     *
     * */
    public PrintStream semantError(AbstractSymbol filename, TreeNode t) {
	errorStream.print(filename + ":" + t.getLineNumber() + ": ");
	return semantError();
    }

    /** Increments semantic error count and returns the print stream for
     * error messages.
     *
     * @return a print stream to which the error message is
     * to be printed.
     *
     * */
    public PrintStream semantError() {
	semantErrors++;
	return errorStream;
    }

    /** Returns true if there are any static semantic errors. */
    public boolean errors() {
	return semantErrors != 0;
    }
}
			  
    
