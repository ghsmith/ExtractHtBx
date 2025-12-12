package edu.emory.eai4hi.extracthtbx;

import edu.emory.eai4hi.extracthtbx.data.anon.HeartBxPatients;

/**
 *
 * @author geoffrey.smith@emory.edu
 */
public class AnonymizeXmlUtil {
    
    public static void main(String[] args) throws Exception {

        HeartBxPatients heartBxPatients;
        heartBxPatients = HeartBxPatients.xmlUnmarshal("heart_bx");
        heartBxPatients.xmlMarshal("heart_bx_anon");
        
    }
    
}