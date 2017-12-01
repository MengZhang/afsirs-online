package org.afsirs.web.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.afsirs.web.dao.bean.User;
import org.afsirs.web.util.DBUtil;

public class UserDAO {

    private static final ConcurrentHashMap<String, User> users = syncUserRecords();
    
    public static ConcurrentHashMap<String, User> syncUserRecords() {
        ConcurrentHashMap<String, User> ret = new ConcurrentHashMap();
        return ret;
    }

    public static User getUserByUsername(String userName) {
        if (users.containsKey(userName)) {
            return users.get(userName);
        } else {

            try {
                Client client = ClientBuilder.newClient();
                WebTarget service = client.target(DBUtil.getDBBaseURI());
                Response response = service.path("user").path("find").path(userName).request().get();

                User ret = null;
                if (response.getStatus() == 200) {
                    String resJson = response.readEntity(String.class);
                    if (!resJson.trim().equals("")) {
                        ObjectMapper mapper = new ObjectMapper();
                        ret = mapper.readValue(resJson, User.class);
                        users.put(userName, ret);
                    }
                }
                client.close();
                return ret;

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }

    public static boolean registerUser(User user) {
        boolean ret = false;
        try {
            Client client = ClientBuilder.newClient();
            WebTarget service = client.target(DBUtil.getDBBaseURI());
            WebTarget regService = service.path("user").path("register");
            for (String var : user.keySet()) {
                regService = regService.queryParam(var, user.get(var));
            }
            Response response = regService.request(MediaType.TEXT_PLAIN_TYPE).get();
            if (response.getStatus() == 200) {
                ret = true;
            }
            client.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    public static Collection<User> getAllUserNames() {
        return users.values();
    }
}
