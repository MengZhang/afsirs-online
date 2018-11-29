package org.afsirs.web.util;

import com.sendgrid.*;
import java.io.IOException;

/**
 *
 * @author Meng Zhang
 */
public class EmailUtil {

    private static final SendGrid SEND_GRID_SERVICE = new SendGrid(System.getenv("SENDGRID_API_KEY"));
    private static final Email AFSIRS_SERVICE_FROM = new Email("no-reply@afsirs-online-test.herokuapp.com", "AFSIRS-Online");
    private static final Email AFSIRS_SERVICE_TO = new Email("meng.zhang15@ufl.edu", "Meng Zhang");
    private static final String SUBJECT_PW_REQ = "AFSIRS-online account password recover request";
    private static final String SUBJECT_REG_CFM = "AFSIRS-online account registration confirmation";
    
    public static void sendPWReqMail() {
        
    }
    
    public static void sendRegiCfmMail() {
        
    }
    
    private static Response sendEmail(Mail mail) throws IOException {
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        Response response = SEND_GRID_SERVICE.api(request);
        StringBuilder sb = new StringBuilder();
        sb.append(response.getStatusCode());
        return response;
    }
    
    public static Mail createMail(Email from, Email to, String subject, Content content) {
        return new Mail(from, subject, to, content);
    }
    
    public static String sendEmail() throws Exception {
        Content content = new Content("text/plain", "Hello, Email!");
        
        try {
            Response response = sendEmail(createMail(
                    AFSIRS_SERVICE_FROM,
                    AFSIRS_SERVICE_TO,
                    SUBJECT_PW_REQ,
                    content));
            StringBuilder sb = new StringBuilder();
            sb.append(response.getStatusCode());
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
            return sb.toString();
        } catch (IOException ex) {
            throw ex;
        }
    }
}
