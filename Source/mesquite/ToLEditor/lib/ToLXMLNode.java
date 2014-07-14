package mesquite.ToLEditor.lib;

import mesquite.lib.*;

import org.dom4j.*;

/*
 TODO:  
 deal with non-standard characters

 add:
 confidence attribute
 name-comment
 combination-author
 */

public class ToLXMLNode {
	static final int MONOPHYLETIC = 0;
	static final int MONOPHYLYUNCERTAIN = 1;
	static final int NONMONOPHYLETIC = 2;

	static final int CONFIDENT= 0;
	static final int TENTATIVE = 1;
	static final int INCERTAESEDIS = 2;


	static final boolean ISIMPORTANTDEFAULT=false;
	static final boolean ISPREFERREDDEFAULT=false;
	static final boolean ITALICIZENAMEDEFAULT=false;


	String taxonName = "";
	Element element=null;
	int rank = ToLRanks.NORANK;

	int otherNameNumber = 0;

	public ToLXMLNode(Element element){
		this.element = element;
	}
	public ToLXMLNode(){
		element = DocumentHelper.createElement("node");
	}

	public void setElement(Element element) {
		this.element = element;
	}
	public Element getElement() {
		return element;
	}
	public void setExtinct(boolean b) {
		if (element!=null)
			if (b)
				element.addAttribute("extinct", "true");
			else
				element.addAttribute("extinct", "false");
	}


	public void setTaxonName(String name) {
		if (StringUtil.blank(name)) return;
		if (element!=null)
			XMLUtil.addFilledElement(element, "name",name);
		taxonName = name;
	}

	public String getTaxonName() {
		return taxonName;
	}


	public void setDate(String value) {
		if (StringUtil.blank(value)) return;
		if (element!=null)
			XMLUtil.addFilledElement(element, "auth-date",value);
	}

	public void setCombinationDate(String value) {
		if (StringUtil.blank(value)) return;
		if (element!=null)
			element.addAttribute("combination-date", value);
	}


	public void setAuthority(String value) {
		if (StringUtil.blank(value)) return;
		if (element!=null)
			XMLUtil.addFilledElement(element, "authority",value);
	}

	public void setPhylesis(int value) {
		if (element!=null)
			switch (value) {
			case MONOPHYLETIC:
				element.addAttribute("phylesis","monophyletic");
				break;
			case MONOPHYLYUNCERTAIN:
				element.addAttribute("phylesis","monophyly-uncertain");
				break;
			case NONMONOPHYLETIC:
				element.addAttribute("phylesis","nonmonophyletic");
				break;
			}
	}

	public void setPositionConfidence(int value) {
		if (element!=null)
			switch (value) {
			case CONFIDENT:
				element.addAttribute("position-confidence","confident");
				break;
			case TENTATIVE:
				element.addAttribute("position-confidence","tentative");
				break;
			case INCERTAESEDIS:
				element.addAttribute("position-confidence","incertaesedis");
				break;
			}
	}
	public int getRankValue() {
		return rank;
	}
	public void setRankValue(int value) {
		rank=value;
	}

	public void setRank(String value) {
		if (StringUtil.blank(value)) return;
		int rank = ToLRanks.getRankInt(value, ToLRanks.ENGLISH);
		setRankValue(rank);
		if (element!=null)
			XMLUtil.addFilledElement(element, "rank",value);
	}

	public void setGeographicDistributionDescription(String value) {
		if (StringUtil.notEmpty(value) && element!=null){
			Element geoElement  = element.element("geographic-distribution");
			if (geoElement==null) {
				geoElement = DocumentHelper.createElement("geographic-distribution");
				element.add(geoElement);
			}
			XMLUtil.addFilledElement(geoElement, "description",value);
		}
	}
	public Element setSourceInformation(String databaseID, String databaseKey) {
		if (element!=null){
			Element databaseElement = DocumentHelper.createElement("source-information");
			element.add(databaseElement);
			if (StringUtil.notEmpty(databaseID))
				XMLUtil.addFilledElement(databaseElement, "source-id",databaseID);
			if (StringUtil.notEmpty(databaseKey))
				XMLUtil.addFilledElement(databaseElement, "source-key",databaseKey);
			return databaseElement;
		}
		return null;
	}


