package mypackage;
import java.util.*;
import java.io.*;

public class SanatizeHTML {
    public static String SanatizedHTML(String Input){
    return Input.replaceAll("<(.+?)</(.+?)>", "Sorry, an HTML whitelist may be implemented in future editions.");
    }
}
