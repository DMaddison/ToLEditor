package mesquite.ToLEditor.SearchWorkingToLTaxon;

import mesquite.lib.CommandChecker;
import mesquite.lib.TreeDisplay;
import mesquite.lib.TreeDisplayExtra;
import mesquite.tol.lib.*;

public class SearchWorkingToLTaxon extends BaseSearchToLTaxon {
	
	/*.................................................................................................................*/
	public String getBaseURLForUser() {
		return "working.tolweb.org";
	}

	/*.................................................................................................................*/
	public   TreeDisplayExtra createTreeDisplayExtra(TreeDisplay treeDisplay) {
		SearchWorkingToLToolTaxonExtra newPj = new SearchWorkingToLToolTaxonExtra(this, treeDisplay);
		if (extras!=null)
			extras.addElement(newPj);
		return newPj;
	}



}


/* ======================================================================== */
class SearchWorkingToLToolTaxonExtra extends BaseSearchToLToolTaxonExtra  {
	public SearchWorkingToLToolTaxonExtra (SearchWorkingToLTaxon ownerModule, TreeDisplay treeDisplay) {
		super(ownerModule,treeDisplay);
	}
	/*.................................................................................................................*/
	public  String getToolName() {
		return "Go To Working ToL";
	}
	/*.................................................................................................................*/
	public  String getToolExplanation() {
		return "This tool downloads the tree from the working page of the Tree of Life Web Project for the taxon touched.";
	}

	/*.................................................................................................................*/
	public  String getToolScriptName() {
		return "goToWorkingToLTaxon";
	}
	/*.................................................................................................................*/
	public String getBaseURL() {
		return "beta.tolweb.org";
	}
	/*.................................................................................................................*/
	public String getGetToLTreeModuleName() {
		return "GetWorkingToLTree";
	}
	/*.................................................................................................................*/
	public String getBaseURLForUser() {
		return "working.tolweb.org";
	}

}
