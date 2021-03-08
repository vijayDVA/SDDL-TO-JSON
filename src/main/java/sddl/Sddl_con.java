package sddl;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Map;


import org.json.JSONObject;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;




public class Sddl_con extends Sddlconstants
{
    static String name,group;
    SddlFlag[] daclFlags;
	Ace[] daces;
	SddlFlag[] saclFlags;
	Ace[] saces;
	static String dacl="",sacl="";

	
	JSONObject DACL_ELEMENTS =new JSONObject();
	JSONObject DACL_ELEMENT =new JSONObject();
	JSONObject SACL_ELEMENTS =new JSONObject();
	JSONObject SACL_ELEMENT =new JSONObject();
	JSONObject json=new JSONObject();
       

    void parser(String sddlformat)
    {        
             
        
        String[] tokens = sddlformat.split(":");
        
        for (int i =0; i < tokens.length-1; i++) 
        {
            char tag = tokens[i].charAt(tokens[i].length()-1);
            String token = tokens[i+1].substring(0, tokens[i+1].length()-1);
    
            switch (tag)
            {
                case 'O':
                    name = sidAliasMap.get(token).descr();
                    //System.out.println(name);
                    break;
                case 'G':
                    group = sidAliasMap.get(token).descr();
                    //System.out.println(group);
                    break;
                case 'D':
                    toACL(token, 'D');
                    int j=0;
                    for(Sddl_con.Ace ace: daces){
                        //dacl = dacl+("   Element #" + j + ": (" + ace.aceStr + ")\n" + ace.toString());
                    
					DACL_ELEMENT= ace.ACLJSON();
					DACL_ELEMENTS.put("ELEMENT "+j, DACL_ELEMENT);
					j++;
					}
					//System.out.println(dacl);

                    break;
                case 'S':
                    toACL(token,'S');
                    int k = 0;
					if(saces != null){
                    for(Sddl_con.Ace ace: saces){
                        //sacl = sacl + ("   Element #" + k++ + ": (" + ace.aceStr + ")\n" + ace.toString());
					SACL_ELEMENT= ace.ACLJSON();
					SACL_ELEMENTS.put("ELEMENT "+k, SACL_ELEMENT);
					k++;
                    }
					}
                   // System.out.println(sacl);
                    break;
                default:                    
            }
        }        
		alltojson();
    }

