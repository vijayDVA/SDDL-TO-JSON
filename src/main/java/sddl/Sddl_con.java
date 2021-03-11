package sddl;

import org.json.JSONObject;
import java.lang.reflect.Field;
import java.util.*;


public class Sddl_con extends Sddlconstants
{
	static int errs;

    
	Sid owner;
	Sid group;
    SddlFlag[] daclFlags;
	Ace[] daces;
	SddlFlag[] saclFlags;
	Ace[] saces;
	String dacl="",sacl="";

	EnumSet<SdCtr>	control;
	List<Sid>	sidlist = Collections.synchronizedList(new LinkedList<Sid>());
	
	JSONObject DACL_ELEMENTS =new JSONObject();
	JSONObject DACL_ELEMENT =new JSONObject();
	JSONObject SACL_ELEMENTS =new JSONObject();
	JSONObject SACL_ELEMENT =new JSONObject();
	JSONObject json=new JSONObject();
       

	class Sid {
		String sidstr;								// SID string or SID constant string
		String name;								// SID name
		String descr;								// human-readable description
		private boolean isConstant;
		private String[] sub;						// S-R-I-S-S...

		Sid(String sidstr) {
			if (sidstr != null && !sidstr.isEmpty()) {
				try {
					sub = sidstr.split("-");
					if (sub.length > 1) {							// it is a standard SID string
						sidlist.add(this);
						this.isConstant = false;
					} else {										// it is a SID constant string
						if (sidAliasMap.containsKey(sidstr)) {
							this.name = sidAliasMap.get(sidstr).toString();
							this.descr = sidAliasMap.get(sidstr).descr();
							this.isConstant = true;
						} else throw new InvalidTokenInSddl(sidstr, 0, sidstr);
					}
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}

			this.sidstr = sidstr;
		}

		boolean isConstant() { return isConstant; }
		@Override
		public String toString() { return ( descr != null ? descr : (name != null ? name : sidstr)); }


	}
    public void parser(String sddlformat) throws NullACLsInSddl, UnSupportedTagInSddl, InvalidTokenInSddl, IllegalFormatSddl
    {        
             
        if (sddlformat == null || sddlformat.isEmpty()) {
			throw new NullACLsInSddl();
		}

		if (sddlformat.charAt(sddlformat.length()-1) == '\0') {
			sddlformat = sddlformat.substring(0, sddlformat.length()-1);
		}

		sddlformat = sddlformat.replaceAll("[ \t\n\b\f\r]", "");

		this.control = EnumSet.of(SdCtr.SE_SELF_RELATIVE);

        String[] tokens = sddlformat.split(":");
        
        for (int i =0; i < tokens.length-1; i++) 
        {
            char tag = tokens[i].charAt(tokens[i].length()-1);
            String token = tokens[i+1].substring(0, tokens[i+1].length()-1);
    
            switch (tag)
            {
                case 'O':
					this.owner = new Sid(token);
                    //System.out.println(name);
                    break;
                case 'G':
					this.group = new Sid(token);
                    //System.out.println(group);
                    break;
                case 'D':
					this.control.add(SdCtr.SE_DACL_PRESENT);
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
					this.control.add(SdCtr.SE_SACL_PRESENT);
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
					throw new UnSupportedTagInSddl(tag, sddlformat);                    
            }
        }
		for (Iterator<Sid> siditer = sidlist.iterator(); siditer.hasNext();) {
			Sid sid = siditer.next();

			if (wellKnownSidMap.containsKey(sid.sidstr)) {
				sid.name = wellKnownSidMap.get(sid.sidstr).toString();
				siditer.remove();
			}
			else if (wellKnownSidMap2.containsKey(sid.sidstr)) {
				sid.name = wellKnownSidMap2.get(sid.sidstr).toString();
				siditer.remove();
			}
			else if (sid.sidstr.startsWith("S-1-5-5-")) {	// handle the case S-1-5-5-X-Y
				sid.name = "SECURITY_LOGON_IDS_SID";
				siditer.remove();
			}
		}

		// the rest SIDs are resolved in Class SddlFormat by calling RPC routine to the server

		// keep record of the normalized sddl string       
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
		json.put("OWNER", owner);
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
		AceType[] aceType;					
		AceFlag[] aceFlags;								
		AceRight[] acePermissions;						
		Sid gUID;										
		Sid iGUID;										
		Sid sID;	

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
						gUID = new Sid(fields[3]);
						iGUID = new Sid(fields[4]);
						sID = new Sid(fields[5]);
					} else throw new IllegalFormatSddl(ace);
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
	int tokenSize, T[] ta) throws InvalidTokenInSddl {
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
			
			if (tSize == 0)	{
				throw new InvalidTokenInSddl(t, index, token);
			} else if (tok.length() < tSize) {	
				tSize--;
				continue;
			}
			tok = token.substring(index);
			tSize = tokenSize;
		} 

		return (list.toArray(ta));
	}


	static class NullACLsInSddl extends Exception {
		private static final long serialVersionUID = 1L;

		NullACLsInSddl() {
			super("The security descriptor string format does not support NULL ACLs.");
			errs++;
		}
	}

	static class UnSupportedTagInSddl extends Exception {
		private static final long serialVersionUID = 1L;

		UnSupportedTagInSddl(char tag, String sddlstr) {
			super("Unsupported tag " + tag
					+ " in the security descriptor string "
					+ sddlstr + ".");
			errs++;
		}
	}

	static class InvalidTokenInSddl extends Exception {
		private static final long serialVersionUID = 1L;

		InvalidTokenInSddl(String tok, int index, String token) {
			super("Invalid token " + tok
					+ " at index " + index
					+ " in the token string "
					+ token + ".");
			errs++;
		}
	}

	static class IllegalFormatSddl extends Exception {
		private static final long serialVersionUID = 1L;

		IllegalFormatSddl(String tok) {
			super("Illegal Format SDDL: " + tok);
			errs++;
		}
	}

}
