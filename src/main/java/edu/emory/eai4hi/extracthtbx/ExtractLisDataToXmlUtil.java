package edu.emory.eai4hi.extracthtbx;

import edu.emory.eai4hi.extracthtbx.data.HeartBxPatients;
import edu.emory.eai4hi.extracthtbx.data.Patient;
import edu.emory.eai4hi.extracthtbx.finder.PatientCaseSlideFinder;
import java.util.Map;
import java.util.TreeSet;

/**
 *
 * @author geoffrey.smith@emory.edu
 */
public class ExtractLisDataToXmlUtil {
    
    public static void main(String[] args) throws Exception {
        
        Map<String, Patient> patientMap = PatientCaseSlideFinder.getPatientMap();
        HeartBxPatients heartBxPatients = new HeartBxPatients(new TreeSet<>(patientMap.values()));
        heartBxPatients.xmlMarshal("heart_bx");
        
    }
    
}