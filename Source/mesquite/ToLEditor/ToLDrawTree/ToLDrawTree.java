/* Mesquite source code.  Copyright 1997-2007 W. Maddison and D. Maddison.
Version 2.0, September 2007.
Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. 
The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.
Perhaps with your help we can be more than a few, and make Mesquite better.

Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.
Mesquite's web site is http://mesquiteproject.org

This source code and its compiled class files are free and modifiable under the terms of 
GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)
 */
package mesquite.ToLEditor.ToLDrawTree;

import java.util.*;
import java.awt.*;

import mesquite.lib.*;
import mesquite.lib.duties.*;
import mesquite.trees.lib.*;

/* ======================================================================== */
public class ToLDrawTree extends DrawTree {
	public void getEmployeeNeeds(){  //This gets called on startup to harvest information; override this and inside, call registerEmployeeNeed
		EmployeeNeed e = registerEmployeeNeed(NodeLocsVH.class, getName() + "  needs the locations of nodes to be calculated.",
		"The calculator for node locations is chosen automatically or initially");
	}
	NodeLocsVH nodeLocsTask;
	MesquiteCommand edgeWidthCommand;
	Vector drawings;
	int oldEdgeWidth = 4;
	int ornt;
	double shortcut = 0.0; //used of for eurogram 
	double shortcutDegree = 0.4;
	MesquiteBoolean cutCorners;
	MesquiteString nodeLocsName;
	/*.................................................................................................................*/
	public boolean startJob(String arguments, Object condition, boolean hiredByName) {
		drawings = new Vector();
		nodeLocsTask= (NodeLocsVH)hireNamedEmployee(NodeLocsVH.class, "#NodeLocsStandard");
		if (nodeLocsTask == null)
			return sorry(getName() + " couldn't start because no node location module was obtained.");
		nodeLocsName = new MesquiteString(nodeLocsTask.getName());
		if (numModulesAvailable(NodeLocsVH.class)>1){
			MesquiteSubmenuSpec mss = addSubmenu(null, "Node Locations Calculator", makeCommand("setNodeLocs", this), NodeLocsVH.class);
			mss.setSelected(nodeLocsName);
		}
		cutCorners = new MesquiteBoolean(false);
		addCheckMenuItem(null, "Cut Corners", makeCommand("toggleCorners", this), cutCorners);

		ornt = nodeLocsTask.getDefaultOrientation();
		ornt = TreeDisplay.RIGHT;
		addMenuItem( "Line Width...", makeCommand("setEdgeWidth",  this));
		return true;
	}

