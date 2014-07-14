package mesquite.ToLEditor.ToLXMLFromBuprestoids;


import org.apache.commons.lang.StringEscapeUtils;

import mesquite.lib.*;
import mesquite.ToLEditor.lib.*;

/* ======================================================================== */
public class ToLXMLFromBuprestoids extends TranslateToToLXMLIndented { 
	/*.................................................................................................................*/
	public String getSourceFileFormatName() {
		return "Bellamy's Buprestoid Classification";
	}

	/*.................................................................................................................*/
	public boolean processLineIntoNode (String line,ToLXMLNode node) {
		Parser parser = new Parser(line);
		parser.setWhitespaceString(" "+(char)9);    
		parser.setPunctuationString("");

//		read the information from this line, and store the values
		String name = parser.getNextToken();
		if (name!=null)
			name=name.trim();   //name

		int rankName = ToLRanks.NORANK;
		if (name.equalsIgnoreCase("Genus")){  // then the real name is the next token
			 name = parser.getNextToken();
			if (name!=null)
				name=name.trim();   //name
			rankName=ToLRanks.GENUS;
		}
		
		if (rankName!=ToLRanks.GENUS){
			String lowerCaseName = name.toLowerCase();
			if (lowerCaseName.endsWith("inae"))
				rankName=ToLRanks.SUBFAMILY;
			else if (lowerCaseName.endsWith("idae"))
				rankName=ToLRanks.FAMILY;
			else if (lowerCaseName.endsWith("ini"))
				rankName=ToLRanks.TRIBE;
			else if (lowerCaseName.endsWith("ina"))
				rankName=ToLRanks.SUBTRIBE;
		}

		String token = parser.getNextToken();  

		MesquiteString date = new MesquiteString();
		String author = getUntilDate(parser,token, date);
		author = StringEscapeUtils.escapeXml(author);
		if (date.isBlank()){
			if (StringUtil.notEmpty(name) && StringUtil.notEmpty(author))
				name+= " " +author;
			author="";
		} 
		node.setTaxonName(name);
		node.setDate(date.getValue());
		node.setAuthority(author);
		if (rankName==ToLRanks.GENUS){  
			node.setItalicizeName(true);
		}
		return true;
	}
}
