package mesquite.ToLEditor.lib;

import java.io.*;

import org.dom4j.*;

import mesquite.lib.*;
import mesquite.lib.duties.*;


public abstract class TranslateToToLXML extends UtilitiesAssistant {
	protected ToLXMLNode treeElement;
	protected Document doc;

	protected Element rootElement;
	protected ToLXMLNode rootNode;

	/*.................................................................................................................*/
	public boolean startJob(String arguments, Object condition, boolean hiredByName){
		addMenuItem(null, "Convert " + getSourceFileFormatName() + " to ToL XML...", makeCommand("convert", this));
		doc =  null;
		rootElement = null;
		return true;
	}
	/*.................................................................................................................*/
	public Object doCommand(String commandName, String arguments, CommandChecker checker) {
		if (checker.compare(this.getClass(), "Converts from " + getSourceFileFormatName() + " to ToL XML", null, commandName, "convert")) {
			beforeConversion();
			startConvert();
		}
		else
			return super.doCommand(commandName, arguments, checker);
		return null;
	}
	/*.................................................................................................................*/
	public void initialize() {
	}
	/*.................................................................................................................*/
	public String getSourceDatabaseID() {
		return "";
	}
	/*.................................................................................................................*/
	public void beforeConversion() {
	}

	/*.................................................................................................................*/
	public String getSourceFileFormatName() {
		return "";
	}
	/*.................................................................................................................*/
	public String getUntilNextUpperCaseChar(Parser parser, String token) {
		String s = token;
		if (token==null)
			s=""; 
		else
			s+= " ";
		char c = parser.getNextChar();

		while (Character.isLowerCase(c) && !parser.atEnd()){
			s+=c;
			c = parser.getNextChar();
		}
		if (parser.atEnd() && Character.isLowerCase(c))
			s+= c;
		s.trim();
		return s;
	}
	/*.................................................................................................................*/
	public String getUntilDate(Parser parser, String token, MesquiteString date) {

		String s = "";
		while (token.length()<4 || (!MesquiteInteger.isNumber(token.substring(0,4)) && !parser.atEnd())){
			if (!s.equals(""))
				s+=" ";
			s+=token;
			token = parser.getNextToken();  
		}
		if (parser.atEnd() && (token.length()<4 && !MesquiteInteger.isNumber(token.substring(0,4))))
			s+=" "+token;
		if (token.length()>3 && MesquiteInteger.isNumber(token.substring(0,4)) && date!=null)
			date.setValue(token);
		return s;
	}
	/*.................................................................................................................*/
	public boolean skipFirstDarkLine() {
		return false;
	}
	/*.................................................................................................................*/
	public boolean separateLineProcessing () {
		return true;
	}
	/*.................................................................................................................*/
	public boolean doInitialPass () {
		return false;
	}
	/*.................................................................................................................*/
	public void processLineInitialPass (String line) {
	}
	/*.................................................................................................................*/
	public void doAfterInitialPass () {   
	}
	/*.................................................................................................................*/
	public void processLine (String line) {
	}


	/*.................................................................................................................*/
	public void convertFile (String fileContents, Document doc, ToLXMLNode rootNode) {
	}
	/*.................................................................................................................*/
	public void addToRoot(ToLXMLNode node) {
		rootElement.add(node.getElement());
	}
	/*.................................................................................................................*/
	public boolean convertToXML(String fileContents, String originalFileName){
		String convertedFilePath = MesquiteFile.saveFileAsDialog("Save ToL XML file as:");
		if (StringUtil.blank(fileContents) || StringUtil.blank(convertedFilePath))
			return false;
		Element treeElement = DocumentHelper.createElement("tree-of-life-web");
		treeElement.addAttribute("version", "1.0");
		Document doc = DocumentHelper.createDocument(treeElement);
		rootElement = treeElement.addElement("nodes");

		rootNode = new ToLXMLNode(rootElement);
		
		int count=0;


		if (separateLineProcessing()) {
			Parser parser = new Parser(fileContents);
			parser.setAllowComments(false);
			parser.setNoQuoteCharacter();
			String line = "";

			if (doInitialPass()) {
				if (skipFirstDarkLine())
					line = parser.getRawNextDarkLine();
				while (!parser.atEnd()) {
					line = parser.getRawNextDarkLine();
					if (!StringUtil.blank(line)) {
						processLineInitialPass(line);
					}
				}
				parser.setPosition(0);
			}

			doAfterInitialPass();  // do any cleanup we need to do

			if (skipFirstDarkLine())
				line = parser.getRawNextDarkLine();

			while (!parser.atEnd()) {
				line = parser.getRawNextDarkLine();
				if (!StringUtil.blank(line)) {
					processLine(line);
					count++;
					if (count % 100==0)
						logln("Processed " + count + " lines");
				}
			}

		} else
			convertFile(fileContents, doc, rootNode);

		logln(" " + count + " lines processed");

		String xml = XMLUtil.getDocumentAsXMLString(doc, false);
		if (!StringUtil.blank(xml))
			MesquiteFile.putFileContents(convertedFilePath, xml, true);
		treeElement = null;
		doc = null;
		rootElement = null;
		rootNode = null;
		return true;
	}
	/*.................................................................................................................*/
	public boolean startConvert(){
		initialize();
		MesquiteString originalDirPath = new MesquiteString();
		MesquiteString originalFileName = new MesquiteString();
		String originalFilePath = MesquiteFile.openFileDialog("Choose file to be converted:", originalDirPath, originalFileName);
		String originalFileContents = "";
		if (!StringUtil.blank(originalFilePath)) {
			originalFileContents = MesquiteFile.getFileContentsAsString(originalFilePath);
			if (!StringUtil.blank(originalFileContents)) {
				return convertToXML(originalFileContents, originalFileName.getValue());
			}
		}
		else {
			return false;
		}
		return false;
	}
	/*.................................................................................................................*/
	public int numberOfLeadingTabs(String line) {
		int numTabs =0;
		for (int i = 0; i<line.length(); i++) {
			if (StringUtil.charAtIsTab(line,i))
				numTabs++;
			else
				break;
		}
		return numTabs;
	}
	/*.................................................................................................................*/
	public boolean isSubstantive(){
		return false;
	}

	/*.................................................................................................................*/
	public String getName() {
		return "Convert " + getSourceFileFormatName() + " to ToL XML";
	}
	/*.................................................................................................................*/
	public boolean showCitation() {
		return false;
	}

	/*.................................................................................................................*/
	public String getExplanation() {
		return "Converts from " + getSourceFileFormatName() + " to ToL XML";
	}

}