	public void employeeQuit(MesquiteModule m){
		iQuit();
	}
	public   TreeDrawing createTreeDrawing(TreeDisplay treeDisplay, int numTaxa) {
		ToLTreeDrawing treeDrawing =  new ToLTreeDrawing (treeDisplay, numTaxa, this);
		treeDrawing.reorient(TreeDisplay.RIGHT);
		if (legalOrientation(treeDisplay.getOrientation())){
			ornt = TreeDisplay.RIGHT;
		}
		else
			treeDisplay.setOrientation(ornt);
		drawings.addElement(treeDrawing);
		return treeDrawing;
	}
	public boolean legalOrientation (int orientation){
		return (orientation == TreeDisplay.UP || orientation == TreeDisplay.DOWN || orientation == TreeDisplay.RIGHT || orientation == TreeDisplay.LEFT);
	}
	/*.................................................................................................................*/
	public String orient (int orientation){
		if (orientation == TreeDisplay.UP)
			return "Up";
		else if (orientation == TreeDisplay.DOWN)
			return "Down";
		else if (orientation == TreeDisplay.RIGHT)
			return "Right";
		else if (orientation == TreeDisplay.LEFT)
			return "Left";
		else return "other";
	}
	/*.................................................................................................................*/
	public Snapshot getSnapshot(MesquiteFile file) { 
		Snapshot temp = new Snapshot();
		temp.addLine("setNodeLocs", nodeLocsTask);
		temp.addLine("setEdgeWidth " + oldEdgeWidth); 
		if (ornt== TreeDisplay.UP)
			temp.addLine("orientUp"); 
		else if (ornt== TreeDisplay.DOWN)
			temp.addLine("orientDown"); 
		else if (ornt== TreeDisplay.LEFT)
			temp.addLine("orientLeft"); 
		else if (ornt== TreeDisplay.RIGHT)
			temp.addLine("orientRight"); 
		temp.addLine("toggleCorners " + cutCorners.toOffOnString());
		return temp;
	}
	MesquiteInteger pos = new MesquiteInteger();
	/*.................................................................................................................*/
	public Object doCommand(String commandName, String arguments, CommandChecker checker) {
		if (checker.compare(this.getClass(), "Sets the node locations calculator", "[name of module]", commandName, "setNodeLocs")) {
			NodeLocsVH temp = (NodeLocsVH)replaceEmployee(NodeLocsVH.class, arguments, "Node Locations Calculator", nodeLocsTask);
			if (temp != null) {
				nodeLocsTask = temp;
				nodeLocsName.setValue(nodeLocsTask.getName());
				parametersChanged();
			}
			return nodeLocsTask;
		}
		else	if (checker.compare(this.getClass(), "Sets the thickness of drawn branches", "[width in pixels]", commandName, "setEdgeWidth")) {
			int newWidth= MesquiteInteger.fromFirstToken(arguments, pos);
			if (!MesquiteInteger.isCombinable(newWidth))
				newWidth = MesquiteInteger.queryInteger(containerOfModule(), "Set edge width", "Edge Width:", oldEdgeWidth, 1, 99);
			if (newWidth>0 && newWidth<100 && newWidth!=oldEdgeWidth) {
				oldEdgeWidth=newWidth;
				Enumeration e = drawings.elements();
				while (e.hasMoreElements()) {
					Object obj = e.nextElement();
					ToLTreeDrawing treeDrawing = (ToLTreeDrawing)obj;
					treeDrawing.setEdgeWidth(newWidth);
					treeDrawing.treeDisplay.setMinimumTaxonNameDistance(treeDrawing.edgewidth, 5); //better if only did this if tracing on
				}
				if (!MesquiteThread.isScripting()) parametersChanged();
			}

		}
		else if (checker.compare(this.getClass(), "Sets whether or not corners are cut", "[on = cut; off]", commandName, "toggleCorners")) {
			cutCorners.toggleValue(parser.getFirstToken(arguments));
			if (cutCorners.getValue())
				shortcut = shortcutDegree;
			else
				shortcut = 0.0;
			Enumeration e = drawings.elements();
			while (e.hasMoreElements()) {
				Object obj = e.nextElement();
				ToLTreeDrawing treeDrawing = (ToLTreeDrawing)obj;
				treeDrawing.shortcut = this.shortcut;
			}
			if (!MesquiteThread.isScripting()) parametersChanged();
		}
		else if (checker.compare(this.getClass(), "Returns the module calculating node locations", null, commandName, "getNodeLocsEmployee")) {
			return nodeLocsTask;
		}
		else if (checker.compare(this.getClass(), "Orients the tree drawing so that the terminal taxa are at right", null, commandName, "orientRight")) {
			Enumeration e = drawings.elements();
			ornt = 0;
			while (e.hasMoreElements()) {
				Object obj = e.nextElement();
				ToLTreeDrawing treeDrawing = (ToLTreeDrawing)obj;
				treeDrawing.reorient(TreeDisplay.RIGHT);
				ornt = treeDrawing.treeDisplay.getOrientation();
			}
			parametersChanged();
		}
		else return  super.doCommand(commandName, arguments, checker);
		return null;
	}
	/*.................................................................................................................*/
	public String getName() {
		return "ToL Tree";
	}
	/*.................................................................................................................*/
	/** returns whether this module is requesting to appear as a primary choice */
	public boolean requestPrimaryChoice(){
		return true;  
	}
	/*.................................................................................................................*/
	public String getVersion() {
		return null;
	}
	/*.................................................................................................................*/

	/** returns an explanation of what the module does.*/
	public String getExplanation() {
		return "Draws trees with standard square branches (\"phenogram\")" ;
	}
	/*.................................................................................................................*/

}


/* ======================================================================== */
class ToLTreeDrawing extends TreeDrawing   {
	public Polygon[] branchPoly;
	public Polygon[] touchPoly;
	public Polygon[] fillBranchPoly;

