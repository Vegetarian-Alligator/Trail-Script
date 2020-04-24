package mypackage;

import java.util.*;
import java.util.concurrent.*; 
import javax.websocket.Session;
import java.io.*;
//3D map should be an extension class, if one is ever implemented

enum mapType {
    TEXT,
    GRID
}

public class TrailMap{
    mapType myType; //The type of map that this is supposed to be.  By default, a lack of a map file will 
    public final Map<String, User> Users = new ConcurrentHashMap<>();
    private String filePath;
    TrailMap (int size, String filePath) {
        if (size<=0) myType=mapType.TEXT; //So, if the word has no dimensions: make it text.  Easy.
        else myType=mapType.GRID;
        this.filePath=filePath;
    }

    public User findUser(String name){
        return Users.get(name); // Whoever calls this should be sure to check "NULL" value for... yup.. he ain't there.
    }

    public boolean addNewUser(String name, Session session) {
        if (this.findUser(name)==null) {        
            //Users.put(name, new User(name,session));
            return true;
        }
        else return false;
    }

    

    public void removeUser(String name) {
        Users.remove(name);
        
    }
/*
    public void package_message(){
            if (this_user.in_public_chat==true) for (String key : Users.keySet()) {  
                  Users.get(key).send_message(message,this_user.get_name(),Message.CHAT);  
            } 
    }
*/
    /*
    private void send_message(String message, String sender, Message type) throws IOException { //boolean public_message

    try {        
        if (type==Message.RAW){ //raw message
            this.session.getBasicRemote().sendText(message);
        }
         
        if (type==Message.CHAT) {
            String Dispatch=new String();
            //SerializeJSON.Serialize("chat",sender +": " + message);           
            this.session.getBasicRemote().sendText(SerializeJSON.Serialize("chat",sender +": " + message));
            return;
  	
        }
    }catch (IOException e) {
        
    }
    }
    */
/*
    public String get_name(){
        return this.name;
    }
*/

}
