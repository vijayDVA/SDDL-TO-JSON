package sddl;
import java.util.Map;
import java.util.HashMap;

interface Descr {
	String value();
	String descr();
}
public class Sddlconstants {
    
    enum SidAlias implements Descr {

		//toString()                             value , description
	
		SDDL_DOMAIN_ADMINISTRATORS				("DA", "Domain adminstrator"),
		SDDL_DOMAIN_GUESTS						("DG", "Domain guests"), 
		SDDL_DOMAIN_USERS						("DU", "Domain users"), 
		SDDL_ENTERPRISE_DOMAIN_CONTROLLERS		("ED", "Enterprise domain controllers"), 
		SDDL_DOMAIN_DOMAIN_CONTROLLERS			("DD", "Domain domain controllers"), 
		SDDL_DOMAIN_COMPUTERS					("DC", "Domain computers"), 
		SDDL_BUILTIN_ADMINISTRATORS				("BA", "Builtin (local) administrators"), 
		SDDL_BUILTIN_GUESTS						("BG", "Builtin (local) guests"), 
		SDDL_BUILTIN_USERS						("BU", "Builtin (local) users"), 
		SDDL_LOCAL_ADMIN						("LA", "Local administrator account"), 
		SDDL_LOCAL_GUEST						("LG", "Local group account"), 
		SDDL_ACCOUNT_OPERATORS					("AO", "Account operators"), 
		SDDL_BACKUP_OPERATORS					("BO", "Backup operators"), 
		SDDL_PRINTER_OPERATORS					("PO", "Printer operators"), 
		SDDL_SERVER_OPERATORS					("SO", "Server operators"), 
		SDDL_AUTHENTICATED_USERS				("AU", "Authenticated users"), 
		SDDL_PERSONAL_SELF						("PS", "Personal self"), 
		SDDL_CREATOR_OWNER						("CO", "Creator owner"), 
		SDDL_CREATOR_GROUP						("CG", "Creator group"), 
		SDDL_LOCAL_SYSTEM						("SY", "Local system"), 
		SDDL_POWER_USERS						("PU", "Power users"), 
		SDDL_EVERYONE							("WD", "Everyone ( World )"), 
		SDDL_REPLICATOR							("RE", "Replicator"), 
		SDDL_INTERACTIVE						("IU", "Interactive logon user"), 
		SDDL_NETWORK							("NU", "Nework logon user"), 
		SDDL_SERVICE							("SU", "Service logon user"), 
		SDDL_RESTRICTED_CODE					("RC", "Restricted code"), 
		SDDL_ANONYMOUS							("AN", "Anonymous Logon"), 
		SDDL_SCHEMA_ADMINISTRATORS				("SA", "Schema Administrators"), 
		SDDL_CERT_SERV_ADMINISTRATORS			("CA", "Certificate Server Administrators"), 
		SDDL_RAS_SERVERS						("RS", "RAS servers group"), 
		SDDL_ENTERPRISE_ADMINS					("EA", "Enterprise administrators"), 
		SDDL_GROUP_POLICY_ADMINS				("PA", "Group Policy administrators"), 
		SDDL_ALIAS_PREW2KCOMPACC				("RU", "alias to allow previous windows 2000"), 
		SDDL_LOCAL_SERVICE						("LS", "Local service account (for services)"), 
		SDDL_NETWORK_SERVICE					("NS", "Network service account (for services)"), 
		SDDL_REMOTE_DESKTOP						("RD", "Remote desktop users (for terminal server)"), 
		SDDL_NETWORK_CONFIGURATION_OPS			("NO", "Network configuration operators ( to manage configuration of networking features )"), 
		SDDL_PERFMON_USERS						("MU", "Performance Monitor Users"), 
		SDDL_PERFLOG_USERS						("LU", "Performance Log Users");
		
		private SidAlias(String value, String descr) { this.value = value; this.descr = descr; }
		private final String value;
		private final String descr; 
		public String value() { return this.value; }
		public String descr() { return this.descr; }
	}
	static final Map<String, SidAlias> sidAliasMap = new HashMap<String, SidAlias>();
	static {
		for (SidAlias sid: SidAlias.values()) sidAliasMap.put(sid.value(), sid);
	}
    
	enum SddlFlag implements Descr {
		SDDL_PROTECTED					("P", "DACL or SACL Protected"), 
		SDDL_AUTO_INHERIT_REQ			("AR", "Auto inherit request"), 
		SDDL_AUTO_INHERITED				("AI", "DACL/SACL are auto inherited");
		
