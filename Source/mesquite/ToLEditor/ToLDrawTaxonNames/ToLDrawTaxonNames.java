package mesquite.ToLEditor.ToLDrawTaxonNames;

import java.util.*;
import java.awt.*;
import mesquite.lib.*;
import mesquite.trees.lib.*;
import mesquite.trees.BasicDrawTaxonNames.BasicDrawTaxonNames;

public class ToLDrawTaxonNames extends BasicDrawTaxonNames {

	NameReference anr = NameReference.getNameReference("ToLLeaves");

	/*.................................................................................................................*/
	protected void drawNamesOnTree(Tree tree, int N, TreeDisplay treeDisplay, TaxaPartition partitions) {
		Debugg.println("ToLDrawTaxonNames");
		if  (tree.nodeIsTerminal(N)) {   //terminal
			if (!showTaxonNames.getValue())
				return;
			Color bgColor = treeDisplay.getBackground();
			textRotator.assignBackground(bgColor);
			int horiz=treeDrawing.x[N];
			int vert=treeDrawing.y[N];
			int lengthString;
			Taxa taxa = tree.getTaxa();
			int taxonNumber = tree.taxonNumberOfNode(N);
			if (taxonNumber<0) {
				//MesquiteMessage.warnProgrammer("error: negative taxon number found in DrawNamesTreeDisplay " + taxonNumber + "  tree: " + tree.writeTree());
				return;
			}
			else if (taxonNumber>=taxa.getNumTaxa()) {
				//MesquiteMessage.warnProgrammer("error: taxon number too high found in DrawNamesTreeDisplay " + taxonNumber + "  tree: " + tree.writeTree());
				return;
			}
			if (taxonNumber>= namePolys.length) {
				//MesquiteMessage.warnProgrammer("error: taxon number " + taxonNumber + " / name boxes " + nameBoxes.length);
				return;
			}
			String s=taxa.getName(taxonNumber);
			if (s== null)
				return;
			Taxon taxon = taxa.getTaxon(taxonNumber);
			if (taxon== null)
				return;
			boolean selected = taxa.getSelected(taxonNumber);
			//check all extras to see if they want to add anything
			boolean underlined = false;
			Color taxonColor;
			if (!tree.anySelected() || tree.getSelected(N))
				taxonColor = fontColor;
			else
				taxonColor = fontColorLight;

			MesquiteBoolean n = (MesquiteBoolean)taxa.getAssociatedObject(anr, taxonNumber);
			if (n!=null && n.getValue())
				taxonColor = Color.green;
			
			ListableVector extras = treeDisplay.getExtras();
			if (extras!=null){
				Enumeration e = extras.elements();
				while (e.hasMoreElements()) {
					Object obj = e.nextElement();
					TreeDisplayExtra ex = (TreeDisplayExtra)obj;
					if (ex.getTaxonUnderlined(taxon))
						underlined = true;
					Color tc = ex.getTaxonColor(taxon);
					if (tc!=null) {
						//taxonColor = tc;
					}
					String es = ex.getTaxonStringAddition(taxon);
					if (!StringUtil.blank(es))
						s+= es;
				}
			}

			Composite composite = null;
			if(MesquiteWindow.Java2Davailable && shades != null && taxonNumber < shades.length) {
				composite = ColorDistribution.getComposite(gL);
				ColorDistribution.setTransparentGraphics(gL,getTaxonShade(shades[taxonNumber]));	
			}
			
			gL.setColor(taxonColor); 

			lengthString = fm.stringWidth(s); //what to do if underlined?
			int centeringOffset = 0;
			if (treeDisplay.centerNames)
				centeringOffset = (longestString-lengthString)/2;


			if (treeDrawing.namesFollowLines && MesquiteWindow.Java2Davailable){
				double slope = (treeDrawing.lineBaseY[N]*1.0-treeDrawing.lineTipY[N])/(treeDrawing.lineBaseX[N]-treeDrawing.lineTipX[N]);
				//setBounds(namePolys[taxonNumber], horiz+separation, vert, lengthString, rise+descent);
				boolean upper = treeDrawing.lineTipY[N]>treeDrawing.lineBaseY[N];
				boolean right = treeDrawing.lineTipX[N]>treeDrawing.lineBaseX[N];
				double radians = Math.atan(slope);
				Font font = gL.getFont();
				FontMetrics fontMet = gL.getFontMetrics(font);
				double height = fontMet.getHeight(); //0.667
				int length = fontMet.stringWidth(s)+ separation;
				int textOffsetH = 0; //fontMet.getHeight()*2/3;
				int textOffsetV = 0;
				if (!right) {
					textOffsetH = -(int)(Math.cos(radians)*length);

					textOffsetV = -(int)(Math.sin(radians)*length);
				}
				else {
					textOffsetH = (int)(Math.cos(radians + Math.atan(height*0.6/separation))*separation*1.4);  //1.0

					textOffsetV = (int)(Math.sin(radians + Math.atan(height*0.6/separation))*separation*1.4);
				}

				int horizPosition = treeDrawing.lineTipX[N];
				int vertPosition = treeDrawing.lineTipY[N];

				textRotator.drawFreeRotatedText(s, gL, horizPosition, vertPosition, radians, new Point(textOffsetH, textOffsetV), false, namePolys[taxonNumber]);

			}
			else if ((treeDrawing.labelOrientation[N]==270) || treeDisplay.getOrientation()==TreeDisplay.UP) {
				horiz += treeDrawing.getEdgeWidth()/2;
				if (Math.abs(treeDrawing.namesAngle)<0.01) {
					horiz -= StringUtil.getStringDrawLength(gL,"A");
				}
				if (MesquiteWindow.Java2Davailable && MesquiteDouble.isCombinable(treeDrawing.namesAngle) && treeDrawing.labelOrientation[N]!=270){
					textRotator.drawFreeRotatedText(s,  gL, horiz-rise/2, vert-separation, treeDrawing.namesAngle, null, true, namePolys[taxonNumber]);
				}
				else {
					vert -= centeringOffset;
					setBounds(namePolys[taxonNumber], horiz-rise/2, vert-separation-lengthString, rise+descent, lengthString);
					textRotator.drawRotatedText(s, taxonNumber, gL, treeDisplay, horiz-rise/2, vert-separation);
					if (underlined){
						Rectangle b =namePolys[taxonNumber].getB();
						gL.drawLine(b.x+b.width, b.y, b.x+b.width, b.y+b.height);
						//gL.fillPolygon(namePolys[taxonNumber]);
					}
				}
			}
			else if ((treeDrawing.labelOrientation[N]==90) || treeDisplay.getOrientation()==TreeDisplay.DOWN) {
				horiz += treeDrawing.getEdgeWidth()/2;
				/*if (MesquiteWindow.Java2Davailable && (MesquiteDouble.isCombinable(treeDrawing.namesAngle) || treeDrawing.labelOrientation[N]!=90)){
					textRotator.drawFreeRotatedText(s,  gL, horiz-rise*2, vert+separation, treeDrawing.namesAngle, null, false, namePolys[taxonNumber]); // /2
				}
				else */
				{
					vert += centeringOffset;
					setBounds(namePolys[taxonNumber], horiz-rise/2, vert+separation, rise+descent, lengthString);
					textRotator.drawRotatedText(s, taxonNumber, gL, treeDisplay, horiz-rise/2, vert+separation, false);
					if (underlined){
						Rectangle b =namePolys[taxonNumber].getB();
						gL.drawLine(b.x+b.width, b.y, b.x+b.width, b.y+b.height);
						//gL.fillPolygon(namePolys[taxonNumber]);
					}
				}
			}
			else if ((treeDrawing.labelOrientation[N]==0) || treeDisplay.getOrientation()==TreeDisplay.RIGHT) {
				vert += treeDrawing.getEdgeWidth()/2;
				/*if (MesquiteWindow.Java2Davailable && (MesquiteDouble.isCombinable(treeDrawing.namesAngle) || treeDrawing.labelOrientation[N]!=0)){
					textRotator.drawFreeRotatedText(s,  gL, horiz+separation, vert-rise*2, treeDrawing.namesAngle, null, false, namePolys[taxonNumber]); ///2
				}
				else */{
					horiz += centeringOffset;
					setBounds(namePolys[taxonNumber], horiz+separation, vert-rise/2, lengthString, rise+descent);

					if (bgColor!=null) {
						gL.setColor(bgColor);
						gL.fillRect(horiz+separation, vert-rise/2, lengthString, rise+descent);
						gL.setColor(taxonColor);
					}
					gL.drawString(s, horiz+separation, vert+rise/2);
					if (underlined){
						Rectangle b =namePolys[taxonNumber].getB();
						gL.drawLine(b.x, b.y+b.height, b.x+b.width, b.y+b.height);
						//gL.fillPolygon(namePolys[taxonNumber]);
					}
				}
			}
			else if ((treeDrawing.labelOrientation[N]==180) || treeDisplay.getOrientation()==TreeDisplay.LEFT) {
				vert += treeDrawing.getEdgeWidth()/2;
				/*if (MesquiteWindow.Java2Davailable && (MesquiteDouble.isCombinable(treeDrawing.namesAngle) || treeDrawing.labelOrientation[N]!=0)){
					textRotator.drawFreeRotatedText(s,  gL, horiz - separation, vert-rise*2, treeDrawing.namesAngle, null, true, namePolys[taxonNumber]);
				}
				else */{
					horiz -= centeringOffset;
					setBounds(namePolys[taxonNumber], horiz - separation - lengthString, vert-rise/2, lengthString, rise+descent);
					if (bgColor!=null) {
						gL.setColor(bgColor);
						gL.fillRect(horiz - separation - lengthString, vert-rise/2, lengthString, rise+descent);
						gL.setColor(taxonColor);
					}
					gL.drawString(s, horiz - separation - lengthString, vert+rise/2);
					if (underlined){
						Rectangle b =namePolys[taxonNumber].getB();
						gL.drawLine(b.x, b.y+b.height, b.x+b.width, b.y+b.height);
						//gL.fillPolygon(namePolys[taxonNumber]);
					}
				}
			}
			else if (treeDisplay.getOrientation()==TreeDisplay.FREEFORM) {
				setBounds(namePolys[taxonNumber], horiz+separation, vert-rise/2, lengthString, rise+descent);
				if (bgColor!=null) {
					gL.setColor(bgColor);
					gL.fillRect(horiz+separation, vert-rise/2, lengthString, rise+descent);
					gL.setColor(taxonColor);
				}
				gL.drawString(s, horiz+separation, vert+rise/2);
				if (underlined){
					Rectangle b =namePolys[taxonNumber].getB();
					gL.drawLine(b.x, b.y+b.height, b.x+b.width, b.y+b.height);
					//gL.fillPolygon(namePolys[taxonNumber]);
				}
			}
			else { 
				double slope = (treeDrawing.lineBaseY[N]*1.0-treeDrawing.lineTipY[N])/(treeDrawing.lineBaseX[N]-treeDrawing.lineTipX[N]);
				if (slope>=-1 && slope <= 1) {  //right or left side
					if (treeDrawing.lineTipX[N]> treeDrawing.lineBaseX[N]) { // right
						setBounds(namePolys[taxonNumber], horiz+separation, vert, lengthString, rise+descent);
						if (bgColor!=null) {
							gL.setColor(bgColor);
							gL.fillRect(horiz+separation, vert, lengthString, rise+descent);
							gL.setColor(taxonColor);
						}
						gL.drawString(s, horiz+separation, vert + rise);
						if (underlined){
							Rectangle b =namePolys[taxonNumber].getB();
							gL.drawLine(b.x, b.y+b.height, b.x+b.width, b.y+b.height);
							//gL.fillPolygon(namePolys[taxonNumber]);
						}
					}
					else {
						setBounds(namePolys[taxonNumber], horiz - separation - lengthString, vert, lengthString, rise+descent);
						if (bgColor!=null) {
							gL.setColor(bgColor);
							gL.fillRect(horiz - separation - lengthString, vert, lengthString, rise+descent);
							gL.setColor(taxonColor);
						}
						gL.drawString(s, horiz - separation - lengthString, vert + rise);
						if (underlined){
							Rectangle b =namePolys[taxonNumber].getB();
							gL.drawLine(b.x, b.y+b.height, b.x+b.width, b.y+b.height);
							//gL.fillPolygon(namePolys[taxonNumber]);
						}
					}
				}
				else {//top or bottom
					if (treeDrawing.lineTipY[N]> treeDrawing.lineBaseY[N]) { // bottom
						setBounds(namePolys[taxonNumber], horiz, vert+separation, rise+descent, lengthString);
						textRotator.drawRotatedText(s, taxonNumber, gL, treeDisplay, horiz, vert+separation, false);
						if (underlined){
							Rectangle b =namePolys[taxonNumber].getB();
							gL.drawLine(b.x+b.width, b.y, b.x+b.width, b.y+b.height);
							//gL.fillPolygon(namePolys[taxonNumber]);
						}
					}
					else { // top
						setBounds(namePolys[taxonNumber], horiz, vert-separation-lengthString, rise+descent, lengthString);
						textRotator.drawRotatedText(s, taxonNumber, gL, treeDisplay, horiz, vert-separation);
						if (underlined){
							Rectangle b =namePolys[taxonNumber].getB();
							gL.drawLine(b.x+b.width, b.y, b.x+b.width, b.y+b.height);
							//gL.fillPolygon(namePolys[taxonNumber]);
						}
					}
				}
			}
			textRotator.assignBackground(null);
			gL.setColor(Color.black);
			if (MesquiteWindow.Java2Davailable) {
				ColorDistribution.setComposite(gL, composite);	
			}
			if (selected && GraphicsUtil.useXORMode(gL, false)){
				gL.setXORMode(Color.white);
				gL.fillPolygon(namePolys[taxonNumber]);

				gL.setPaintMode();
			}
		}
		else {
			for (int d = tree.firstDaughterOfNode(N); tree.nodeExists(d); d = tree.nextSisterOfNode(d))
				drawNamesOnTree(tree, d, treeDisplay, partitions);

			String label = null;
			if (showNodeLabels.getValue())
				label = tree.getNodeLabel(N);
			if (label!=null && label.length() >0 && label.charAt(0)!='^') {
				//check all extras to see if they want to add anything
				boolean underlined = false;
				Color taxonColor = Color.black;
				if (!tree.anySelected()|| tree.getSelected(N))
					taxonColor = fontColor;
				else
					taxonColor = fontColorLight;
				String s = StringUtil.deTokenize(label);
				ListableVector extras = treeDisplay.getExtras();
				if (extras!=null){
					Enumeration e = extras.elements();
					while (e.hasMoreElements()) {
						Object obj = e.nextElement();
						TreeDisplayExtra ex = (TreeDisplayExtra)obj;
						if (ex.getCladeLabelUnderlined(label, N))
							underlined = true;
						Color tc = ex.getCladeLabelColor(label, N);
						if (tc!=null)
							taxonColor = tc;
						String es = ex.getCladeLabelAddition(label, N);
						if (!StringUtil.blank(es))
							s+= es;
					}
				}
				/*New code added Feb.15.2007 oliver*/ //TODO: delete new code comments
//				TODO: Currently only really works for square trees, and an ugly hack at that
				if (!centerNodeLabels.getValue() || !(MesquiteModule.getShortClassName(treeDrawing.getClass()).toString().equalsIgnoreCase("SquareTreeDrawing"))){
					StringUtil.highlightString(gL,s, treeDrawing.x[N], treeDrawing.y[N], taxonColor, Color.white);
					if (MesquiteModule.getShortClassName(treeDrawing.getClass()).toString().equalsIgnoreCase("SquareTreeDrawing"))
						centerNodeLabelItem.setEnabled(true);
					else centerNodeLabelItem.setEnabled(false); // TODO: these conditionals don't work right.  Should work now April.03.2007 oliver
				}
				else {
					centerNodeLabelItem.setEnabled(true);
					int edgeWidth = treeDrawing.getEdgeWidth();
					int parentN = tree.motherOfNode(N);
					int centerH, centerV, startH, startV;
					int nameDrawLength = StringUtil.getStringDrawLength(gL, s);
					int nameDrawHeight = StringUtil.getTextLineHeight(gL);
					if (treeDisplay.getOrientation()==TreeDisplay.UP){
						startV = treeDrawing.y[N] + (int)((treeDrawing.y[parentN] - treeDrawing.y[N])/2) + edgeWidth; 
						startH = treeDrawing.x[N] + edgeWidth;
						StringUtil.highlightString(gL, s, startH, startV, taxonColor, Color.white);
					}
					else if (treeDisplay.getOrientation()==TreeDisplay.DOWN){
						startV = treeDrawing.y[N] - (int)((treeDrawing.y[N] - treeDrawing.y[parentN])/2); 
						startH = treeDrawing.x[N] + edgeWidth;
						StringUtil.highlightString(gL, s, startH, startV, taxonColor, Color.white);
					}
					else if (treeDisplay.getOrientation()==TreeDisplay.RIGHT){
						centerH = treeDrawing.x[N] - (int)((treeDrawing.x[N] - treeDrawing.x[parentN])/2) - edgeWidth; 
						startH = centerH -(int)(nameDrawLength/2);
						// this conditional tests for overlap between branch and names, and shifts name accordingly.
						if((centerH + (int)nameDrawLength/2) > treeDrawing.x[N] - edgeWidth){
							startH -= (centerH + (int)nameDrawLength/2) - (treeDrawing.x[N] - edgeWidth);
						}
						startV = (int)(treeDrawing.y[N] - 1);
						StringUtil.highlightString(gL, s, startH, startV, taxonColor, Color.white);
					}
					else if (treeDisplay.getOrientation()==TreeDisplay.LEFT){
						centerH = treeDrawing.x[N] + (int)((treeDrawing.x[parentN] - treeDrawing.x[N])/2) + edgeWidth; 
						startH = centerH -(int)(nameDrawLength/2);
						// this conditional tests for overlap between branch and names, and shifts name accordingly.
						if((centerH - (int)nameDrawLength/2 < treeDrawing.x[N] + edgeWidth)){
							startH += (treeDrawing.x[N] + edgeWidth) - (centerH - (int)nameDrawLength/2);
						}
						startV = (int)(treeDrawing.y[N] - 1);
						StringUtil.highlightString(gL, s, startH, startV, taxonColor, Color.white);
					}
					// TODO: figure out how to check for initialization of startH & startV, then pull the highlightString method out of the four conditionals above and put it here
					// StringUtil.highlightString(gL, s, startH, startV, taxonColor, Color.white);
				}
				/*end new code added Feb.15.2007 oliver*/
				gL.setColor(taxonColor);
				if (underlined)
					gL.drawLine(treeDrawing.x[N], treeDrawing.y[N]+1,treeDrawing.x[N] +  fm.stringWidth(s), treeDrawing.y[N]+1);
			}
		}
	}


	/*.................................................................................................................*/
	public String getName() {
		return "ToL Draw Names for Tree Display";
	}

	public String getExplanation() {
		return "Draws taxon names on a tree for editing ToL trees." ;
	}

}
