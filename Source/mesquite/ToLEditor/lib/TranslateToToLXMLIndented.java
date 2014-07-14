package mesquite.ToLEditor.lib;



import mesquite.lib.*;
import mesquite.ToLEditor.lib.*;

/* ======================================================================== */
public abstract class TranslateToToLXMLIndented extends TranslateToToLXML { 
	protected ToLXMLNode[] previousNodes = null;
	protected int previousRank = ToLRanks.NORANK;
	protected ToLXMLNode previousNode = null;
	 int previousHighestRankAdded = ToLRanks.LOWESTRANK;
	protected int maxRank =0;
	int numRanks=0;

	/*.................................................................................................................*/
	public void initialize() {
		previousRank = ToLRanks.NORANK;
		previousNode = null;
		maxRank=0;
		numRanks=0;
		previousHighestRankAdded = ToLRanks.LOWESTRANK;

	}
	/*.................................................................................................................*/
	public boolean doInitialPass () {
		return true;
	}
	/*.................................................................................................................*/
	public void processLineInitialPass (String line) {   // this pass only serves to figure out maximum rank
		int rank = numberOfLeadingTabs(line);

		if (rank>maxRank) 
			maxRank=rank;
	}

	/*.................................................................................................................*/
	public void doAfterInitialPass () {   
		numRanks = maxRank+1;
		previousNodes = new ToLXMLNode[numRanks];
		for (int i=0; i<numRanks; i++){
			previousNodes[i] = null;
		}
	}

	/*.................................................................................................................*/
	public abstract boolean processLineIntoNode (String line, ToLXMLNode node) ;

	/*.................................................................................................................*/
	public void processLine (String line) {

		int rank = numberOfLeadingTabs(line);

		ToLXMLNode node = new ToLXMLNode();
		boolean newNode = processLineIntoNode(line,node);


//		now we need to attach this node to its parental node
		if (newNode) {
			if (rank==0 || previousNode==null || previousHighestRankAdded>=rank) {
				addToRoot(node);
			} else if (rank>=0){  // we are not at the start
				if (rank>previousRank && previousNode!=null){ // then this one should be descendant of the previous node.
					previousNode.addDescendant(node);
				}
				else {// we've now got a node that belongs to some deeper group
					for (int i = rank-1; i>=0; i--) {
						if (previousNodes[i]!=null){  // we've found the next deepest previous node that is still around
							for (int j = i+1; j<numRanks; j++)  // wipe out shallower ones
								previousNodes[j]=null;
							previousNodes[i].addDescendant(node);  //this must be the parent; add this current node to that parent
							break;
						}
					}
				}
			}

//			some bookkeeping to store things for next line			
			if (rank>=0){
				previousNodes[rank] = node;
				previousRank = rank;
				if (rank<previousHighestRankAdded)
					previousHighestRankAdded = rank;
			}
			if (node!=null)
				previousNode = node;
		}

	}

	/*.................................................................................................................*/
	/** returns whether this module is requesting to appear as a primary choice */
	public boolean requestPrimaryChoice(){
		return true;  
	}
	/*.................................................................................................................*/
	public boolean isPrerelease(){
		return true;
	}
}
