package mesquite.ToLEditor.TreeToToLXML;


import mesquite.ToLEditor.lib.ToLXMLNode;
import mesquite.lib.*;
import mesquite.lib.duties.*;
import java.net.*;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/** ======================================================================== */

public class TreeToToLXML extends TreeUtility {
	protected ToLXMLNode treeElement;
	protected Document doc;

	protected Element rootElement;
	protected ToLXMLNode rootNode;

	NameReference extinctionNR = NameReference.getNameReference("ToLExtinct");
	NameReference leafNR = NameReference.getNameReference("ToLLeaves");

	
	
	/*.................................................................................................................*/
	public boolean startJob(String arguments, Object condition, boolean hiredByName) {
		return true;  
 	}
 	
	
	/*.................................................................................................................*/
	private void addNodesToDoc(Tree tree, int node, ToLXMLNode tolNode) {
		if (tree.nodeIsInternal(node)) {  
			for (int daughter = tree.firstDaughterOfNode(node); tree.nodeExists(daughter); daughter = tree.nextSisterOfNode(daughter)){
				ToLXMLNode daughterNode = new ToLXMLNode();
				addNodesToDoc(tree, daughter,daughterNode);
				tolNode.addDescendant(daughterNode);
			}
		}
		String name = "";
		name=tree.getNodeLabel(node);
		if (StringUtil.notEmpty(name))
			tolNode.setTaxonName(name);
		if (tree.nodeIsTerminal(node)) {
			Taxa taxa = tree.getTaxa();
			if (taxa!=null){
				int taxonNumber = tree.taxonNumberOfNode(node);
				boolean extinctionStatus = taxa.getAssociatedBit(extinctionNR, taxonNumber);
				if (extinctionStatus) {
					tolNode.setExtinct(true);
				}
				boolean leafStatus = taxa.getAssociatedBit(leafNR, taxonNumber);
				if (leafStatus) {
					tolNode.setIsLeaf(true);
				}
			}
		}

	}
	/*.................................................................................................................*/

	public  void useTree(Tree tree) {
		String convertedFilePath = MesquiteFile.saveFileAsDialog("Save ToL XML file as:");
		if (StringUtil.blank(convertedFilePath))
			return;
		Element treeElement = DocumentHelper.createElement("tree-of-life-web");
		treeElement.addAttribute("version", "1.0");
		Document doc = DocumentHelper.createDocument(treeElement);
		rootElement = treeElement.addElement("nodes");
		rootNode = new ToLXMLNode(rootElement);
		int count=0;

		addNodesToDoc(tree,tree.getRoot(), rootNode);

		
		String xml = XMLUtil.getDocumentAsXMLString(doc, false);
		if (!StringUtil.blank(xml))
			MesquiteFile.putFileContents(convertedFilePath, xml, true);
		
	}
	
	
	public boolean isSubstantive(){
		return false;
	}
	/*.................................................................................................................*/
	 public String getName() {
	return "Tree to ToLXML";
	 }
		/*.................................................................................................................*/
	 public String getNameForMenuItem() {
	return "Tree to ToLXML...";
	 }
	/*.................................................................................................................*/
 	/** returns an explanation of what the module does.*/
 	public String getExplanation() {
 		return "Takes current tree and writes an XML file in Tree of Life input format.";
   	 }
   	 
}

