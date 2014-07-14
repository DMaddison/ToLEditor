package mesquite.ToLEditor.ToLXMLFromLorenz;


import org.apache.commons.lang.StringEscapeUtils;

import mesquite.lib.*;
import mesquite.ToLEditor.lib.*;


/* TODO: 
 * 	check to see if a subgeneric name matches the generic name, in which case use name (name)
 */
/* ======================================================================== */
public class ToLXMLFromLorenz extends TranslateToToLXML { 
	static String sourceDatabaseID = "LorenzCarabCat";
	ToLXMLNode[] previousNodes = null;
	int previousRank = -1;
	ToLXMLNode previousNode = null;
	int maxRank = -1;
	String previousGenus = "";

	/*.................................................................................................................*/
	public void initialize() {
		previousNodes = new ToLXMLNode[ToLRanks.NUMRANKS];
		for (int i=0; i<ToLRanks.NUMRANKS; i++){
			previousNodes[i] = null;
		}
		previousRank = -1;
		previousNode = null;
	}
	/*.................................................................................................................*/
	public String getSourceFileFormatName() {
		return "Lorenz CarabCat File Format";
	}
	/*.................................................................................................................*/
	public String getSourceDatabaseID() {
		return sourceDatabaseID;
	}
	/*.................................................................................................................*/
	public boolean skipFirstDarkLine() {
		return false;
	}
	/*.................................................................................................................*/
	public boolean doInitialPass () {
		return true;
	}
	/*.................................................................................................................*/
	public void processLineInitialPass (String line) {   // this pass only serves to figure out maximum rank
		Parser parser = new Parser(line);
		parser.setWhitespaceString("\"|");
		parser.setPunctuationString("");

		String UID = parser.getFirstToken().trim();   // UID
		String rankName = parser.getNextTrimmedToken();   //rank
		int rank = ToLRanks.getRankInt(rankName, ToLRanks.ENGLISH);
		if (rank<maxRank) 
			maxRank=rank;
	}
	/*.................................................................................................................*/
	public void processLine (String line) {
//		each line has the following fields:       "UID"|"RANKNAME"|"NOM"|"AUTHOR"|"YEAR"|"ORIGCOMB"|"STATUS"|"VALNAME"|"SUPRAGEN"|"RANK-ID"|"SORTCODE"
		Parser parser = new Parser(line);
		parser.setAllowComments(false);
		parser.setWhitespaceString("\"|");
		parser.setPunctuationString("");
		parser.setNoQuoteCharacter();
		ToLXMLNode node = null;
		boolean atRootNode = false;

//		read the information from this line, and store the values
		String UID = parser.getFirstToken();   // UID
		if (UID!=null)
			UID = UID.trim();
		String rankName = parser.getNextTrimmedToken();   //rank
		rankName = StringUtil.removeCharacters(rankName, "()");
		int rank = ToLRanks.getRankInt(rankName, ToLRanks.ENGLISH);
		String name = parser.getNextTrimmedToken();   //name
		String author = parser.getNextTrimmedToken();   
		if (author==null) {
			logln(line);
			boolean wow = false;
		}
		if (author.equalsIgnoreCase("_"))
			author="";
		else if (author.equalsIgnoreCase("[provisional]"))
			author="";
		else 
			author = StringEscapeUtils.escapeXml(author);
		String date = parser.getNextTrimmedToken();   
		if (!MesquiteInteger.isNumber(date))
			date="";
		String origCombo = parser.getNextTrimmedToken();   
		if (origCombo==null) {
			logln(line);
			boolean wow = false;
		}
			
		boolean inOriginalGenus = true;
		if (origCombo!=null && origCombo.equalsIgnoreCase("different"))
			inOriginalGenus = false;
		String status = parser.getNextTrimmedToken();  
		boolean isValid = true;
		if (!status.equalsIgnoreCase("valid"))
			isValid = false;
		String valname = parser.getNextTrimmedToken();   
		String supragen = parser.getNextTrimmedToken();   
		String rankID = parser.getNextTrimmedToken();   
		String sortcode = parser.getNextTrimmedToken();  
		String range = "";  //TODO:  get this once Lorenz adds it to the output!


		if (!isValid) {   // here just add an "other name" to the previous node
			if (previousNode!=null)
				previousNode.setOtherName(name, author,  date, false, false, rank==ToLRanks.SPECIES || rank == ToLRanks.SUBSPECIES, null);
		}
		else { //this is a new node

			node = new ToLXMLNode();

			if(rank==ToLRanks.SUBGENUS) {
				name = previousGenus + " (" + name + ")";
				node.setHasPage(true);
			}


			if(rank==ToLRanks.GENUS) {
				previousGenus = name;
				node.setHasPage(true);
			}


//			now that we have the node, set the values for this node
			node.setTaxonName(name);
			node.setRank(ToLRanks.getRankString(rank, ToLRanks.ENGLISH));
			node.setDate(date);
			node.setAuthority(author);
			if (rank==ToLRanks.SPECIES || rank == ToLRanks.SUBSPECIES){  
				node.setInOriginalCombination(inOriginalGenus);
				node.setShowAuthority(true);
				node.setItalicizeName(true);
				node.setIsLeaf(true);   // by default set these to leaves
			}
			if (previousRank==ToLRanks.SPECIES && rank==ToLRanks.SUBSPECIES) // then that previous one needs to be reset to be not a leaf
				previousNode.setIsLeaf(false);
			node.setGeographicDistributionDescription(range);
			node.setSourceInformation(sourceDatabaseID, UID);

//			now we need to attach this node to its parental node
			if (rank==maxRank || previousNode==null)
				addToRoot(node);
			else if (rank>=0 && !atRootNode){  // we are not at the start
				if (rank>previousRank){ // then this one should be descendant of the previous node.
					previousNode.addDescendant(node);
				}
				else {// we've now got a node that belongs to some deeper group
					for (int i = rank-1; i>=0; i--) {
						if (previousNodes[i]!=null){  // we've found the next deepest previous node that is still around
							for (int j = i+1; j<ToLRanks.NUMRANKS; j++)  // wipe out shallower ones
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
