package mesquite.ToLEditor.lib;

import mesquite.tol.lib.ToLProjectOpener;

public class WorkingToLProjectOpener extends ToLProjectOpener {


	/*.................................................................................................................*/
	public String getBaseURL() {
		return "http://beta.tolweb.org";
	}
	/*.................................................................................................................*/
	public String getToolModule() {
		return "mesquite.ToLEditor.SearchWorkingToLTaxon.SearchWorkingToLTaxon";
	}

	/*.................................................................................................................*/
	public String getSetToolScript() {
		return "setTool mesquite.ToLEditor.SearchWorkingToLTaxon.SearchWorkingToLToolTaxonExtra.goToWorkingToLTaxon";
	}

	/*.................................................................................................................*/
	public String getTreeDrawingModule() {
		return "mesquite.ToLEditor.ToLDrawTree.ToLDrawTree";
	}

}