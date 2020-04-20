/*
This class IS the world.  It will contain all general setting (mutiplayer) and a list of players, as well as be the repository for all global variables.
This will be implemented after trail does a little bit of parsing, probably.
*/
package mypackage;
import javax.websocket.Session;
import java.util.*;
import java.util.concurrent.*; 
import javax.websocket.Session;

public class Park {
//    public static final Map<String, User> Users = new ConcurrentHashMap<>();
      public static final Map<String, User> Users = new ConcurrentHashMap<>();
      
      String myName;
      //User a_user=new User("human",session);
      Park(String myName) { //"Park" creates the universe where any given game exists in.  I imagine each game should have only one "park" class
        this.myName=myName;
      }

      public void addUser(String uniqueID, boolean human, Session session) {
        User a_user=new User("human",session);        
        synchronized (Users) { Users.put(uniqueID, a_user);}
      }

      public void removeUser(String uniqueID) {
        synchronized (Users) {Users.remove(uniqueID);}
      }

      public void entityInteract(String rawInput, Session session) {
        if (session==null) return; //This means it is a non-player user!
        //This is not yet supported
        List<String> Input = new ArrayList<String>();
        Input=SerializeJSON.deserializeCommand(rawInput);
        String message;
        User this_user;
        this_user = Users.get(session.getId());         
        synchronized (Users) {                
            if (this_user.input(Input.get(0),Input.get(1)))
                for (String key : Users.keySet()) { 
                    Users.get(key).send_message(Input.get(1),this_user.getChatName(),Message.CHAT); //Was message
                }
        }
        this_user.Refresh();
      }
}
