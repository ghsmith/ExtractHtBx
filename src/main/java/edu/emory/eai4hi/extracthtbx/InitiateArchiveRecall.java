package edu.emory.eai4hi.extracthtbx;

import edu.emory.eai4hi.extracthtbx.data.Case;
import edu.emory.eai4hi.extracthtbx.data.HeartBxPatients;
import edu.emory.eai4hi.extracthtbx.data.Patient;
import edu.emory.eai4hi.extracthtbx.data.Slide;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.json.JSONObject;

/**
 *
 * @author geoffrey.smith@emory.edu
 */
public class InitiateArchiveRecall {

    public static void main(String[] args) throws Exception {

        HeartBxPatients heartBxPatients = HeartBxPatients.xmlUnmarshal("heart_bx");
        
        boolean anon = true;
        
        Properties prop = new Properties();
        InputStream stream = new FileInputStream(args[0]);
        prop.load(stream);        
        
        String url = prop.getProperty("url");
        String u = prop.getProperty("u");
        String p = prop.getProperty("p");
        
        CookieHandler.setDefault(new CookieManager());
        HttpClient client = HttpClient.newBuilder()
                .cookieHandler(CookieHandler.getDefault())
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        
        for(Patient patient : heartBxPatients.patients) { for(Case case_ : patient.cases) { for(Slide slide : case_.slides) {

            String accNo = case_.accNo;
            String slideId = String.format("%s-%s%d-%d", case_.accNo, slide.partId, slide.blockNo, slide.slideNo);

            System.out.println(String.format("processing slide %s", slideId));

            if(case_.recallInitiatedDate != null) {
                System.out.println("skipping (case recall already initiated)");
                continue;
            }

            if(slide.anonSlideFileName != null && slide.anonSlideFileName.length() > 0) {
                System.out.println("skipping (already downloaded)");
                continue;
            }
            
            // LOGIN
            {
                HttpRequest request = HttpRequest.newBuilder()
                  .uri(URI.create(url + "/uniview/Logon.ashx"))
                  .setHeader("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
                  .POST(HttpRequest.BodyPublishers.ofString(String.format("{\"Domain\":\"\",\"Password\":\"%s\",\"UserName\":\"%s\"}", p, u)))
                  .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if(response == null || response.statusCode() != 200) {
                    System.out.println("login failed");
                    System.exit(1);
                }
                System.out.println("logged into server");
            }

            // FIND DESIRED CASE
            String uniViewPatientIdentifier = null;
            String patientHistoryFetchToken = null;
            String uniViewHistoryItemIdentifier = null;
            String fetchToken = null;
            String studyDescription = null;
            {

                HttpRequest requestSearch = HttpRequest.newBuilder()
                  .uri(URI.create(url + "/uniview/Search.ashx"))
                  .setHeader("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
                  .POST(HttpRequest.BodyPublishers.ofString(String.format("searchServer=2&freeText=\"%s\"", accNo)))
                  .build();
                HttpResponse<String> responseSearch = client.send(requestSearch, HttpResponse.BodyHandlers.ofString());

                for(Iterator iterPatient = (new JSONObject(responseSearch.body())).getJSONArray("Patients").iterator(); iterPatient.hasNext(); ) {

                    JSONObject joPatient = (JSONObject)iterPatient.next();
                    uniViewPatientIdentifier = joPatient.getString("UniViewPatientIdentifier");
                    patientHistoryFetchToken = joPatient.getString("PatientHistoryFetchToken");

                    HttpRequest requestHistory = HttpRequest.newBuilder()
                      .uri(URI.create(url + "/uniview/GetPatientHistory.ashx"))
                      .setHeader("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
                      .POST(HttpRequest.BodyPublishers.ofString(String.format("patientHistoryFetchToken=%s", URLEncoder.encode(patientHistoryFetchToken))))
                      .build();
                    HttpResponse<String> responseHistory = client.send(requestHistory, HttpResponse.BodyHandlers.ofString());

                    for(Iterator iterHistory = new JSONObject(responseHistory.body()).getJSONArray("History").iterator(); iterHistory.hasNext(); ) {
                        JSONObject joHistory = (JSONObject)iterHistory.next();
                        String accessionNumber = joHistory.getString("AccessionNumber");
                        if(accessionNumber.equals(accNo)) {
                            uniViewHistoryItemIdentifier = joHistory.getString("UniViewHistoryItemIdentifier");
                            fetchToken = joHistory.getJSONArray("RequestedProcedures").getJSONObject(0).getString("FetchToken");
                            studyDescription = joHistory.getJSONArray("RequestedProcedures").getJSONObject(0).getString("StudyDescription");
                            break;
                        }
                    }

                    if(uniViewHistoryItemIdentifier != null) {
                        break;
                    }

                }

            }

            if(uniViewHistoryItemIdentifier == null ) {
                System.out.println(String.format("examination \"%s\" not found", accNo));
                logout(url, client);
                continue;
            }

            // INITIALIZE PATHOLOGY SESSION
            String value = null;
            String hash = null;
            {
                HttpRequest requestPathSession = HttpRequest.newBuilder()
                  .uri(URI.create(url + "/uniview/InitializePathologySession.ashx"))
                  .setHeader("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
                  .POST(HttpRequest.BodyPublishers.ofString(String.format("fetchToken=%s", URLEncoder.encode(fetchToken))))
                  .build();
                HttpResponse<String> responsePathSession = client.send(requestPathSession, HttpResponse.BodyHandlers.ofString());
                JSONObject joPathSession = new JSONObject(responsePathSession.body());
                value = joPathSession.getString("Value");
                hash = joPathSession.getString("Hash");
            }

            // LOOP THROUGH SLIDES
            boolean slideFound = false;
            {
                HttpRequest requestSlides = HttpRequest.newBuilder()
                  .uri(URI.create(String.format(url + "/SectraPathologyServer/api/requestslides?requestId=%s&hash=%s", URLEncoder.encode(value), URLEncoder.encode(hash))))
                  .setHeader("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
                  .GET()
                  .build();
                HttpResponse<String> responseSlides = client.send(requestSlides, HttpResponse.BodyHandlers.ofString());
                JSONObject joSlides = new JSONObject(responseSlides.body());
                for(Iterator iterSlides = joSlides.getJSONArray("slides").iterator(); iterSlides.hasNext(); ) {
                    JSONObject slideJson = (JSONObject)iterSlides.next();
                    if(slideJson.get("labSlideIdString").equals(slideId) && slideJson.getBoolean("hasImage")) {
                        System.out.println(String.format("%s\t%s\t%s\t%s\t%s\t%s",
                            slideJson.getString("requestIdString"),
                            slideJson.getString("labSlideIdString"),
                            slideJson.getString("staining"),
                            slideJson.getString("scanDateTime"),
                            slideJson.getBoolean("isViewable"),
                            slideJson.isNull("archived") ? null : slideJson.getString("archived")
                        ));
                        if(!slideJson.getBoolean("isViewable") && "offline".equals(slideJson.isNull("archived") ? null : slideJson.getString("archived"))) {
                            slideFound = true;
                            System.out.println("Initiating recall in 60 secondes...");
                            Thread.sleep(60000);
                            HttpRequest requestRecall = HttpRequest.newBuilder()
                              .uri(URI.create(url + "/SectraPathologyServer/api/requests/retrieve"))
                              .setHeader("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
                              .POST(HttpRequest.BodyPublishers.ofString(String.format("%s", case_.accNo)))
                              .build();
                            HttpResponse<String> responseRecall = client.send(requestRecall, HttpResponse.BodyHandlers.ofString());
                            System.out.println(String.format("...recall initiated (status code = %d).", responseRecall.statusCode()));
                            case_.recallInitiatedDate = new Date();
                            heartBxPatients.xmlMarshal("heart_bx");
                        }
                        else {
                            System.out.println("slide is not offline (recall not required)");
                        }
                    }
                }
            }

            if(!slideFound) {
                System.out.println(String.format("slide \"%s\" ERROR", slideId));
                logout(url, client);
                continue;
            }
            
            logout(url, client);
            
        }}}

    }

    public static void logout(String url, HttpClient client) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(url + "/uniview/Logout.ashx"))
          .setHeader("content-type", "application/x-www-form-urlencoded; charset=UTF-8")
          .POST(HttpRequest.BodyPublishers.ofString(String.format("0")))
          .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("logged out of server");
    }
    
}