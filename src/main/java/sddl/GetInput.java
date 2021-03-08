package sddl;

import java.util.*;
import org.json.*;

public class GetInput
{
        public static void main(String[] args)
    {
        String sddl;
        System.out.println();
        System.out.println();
        System.out.println("Enter your sddl format :");
        Scanner sc = new Scanner(System.in);
        sddl = sc.next();
      
        Sddl_con sddl_con = new Sddl_con();
        sddl_con.parser(sddl);

    }
}