	public Element setOtherName(String name, String authority, String date, boolean isImportant, boolean isPreferred, boolean italicizeName, String comments) {
		if (StringUtil.blank(name)) return null;
		if (element!=null){
			Element otherNamesElement  = element.element("othernames");
			if (otherNamesElement==null) {
				otherNameNumber = 0;
				otherNamesElement = DocumentHelper.createElement("othernames");
				element.add(otherNamesElement);
			}
			Element otherNameElement = DocumentHelper.createElement("othername");
			otherNamesElement.add(otherNameElement);
			if (StringUtil.notEmpty(name))
				XMLUtil.addFilledElement(otherNameElement, "name",name);
			if (StringUtil.notEmpty(authority))
				XMLUtil.addFilledElement(otherNameElement, "authority",authority);
			if (StringUtil.notEmpty(date))
				XMLUtil.addFilledElement(otherNameElement, "auth-date",date);
			if (isImportant)
				otherNameElement.addAttribute("is-important", "true");
			else
				otherNameElement.addAttribute("is-important", "false");
			if (isPreferred)
				otherNameElement.addAttribute("is-preferred", "true");
			else
				otherNameElement.addAttribute("is-preferred", "false");
			if (italicizeName)
				otherNameElement.addAttribute("italicize-name", "true");
			else
				otherNameElement.addAttribute("italicize-name", "false");
			otherNameElement.addAttribute("sequence", "" + otherNameNumber);
			if (StringUtil.notEmpty(comments))
				XMLUtil.addFilledElement(otherNameElement, "comments",comments);
			otherNameNumber++;

			return otherNameElement;
		}
		return null;
	}

	public Element setOtherName(String name, String authority, String date) {
		return setOtherName (name, authority, date, ISIMPORTANTDEFAULT, ISPREFERREDDEFAULT, ITALICIZENAMEDEFAULT, null);
	}
	public Element setOtherName(String name) {
		return setOtherName (name, null, null, ISIMPORTANTDEFAULT, ISPREFERREDDEFAULT, ITALICIZENAMEDEFAULT, null);
	}
	public Element setOtherName() {
		return setOtherName (null, null, null, ISIMPORTANTDEFAULT, ISPREFERREDDEFAULT, ITALICIZENAMEDEFAULT, null);
	}


	public void setInOriginalCombination(boolean b) {
		if (element!=null)
			if (b)
				element.addAttribute("is-new-combination", "false");
			else
				element.addAttribute("is-new-combination", "true");
	}

	public void setShowAuthorityContaining(boolean b) {
		if (element!=null)
			if (b)
				element.addAttribute("show-authority-containing", "true");
			else
				element.addAttribute("show-authority-containing", "false");
	}

	public void setIncompleteSubgroups(boolean b) {
		if (element!=null)
			if (b)
				element.addAttribute("incomplete-subgroups", "true");
			else
				element.addAttribute("incomplete-subgroups", "false");
	}

	public void setShowAuthority(boolean b) {
		if (element!=null)
			if (b)
				element.addAttribute("show-authority", "true");
			else
				element.addAttribute("show-authority", "false");
	}

	public void setItalicizeName(boolean b) {
		if (element!=null)
			if (b)
				element.addAttribute("italicize-name", "true");
			else
				element.addAttribute("italicize-name", "false");
	}

	public void setIsLeaf(boolean b) {
		if (element!=null)
			if (b)
				element.addAttribute("leaf", "true");
			else
				element.addAttribute("leaf", "false");
	}

	public void setHasPage(boolean b) {
		if (element!=null)
			if (b)
				element.addAttribute("has-page", "true");
			else
				element.addAttribute("has-page", "false");
	}


	public void addNewElement(String elementName, String content) {
		if (StringUtil.blank(elementName)) return;
		if (element!=null)
			XMLUtil.addFilledElement(element, elementName, content);
	}

	public void addDescendant(ToLXMLNode node) {
		Element nodesElement  = element.element("nodes");
		if (nodesElement==null) {
			nodesElement = DocumentHelper.createElement("nodes");
			element.add(nodesElement);
		}
		try {
			nodesElement.add(node.getElement());
		}
		catch (IllegalAddException e) {
			MesquiteMessage.println("       IllegalAddException (" + node.getTaxonName() + ")    " + node + "            "+node.getElement());
		}
	}

}
