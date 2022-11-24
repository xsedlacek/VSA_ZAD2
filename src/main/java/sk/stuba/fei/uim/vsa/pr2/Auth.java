package sk.stuba.fei.uim.vsa.pr2;

import sk.stuba.fei.uim.vsa.pr2.services.CustomerService;

import java.util.Base64;

public class Auth {

    static CustomerService cs = new CustomerService();

    public static String getEmail(String authHeader){
        String base64Encoded = authHeader.substring("Basic ".length());
        String decoded = new String(Base64.getDecoder().decode(base64Encoded));
        return decoded.split(":")[0];
    }

    public static String getPassword(String authHeader){
        String base64Encoded = authHeader.substring("Basic ".length());
        String decoded = new String(Base64.getDecoder().decode(base64Encoded));
        return decoded.split(":")[1];
    }

    public static boolean auth(String header){
        return !(cs.getUser(getEmail(header)).getId() == Long.parseLong(getPassword(header)));
    }

}
