

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * 
 * @author geoffrey.smith@emory.edu
 */
public class TestConnection {

    public static String getServerInfo(String jdbcUrl) throws ClassNotFoundException, ParseException, SQLException {

        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        // integrated security requires sqljdbc_auth.dll on -Djava.library.path
        //Connection connClr = DriverManager.getConnection("jdbc:sqlserver://prd-clar-lsnr.eushc.org;database=Clarity;integratedSecurity=true;");
        Connection conn = DriverManager.getConnection(jdbcUrl);

        PreparedStatement pstmt = conn.prepareStatement("""
            select db_name() dbname, @@servername servername, @@version serverversion
        """);
        
        ResultSet rs = pstmt.executeQuery();
        String serverInfo = null;
        while(rs.next()) {
             serverInfo = String.format("db_name = %s; server = %s; version = %s", rs.getString("dbname"), rs.getString("servername"), rs.getString("serverversion"));
        }
        
        conn.close();
        
        return serverInfo;
        
    }
    
    public static void main(String[] args) throws ClassNotFoundException, ParseException, ParseException, SQLException {
        System.out.println();
        System.out.println(getServerInfo(args[0]));
    }
 
}