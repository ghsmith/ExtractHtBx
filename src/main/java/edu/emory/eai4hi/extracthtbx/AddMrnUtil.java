package edu.emory.eai4hi.extracthtbx;

import edu.emory.eai4hi.extracthtbx.data.Case;
import edu.emory.eai4hi.extracthtbx.data.HeartBxPatients;
import edu.emory.eai4hi.extracthtbx.data.Patient;
import edu.emory.eai4hi.extracthtbx.data.Slide;
import edu.emory.eai4hi.extracthtbx.finder.PatientCaseSlideFinder;
import java.util.Arrays;
import java.util.Date;

/**
 *
 * @author geoffrey.smith@emory.edu
 */
public class AddMrnUtil {
    
    public static void main(String[] args) throws Exception {

/*        edu.emory.eai4hi.extracthtbx.data.HeartBxPatients heartBxPatients;
        heartBxPatients = edu.emory.eai4hi.extracthtbx.data.HeartBxPatients.xmlUnmarshal("heart_bx");
        //int x = 0;
        for(Patient patient : heartBxPatients.patients) {
            for(Case case_ : patient.cases) {
                System.out.println(case_.accNo);
                String[] dx = PatientCaseSlideFinder.getDx(case_.accNo);
                case_.finalDx = dx[0] != null ? dx[0].replace("<br/>", "\n") : "";
                case_.addendumDx = dx[1] != null ? dx[1].replace("<br/>", "\n") : "";
                //if(x++ > 5) { break; }
                for(Slide slide : case_.slides) {
                }
            }
            //if(x > 5) { break; }
        }
        heartBxPatients.xmlMarshal("heart_bx_with_dx");*/

        {
            edu.emory.eai4hi.extracthtbx.data.HeartBxPatients heartBxPatients;
            heartBxPatients = edu.emory.eai4hi.extracthtbx.data.HeartBxPatients.xmlUnmarshal("heart_bx_with_dx");
            for(Patient patient : heartBxPatients.patients) {
                Date collectionDateBase = null;
                for(Case case_ : patient.cases) {
                    if(collectionDateBase == null) { collectionDateBase = case_.collectionDate; }
                    case_.collectionDayDelta = (case_.collectionDate.getTime() - collectionDateBase.getTime()) / (24L*60L*60L*1000L);
                    for(Slide slide : case_.slides) {
                    }
                }
            }
            heartBxPatients.xmlMarshal("heart_bx_with_dx2");                

        }
    }
    
}