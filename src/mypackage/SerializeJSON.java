package mypackage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;
import java.nio.*;
import java.nio.file.*; //Used for "Path" variable;Also: StandardOpenOption //Also: Files
import java.nio.charset.*; //for the StandardCharsets.UTF_8 options
import java.util.logging.*;//*Logger

public class SerializeJSON {
public static java.nio.file.Path logger=null;
public static List<String> lines = Arrays.asList("The first line", "The second line");
public static Logger LOGGER = Logger.getLogger(SerializeJSON.class.getName());

public static String getDir(){
    //return  "/var/lib/tomcat9/webapps/myapp-0.1-dev/multiplayertest/";
    return "/var/lib/tomcat9/webapps/myapp-0.1-dev/sidetest/";
}

public static void initilizeLogger(){
        LOGGER.info("Logger Name: "+LOGGER.getName());
}

public static void addLog(String info) {
    LOGGER.severe("SerializeJSON logging:" + info);
}

public static void log(String input) {
    try {Files.write(logger, Arrays.asList(input), StandardCharsets.UTF_8, StandardOpenOption.APPEND);}catch(Exception e){}
}

public static String Serialize(String type, String data){
    return "{\"type\":\"" + type + "\",\"data\":\"" + data + "\"}";
}

public static String Serialize(String type, List<String> data) throws RuntimeException {
//myObj = {
//  "name":"John",
//  "age":30,
//  "cars":[ "Ford", "BMW", "Fiat" ]
//};



    String output;
    output="{\"type\":\"" + type + "\",\"datalist\":[\"";
    boolean first=true;
    for (String key : data) {
            if (first) output+=key+"\"";
            else output+=", \"" + key + "\"";
            first=false;
    }
    output+="]}";
    SerializeJSON.addLog("The data being sent to the user is: " + output);
    return output;
}

public static List<String> deserializeCommand(String Command) {
        Pattern findCommand;
        findCommand=Pattern.compile("(?<=\").*?(?=\")");
        Matcher extractCommand = findCommand.matcher(Command);
        int x =0;
        String Type=""; // This equals avoids initilization errors, ergo "" means no match was found
        String Data="";
        while (extractCommand.find()){
            if ((x)==2) Type = Command.substring(extractCommand.start(),extractCommand.end());
            if ((x)==6) Data = Command.substring(extractCommand.start(),extractCommand.end());
            x+=1;
        }

        List<String> formattedCommand = new ArrayList<String>();
        formattedCommand.add(Type);
        formattedCommand.add(Data);
        
        return formattedCommand;
        //2: The typestring
        //4: The message
}

//TO-DO: Add serialize array, to reduce the number of messages being sent
};
