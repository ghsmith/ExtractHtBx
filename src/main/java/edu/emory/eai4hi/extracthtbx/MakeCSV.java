package edu.emory.eai4hi.extracthtbx;

import edu.emory.eai4hi.extracthtbx.data.Case;
import edu.emory.eai4hi.extracthtbx.data.HeartBxPatients;
import edu.emory.eai4hi.extracthtbx.data.Patient;
import edu.emory.eai4hi.extracthtbx.data.Slide;
import edu.emory.eai4hi.extracthtbx.finder.PatientCaseSlideFinder;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 *
 * @author geoffrey.smith@emory.edu
 */
public class MakeCSV {
    
    public static void main(String[] args) throws Exception {

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        System.out.println(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s",
            "patMrn",
            "patStudyId",
            "pathologyCaseNo",
            "pathologyCaseStudyId",
            "pathologyCollectionDt",
            "pathologyCollectDaysDelta",
            "partId",
            "blockId",
            "slideNo",
            "stain",
            "slideStudyId",
            "slideFileName",
            "pathologyFinalDx",
            "pathologyAddendumDx"
        ));
        edu.emory.eai4hi.extracthtbx.data.HeartBxPatients heartBxPatients;
        heartBxPatients = edu.emory.eai4hi.extracthtbx.data.HeartBxPatients.xmlUnmarshal("heart_bx_with_dx");
        int xpat = 0;
        int xcase = 0;
        int xslide = 0;
        for(Patient patient : heartBxPatients.patients) { xpat++;
            for(Case case_ : patient.cases) { xcase++;
                for(Slide slide : case_.slides) { xslide++;
                    System.out.println(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s",
                        patient.mrn,
                        patient.patIdHash.substring(0, 8),
                        case_.accNo,
                        case_.accNoHash.substring(0, 8),
                        formatter.format(case_.collectionDate),
                        case_.collectionDayDelta,
                        slide.partId,
                        slide.blockNo,
                        slide.slideNo,
                        slide.stain,
                        slide.anonSlideId,
                        slide.anonSlideFileName,
                        case_.finalDx.replace("\n", "<br/>"),
                        case_.addendumDx.replace("\n", "<br/>")
                    ));
                }
            }
        }
        System.out.println(xpat +" "+xcase+" "+xslide);
                
    }
    
}