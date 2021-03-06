package mypackage;
import javax.websocket.Session;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class User { // implements Runnable {
    private String name=null;
    private Session session; //Doubt we need the static keyword, I will doublecheck
    public boolean in_public_chat;
    private boolean ask;
    //String waiting;
    private TrailBlazer myTrail;
    private TrailBlazer baseTrail;
    private TrailBlazer myVerbTrail;
    List<Attribute> myAttributes=new ArrayList<Attribute>();
    List<String> attrChoice;
    private String chatName;
    boolean AI=false;
    private Park myPark;
    private String lastQuestion;
    private boolean lastNumeric;
    private List<String> lastOptions;
    private List<User> eligiblePlayers;
    public boolean gameover;
    private List<TrailBlazer> myPaths = new ArrayList<TrailBlazer>();
    private Map<String, Group> Groups = new ConcurrentHashMap<>();//static final?
    private Group currentGroup=new Group();


    int tempcount;
    User(String name, Session my_session, Park myPark)
    {
        Groups.put("default",currentGroup);
        gameover=false;
        tempcount=0;
        this.myPark=myPark;
        this.AI=false;
        this.name=name;
        this.chatName=name;
        ask=false;
        this.session=my_session;
        this.in_public_chat=true;
        String rootdir = SerializeJSON.getDir();
        myTrail=new TrailBlazer(rootdir,this,null,null,"trailhead.trail");
        baseTrail=myTrail;
        if (myTrail==null) SerializeJSON.addLog("Mytrail is null within User initilization");
        else SerializeJSON.addLog("Mytrail is not null within User INitilization");
        SerializeJSON.addLog("StartUser() being called in constructor");
        myTrail.startUser();
        //myTrail.waiting="";
    }
    
    public void callVerb(String target, String verb){
        myPark.executeVerb(target, verb, this.session);
    }

//myUser.broadcast(attrName, value,st,Message.CHAT);
//myUser.broadcast(st, Message.CHAT);

    public void broadcast (String attrName, String value, String message, Message type){
        myPark.broadcast(attrName, value, message, type);
    }

    public void broadcast (String message, Message type){
        myPark.broadcast(message,type);
    }

    public void setChatName(String myNewChatName) {
        this.chatName=myNewChatName;
    }

    public String getChatName() {
        return this.chatName;
    }

    public void Refresh() {
    	  SerializeJSON.addLog("Calling myTrail.Parse() from Refresh()");
        if (myTrail!=null) myTrail.Parse();
    }

    public boolean targetVerb(Verb myVerb) { //This always returns true for testing reasons
        return true; //This should check conditions to see if the player is eligible for verbage
    }

    public boolean callerVerb(Verb myVerb,List<User> eligiblePlayers){ //This will publish the verb to the player if eligible; it also returns a value to prevent hackers lol
        this.eligiblePlayers=eligiblePlayers;
        this.send_message(myVerb.getName(),myVerb.getName(),Message.VERB_UPDATE);
        this.eligiblePlayers=null; //free the memory, we should never need that again??
        return true;
    }

    public boolean executeVerb(Verb myVerb){ //This executes the verb....
            String rootdir = SerializeJSON.getDir();
        //TrailBlazer verbTrail=new TrailBlazer(
        return false;
    }
    
    public void sidePath(String input){
    	SerializeJSON.addLog("Entering sideTrail");
		if (myTrail!=null) myPaths.add(myTrail);
		myTrail.my_blazes=TrailBlazes.WAIT;
	 	myTrail = new TrailBlazer(SerializeJSON.getDir(),this,null,null,input);
	 	SerializeJSON.addLog("About to enter path: " + input);
	 	myTrail.setsidePath(true);
			SerializeJSON.addLog("startUser() being called in sidePath");
	 	myTrail.startUser();
    }
    
    
    public void returnFromPath(){ //Simply moves back one step
    	if (myPaths.size()!=0 && myTrail!=null && myTrail.my_blazes==TrailBlazes.INVALID) {
			SerializeJSON.addLog("Returning from a path");			
			myTrail=myPaths.get(myPaths.size()-1);
			myPaths.remove(myPaths.size()-1);
			if (myTrail.my_blazes==TrailBlazes.WAIT) myTrail.my_blazes=TrailBlazes.INVALID;
						SerializeJSON.addLog("startUser() being called in returnFromPath");
			myTrail.startUser();
    	}
    }
    
    public void gotoVerb(String input){
		SerializeJSON.addLog("Going to item from user: " + input);		
		try {myTrail.gotoTrail(input);
		}catch (Exception e){
			SerializeJSON.addLog("Failure in gotoVerb");
		}
			SerializeJSON.addLog("startUser() being called in gotoVerb");
			myTrail.startUser();
			SerializeJSON.addLog("Count of myPaths: " + myPaths.size());
    }
    
//TODO!  Add a goto function that can be used for verbs with that option rather than a *sidepath* option
//See if I gameover in a sidepath verb, does it cause any weird behavoir later    
    

    public boolean executeVerb (String instructions, boolean gotoType){ //This needs to be modified to support either sidepath or goto-like behavior
        synchronized (this){ //This may not allow for multiple verbs at once, but let's just get this working.  Can this even be called multiple times?
			//this.sidePath(instructions);
			if (gotoType==true) this.gotoVerb(instructions);
			else this.sidePath(instructions);
        }
        return true;
    }

    public void doneVerb(){
        if (this.myTrail==null) return; //The game is over if this happens!  It should be the only condition that can cause this
        this.myTrail=baseTrail;
        if (myTrail!= null)
        if  (myTrail.waiting != null) { //HOw can this be a null pointer error?  I access no properties of myTrail.waiting
            myTrail.sendLastQuestion();
        }
        SerializeJSON.addLog("Calling this.refresh() from doneverb");
        this.Refresh();
    }
    
    public void gameover(){
        gameover=true;
        this.in_public_chat=false;
        this.send_message("The game is over, and you have been disconnected.","Server",Message.CHAT);
        myTrail=null;
    }

    public void verbTrail(Verb verbReference) {
            String rootdir = SerializeJSON.getDir();
        //myVerbTrail=new(rootdir,this,null,verbReference);
    }

    public boolean input(String Type, String message) {

        if (myTrail==null) return false;
        if (Type.equals("CHAT"))
        {
            if (this.in_public_chat==true) return true;
            else return false;
        }

        if (ask) { //Type should equal "COMMAND" at this point; this "ask" statement must be removed in order for unprompted commands to happen
        		SerializeJSON.addLog("Ask is TRUE");
            if (myPark.uniqueAttributeAllowed(myTrail.waiting,message)) {
                if (attrChoice==null) {
                    try {
                        this.setAttribute("Numeric",myTrail.waiting,null,Integer.parseInt(message),0);
                        //this.send_message(" Numeric message sent: "+myTrail.waiting+":"+message,"Server",Message.CHAT);
                    } catch (Exception e) {
                        this.setAttribute("Text",myTrail.waiting,message,0,0);
                        //this.send_message(" Text message sent " + myTrail.waiting+":"+message,"Server",Message.CHAT);
                    }
                    myTrail.waiting=null;
                    ask=false;
                    //this.send_message("Thank you for setting the attribute","Server",Message.CHAT);
                    myTrail.my_blazes=TrailBlazes.CONTINUE;
                    return false;
                }
                //Check to see if their response matches on of our options!

                for (String key : attrChoice) {
                    //this.send_message("Attribute Choice: "+key,"Server",Message.CHAT);
                    if (key.equals(message)) {
                        try {
                            this.setAttribute("Numeric",myTrail.waiting,null,Integer.parseInt(message),0);

                        } catch (Exception e) {
                            this.setAttribute("Text",myTrail.waiting,message,0,0);
                        }
                        myTrail.waiting=null;
                        ask=false;
                        attrChoice=null;
                        //this.send_message("Thank you for setting the attribute","Server",Message.CHAT);
                        myTrail.my_blazes=TrailBlazes.CONTINUE;
                        SerializeJSON.addLog("MyBlazes is being set to continue!");
                        return false;
                    }
                }
                this.send_message("Answer not Listed","Server",Message.CHAT);
                this.askQuestion(myTrail.waiting,lastQuestion,lastNumeric,lastOptions);
                return false;
            } else {    
                this.send_message("It appears that someone else has already chosen that.  Can you try a different one?","Server",Message.CHAT);
                this.askQuestion(myTrail.waiting,lastQuestion,lastNumeric,lastOptions);
                return false;
            }
        }

        
        return false; //We shouldn't really get to this statement UNLESS someone sends a bad chat command "type" that we do not recognize
    }


    public boolean askQuestion(String attrName,String Question,boolean isNumeric,List<String> Options) {
        if (!AI) {
            //this.send_message("We are in the ask question subroutine","Server",Message.CHAT);
            SerializeJSON.addLog("Entering the Question");
            ask=true;
            SerializeJSON.addLog("ask=true");
            this.lastQuestion=Question;
            //SerializeJSON.addLog("this.lastQuestion=Question");
            this.lastNumeric=isNumeric;
            //SerializeJSON.addLog("this.lastNumeric=isNumeric;");
            this.lastOptions= Options;
            //SerializeJSON.addLog("this.lastOptions= Options;");
            if (attrName==null) SerializeJSON.addLog("The name is null");
            if (myTrail==null) SerializeJSON.addLog("Mytrail is null!");
            myTrail.waiting=attrName;
            //SerializeJSON.addLog("myTrail.waiting=attrName;");
            SerializeJSON.addLog("Question data has been stored.");
            if (Options==null) {
                this.send_message(Question,"Server",Message.COMMAND);
                return ask;
            }
            //If we reach this point in the code, we have several options to choose from.
            //For the poruporses of this initial test, we do not enumerate options to the user.  This
            //Should really be done with a "Print" Command, and collection of the data should be silent

            this.attrChoice=Options;
            SerializeJSON.addLog("Now sending message with commands....");
            this.send_message(Question,"Server",Message.COMMAND);
            return ask;
        }
        return false;
    }

    public boolean get_public_chat() {
        return in_public_chat;
    }

    public void removeAttribute(String Name){
        currentGroup.removeAttribute(Name);
    }

    public void removeAttribute(int count){
        currentGroup.removeAttribute(count);
    }
    

    public void setAttribute(String Type, String Name, String Data, int intData, float floatdata) {
        currentGroup.setAttribute(Type, Name, Data, intData, floatdata);
    }

    public void clearAttributes(){
        currentGroup.clearAttributes();
    }

    public void returnAttribute(int count){
        currentGroup.returnAttribute(count);
    }

    public boolean changeGroup(String Name){
        Group bufferGroup;
        bufferGroup=currentGroup;
        currentGroup=Groups.get(Name);
        if (currentGroup==null) {
            return false;
        }
        return true;
    }

    public void assignGroupCount(String attrName){
        this.setAttribute("Numeric",attrName,null,currentGroup.getCount(),0);
    }

    public boolean addGroup(String Name) {
        Group bufferGroup;
        bufferGroup=currentGroup;
        currentGroup=Groups.get(Name);
        if (currentGroup!=null) return false;
        Groups.put(Name,new Group());
        return true;
    }

    public boolean copyFromGroup(String groupName,String source, String dest){
        Group sourceGroup=Groups.get(groupName);
        if (sourceGroup==null) return false;
        Attribute sourceAttr =sourceGroup.returnAttribute(source);
        if (sourceAttr==null) return false;
        sourceAttr.setName(dest);
        this.setAttribute(sourceAttr);
        return true;
    }

    public boolean copyFromGroup(String groupName,Attribute source, String dest) {
        Group sourceGroup=Groups.get(groupName);
        if (sourceGroup==null) return false;
        Attribute sourceAttr =sourceGroup.returnAttribute(source.getName());
        if (sourceAttr==null) return false;
        sourceAttr.setName(dest);
        this.setAttribute(sourceAttr);
        return true;
    }

    public boolean copyFromGroup(String groupName,Attribute source, Attribute dest) {
        return false;
    }

     public boolean copyFromGroup(String groupName,String source, Attribute dest) {
        return false;
    }
    
    public Attribute returnAttribute(String Name) {
        return currentGroup.returnAttribute(Name);
    }

    public void setAttribute(Attribute newAttribute) {
        currentGroup.setAttribute(newAttribute);
    }
    public void send_message(String message, String sender, Message type) { //boolean public_message
        try {
            if (type==Message.RAW) { //raw message
                this.session.getBasicRemote().sendText(message);
            }

            if (type==Message.CHAT) {
                //String Dispatch=new String();
                //SerializeJSON.Serialize("chat",sender +": " + message);
                this.session.getBasicRemote().sendText(SerializeJSON.Serialize("chat",sender +": " + message));
                return;
            }

            if (type==Message.COMMAND) {
                //Question, "Server", Message.COMMAND
                if (this.attrChoice==null) this.session.getBasicRemote().sendText(SerializeJSON.Serialize("command",sender + ": " + message));
                else {
                    //this.send_message("We are starting to Serialize the JSON now... Item Count: +" + this.attrChoice.size(),"Server",Message.CHAT);
                    //String result=SerializeJSON.Serialize("commandlist",this.attrChoice);
                    //if (result==null)  this.send_message("Result was null!","Server commandlist: ",Message.CHAT); else this.send_message(result,"Server commandlist: ",Message.CHAT);
                    //System.out.println("Serialization Result: " + result);
                    //this.send_message("Should have sent the command list as a chat string","Server",Message.CHAT);
                    this.session.getBasicRemote().sendText(SerializeJSON.Serialize("commandlist",this.attrChoice));
                }
                return;
            }

            if (type==Message.HTML_IMAGE) {
                this.session.getBasicRemote().sendText(SerializeJSON.Serialize("htmlimage",message));
            }

            if (type==Message.VERB_UPDATE) {
                //eligiblePlayers.add(0,message);
                tempcount+=1;
                //String listy=new String();
                List<String> targetList=new ArrayList<String>();
//                targetList.add(0,message);
                targetList.add(message); //message must be the name of the verb
                //for (String nextUser : myPark.playersToNames()){
                //    listy+=nextUser;
                //    targetList.add(nextUser);
                //}
                //this.session.getBasicRemote().sendText(SerializeJSON.Serialize("chat","There has been an update from the verb processing.  Targets: " + listy));
                this.session.getBasicRemote().sendText(SerializeJSON.Serialize("verb",targetList));
            }
        } catch (Exception e) {
            try {this.send_message("EXCEPTION in send_message: " + e.toString(),"Server",Message.CHAT);}
            catch (Exception ee) {}
        }
    }

    public String get_name() {
        return this.name;
    }
}