	public ToLDrawTree ownerModule;
	public int edgewidth = 6;
	public int preferredEdgeWidth = 6;
	int oldNumTaxa = 0;
	private int foundBranch;
	private boolean ready=false;
	private static final int  inset=1;
	private Polygon utilityPolygon;
	NameReference triangleNameRef;
	double shortcut;

	public ToLTreeDrawing(TreeDisplay treeDisplay, int numTaxa, ToLDrawTree ownerModule) {
		super(treeDisplay, MesquiteTree.standardNumNodeSpaces(numTaxa));
		treeDisplay.setMinimumTaxonNameDistance(edgewidth, 5); //better if only did this if tracing on
		this.ownerModule = ownerModule;
		this.treeDisplay = treeDisplay;
		triangleNameRef = NameReference.getNameReference("triangled");

		shortcut = ownerModule.shortcut;

		oldNumTaxa = numTaxa;
		ready = true;
		utilityPolygon=new Polygon();
		utilityPolygon.xpoints = new int[16];
		utilityPolygon.ypoints = new int[16];
		utilityPolygon.npoints=16;

	}

	public void resetNumNodes(int numNodes){
		super.resetNumNodes(numNodes);
		branchPoly= new Polygon[numNodes];
		touchPoly= new Polygon[numNodes];
		fillBranchPoly= new Polygon[numNodes];
		for (int i=0; i<numNodes; i++) {
			branchPoly[i] = new Polygon();
			branchPoly[i].xpoints = new int[16];
			branchPoly[i].ypoints = new int[16];
			branchPoly[i].npoints=16;
			touchPoly[i] = new Polygon();
			touchPoly[i].xpoints = new int[16];
			touchPoly[i].ypoints = new int[16];
			touchPoly[i].npoints=16;
			fillBranchPoly[i] = new Polygon();
			fillBranchPoly[i].xpoints = new int[16];
			fillBranchPoly[i].ypoints = new int[16];
			fillBranchPoly[i].npoints=16;
		}		
	}
	/*_________________________________________________*/
	private int length(int node, int mother){
		return x[node]-x[mother];
	}
	private int getShortcutOfDaughters(Tree tree, int node){
		if (shortcut == 0)
			return 0;
		int s = MesquiteInteger.unassigned;
		for (int d = tree.firstDaughterOfNode(node); tree.nodeExists(d); d = tree.nextSisterOfNode(d))
			s = MesquiteInteger.minimum(s, length(d, node));  //find shortest descendant
		return (int)(shortcut*s);
	}
	/*_________________________________________________*/
	//makes polygon clockwise
	private void RIGHTdefineFillPoly(Polygon poly, boolean isRoot, int Nx, int Ny, int mNx, int mNy, int sliceNumber, int numSlices,  int nShortcut) {
		int sliceWidth=edgewidth;
		if (numSlices>1) {
			Ny+= (sliceNumber-1)*(edgewidth-inset)/numSlices;
			sliceWidth=(edgewidth-inset)-((sliceNumber-1)*(edgewidth-inset)/numSlices);
		}
		if (isRoot) {
			poly.npoints=0;
			poly.addPoint(Nx-inset, Ny+inset);// root left
			poly.addPoint(Nx-inset, Ny+sliceWidth-inset);	//root right 
			poly.addPoint(mNx, Ny+sliceWidth-inset); //subroot right
			poly.addPoint(mNx, Ny+inset); //subroot left
			poly.addPoint(Nx-inset, Ny+inset); //return to root left
			poly.npoints=4;
		}
		else if (Ny<mNy) //leans left
		{
			poly.npoints=0;
			//if (numSlices>1)
			//	mNx+= (sliceNumber-1)*(edgewidth-inset)/numSlices;
			if (numSlices>1)
				mNx-=inset;
			poly.addPoint(Nx-inset, Ny+inset); // daughter left
			poly.addPoint(Nx-inset, Ny+sliceWidth-inset);	//daughter right
			poly.addPoint(mNx-inset + nShortcut, Ny+sliceWidth-inset);//corner right
			poly.addPoint(mNx-inset, mNy +sliceWidth-inset); //mother up * on y
			poly.addPoint(mNx-sliceWidth+inset, mNy); //mother down
			poly.addPoint(mNx-sliceWidth+inset + nShortcut, Ny+inset); //corner left
			poly.addPoint(Nx-inset, Ny+inset); //return to daughter left
			poly.npoints=7;
		}
		else {
			poly.npoints=0;
			if (numSlices>1)
				mNx-= (sliceNumber-1)*(edgewidth-inset)/numSlices;
			poly.addPoint(Nx-inset, Ny+inset);// daughter left
			poly.addPoint(Nx-inset, Ny+sliceWidth-inset);//daughter right
			poly.addPoint(mNx-sliceWidth+inset + nShortcut, Ny+sliceWidth-inset);//corner right
			poly.addPoint(mNx-sliceWidth+inset, mNy+sliceWidth-inset);//mother down * on y
			poly.addPoint(mNx-inset, mNy);//mother up
			poly.addPoint(mNx-inset + nShortcut, Ny+inset); //corner left
			poly.addPoint(Nx-inset, Ny+inset); //return to daughter left
			poly.npoints=7;
		}
	}
	/*_________________________________________________*/
	private void RIGHTCalcFillBranchPolys(Tree tree, int node, int nShortcut)
	{
		if (!tree.getAssociatedBit(triangleNameRef,node)) {
			int dShortcut = getShortcutOfDaughters(tree, node);
			for (int d = tree.firstDaughterOfNode(node); tree.nodeExists(d); d = tree.nextSisterOfNode(d))
				RIGHTCalcFillBranchPolys(tree, d, dShortcut);
		}
		RIGHTdefineFillPoly(fillBranchPoly[node], (node==tree.getRoot()), x[node], y[node], x[tree.motherOfNode(node)], y[tree.motherOfNode(node)], 0, 0,  nShortcut);
	}
	/*_________________________________________________*/
	private void RIGHTCalcBranchPolys(Tree tree, int node, int nShortcut, Polygon[] polys, int width)
	{
		if (!tree.getAssociatedBit(triangleNameRef,node)){
			int dShortcut = getShortcutOfDaughters(tree, node);
			for (int d = tree.firstDaughterOfNode(node); tree.nodeExists(d); d = tree.nextSisterOfNode(d))
				RIGHTCalcBranchPolys(tree, d, dShortcut, polys, width);
		}
		DrawTreeUtil.RIGHTdefineSquarePoly(this,polys[node], width, (node==tree.getRoot()), x[node], y[node], x[tree.motherOfNode(node)], y[tree.motherOfNode(node)],  nShortcut);
	}
	/*_________________________________________________*/
	private void LEFTRIGHTcalculateLines(Tree tree, int node) {
		for (int d = tree.firstDaughterOfNode(node); tree.nodeExists(d); d = tree.nextSisterOfNode(d))
			LEFTRIGHTcalculateLines( tree, d);
		lineTipY[node]=y[node];
		lineTipX[node]=x[node];
		lineBaseY[node]=y[node];
		lineBaseX[node]=x[tree.motherOfNode(node)];
	}
	int oops = 0;
	/*_________________________________________________*/
	private void calcBranches(Tree tree, int drawnRoot) {
		if (ownerModule==null) {MesquiteTrunk.mesquiteTrunk.logln("ownerModule null"); return;}
		if (ownerModule.nodeLocsTask==null) {ownerModule.logln("nodelocs task null"); return;}
		if (treeDisplay==null) {ownerModule.logln("treeDisplay null"); return;}
		if (tree==null) { ownerModule.logln("tree null"); return;}

		ownerModule.nodeLocsTask.calculateNodeLocs(treeDisplay,  tree, drawnRoot,  treeDisplay.getField()); //Graphics g removed as parameter May 02
		edgewidth = preferredEdgeWidth;
		if (treeDisplay.getTaxonSpacing()<edgewidth+2) {
			edgewidth= treeDisplay.getTaxonSpacing()-2;
			if (edgewidth<2)
				edgewidth=2;
		}
		treeDisplay.setMinimumTaxonNameDistance(edgewidth, 5);
		RIGHTCalcBranchPolys(tree, drawnRoot, oops, branchPoly, getEdgeWidth());
		RIGHTCalcBranchPolys(tree, drawnRoot, oops, touchPoly, getNodeWidth());
		RIGHTCalcFillBranchPolys(tree, drawnRoot, oops);
		LEFTRIGHTcalculateLines(tree, drawnRoot);
	}

