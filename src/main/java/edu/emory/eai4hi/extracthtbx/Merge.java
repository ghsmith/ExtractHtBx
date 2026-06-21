package edu.emory.eai4hi.extracthtbx;

import edu.emory.eai4hi.extracthtbx.data.*;
import edu.emory.eai4hi.extracthtbx.finder.PatientCaseSlideFinder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author geoffrey.smith@emory.edu
 */
public class Merge {
    
    public static void main(String[] args) throws Exception {

        HeartBxPatients heartBxPatientsOld = edu.emory.eai4hi.extracthtbx.data.HeartBxPatients.xmlUnmarshal(args[0]);
        Map<String, Slide> slideMap = new HashMap<>();
        for(Patient patient : heartBxPatientsOld.patients) {
            for(Case case_ : patient.cases) {
                for(Slide slide : case_.slides) {
                    slideMap.put(String.format("%s-%s%s-%s", case_.accNo, slide.partId, slide.blockNo, slide.slideNo), slide);
                }
            }
        }

        HeartBxPatients heartBxPatientsNew = edu.emory.eai4hi.extracthtbx.data.HeartBxPatients.xmlUnmarshal(args[1]);
        for(Patient patient : heartBxPatientsNew.patients) {
            for(Case case_ : patient.cases) {
                for(Slide slide : case_.slides) {
                    if(slide.anonSlideId == null) {
                        String slideId = String.format("%s-%s%s-%s", case_.accNo, slide.partId, slide.blockNo, slide.slideNo);
                        Slide slideOld = slideMap.get(slideId);
                        if(slideOld == null) {
                            System.out.println("*** can't find " + slideId);
                            continue;
                        }
                        //System.out.println("found " + slideId);
                        slide.anonSlideId = slideOld.anonSlideId;
                        slide.anonSlideFileName = slideOld.anonSlideFileName;
                    }
                    else {
                        String stainAbridged = slide.stain.replace(" ", "_").replace("&", "").replace("-", "").replace("/", "");
                        stainAbridged = stainAbridged.substring(0, Math.min(10, stainAbridged.length()));
                        String correctSvsFileName = String.format("%s-%s-%s%02d-%02d-%s-%s.svs", patient.patIdHash.substring(0, 8), case_.accNoHash.substring(0, 8), slide.partId, slide.blockNo, slide.slideNo, stainAbridged, slide.anonSlideId);
                        System.out.println(String.format("rename \"%s\" \"%s\"", slide.anonSlideFileName, correctSvsFileName));
                        slide.anonSlideFileName = correctSvsFileName;
                    }
                }
            }
        }
        heartBxPatientsNew.xmlMarshal(args[2]);

    }
    
}