	public void alltojson()
	{
		try {
			Field changeMap = json.getClass().getDeclaredField("map");
			changeMap.setAccessible(true);
			changeMap.set(json, new LinkedHashMap<String,Object>());
			changeMap.setAccessible(false);
		  } catch (Exception e) {
			System.out.println(e.getMessage());
		  }
		json.put("OWNER", name);
        json.put("GROUP", group);
        json.put("DACL",DACL_ELEMENTS);
        json.put("SACL",SACL_ELEMENTS);
		System.out.println();
        System.out.println();
        System.out.println("SDDL Format to JSON Format :");
		System.out.println();
		System.out.println(json);
	}
    private void toACL(String token, final char tag) {
		SddlFlag[] aclFlags = null;
		Ace[] aces = null;

		if (!token.isEmpty()) {
			//there must be a '('
			int f_endindex = token.indexOf("(");
			if (f_endindex > 0)
				try {
					aclFlags = tokenParse(token.substring(0, f_endindex), sddlFlagMap, 2, new SddlFlag[0]);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			String[] str_aces = token.substring(f_endindex).split("[" + "(" + "]|["
					+ ")" + "][" + "(" + "]|[" + ")" + "]");
			// the first is empty, start from 1
			aces = new Ace[str_aces.length-1];
			for (int i = 1; i < str_aces.length; i++) {
				aces[i-1] = new Ace(str_aces[i]);
			}
		}
		if (tag == 'D') {
			this.daclFlags = aclFlags;
			this.daces = aces;
		} else {
			this.saclFlags = aclFlags;
			this.saces = aces;
		}
    }    

	
    public class Ace {
        String aceStr;
		AceType[] aceType;								//allow/deny/audit
		AceFlag[] aceFlags;								//inheritance and audit settings
		AceRight[] acePermissions;						//list of incremental permissions
		String gUID="";									//Object type
		String iGUID="";								//Inherited object type
		String sID="";									//Trustee

		Ace(String ace) 
        {
			if (ace != null && !ace.isEmpty()) 
            {
				try
                 {
                    this.aceStr = ace;
					String[] fields = ace.split(";");
					if (fields.length == 6) 
                    {
						aceType = tokenParse(fields[0], aceTypeMap, 2, new AceType[0]);
						aceFlags = tokenParse(fields[1], aceFlagMap, 2, new AceFlag[0]);
						acePermissions = tokenParse(fields[2], aceRightMap, 2, new AceRight[0]);
						if(fields[3].length()!=0){
						gUID = sidAliasMap.get(fields[3]).descr();}
					
						if(fields[4].length()!=0){
						iGUID = sidAliasMap.get(fields[4]).descr();}

						if(fields[5].length()!=0){
						sID = sidAliasMap.get(fields[5]).descr();
						}
					} 
				} catch (Exception e) 
                {
					System.out.println(e.getMessage());
				}
			}
		}

        
		@Override
		public String toString() {
			StringBuilder str = new StringBuilder();

			
			if (this.aceType == null)
				str.append("\tACEType:\t" + "\n");
			else
				str.append("\t\tACEType: " + Arrays.toString(this.aceType) + "\n");

			if (this.aceFlags == null)
				str.append("\tACEFlags:\t" + "\n");
			else
				str.append("\t\tACEFlags: " + Arrays.toString(this.aceFlags) + "\n");

			if (this.acePermissions == null)
				str.append("\tACEPermissions:\t" + "\n");
			else
				str.append("\t\tACEPermissions: " + Arrays.toString(this.acePermissions) + "\n");

			str.append("\t\tACEObjectType: " + this.gUID + "\n");
			str.append("\t\tACEInheritedObjectType: " + this.iGUID + "\n");
			str.append("\t\tACETrustee: " + this.sID + "\n");

			return str.toString();
		}
		public JSONObject ACLJSON()
		{
			JSONObject ACE_ALL = new JSONObject();
			try {
				Field changeMap = ACE_ALL.getClass().getDeclaredField("map");
				changeMap.setAccessible(true);
				changeMap.set(ACE_ALL, new LinkedHashMap<String,Object>());
				changeMap.setAccessible(false);
			  } catch (Exception e) {
				System.out.println(e.getMessage());
			  }
			ACE_ALL.put("ACETYPE", this.aceType);
			ACE_ALL.put("ACEFLAGS",this.aceFlags);
			ACE_ALL.put("ACEPERMISSIONS", this.acePermissions);
			ACE_ALL.put("ACEOBJECTTYPE",this.gUID);
			ACE_ALL.put("ACEINHERITEDOBJECTTYPE",this.iGUID);
			ACE_ALL.put("ACETRUSTEE", this.sID);
			return ACE_ALL;
		}
		
	}
    private static <T extends Enum<T>> T[] tokenParse(String token, Map<String, T> tokenMap,
	int tokenSize, T[] ta)  {
		List<T> list = new ArrayList<T>();

		int	index = 0;
		String tok = token.substring(index);
		int tSize = tokenSize;
		String t = null;

	
		while(!tok.isEmpty()) {
			while (tSize > 0 && tok.length() >= tSize) {
				t = tok.substring(0, tSize);
				if (tokenMap.containsKey(t)) {
					list.add(tokenMap.get(t));
					index += tSize;
					break;
				} else {
					tSize--;
					continue;
				}
			}	
			
			 if (tok.length() < tSize) {	
				tSize--;
				continue;
			}
			tok = token.substring(index);
			tSize = tokenSize;
		} 

		return (list.toArray(ta));
	}

}
