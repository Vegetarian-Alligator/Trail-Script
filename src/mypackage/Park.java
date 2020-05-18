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
    private Map<String, User> Users = new ConcurrentHashMap<>();//static final?
    private Map<Verb, List<User>> capableUsers;
    private List<String> uniqueAttributes = new ArrayList<String>();
    private String myName;
    private TrailBlazer myBlazes;
    private TrailBlazer readVerbs;
    private List<Verb> myVerbs=new ArrayList<Verb>();
    private List<User> eligiblePlayers=new ArrayList<User>(); //Not sure I need to declare this

    Park(String myName) { //"Park" creates the universe where any given game exists in.  I imagine each game should have only one "park" class
        SerializeJSON.addLog("LoadWorld");
        this.myName=myName;
        String rootdir = "/var/lib/tomcat9/webapps/myapp-0.1-dev/";
        SerializeJSON.initilizeLogger();
        SerializeJSON.addLog("This should be in the log for sure.");
        this.myBlazes=new TrailBlazer(rootdir,null,this,null,null); //This initilizes the world
        new Verb();
        try {
            //myVerbs.add(new Verb()); //Hacky placeholder, but let's debug this thing..
        } catch (Exception e) {
            SerializeJSON.addLog("This is what was making .add puke: " + e.toString());
        }
        Verb booleanVerb=new Verb();
        readVerbs=new TrailBlazer(rootdir,null,null,myVerbs,null);
        myVerbs=readVerbs.returnVerbs();
        SerializeJSON.addLog("Size of myVerbs: "+myVerbs.size());
        //myVerbs.add(booleanVerb); ///And we can only handle one verb so far.
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

        for (User value : Users.values()) {
            Attribute Compare= value.returnAttribute(name);//(User)element.getValue().returnAttribute(name);//checkUser.returnAttribute(name);
            if (Compare != null) {
                if (Compare.getType().equals("Text")) {
                    if (Compare.getData().toLowerCase().equals(input.toLowerCase())) return false;
                } else {
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
            this_user.Refresh(); //If you don't do this, more than one thread might be calling readNextCommand simultanously
            //So with this system, we can really only read one of them at a time
        }

        List<Boolean> affirmations=new ArrayList<Boolean>();
        synchronized (Users) {
            for (Verb myVerb : myVerbs) {
                for (String key : Users.keySet()) { //This first loop figures out all players that are eligible
                    affirmations.add(new Boolean(Users.get(key).targetVerb(myVerb)));
                }
                eligiblePlayers.clear(); //Or the list from the last verb.. will still be there
                int listCount;
                listCount=0; //Must be seperate because we are in a loop, or it do horrible things
                for (String key : Users.keySet()) { //This one mostly just turns it into a list; could be integrated
                    if (affirmations.get(listCount).booleanValue()) {
                        eligiblePlayers.add(Users.get(key));
                    }
                    listCount+=1;
                }
                //The Following loop CANNOT be integrated into the first two because it needs a complete list of player targets

                for (String key : Users.keySet()) { //Publish the list of players to all users (presumably associated with compatible verbs)
                    Users.get(key).callerVerb(myVerb,eligiblePlayers);
                }
            }


            //TESTING ONLY!
            //Now is time for the
            SerializeJSON.addLog("Starting to look at verbs again");
            SerializeJSON.addLog("variance");
            if (Input.get(1).contains("!")) {

                String target= Input.get(1).substring(0,Input.get(1).indexOf("!"));
                String verbName = Input.get(1).substring(Input.get(1).indexOf("!")+1);
                SerializeJSON.addLog("Name: " + target + " Verb: " + verbName);
                User targetUser=null;
                User callingUser;
                Verb verbVerb=null;

                for (String key : Users.keySet()) { //This one mostly just turns it into a list; could be integrated

                    Attribute myAttribute=null;
                    if (Users.get(key)!=null) myAttribute=Users.get(key).returnAttribute("name");
                    else SerializeJSON.addLog("null Users.get(key) where key = " + key);
                    //if (Users.get(key).returnAttribute("name").getData().equals(target)) {
                    if (myAttribute!=null)
                       if (myAttribute.getData() != null) SerializeJSON.addLog("User: "+myAttribute.getData());
                        if (myAttribute.getData().equals(target)) {
                            targetUser=Users.get(key);
                        }
                }
//    public void send_message(String message, String sender, Message type) { //boolean public_message
                SerializeJSON.addLog("assessing target user");
                if (targetUser!=null) {
                    SerializeJSON.addLog("Target User is not Null");
//                    Users.get(session.getId()).send_message("targetUser is not NULL; name of verb: "+myVerbs.get(0).getName(),"Server", Message.CHAT);

                    for (Verb vKey : myVerbs) { //Now check to make sure that the requested verb.. actually is a verb
                        SerializeJSON.addLog("Name of verb: " + vKey.getName());
                        if (vKey.getName().equals(verbName)) {
                            verbVerb=vKey;
                        }
                    }
                }

                if (verbVerb != null) { //Its time to go execute this verb!
                    SerializeJSON.addLog("assessing target user");
                    verbVerb.execute(targetUser,Users.get(session.getId()));
                } else SerializeJSON.addLog("No verb found");

            }
        }
    }

    public List<String> playersToNames() {
        List<String> result=new ArrayList<String>();
        for (User thisUser : eligiblePlayers) { //Weakness: the name attribute is hardcoded inside of here.  This should be in the park file, really/
            result.add(thisUser.returnAttribute("name").getData()); //We need to to check for null attributes, or this is a bug!!!
        }
        return result;
    }

}

