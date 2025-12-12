package edu.emory.eai4hi.extracthtbx.finder;

import edu.emory.eai4hi.extracthtbx.data.Case;
import edu.emory.eai4hi.extracthtbx.data.Patient;
import edu.emory.eai4hi.extracthtbx.data.Slide;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author geoffrey.smith@emory.edu
 */
public class PatientCaseSlideFinder {

    // MD5 hash salt for identifiers - change before running for production purposes
    private final static String HASH_SALT = "981DE517FC8E2AA95E3570BD3C54CFAA";
    
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
    public static Map<String, Patient> getPatientMap() throws ClassNotFoundException, ParseException, SQLException {

        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        // integrated security requires sqljdbc_auth.dll on -Djava.library.path
        Connection connClr = DriverManager.getConnection("jdbc:sqlserver://prd-clar-lsnr.eushc.org;database=Clarity;integratedSecurity=true;");

        PreparedStatement pstmt = connClr.prepareStatement("""
            select top 30
              lcdm.case_id [caseId],
              lcdm.case_pat_id [patId],
              convert(varchar(32), hashbytes('MD5', ? + lcdm.case_pat_id), 2) [patIdHash],
              zcf.abbr [subspecialty],
              odm.container_name [slideBarCode],
              lci.case_num [accNo],
              convert(varchar(32), hashbytes('MD5', ? + lci.case_num), 2) [accNoHash],
              substring(sdm.spec_number_ln1, len(lci.case_num) + 2, 10) [partId],
              cast(replace(substring(odm.container_name, len(sdm.spec_number_ln1) + 1, 2), '-', '') as int) [blockNo],
              cast(substring(substring(odm.container_name, len(sdm.spec_number_ln1) + 1, 10), charindex('-', substring(odm.container_name, len(sdm.spec_number_ln1) + 1, 10)) + 1, 10) as int) [slideNo],
              zt.name [stain],
              format(lcdm.case_coll_dttm, 'yyyy-MM-dd') [collectionDt],
              format(lcdm.date_entr_dt, 'yyyy-MM-dd') [orderDt]
            from
              lab_case_db_main lcdm
              join lab_case_info lci on(lcdm.case_id = lci.requisition_id)
              join spec_db_main sdm on(lcdm.case_id = sdm.case_id)
              join spec_task_list stl on(sdm.specimen_id = stl.specimen_id)
              join spec_task_list_sub stls on(sdm.specimen_id = stls.specimen_id and stl.line = stls.group_line)
              join ovc_db_main odm on(stls.task_linked_sctr_id = odm.container_id)
              join zc_task zt on(stl.task_c = zt.task_c)
              left outer join
              (
                lab_case_flags lcf
                join zc_case_flags zcf on(lcf.case_flags_c = zcf.case_flags_c and lcf.ap_case_flag_grp_c = '1')
              ) on(lcdm.case_id = lcf.requisition_id)
            where
              stl.task_deleted_yn = 'N'
              and stl.task_inst is not null
              and stl.task_action_c = 3
              and zcf.abbr = 'DP-Ht Bx'
              and lcdm.case_coll_dttm < '2025-12-01'
            order by
              lcdm.case_pat_id,
              lcdm.case_id,
              8, 9, 10""");
        
        pstmt.setString(1, HASH_SALT);
        pstmt.setString(2, HASH_SALT);
        
        ResultSet rs = pstmt.executeQuery();
        Map<String, Patient> patientMap = new TreeMap<>();
        while(rs.next()) {
            Patient patient = patientMap.get(rs.getString("patId"));
            if(patient == null) {
                patient = new Patient(rs.getString("patId"), rs.getString("patIdHash"));
                patientMap.put(rs.getString("patId"), patient);
            }
            Case case_ = patient.caseMap.get(rs.getString("accNo"));
            if(case_ == null) {
                case_ = new Case(rs.getString("accNo"), rs.getString("accNoHash"), sdf.parse(rs.getString("collectionDt")));
                patient.caseMap.put(rs.getString("accNo"), case_);
                patient.cases.add(case_);
            }
            String slideId = String.format("%s%d-%d", rs.getString("partId"), rs.getInt("blockNo"), rs.getInt("slideNo"));
            Slide slide = new Slide(rs.getString("partId"), rs.getInt("blockNo"), rs.getInt("slideNo"), rs.getString("stain"));
            case_.slideMap.put(slideId, slide);
            case_.slides.add(slide);
        }
        
        connClr.close();
        
        return patientMap;
        
    }
 
}