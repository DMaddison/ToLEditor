package mesquite.ToLEditor.ToLXMLFromIndentedSimple;


import mesquite.lib.*;
import mesquite.ToLEditor.lib.*;

/* ======================================================================== */
public class ToLXMLFromIndentedSimple extends TranslateToToLXMLIndented { 
	/*.................................................................................................................*/
	public String getSourceFileFormatName() {
		return "Simple Indented Classification";
	}

	/*.................................................................................................................*/
	public boolean processLineIntoNode (String line, ToLXMLNode node) {
		Parser parser = new Parser(line);
		parser.setWhitespaceString(""+(char)9);    // the only characters separating name from author from date are tabs
		parser.setPunctuationString("");

//		read the information from this line, and store the values
		int rank = numberOfLeadingTabs(line);
		String name = parser.getNextToken();   //name
		String author = parser.getNextToken();   
		String date = parser.getNextToken();  

//		now that we have the node, set the values for this node
		node.setTaxonName(name);
		node.setDate(date);
		node.setAuthority(author);
		if (rank==maxRank){  
			node.setItalicizeName(true);
			node.setIsLeaf(true);   // by default set these to leaves
		}
		return true;
	}

}