		private SddlFlag(String value, String descr) { this.value = value; this.descr = descr; }
		private final String value;
		private final String descr;
		public String value() { return this.value; }
		public String descr() { return this.descr; }
	}
	
	//A map of SDDL flags to their names and descriptions

	static final Map<String, SddlFlag> sddlFlagMap = new HashMap<String, SddlFlag>();
	static {
		for (SddlFlag f: SddlFlag.values()) sddlFlagMap.put(f.value(), f);
	}

	enum AceRight implements Descr {
		SDDL_READ_PROPERTY		("RP"), 
		SDDL_WRITE_PROPERTY		("WP"), 
		SDDL_CREATE_CHILD		("CC"), 
		SDDL_DELETE_CHILD		("DC"), 
		SDDL_LIST_CHILDREN		("LC"), 
		SDDL_SELF_WRITE			("SW"), 
		SDDL_LIST_OBJECT		("LO"), 
		SDDL_DELETE_TREE		("DT"), 
		SDDL_CONTROL_ACCESS		("CR"), 
		SDDL_READ_CONTROL		("RC"), 
		SDDL_WRITE_DAC			("WD"), 
		SDDL_WRITE_OWNER		("WO"), 
		SDDL_STANDARD_DELETE	("SD"), 
		SDDL_GENERIC_ALL		("GA"), 
		SDDL_GENERIC_READ		("GR"), 
		SDDL_GENERIC_WRITE		("GW"), 
		SDDL_GENERIC_EXECUTE	("GX"), 
		SDDL_FILE_ALL			("FA"), 
		SDDL_FILE_READ			("FR"), 
		SDDL_FILE_WRITE			("FW"), 
		SDDL_FILE_EXECUTE		("FX"), 
		SDDL_KEY_ALL			("KA"), 
		SDDL_KEY_READ			("KR"), 
		SDDL_KEY_WRITE			("KW"), 
		SDDL_KEY_EXECUTE		("KX");
		
		private AceRight(String value) { this.value = value; }
		private final String value;
		public String value() { return this.value; }
		public String descr() { return this.toString(); }
	}
	
	//A map of ACE rights to their names

	static final Map<String, AceRight> aceRightMap = new HashMap<String, AceRight>();
	static {
		for (AceRight right: AceRight.values()) aceRightMap.put(right.value(), right);
	}

	enum AceType implements Descr {
		ACCESS_ALLOWED						("A", "Access allowed"), 
		SDDL_ACCESS_DENIED					("D", "Access denied"), 
		SDDL_OBJECT_ACCESS_ALLOWED			("OA", "Object access allowed"), 
		SDDL_OBJECT_ACCESS_DENIED			("OD", "Object access denied"), 
		SDDL_AUDIT							("AU", "Audit"), 
		SDDL_ALARM							("AL", "Alarm"), 
		SDDL_OBJECT_AUDIT					("OU", "Object audit"), 
		SDDL_OBJECT_ALARM					("OL", "Object alarm");
		
		private AceType(String value, String descr) { this.value = value; this.descr = descr; }
		private final String value;
		private final String descr;
		public String value() { return this.value; }
		public String descr() { return this.descr; }
	}
	
	//A map of ACE types to their names and descriptions

	static final Map<String, AceType> aceTypeMap = new HashMap<String, AceType>();
	static {
		for (AceType type: AceType.values()) aceTypeMap.put(type.value(), type);
	}


	enum AceFlag implements Descr {
		SDDL_CONTAINER_INHERIT			("CI", "Container inherit"), 
		SDDL_OBJECT_INHERIT				("OI", "Object inherit"), 
		SDDL_NO_PROPAGATE				("NP", "Inherit no propagate"), 
		SDDL_INHERIT_ONLY				("IO", "Inherit only"), 
		SDDL_INHERITED					("ID", "Inherited"), 
		SDDL_AUDIT_SUCCESS				("SA", "Audit success"), 
		SDDL_AUDIT_FAILURE				("FA", "Audit failure");
		
		private AceFlag(String value, String descr) { this.value = value; this.descr = descr; }
		private final String value;
		private final String descr;
		public String value() { return this.value; }
		public String descr() { return this.descr; }
	}
	
	//A map of ACE flags to their names and descriptions

	static final Map<String, AceFlag> aceFlagMap = new HashMap<String, AceFlag>();
	static {
		for (AceFlag flag: AceFlag.values()) aceFlagMap.put(flag.value(), flag);
	}

}
