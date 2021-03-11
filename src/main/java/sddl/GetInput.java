package sddl;

import java.util.*;

public class GetInput
{
        public static void main(String[] args)
    {
        try {
            String sddl;
            System.out.println();
            System.out.println();
            System.out.println("Enter your sddl format :");
            Scanner sc = new Scanner(System.in);
            sddl = sc.next();
            /*ssdl="O:AOG:DAD:(A;;CCDCLCSWRPWPDTLOCRSDRCWDWO;;;BA)(A;;CCDCLCSWRPWPDTLOCRSDRCWDWO;;;SY)(A;;CCLCSWLORC;;;BO)S:(AU;FA;CCDCLCSWRPWPDTLOCRSDRCWDWO;;;WD)"; */
            Sddl_con sddl_con = new Sddl_con();
            sddl_con.parser(sddl);
        }
        catch (Sddl_con.NullACLsInSddl nullACLsInSddl) {
        nullACLsInSddl.printStackTrace();
        } catch (Sddl_con.UnSupportedTagInSddl unSupportedTagInSddl) {
            unSupportedTagInSddl.printStackTrace();
        } catch (Sddl_con.InvalidTokenInSddl invalidTokenInSddl) {
            invalidTokenInSddl.printStackTrace();
        } catch (Sddl_con.IllegalFormatSddl illegalFormatSddl) {
            illegalFormatSddl.printStackTrace();
        }

    }
}