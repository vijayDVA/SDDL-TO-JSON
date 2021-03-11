package sddl;

import static org.junit.Assert.*;

import org.junit.Test;
import sddl.Sddl_con.InvalidTokenInSddl;
import sddl.Sddl_con.NullACLsInSddl;
import sddl.Sddl_con.UnSupportedTagInSddl;
import sddl.Sddl_con.IllegalFormatSddl;

public class SddlformatTest
{

    Sddl_con sddl = new Sddl_con();

	@Test(expected=Sddl_con.NullACLsInSddl.class)
    public void NullACLsInSddlException()
	throws NullACLsInSddl, UnSupportedTagInSddl, InvalidTokenInSddl, Sddl_con.IllegalFormatSddl {
		sddl.parser(null);
    }

	@Test(expected=Sddl_con.NullACLsInSddl.class)
    public void NullACLsInSddlException2()
	throws NullACLsInSddl, UnSupportedTagInSddl, InvalidTokenInSddl, IllegalFormatSddl {
		sddl.parser("");
    }
	
	@Test(expected=Sddl_con.UnSupportedTagInSddl.class)
    public void UnSupportedTagInSddlException()
	throws NullACLsInSddl, UnSupportedTagInSddl, InvalidTokenInSddl, IllegalFormatSddl {
        sddl.parser("F:AOG:DAD:(A;;RPWPCCDCLCSWRCWDWOGA;;;S-1-0-0)");
    }
	
	
	@Test
	public final void testSddl()
     {
		try 
        {
			//owner sid is constant string
            sddl.parser("O:AOG:DAD:(A;;RPWPCCDCLCSWRCWDWOGA;;S-1-5-5-8-12;S-1-0-0)");
			assertTrue(sddl.owner.isConstant());
			//owner sid constant
			assertEquals("SDDL_ACCOUNT_OPERATORS", sddl.owner.name);
			//group sid is constant string
			assertTrue(sddl.group.isConstant());
			//group sid contant
			assertEquals("SDDL_DOMAIN_ADMINISTRATORS", sddl.group.name);
			//empty daclflags
			assertTrue(sddl.daclFlags == null || sddl.daclFlags.length == 0);
			//one dace
			assertTrue(sddl.daces.length == 1);
			//one ace type
			assertEquals(1, sddl.daces[0].aceType.length);
			//empty ace flag
			assertEquals(0, sddl.daces[0].aceFlags.length);
			//10 ace permissions
			assertEquals(10, sddl.daces[0].acePermissions.length);
			//empty ace guid
			assertTrue(sddl.daces[0].gUID == null || sddl.daces[0].gUID.sidstr == null || sddl.daces[0].gUID.sidstr.isEmpty());
			//ace iguid is not a constant string, but a specialized well-known sid
			assertEquals("SECURITY_LOGON_IDS_SID", sddl.daces[0].iGUID.name);
			//ace sid is not a constant string
			assertFalse(sddl.daces[0].sID.isConstant());
			//ace sid is a well-known sid
			assertEquals("SECURITY_NULL_SID", sddl.daces[0].sID.name);
			//sacl is not present
			assertFalse(sddl.control.contains(Sddlconstants.SdCtr.SE_SACL_PRESENT));
			//there is no other non-constant and non-well-known sids
			assertEquals(0, sddl.sidlist.size());
System.out.printf("There are total %d error(s) in the string(s). See the above for details.\n", sddl.errs);
		} catch (Exception e) 
        {
			System.out.println(e.getMessage());
		}
	}

            public static void main(String args[])
            {
                org.junit.runner.JUnitCore.main("sddl.SddlTest");
            }

}
