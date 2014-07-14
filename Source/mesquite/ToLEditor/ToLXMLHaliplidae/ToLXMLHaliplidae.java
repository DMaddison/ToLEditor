package mesquite.ToLEditor.ToLXMLHaliplidae;

import org.apache.commons.lang.StringEscapeUtils;

import mesquite.lib.*;
import mesquite.ToLEditor.lib.*;


/* ======================================================================== */
public class ToLXMLHaliplidae extends TranslateToToLXMLIndented { 
	String currentGenus = "";

	/*.................................................................................................................*/
	public String getSourceFileFormatName() {
		return "Van Vondel's Classification of Haliplidae";
	}
	/*.................................................................................................................*/
	public String getSourceDatabaseID() {
		return "VanVondelHaliplidae";
	}

	/*.................................................................................................................*/
	public void beforeConversion() {
		if (!MesquiteThread.isScripting())
			currentGenus = MesquiteString.queryShortString(getModuleWindow(), "Genus Name", "Genus Name", "");
	}

	public String extractDateFromString(String s){
		if (s.length()<=4)
			return s;
		return s.substring(0,4);
	}

	/*.................................................................................................................*/
	public boolean processLineIntoNode (String line,ToLXMLNode node) {
		Parser parser = new Parser(line);
		parser.setWhitespaceString(" "+(char)9);    
		parser.setPunctuationString(",()");

		String author= "";
		MesquiteString date = new MesquiteString();

//		read the information from this line, and store the values
		String name = parser.getNextToken();
		if (name!=null)
			name=name.trim();   //name

		int rankName = ToLRanks.NORANK;

		if (name.equalsIgnoreCase("=") || name.equalsIgnoreCase("=?")) {  // then this is a synonym
			name = parser.getNextToken();
			if (previousNode!=null && previousNode.getRankValue()>=ToLRanks.SPECIES) {
				name = getUntilNextUpperCaseChar(parser, name);
			}
			String token = parser.getNextToken();
			if (token.equalsIgnoreCase("var.")) {
				name += " " + token + " " + parser.getNextToken();
				token = parser.getNextToken();
			}
			author = getUntilDate(parser,token, date);
			author = StringUtil.stripTrailingWhitespaceAndPunctuation(author);
			author = StringEscapeUtils.escapeXml(author);
			date.setValue(extractDateFromString(date.getValue()));
			if (previousNode!=null)
				previousNode.setOtherName(name, author, date.getValue());
			node.setItalicizeName(true);
			return false;
		}

		else if (name.equalsIgnoreCase("DISTRIBUTION:")) {  // then this is geographic distribution
			if (previousNode!=null)
				previousNode.setGeographicDistributionDescription(parser.getRemaining());
			return false;
		} 

		else  {
			node.setItalicizeName(true);

			if (name.equalsIgnoreCase("Genus") || name.equalsIgnoreCase("Subgenus")){  // then the real name is the next token
				if (name.equalsIgnoreCase("Genus")){
					rankName=ToLRanks.GENUS;
				} else
					rankName=ToLRanks.SUBGENUS;
				name = parser.getNextToken();
				if (name!=null)
					name=name.trim();   //name
				if (rankName==ToLRanks.GENUS)
					currentGenus = name;

				author = getUntilDate(parser,parser.getNextToken() , date);
				author = StringUtil.stripTrailingWhitespaceAndPunctuation(author);
				author = StringEscapeUtils.escapeXml(author);

				date.setValue(extractDateFromString(date.getValue()));
				node.setTaxonName(name);
				node.setDate(date.getValue());
				node.setAuthority(author);
				node.setRank(ToLRanks.getRankString(rankName, ToLRanks.ENGLISH));

			}
			else {
				rankName = ToLRanks.SPECIES;
				node.setIsLeaf(true);
				if (StringUtil.notEmpty(currentGenus)) {
					name = currentGenus + " " + name;
				}
				String token = parser.getNextToken();  
				if (token.startsWith("(")) {
					line = parser.getRemainingUntilChar(')');
					parser.setString(line);
					node.setInOriginalCombination(false);
					author = getUntilDate(parser,parser.getNextToken() , date);
				} else {
					String s = getUntilDate(parser,parser.getNextToken() , date);
					if (StringUtil.blank(s))
						author= token;
					else
						author= token+" " + s;
				}
				author = StringUtil.stripTrailingWhitespaceAndPunctuation(author);
				author = StringEscapeUtils.escapeXml(author);
				//date.setValue(parser.getNextToken());

				node.setTaxonName(name);
				node.setDate(date.getValue());
				node.setAuthority(author);
				node.setRank(ToLRanks.getRankString(rankName, ToLRanks.ENGLISH));

			}
		}
		return true;
	}
}
