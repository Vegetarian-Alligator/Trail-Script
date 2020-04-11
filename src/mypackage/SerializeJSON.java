package mypackage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;

public class SerializeJSON {
    
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
