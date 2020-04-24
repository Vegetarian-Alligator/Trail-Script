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
//Everything here should be private since this class is essentially the overlord of any given game: nothing external should be manipulating it
      private static final Map<String, User> Users = new ConcurrentHashMap<>();
      private List<String> uniqueAttributes = new ArrayList<String>();
      private String myName;
      private TrailBlazer myBlazes;
    

      Park(String myName) { //"Park" creates the universe where any given game exists in.  I imagine each game should have only one "park" class
        this.myName=myName;
        String rootdir = "/var/lib/tomcat9/webapps/myapp-0.1-dev/";
        this.myBlazes=new TrailBlazer(rootdir,null,this);
        //uniqueAttributes.add("name"); //currently hard coding "name" as a unique attribute - much easier of testing, also possibly a good idea
      }


      private int findUniqueAttribute(String name) { //Remember 0 evaluates to false
        int index=0;
        for (String list : uniqueAttributes) {
            if (name.toLowerCase().equals(list.toLowerCase()))return index;
            index++;
        }
        return -1;
      }

      public void setUniqueAttribute(String name) { //This function simply takes the name of the attribute to me made unique, and checks to make sure it is not already there\   
        if (findUniqueAttribute(name)!=-1) return;
        uniqueAttributes.add(name);
      }

      public void unsetUniqueAttribute(String name) {
        int remove=findUniqueAttribute(name);
        if (remove==-1) return;
        uniqueAttributes.remove(remove);
      }

      public void addUser(String uniqueID, boolean human, Session session) { //Currently does not check to see if we are human or not.  We accept the parameter though
        User a_user=new User("human",session,this);        
        synchronized (Users) { Users.put(uniqueID, a_user);}
      }

      public void removeUser(String uniqueID) {
        synchronized (Users) {Users.remove(uniqueID);}
      }

      private boolean subsearch(String name,String input) {
        /*If, however, the thing is in the list of unique attributes we have to check every player
        on the map for first having this attribute, then seeing what it is*/

//Get the iterator framework off of geeksforgeeks, I don't understand the type casting here
        //Iterator myIterator=Users.entrySet().iterator();
        //for (User checkUser : Users.keySet()) {
        //while (myIterator.hasNext()) {
        for (User value : Users.values()) {
            Attribute Compare= value.returnAttribute(name);//(User)element.getValue().returnAttribute(name);//checkUser.returnAttribute(name);
            if (Compare != null) {
                if (Compare.getType().equals("Text")) {
                    if (Compare.getData().toLowerCase().equals(input.toLowerCase())) return false;
                }else {
                    if (Integer.toString(Compare.getintData()).equals(input)) return false;
                }
            }
        }
        return true;
      }

      public boolean uniqueAttributeAllowed(String name, String input) {
        if (findUniqueAttribute(name)!=-1) return subsearch(name,input);
        return true;
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