	/*New version, accounting for width of drawn branches*/
	public void getMiddleOfBranch(Tree tree, int N, MesquiteNumber xValue, MesquiteNumber yValue, MesquiteDouble angle){
		if(tree==null || xValue==null || yValue==null)
			return;
		if(!tree.nodeExists(N))
			return;
		int mother = tree.motherOfNode(N);
		xValue.setValue(x[mother]+(x[N]-x[mother])/2 - getEdgeWidth()/2);
		yValue.setValue(y[N] + getEdgeWidth()/2);
		angle.setValue(0.0);
	}
	/*_________________________________________________*/
	private   void drawOneBranch(Tree tree, Graphics g, int node) {
		if (tree.nodeExists(node)) {
			//g.setColor(Color.black);//for testing

			g.setColor(treeDisplay.getBranchColor(node));
			g.setColor(Color.gray);
			if ((tree.getRooted() || tree.getRoot()!=node) && branchPoly[node] != null){
				if (SHOWTOUCHPOLYS && touchPoly!=null && touchPoly[node]!=null) {  //fordebugging
					Color prev = g.getColor();
					g.setColor(ColorDistribution.burlyWood);
					g.fillPolygon(touchPoly[node]);
					g.setColor(prev);
				}
				g.fillPolygon(branchPoly[node]);
			}

			if (tree.numberOfParentsOfNode(node)>1) {
				for (int i=1; i<=tree.numberOfParentsOfNode(node); i++) {
					int anc =tree.parentOfNode(node, i);
					if (anc!= tree.motherOfNode(node)) {
						g.drawLine(x[node],y[node], x[tree.parentOfNode(node, i)],y[tree.parentOfNode(node, i)]);
						g.drawLine(x[node]+1,y[node], x[tree.parentOfNode(node, i)]+1,y[tree.parentOfNode(node, i)]);
						g.drawLine(x[node],y[node]+1, x[tree.parentOfNode(node, i)],y[tree.parentOfNode(node, i)]+1);
						g.drawLine(x[node]+1,y[node]+1, x[tree.parentOfNode(node, i)]+1,y[tree.parentOfNode(node, i)]+1);
					}
				}
			}
			if (tree.getAssociatedBit(triangleNameRef,node)) {
				for (int j=0; j<2; j++)
					for (int i=0; i<2; i++) {
						g.drawLine(x[node]+i,y[node]+j, x[tree.leftmostTerminalOfNode(node)]+i,y[tree.leftmostTerminalOfNode(node)]+j);
						g.drawLine(x[tree.leftmostTerminalOfNode(node)]+i,y[tree.leftmostTerminalOfNode(node)]+j, x[tree.rightmostTerminalOfNode(node)]+i,y[tree.rightmostTerminalOfNode(node)]+j);
						g.drawLine(x[node]+i,y[node]+j, x[tree.rightmostTerminalOfNode(node)]+i,y[tree.rightmostTerminalOfNode(node)]+j);
					}
			}

			if (!tree.getAssociatedBit(triangleNameRef,node))
				for (int d = tree.firstDaughterOfNode(node); tree.nodeExists(d); d = tree.nextSisterOfNode(d))
					drawOneBranch(tree, g, d);
			//g.setColor(Color.green);//for testing
			//g.fillPolygon(fillBranchPoly[node]); //for testing
			//g.setColor(Color.black);//for testing
			//	redCrosses(g, tree, node);

			if (emphasizeNodes()) {
				Color prev = g.getColor();
				g.setColor(Color.red);//for testing
				g.fillPolygon(nodePoly(node));
				g.setColor(prev);
			}
		}
	}
	/*_________________________________________________*/
	public   void drawTree(Tree tree, int drawnRoot, Graphics g) {
		if (MesquiteTree.OK(tree)) {
			if (tree.getNumNodeSpaces()!=numNodes)
				resetNumNodes(tree.getNumNodeSpaces());
			g.setColor(treeDisplay.branchColor);
			/*if (oldNumTaxa!= tree.getNumTaxa())
	        		adjustNumTaxa(tree.getNumTaxa()); */
			drawOneBranch(tree, g, drawnRoot);  
		}
	}
	/*_________________________________________________*/
	public   void recalculatePositions(Tree tree) {
		if (MesquiteTree.OK(tree)) {
			if (tree.getNumNodeSpaces()!=numNodes)
				resetNumNodes(tree.getNumNodeSpaces());
			if (!tree.nodeExists(getDrawnRoot()))
				setDrawnRoot(tree.getRoot());
			calcBranches(tree, getDrawnRoot());
		}
	}
	/*_________________________________________________*/
	/** Draw highlight for branch node with current color of graphics context */
	public void drawHighlight(Tree tree, int node, Graphics g, boolean flip){
		Color tC = g.getColor();
		if (flip)
			g.setColor(Color.red);
		else
			g.setColor(Color.blue);
		for (int i=0; i<4; i++)
			g.drawLine(x[node], y[node]-2 - i, x[tree.motherOfNode(node)], y[node]-2 - i);

		g.setColor(tC);
	}
	/*_________________________________________________*/
	public  void fillTerminalBox(Tree tree, int node, Graphics g) {
		Rectangle box;
		int ew = edgewidth-1;
		box = new Rectangle(x[node]+1, y[node], ew, ew);
		g.fillRect(box.x, box.y, box.width, box.height);
		g.setColor(treeDisplay.getBranchColor(node));
		g.drawRect(box.x, box.y, box.width, box.height);
	}
	/*_________________________________________________*/
	public  void fillTerminalBoxWithColors(Tree tree, int node, ColorDistribution colors, Graphics g){
		Rectangle box;
		int numColors = colors.getNumColors();
		int ew = edgewidth-1;
		box = new Rectangle(x[node]+1, y[node], ew, ew);
		for (int i=0; i<numColors; i++) {
			g.setColor(colors.getColor(i, !tree.anySelected()|| tree.getSelected(node)));
			g.fillRect(box.x + (i*box.width/numColors), box.y, box.width-  (i*box.width/numColors), box.height);
		}
		g.setColor(treeDisplay.getBranchColor(node));
		g.drawRect(box.x, box.y, box.width, box.height);
	}
	/*_________________________________________________*/
	public  int findTerminalBox(Tree tree, int drawnRoot, int x, int y){
		return -1;
	}
	/*_________________________________________________*/
	private boolean ancestorIsTriangled(Tree tree, int node) {
		if (tree.getAssociatedBit(triangleNameRef, tree.motherOfNode(node)))
			return true;
		if (tree.getRoot() == node || tree.getSubRoot() == node)
			return false;
		return ancestorIsTriangled(tree, tree.motherOfNode(node));
	}
	/*_________________________________________________*/
	public   void fillBranch(Tree tree, int node, Graphics g) {
		if (node>0 && (tree.getRooted() || tree.getRoot()!=node) && !ancestorIsTriangled(tree, node) && node<fillBranchPoly.length)
			g.fillPolygon(fillBranchPoly[node]);  //CRASHES HERE ON MOUSE ENTER BRANCH
	}

