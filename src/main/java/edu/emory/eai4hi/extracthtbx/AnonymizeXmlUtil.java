package edu.emory.eai4hi.extracthtbx;

import edu.emory.eai4hi.extracthtbx.data.anon.*;

/**
 *
 * @author geoffrey.smith@emory.edu
 */
public class AnonymizeXmlUtil {
    
    public static void main(String[] args) throws Exception {

        HeartBxPatients heartBxPatients;
        heartBxPatients = HeartBxPatients.xmlUnmarshal("heart_bx_with_dx_20260615");
        for(Patient patient : heartBxPatients.patients) {
           patient.patIdHash = patient.patIdHash.substring(0, 8);
           for(Case case_ : patient.cases) {
               case_.accNoHash = case_.accNoHash.substring(0, 8);
               for(Slide slide : case_.slides) {
               }
           }
       }
       heartBxPatients.xmlMarshal("heart_bx_with_dx_anon_20260615");
        
    }
    
}