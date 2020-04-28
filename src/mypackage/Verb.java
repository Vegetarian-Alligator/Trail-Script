package mypackage;

import javax.websocket.Session;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

enum verbTarget {
    TARGET, //This action happens to the target
    SELF,   //This action happens to the caller
    SEARCH  //This action requires a paramater-based search of surrounding entities
}



abstract class action {
    public action nextItem;//Kind of like a linked list
    verbTarget receipient;
    private User myCaller; //An action should never be operating on both the caller and the seller, however this is easy
    private User myTarget; //Because it can simply choose the next

    action(verbTarget atarget) {//,myCaller,myTarget) {
        this.receipient=atarget;
        //this.myCaller=myCaller;
        //this.myTarget=myTarget;
    }

    public void setCaller(User aUser) {
        this.myCaller=aUser;
    }
    
    public void setTarget(User aUser) {
        this.myTarget=aUser;
    }

    public boolean start() { //returns true for all successful executions down the chain
        if (this.myCaller != null && this.myTarget!=null){ //MAKE SURE NULL IS WHAT HAPPENS WHEN REMOVED FROM CHAIN
                return this.execute(); 
        }else return false;
    }

    protected abstract boolean execute(); //This is where each supported verb action will work

    private boolean gotoNextAction() { //use extreme caution: the user could have logged off
        if (nextItem!=null) {
            nextItem.setCaller(this.myCaller);
            nextItem.setTarget(this.myTarget);
            return nextItem.start();
        }
        return false;
    }
};

class Path extends action {
    String fileName;
    Path(verbTarget atarget, String fileName) {
        super(atarget);
        this.fileName=fileName;
    }
    protected boolean execute(){
        return false;
    }
}

 

/*
class simpleMath extends action {
    private String Operation;
    simpleMath(String Action, String firstAttr, String secondAttr, boolean firstAttrTarget, boolean secondAttrTarget,String Operation) {
        super(Action, firstAttr, secondAttr, firstAttrTarget,secondAttrTarget);
        if (Operation.equals("*") || Operation.equals("/") || Operation.equals("+") || Operation.equals("-"))
            this.Operation=Operation;//else throw new Exception("Invalid Operation!");
    }
}

class print extends action {
    String Message;
    print(String Action, String firstAttr, String secondAttr, boolean firstAttrTarget, boolean secondAttrTarget,String Message) {
        super(Action, firstAttr, secondAttr, firstAttrTarget,secondAttrTarget);
        this.Message=Message;
    }
}
*/

class verbAttribute {
    private String Name;
    boolean numeric;
    int data;
    String Data;
};



//This will store attributes that are used inside of the
//verb file First, however, the active user will be checked
//For attributes of the same name.

//It might be nice to create a convetion, because this could possibly
//Cause a nasty error

//GUI NOTE: When implementing the GUI, check for this error!

public class Verb {
    private String displayName; //This is how the verb will be displayed to the player
    private List<Attribute> callerRequirements; //Things that must be true of the caller to enact the verb
    private List<Attribute> targetRequirements; //Things that must be true of the target to enact the verb
    private List<Attribute> callerNegation; //Must not be true of the caller
    private List<Attribute> targetNegation; //Must not be true of the target
    private boolean self=false;
    private action myAction;
    private action currentAction;
    Map<String, verbAttribute> memory;
    TrailBlazer myBlazer;
    verbTarget myTarget;
    //All actions types will be either SIMPLEMATH or a COMARISON or a DISPLAY
    //Each type of action should have it's own class

    Verb () {
        SerializeJSON.addLog("We are starting up a verb...");
        callerRequirements=new ArrayList<Attribute>();
        targetRequirements=new ArrayList<Attribute>();
        callerNegation=new ArrayList<Attribute>();
        callerNegation=new ArrayList<Attribute>();
        myTarget=verbTarget.TARGET;
        String rootdir = "/var/lib/tomcat9/webapps/myapp-0.1-dev/"; //This needs to be a parameter, but it does not matter for now
        myAction=null;
        currentAction=null;
        SerializeJSON.addLog("Verb has been completed");
//        myBlazer=new TrailBlazer(rootdir,null,null,this);
    }

    private List<String> targetAction;
    
    public String getName() {
        return displayName; //Need to examine if this is a copy or a reference since this is a private variabel
    }

    public void setSelf(boolean self) { //This could really have been a public variable, but perhaps that will change
        this.self=self;
    }

    public void setDisplayName(String name) {
        this.displayName=name;
    }

    public void addRequirement(String name, String value, int intValue){
        //We can use just one list and make it "point", right?
        if (myTarget==verbTarget.TARGET) {
            
        }
    }

    private action getLastAction() {
        if (myAction==null) return myAction;
        SerializeJSON.addLog("Returning something other than myAction");
        return currentAction.nextItem;
           
    }

    public void addTargetPath(String path){
        SerializeJSON.addLog("adding a target path");
        action targetAction=getLastAction();
        targetAction=new Path(verbTarget.TARGET,path);
        currentAction=targetAction;
    }
    
    public void addCallerPath(String path) {
                SerializeJSON.addLog("adding a caller path");
        action targetAction=getLastAction();
        targetAction=new Path(verbTarget.SELF,path);
        currentAction=targetAction;
    }

    //createList assembles the list of verbs that a user may go through
    //It also determines which users it may apply them to as well
    public void createList(User user, Map<String, User> Users) {

    }

    public void execute(User myTarget, User myCaller) {
        
    }
}