	/** Fill branch N with indicated set of colors as a sequence, e.g. for stochastic character mapping.  This is not abstract because many tree drawers would have difficulty implementing it */
	public void fillBranchWithColorSequence(Tree tree, int node, ColorEventVector colors, Graphics g){
		if (node>0 && (tree.getRooted() || tree.getRoot()!=node) && !ancestorIsTriangled(tree, node)) {
			Color c = g.getColor();
			int numEvents = colors.size();
			int nShortcut = getShortcutOfDaughters(tree, tree.motherOfNode(node));
			int desc = x[node];
			int anc = x[tree.motherOfNode(node)];
			for (int i=numEvents-1; i>=0; i--) {
				ColorEvent e = (ColorEvent)colors.elementAt(i);
				double pos;
				if (i == numEvents-1)
					pos = 1.0;
				else {
					ColorEvent ec = (ColorEvent)colors.elementAt(i+1);
					pos = ec.getPosition();
				}
				RIGHTdefineFillPoly(utilityPolygon, (node==tree.getRoot()),  anc- (int)(pos*(anc-desc)), y[node], x[tree.motherOfNode(node)], y[tree.motherOfNode(node)], 0, 1, nShortcut);
				g.setColor(e.getColor());
				g.fillPolygon(utilityPolygon);
			}
			if (c!=null) g.setColor(c);
		}
	}
	/*_________________________________________________*/
	public void fillBranchWithColors(Tree tree, int node, ColorDistribution colors, Graphics g) {
		if (node>0 && (tree.getRooted() || tree.getRoot()!=node) && !ancestorIsTriangled(tree, node)) {
			Color c = g.getColor();
			int numColors = colors.getNumColors();
			int nShortcut = getShortcutOfDaughters(tree, tree.motherOfNode(node));
			for (int i=0; i<numColors; i++) {
				RIGHTdefineFillPoly(utilityPolygon, (node==tree.getRoot()), x[node], y[node], x[tree.motherOfNode(node)], y[tree.motherOfNode(node)], i+1, numColors, nShortcut);
				Color color;
				if ((color = colors.getColor(i, !tree.anySelected()|| tree.getSelected(node)))!=null)
					g.setColor(color);
				g.fillPolygon(utilityPolygon);
			}
			if (c!=null) g.setColor(c);
		}
	}
	/*_________________________________________________*/
	public Polygon nodePoly(int node) {
		int offset = (getNodeWidth()-getEdgeWidth())/2;
		int doubleOffset = (getNodeWidth()-getEdgeWidth());
		int startX = x[node] - offset;
		int startY= y[node] - offset;
		startX -= getNodeWidth()-doubleOffset;
		Polygon poly = new Polygon();
		poly.npoints=0;
		poly.addPoint(startX,startY);
		poly.addPoint(startX+getNodeWidth(),startY);
		poly.addPoint(startX+getNodeWidth(),startY+getNodeWidth());
		poly.addPoint(startX,startY+getNodeWidth());
		poly.addPoint(startX,startY);
		poly.npoints=5;
		return poly;
	}
	/*_________________________________________________*/
	public boolean inNode(int node, int x, int y){
		Polygon nodeP = nodePoly(node);
		if (nodeP!=null && nodeP.contains(x,y))
			return true;
		else
			return false;
	}
	/*_________________________________________________*/
	private void ScanBranches(Tree tree, Polygon[] polys, int node, int x, int y, MesquiteDouble fraction)
	{
		if (foundBranch==0) {
			if (polys != null && polys[node] != null && polys[node].contains(x, y) || inNode(node,x,y)){
				foundBranch = node;
				if (fraction!=null)
					if (inNode(node,x,y))
						fraction.setValue(ATNODE);
					else {
						int motherNode = tree.motherOfNode(node);
						fraction.setValue(EDGESTART);  //TODO: this is just temporary: need to calculate value along branch.
						if (tree.nodeExists(motherNode)) {
							fraction.setValue( Math.abs(1.0*(x-this.x[motherNode])/(this.x[node]-this.x[motherNode])));

						}
					}
			}
			if (!tree.getAssociatedBit(triangleNameRef, node)) 
				for (int d = tree.firstDaughterOfNode(node); tree.nodeExists(d); d = tree.nextSisterOfNode(d))
					ScanBranches(tree, polys, d, x, y, fraction);

		}
	}
	/*_________________________________________________*/
	public   int findBranch(Tree tree, int drawnRoot, int x, int y, MesquiteDouble fraction) { 
		if (MesquiteTree.OK(tree) && ready) {
			foundBranch=0;
			ScanBranches(tree, branchPoly, drawnRoot, x, y, fraction);
			if (foundBranch==0 && getEdgeWidth()<ACCEPTABLETOUCHWIDTH)
				ScanBranches(tree, touchPoly, drawnRoot, x, y, fraction);  //then scan through thicker versions
			if (foundBranch == tree.getRoot() && !tree.getRooted())
				return 0;
			else
				return foundBranch;
		}
		return 0;
	}

	/*_________________________________________________*/
	public void reorient(int orientation) {
		treeDisplay.setOrientation(orientation);
		treeDisplay.pleaseUpdate(true);
	}
	/*_________________________________________________*/
	public void setEdgeWidth(int edw) {
		edgewidth = edw;
		preferredEdgeWidth = edw;
	}
	/*New code Feb.22.2007 allows eavesdropping on edgewidth by the TreeDrawing oliver*/ //TODO: delete new code comments
	/*_________________________________________________*/
	public int getEdgeWidth() {
		return edgewidth;
	}
	/*End new code Feb.22.2007 oliver*/
	/*_________________________________________________*/
	public   void dispose() { 
		for (int i=0; i<numNodes; i++) {
			branchPoly[i] = null;
			fillBranchPoly[i] = null;
			touchPoly[i] = null;
		}
		super.dispose();
	}
}

