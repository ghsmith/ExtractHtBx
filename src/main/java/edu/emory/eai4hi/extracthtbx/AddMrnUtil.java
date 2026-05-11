package edu.emory.eai4hi.extracthtbx;

import edu.emory.eai4hi.extracthtbx.data.Case;
import edu.emory.eai4hi.extracthtbx.data.HeartBxPatients;
import edu.emory.eai4hi.extracthtbx.data.Patient;
import edu.emory.eai4hi.extracthtbx.data.Slide;
import edu.emory.eai4hi.extracthtbx.finder.PatientCaseSlideFinder;
import java.util.Arrays;

/**
 *
 * @author geoffrey.smith@emory.edu
 */
public class AddMrnUtil {
    
    public static void main(String[] args) throws Exception {

        edu.emory.eai4hi.extracthtbx.data.HeartBxPatients heartBxPatients;
        heartBxPatients = edu.emory.eai4hi.extracthtbx.data.HeartBxPatients.xmlUnmarshal("heart_bx2");
        HeartBxPatients heartBxPatientsFiltered = new HeartBxPatients();
        for(Patient patient : heartBxPatients.patients) {
            System.out.println(patient.patId);
            if(Arrays.asList(new String[] { "1882353", "8236908", "18711695", "7172213", "11293856", "7363178", "1657908" }).contains(patient.mrn)) {
                System.out.println("hit!");
                heartBxPatientsFiltered.patients.add(patient);
            }
            for(Case case_ : patient.cases) {
                for(Slide slide : case_.slides) {
                }
            }
        }
        heartBxPatientsFiltered.xmlMarshal("heart_bx_enrolled_20260131");
        
    }
    
}