package mesquite.ToLEditor.lib;

import mesquite.lib.*;

public class ToLRanks {
	public static final int NORANK=-1;
	public static final int URKINGDOM = 0;
	public static final int KINGDOM = 1;
	public static final int PHYLUM = 2;
	public static final int SUBPHYLUM = 3;
	public static final int SUPERCLASS = 4;
	public static final int CLASS = 5;
	public static final int SUBCLASS = 6;
	public static final int SUPERORDER = 7;
	public static final int ORDER = 8;
	public static final int SUBORDER = 9;
	public static final int SUPERFAMILY = 10;
	public static final int FAMILY = 11;
	public static final int SUBFAMILY=12;
	public static final int SUPERTRIBE = 13;
	public static final int TRIBE=14;
	public static final int SUBTRIBE = 15;
	public static final int GENUS = 16;
	public static final int SUBGENUS = 17;
	
	public static final int SECTION =18;
	public static final int SUBSECTION =19;
	public static final int SERIES = 20;
	public static final int SUBSERIES = 21;
	public static final int SPECIESGROUP = 22;
	
	public static final int SPECIES = 23;
	public static final int SUBSPECIES = 24;
	
	public static final int NUMRANKS = 25;
	
	public static final int LOWESTRANK = Integer.MAX_VALUE;
	
	public static final int ENGLISH =0;

	/*.................................................................................................................*/
	public static String getRankString(int rankInt, int language){
		switch (language) {
		case ENGLISH:
			switch (rankInt) {
			case URKINGDOM:
				return "Ur-Kingdom";
			case KINGDOM:
				return "Kingdom";
			case PHYLUM:
				return "Phylum";
			case SUBPHYLUM:
				return "Subphylum";
			case SUPERCLASS:
				return "Superclass";
			case CLASS:
				return "Class";
			case SUBCLASS:
				return "Subclass";
			case SUPERORDER:
				return "Superorder";
			case ORDER:
				return "Order";
			case SUBORDER:
				return "Suborder";
			case SUPERFAMILY :
				return "Superfamily";
			case FAMILY :
				return "Family";
			case SUBFAMILY:
				return "Subfamily";
			case SUPERTRIBE :
				return "Supertribe";
			case TRIBE:
				return "Tribe";
			case SUBTRIBE :
				return "Subtribe";
			case GENUS:
				return "Genus";
			case SUBGENUS :
				return "Subgenus";
				
			case SECTION:
				return "Section";
			case SUBSECTION:
				return "Subsection";
			case SERIES:
				return "Series";
			case SUBSERIES:
				return "Subseries";
			case SPECIESGROUP:
				return "Species group";

			case SPECIES :
				return "Species";
			case SUBSPECIES:
				return "Subspecies";
			}
		}
		return null;
	}

	/*.................................................................................................................*/
	public static int getRankInt(String rankName, int language){
		if (StringUtil.blank(rankName)) return -1;
		switch (language) {
		case ENGLISH:
			if (rankName.equalsIgnoreCase("URKINGDOM"))
				return URKINGDOM;
			if (rankName.equalsIgnoreCase("UR-KINGDOM"))
				return URKINGDOM;
			if (rankName.equalsIgnoreCase("KINGDOM"))
				return KINGDOM;
			if (rankName.equalsIgnoreCase("PHYLUM"))
				return PHYLUM;
			if (rankName.equalsIgnoreCase("SUBPHYLUM"))
				return SUBPHYLUM;
			if (rankName.equalsIgnoreCase("SUPERCLASS"))
				return SUPERCLASS;
			if (rankName.equalsIgnoreCase("CLASS"))
				return CLASS;
			if (rankName.equalsIgnoreCase("SUBCLASS"))
				return SUBCLASS;
			if (rankName.equalsIgnoreCase("SUPERORDER"))
				return SUPERORDER;
			if (rankName.equalsIgnoreCase("ORDER"))
				return ORDER;
			if (rankName.equalsIgnoreCase("SUBORDER"))
				return SUBORDER;
			if (rankName.equalsIgnoreCase("SUPERFAMILY"))
				return SUPERFAMILY;
			if (rankName.equalsIgnoreCase("FAMILY"))
				return FAMILY;
			if (rankName.equalsIgnoreCase("subfamily"))
				return SUBFAMILY;
			if (rankName.equalsIgnoreCase("supertribe"))
				return SUPERTRIBE;
			if (rankName.equalsIgnoreCase("tribe"))
				return TRIBE;
			if (rankName.equalsIgnoreCase("subtribe"))
				return SUBTRIBE;
			if (rankName.equalsIgnoreCase("genus"))
				return GENUS;
			if (rankName.equalsIgnoreCase("subgenus"))
				return SUBGENUS;
			
			if (rankName.equalsIgnoreCase("SECTION"))
				return SECTION;
			if (rankName.equalsIgnoreCase("SUBSECTION"))
				return SUBSECTION;
			if (rankName.equalsIgnoreCase("SERIES"))
				return SERIES;
			if (rankName.equalsIgnoreCase("SUBSERIES"))
				return SUBSERIES;
			if (rankName.equalsIgnoreCase("SPECIESGROUP"))
				return SPECIESGROUP;
			if (rankName.equalsIgnoreCase("SPECIES-GROUP"))
				return SPECIESGROUP;

			if (rankName.equalsIgnoreCase("species"))
				return SPECIES;
			if (rankName.equalsIgnoreCase("subspecies"))
				return SUBSPECIES;
		}
		return -1;
	}

}
