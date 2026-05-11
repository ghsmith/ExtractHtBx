package edu.emory.eai4hi.extracthtbx;

import edu.emory.eai4hi.extracthtbx.data.Case;
import edu.emory.eai4hi.extracthtbx.data.HeartBxPatients;
import edu.emory.eai4hi.extracthtbx.data.Patient;
import edu.emory.eai4hi.extracthtbx.data.Slide;

/**
 *
 * @author geoffrey.smith@emory.edu
 */
public class FileOps {
    
    public static void main(String[] args) throws Exception {

        HeartBxPatients heartBxPatients;
        heartBxPatients = HeartBxPatients.xmlUnmarshal("heart_bx");
        int x = 0;
        for(Patient patient : heartBxPatients.patients) {
            for(Case case_ : patient.cases) {
                for(Slide slide : case_.slides) {
                    /*slide.anonSlideFileNameOld = slide.anonSlideFileName;
                    String stainAbridged = slide.stain.replace(" ", "_").replace("&", "").replace("-", "");
                    stainAbridged = stainAbridged.substring(0, Math.min(10, stainAbridged.length()));
                    slide.anonSlideFileName = String.format("%s-%s-%s%02d-%02d-%s-%s.svs", patient.patIdHash.substring(0, 8), case_.accNoHash.substring(0, 8), slide.partId, slide.blockNo, slide.slideNo, stainAbridged, slide.anonSlideId);*/
//                    if(slide.anonSlideFileNameOld != null) {
//                        x++;
//                        System.out.println(String.format("copy \"%s\" %s", slide.anonSlideFileName, "z:"));
//                    }
                }
            }
        }
        //heartBxPatients.xmlMarshal("heart_bx");
        System.out.println(x);
    }
    
}