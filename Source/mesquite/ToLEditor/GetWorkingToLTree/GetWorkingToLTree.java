package mesquite.ToLEditor.GetWorkingToLTree;

import mesquite.lib.MesquiteProject;
import mesquite.tol.GetToLTree.GetToLTree;
import mesquite.tol.lib.ToLProjectOpener;
import mesquite.ToLEditor.lib.*;

public class GetWorkingToLTree extends GetToLTree {

	/*.................................................................................................................*/
	public String getName() {
		return "Working Tree from ToL Web Project...";
	}
	public String getExplanation() {
		return "Gets the working tree for the page of the Tree of Life Web Project for the group specified.";
	}

	public String getExtraArguments() {
		return "&use_working=true";
	}

	/*.................................................................................................................*/
	public String getDialogLabel() {
		return "Working Clade in Tree of Life Web Project";
	}

	/*.................................................................................................................*/
	public MesquiteProject establishProject(String arguments){
		if (arguments ==null) {
			if (queryOptions())
				arguments=cladeName;
		}
		if (arguments == null)
			return null;
		WorkingToLProjectOpener po = new WorkingToLProjectOpener();
		return po.establishProject(this, arguments, pageDepth, getExtraArguments());
	}

}
