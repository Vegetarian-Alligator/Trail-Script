package mypackage;

import javax.websocket.Session;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

enum verbTarget {
    TARGET, //This action happens to the target
    SELF,   //This action happens to the caller
    SEARCH,  //This action requires a paramater-based search of surrounding entities
    IRR      //This action means that it doesn't matter who is the target
}



abstract class action {
    protected action nextItem;//Kind of like a linked list
    verbTarget receipient;
    protected User myCaller; //An action should never be operating on both the caller and the seller, however this is easy
    protected User myTarget; //Because it can simply choose the next

    action(verbTarget atarget) {//,myCaller,myTarget) {
      //action() {
        this.receipient=atarget;
        nextItem=null;
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
        if (this.receipient==verbTarget.TARGET) { //The path is to be followed by the user
            myTarget.executeVerb(this.fileName);
            return true;
        }

        if (this.receipient==verbTarget.SELF){
            myCaller.executeVerb(this.fileName);
        } 
        return false;
    }
}

class copyAttribute extends action {
    private String attributeName;
    private String copyName;
    copyAttribute(verbTarget atarget, String attributeName,String copyName){
        super(atarget);
        this.attributeName=attributeName;
        this.copyName=copyName;
    }

    protected boolean execute() {
        User destination=null; //Null only to avoid initilization errors
        User source=null;
        if (receipient==verbTarget.SELF){ //The caller is getting data from the USER
            destination=this.myCaller;
            source=this.myTarget;
        }

        if (receipient==verbTarget.TARGET) {//The target is getting data from the caller
            destination=this.myTarget;
            source=this.myCaller;
        }

        if (destination==null || source==null) return false;

        Attribute data=null;
        data=source.returnAttribute(this.attributeName);
        if (data==null) return false;
        try {data=(Attribute)data.clone();}catch(Exception e){SerializeJSON.addLog("cloning failure");return false;}//why is this cast required?
//          data=(Attribute)((Attribute)test).clone();  What might this ever do?
        data.setName(this.copyName);
        destination.setAttribute(data);
        return true;
    }
}

 


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
    //private action myAction;
    //private action currentAction;
    private List<action> myInstructions=new ArrayList<action>();
    private int listCount;
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
        //myAction=null;
        //currentAction=null;
        SerializeJSON.addLog("Verb has been completed");
        listCount=0;
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

    public void executefirst(User target) { //Just for testing
        myInstructions.get(0).setTarget(target);
        myInstructions.get(0).execute();
    }

    public void execute(User target, User caller) {
        SerializeJSON.addLog("A verb is executing: " + this.displayName);
        SerializeJSON.addLog("There are " + myInstructions.size() + "instructions in this verb");
        for (int i=0;i<myInstructions.size();i++){
            myInstructions.get(i).setTarget(target);
            myInstructions.get(i).setCaller(caller);
            myInstructions.get(i).execute();
        }
    }

    public void addCopyAttributetoTarget(String attributeName, String copyName) {
        addCopyAttribute(verbTarget.TARGET,attributeName,copyName);
    }

    public void addCopyAttributetoCaller(String attributeName, String copyName) {
        addCopyAttribute(verbTarget.SELF,attributeName,copyName);
    }

    private void addCopyAttribute(verbTarget mySource, String attributeName, String copyName){
        myInstructions.add(new copyAttribute(mySource,attributeName, copyName));
    }

    public void addTargetPath(String path){
          myInstructions.add(new Path(verbTarget.TARGET,path));
    }


    public void addAction(action nextAction) {
        
        listCount+=1;
    }
    public void addCallerPath(String path) {
        myInstructions.add(new Path(verbTarget.SELF,path));
    }

    //createList assembles the list of verbs that a user may go through
    //It also determines which users it may apply them to as well
    public void createList(User user, Map<String, User> Users) {

    }
}
