package mypackage;
import java.io.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import java.util.*;

//public boolean askQuestion(String attrName,String Question,boolean isNumeric,List<String> Options) {

class storeQuestion{ //Okay this class is basically like a "struct" to hold information about the question
    public String attrName;
    public String Question;
    public boolean isNumeric;
    public List<String> Options;

    storeQuestion(String attrName,String Question,boolean isNumeric,List<String> Options){
        this.attrName=attrName;
        this.Question=Question;
        this.isNumeric=isNumeric;
        this.Options=Options;
    }
}

public class TrailBlazer {
    public TrailBlazes my_blazes; //The status of this trail
    protected String Command;
    private String filePath;
    private String currentFile;
    private BufferedReader br;
    public boolean refresh=true;
    storeQuestion myLastQuestion;
    User myUser;
    Park myPark;
    Verb myVerb;
    boolean sidePath;    
    
    public String waiting;
    private List<Verb> myVerbs = new ArrayList<Verb>();

    public List<Verb> returnVerbs(){
        return myVerbs;
    }
    
    TrailBlazer (String startPath, User calling_user, Park calling_park, List<Verb> calling_verb,String instructions) { //Honestly, verb may as well have been a boolean at this point
		  sidePath=false;        
        waiting=new String("test");
        SerializeJSON.addLog("Blazing a trail!");
        myUser=calling_user;
        //We COULD read the verb every time, but I choose to keep the verb in memory
        //so the game understands when it is appropriate to call it
        if (calling_verb != null && calling_user==null) { 
            try {
                SerializeJSON.addLog("We should be trying to read verblist.trail");
                //this.myVerb=calling_verb;
                //myVerb=new Verb();
                this.br = this.setReader(startPath,"verblist.trail");
                this.filePath=startPath;
                my_blazes=TrailBlazes.CONTINUE;
                this.parseVerb();
                calling_verb=myVerbs;
                SerializeJSON.addLog("Count in calling_verb: " + calling_verb.size());
                return;
            }catch (Exception e) {
                SerializeJSON.addLog("Something went terribly wrong trying to start reading the file."+ e.toString());
            }
        }


        if (calling_park !=null) { //It is important this if statement is first: just in case we want to allow the world (park) itself to play someday, it is set up before playing
            try {
                SerializeJSON.addLog("Loading the world");
                this.myPark=calling_park;
                this.br = this.setReader(startPath,"gameworld.trail");
                my_blazes=TrailBlazes.CONTINUE;
                this.filePath=startPath;
                this.parsePark();
                return;
            } catch (Exception e) {
                SerializeJSON.addLog("CANNOT LOAD THE WORLD!"+e.toString());
                //We don't handle this very well - we should probably log failures of communication, at least
            }
        }

        if (calling_verb != null && calling_user != null) {
            //I'm not actually sure we need any changes just yet
        }

        if (calling_user != null) {
            try {
                //this.br = new BufferedReader(new FileReader(startPath+"trailhead.trail"));
                //this.br = this.setReader(startPath,"trailhead.trail");
                this.br=this.setReader(startPath,instructions);
                this.filePath=startPath;
                this.currentFile=instructions;
                my_blazes=TrailBlazes.INVALID;
                //this.Parse();
            } catch (IOException e) { //NoSuchElementException
                //myUser.send_message("IO Exception has occured "+ e.toString(),"Server",Message.CHAT);
                SerializeJSON.addLog("Error occured trying to create a user: " +e.toString());
            }
            //while ((st = br.readLine()) != null)
        }

    }

    public void startUser() { //This is the appropriate place to add code which will withdraw previous questions!
    	 //this.Parse() by itself will cause errors with goto because then there is a recursive execution of the parsing function.  However, we DO need to call parse again for verbs
        if (my_blazes==TrailBlazes.INVALID) {
				my_blazes=TrailBlazes.CONTINUE;
	        	this.Parse();
        	}else SerializeJSON.addLog("Rejecting start: " + my_blazes);
    }

    private void parseVerb() throws Exception {
        SerializeJSON.addLog("We are starting to parse the verb.");
        boolean success=false; //Might not have been initilized?  Is this the try....catch?
        String st=br.readLine();
        while  (st!=null) {
            myVerb = new Verb();            
            BufferedReader myFile;
            myFile= this.setReader(this.filePath,st);
            if (myFile==null) SerializeJSON.addLog("File not found!");else SerializeJSON.addLog("myFile is not NULL");
            my_blazes=TrailBlazes.CONTINUE;
            while (my_blazes==TrailBlazes.CONTINUE)success=readVerbFile(myFile);
            if (success) myVerbs.add(myVerb);
            st=br.readLine();
        }
    }

    private boolean readVerbFile(BufferedReader myFile) {
        System.out.println("Reading from the readVerbFile routine");
        if (this.advance(myFile)== false){
            my_blazes=TrailBlazes.INVALID;
            return true;
        }
        String st;
        String command;
        try {
            st=myFile.readLine();
            command = this.extractTag(st, '*','*');
            
            if (command.equals("DISPLAYNAME")){
                SerializeJSON.addLog("DISPLAYNAME is being read");
                st=myFile.readLine();
                if (myFile.readLine().equals("---")){ myVerb.setDisplayName(st);SerializeJSON.addLog("adding display name: " + st);}
                else throw new Exception("Problem in Displayname");
                
            }
            //DISPLAYNAME TARGETPATH CALLERPATH COPYTOTARGET COPYTOCALLER
            if (command.equals("SELF")){
                if (myFile.readLine().equals("---"))myVerb.setSelf(true);
                else throw new Exception("Problem in SELF");
            }

            if (command.equals("NOSELF")){
                if (myFile.readLine().equals("---"))myVerb.setSelf(false);
                else throw new Exception("Problem in NOSELF");
            }   

            if (command.equals("CALLERREQUIREMENT")){ //We don't parseBracket because we don't care about the actual value
                String operation=myFile.readLine();
                comparison myOperation;
                if (operation.equals("*NOTEQUAL*")) myOperation=comparison.notequals;
                else if (operation.equals("*GREATERTHAN*")) myOperation=comparison.greaterthan;
                else if (operation.equals("*LESSTHAN*")) myOperation=comparison.lessthan;           
                else if (operation.equals("*EQUALS*")) myOperation=comparison.equals;
                else throw new Exception("An appropriate comparison was not found");
                String name=myFile.readLine();//this.extractTag(myFile.readLine(),'[',']'); 
                Attribute value=dataToAttribute(name,myFile.readLine());
                if (myFile.readLine().equals("---")){                
                if (value.getType().equals("Numeric")) {
//                    addRequirement(String name, String value, int value){
                    myVerb.addCallerRequirement(name,Integer.toString(value.getintData()),myOperation);
                }else{
                    myVerb.addCallerRequirement(name,value.getData(),myOperation); //Integer cannot be null?
                }
                } else throw new Exception("Problem in REQUIREMENT");
            }

            if (command.equals("TARGETREQUIREMENT")){ //We don't parseBracket because we don't care about the actual value
                String operation=myFile.readLine();
                comparison myOperation;
                if (operation.equals("*NOTEQUAL*")) myOperation=comparison.notequals;
                else if (operation.equals("*GREATERTHAN*")) myOperation=comparison.greaterthan;
                else if (operation.equals("*LESSTHAN*")) myOperation=comparison.lessthan;           
                else if (operation.equals("*EQUALS*")) myOperation=comparison.equals;
                else throw new Exception("An appropriate comparison was not found");
                String name=myFile.readLine();//this.extractTag(myFile.readLine(),'[',']'); 
                Attribute value=dataToAttribute(name,myFile.readLine());

                if (myFile.readLine().equals("---")){                
                if (value.getType().equals("Numeric")) {
//                    addRequirement(String name, String value, int value){
                    myVerb.addTargetRequirement(name,Integer.toString(value.getintData()),myOperation);
                }else{
                    myVerb.addTargetRequirement(name,value.getData(),myOperation); //Integer cannot be null?
                }
                } else throw new Exception("Problem in REQUIREMENT");
            }

            if (command.equals("TARGETPATH")){
                SerializeJSON.addLog("TARGETPATH is being read");
                st=myFile.readLine();
                if (myFile.readLine().equals("---")){ myVerb.addTargetPath(st);
                SerializeJSON.addLog("Adding target path " + st);}
                else throw new Exception("Problem in *TARGETPATH*");
            }

            if (command.equals("CALLERPATH")){
                SerializeJSON.addLog("CALLERPATH is being read");
                st=myFile.readLine();
                if (myFile.readLine().equals("---")) myVerb.addCallerPath(st);
                else throw new Exception("Problem in *CALLERPATHPATH*");
            }

            if (command.equals("COPYTOTARGET")) {
                //    public void addCopyAttribute(verbTarget mySource, String attributeName, String copyName){
                //st=myFile.readLine();
                SerializeJSON.addLog("COPYTOTARGET is being read");
                String sourceName=this.extractTag(myFile.readLine(),'[',']');
                String destinationName=this.extractTag(myFile.readLine(),'[',']');
                if (myFile.readLine().equals("---")){
                    myVerb.addCopyAttributetoTarget(sourceName,destinationName);
                }else throw new Exception("Problem in COPYTOTARGET");               
            }

            if (command.equals("COPYTOCALLER")) {
                SerializeJSON.addLog("COPTYTOCALLER is being read");
                String sourceName=this.extractTag(myFile.readLine(),'[',']');
                String destinationName=this.extractTag(myFile.readLine(),'[',']');
                if (myFile.readLine().equals("---")){
                    myVerb.addCopyAttributetoCaller(sourceName,destinationName); //Only difference between copytocaller
                }else throw new Exception("Problem in COPYTOCALLER");               
            }
            
				if (command.equals("RETURNAFTER")){
					String operation=myFile.readLine();
					if (myFile.readLine().equals("---")){
						if (operation.equals("*RETURN*")) myVerb.setgotoType(false);
						else if (operation.equals("*NORETURN*")) myVerb.setgotoType(true);
						else throw new Exception("Problem in returnafter");
					}else throw new Exception("Problem in returnafter");
				}
				/*
				---
*TIMER*
*GOTO* or *SIDEPATH* //Wether or not your game continues executing where it was or if the game simply changes trails when the timer activates
1000
yourPath.trail
---
*/
				//	scriptTimer(User myUser,String myTrail,boolean gotoType,long seconds){

				
            SerializeJSON.addLog("We are returning true.");
            return true;
        } catch (Exception e) {
            my_blazes=TrailBlazes.INVALID;
            SerializeJSON.addLog("ERROR!" + e.toString());
            return false;
        }
        
    }
    //    storeQuestion(String attrName,String Question,boolean isNumeric,List<String> Options){
    private void sendQuestion(storeQuestion currentQuestion){
        myUser.askQuestion(currentQuestion.attrName, currentQuestion.Question,currentQuestion.isNumeric,currentQuestion.Options);
    }

    public void sendLastQuestion(){
        sendQuestion(myLastQuestion);
    }
    
    private String extractMultipleTags(String parseObject, char startTag, char endTag) throws Exception { //Tested working although nothing calls it yet
        int startCount=0;
        int endCount=0;
        int[] startArray= new int[1000];
        int[] endArray=new int[1000];
        String returnString="";

        for (int i = 0; i < parseObject.length(); i++) {
            if (parseObject.charAt(i)==startTag){
                startArray[startCount]=i; // Stores all of the places where a tag starts
                startCount++;
            }
            if (parseObject.charAt(i)==endTag){
                endArray[endCount]=i;
                endCount++;
            }
            if (endCount-2==startCount) throw new Exception("Too many " + endTag + " instances found"); //This code makes it so only one [] pair is ever allowed.
            if (startCount-2==endCount) throw new Exception("Too many " + startTag + " instances found");//In other words, you cannot nest tags in this language
        }
        if (endCount!=startCount) throw new Exception("Unclosed tags or extranous characters detected");
        if (endCount==0) throw new Exception("No tags detected"); //return parseObject, which would allow to simply write references with not tags at all
        for (int z=0;z<endCount;z++){
            //Name = parseObject.substring(parseObject.indexOf('[',look)+1,parseObject.indexOf(']',look));
            //this.extractTag(parseObject.substring(startArray[z]+1,endArray[z]),,startTag,endTag)
            returnString+=(parseObject.substring(startArray[z]+1,endArray[z]));
        }
        return returnString;
    }

    private BufferedReader setReader(String startPath, String startFile) throws IOException {
        return new BufferedReader(new FileReader(startPath+startFile)); // Returns instead of sets br in case we ever implement "return" branches
    }

    private boolean advance(BufferedReader thisBuffer) {
        String st;
        try {
            st=thisBuffer.readLine();
        } catch(Exception e) {
            my_blazes=TrailBlazes.INVALID;
            myUser.send_message("Probably end of file: "+ e.toString(),"Server",Message.CHAT);
            return false;
        }

        if (st==null) {
            //myUser.send_message("End of file","Server",Message.CHAT);
            my_blazes=TrailBlazes.INVALID;
            return false;
        }

        if (st.equals("---")==false) {
            myUser.send_message("malformed Result was: "+st,"Server: ",Message.CHAT);
            my_blazes=TrailBlazes.INVALID;
            return false;
        }
        return true;
    }

    public void parsePark(){
        while (my_blazes==TrailBlazes.CONTINUE) {
            this.readGameWorld();
        }
    }

    public void readGameWorld() {
        try {
            String st;
            if (!this.advance(br)){
                my_blazes=TrailBlazes.INVALID;
                return;
            }
            st=this.br.readLine();
            String command = this.extractTag(st, '*','*');
            if (command.equals("UNIQUE")) {
                SerializeJSON.addLog("worldloading: parsing a UNIQUE");
                st=br.readLine();
                if (br.readLine().equals("---")) this.myPark.setUniqueAttribute(st); //all the lower case handling is inside of
                else throw new Exception("Cannot read UNIQUE in gameworld");
            }

            if (command.equals("BANNEDCONTENT")) {
                SerializeJSON.addLog("worldloading: parsing a BANNEDCONTENT");
                String name=br.readLine();
                String value=br.readLine();
                if (br.readLine().equals("---")) this.myPark.setBannedContent(name,value);
                else throw new Exception("cannot read BANNEDCONTENT in gameworld");
            }
            
            if (command.equals("EXITSCRIPT")){ //I should probably start to support multiple exit scripts at some piont
					String script=br.readLine();			
					if (br.readLine().equals("---"))	myPark.addExitScript(script);
					else throw new Exception("Problem in EXITSCRIPT");
            }


        } catch (Exception ee) {
            my_blazes=TrailBlazes.INVALID;
            myUser.send_message("problems has Occured when creating the world: "+ ee.toString(),"Server",Message.CHAT);
            return;
        }
    }

    public void Parse() {
        //This code is meaningful because we DON'T know what state the world might be called in when this is executed
		  SerializeJSON.addLog("Parse() function is starting");        
        while (my_blazes==TrailBlazes.CONTINUE && myUser.gameover==false) { //Gameover should not be needed.. why is this called!
            refresh=true;
            this.readNextCommand();
        }
        SerializeJSON.addLog("Returning from the Parse() function at:"+currentFile);
        //if (my_blazes!=TrailBlazes.INQUIRE || this.sidePath==true) myUser.returnFromPath();
        if (this.sidePath==true && my_blazes != TrailBlazes.INQUIRE) myUser.returnFromPath();
        if (my_blazes==TrailBlazes.INVALID) SerializeJSON.addLog("MyBlazes are now invalid");
        //if (my_blazes==TrailBlazes.INVALID && refresh){
        //    refresh=false;
        //    myUser.doneVerb();
        //}

        //try{myUser.send_message("IT seems we are done parsing with my_blazes="+my_blazes,"Server",Message.CHAT);
        //}catch (IOException ee) {}
    }

    private String getAttribute() {
        return null;
    }
    /*WARNING! This creates an unlikely, but possible, arbitrary situation with the names of attributes specified by the user: if *target* from a verb is an attribute*/
    //See simple math for an example
    private List<Attribute> parseBracket(String parseObject) throws Exception { //Can only be called from readNextCommand, therefore throwing is acceptable
        /*I really can't believe this worked the first time I built it - see file "cool"*/
        List<Attribute> result=new ArrayList<Attribute>();
        String Result="";
        String Name;//=""; //Initilization Unneeded
        int look=0;// = parseObject.indexOf('[');
        if (parseObject.indexOf('[',look) != -1) //What happens if we remove look?
            do {
                try {
                    try {
                        //Result+=parseObject.substring(look,parseObject.indexOf('[',look));   //Because the solution will be behind the start, exception is thrown
//                        public void setAttributes(String Type,String Name, String Data, float floatdata, int intData) { //Not reason to return a class as with Attributes
                        //private Attribute dataToAttribute(String attrName, String parseObject) {
                        if (parseObject.indexOf('[',look)!= 0)
                        {
                            result.add(dataToAttribute("nonunique",parseObject.substring(look,parseObject.indexOf('[',look))));
                            // Without the above if statement, an attribute will be created containing NOTHING - this is bad for functions that expect only one attribute
                            result.get(result.size() - 1).tag="typed";
                        }
                    }
                    catch (Exception e) {
                        break;
                    }
                    Name = parseObject.substring(parseObject.indexOf('[',look)+1,parseObject.indexOf(']',look));
                    //myUser.send_message("Name is " + Name,"Server: ",Message.CHAT);
                } //"Consuming" the string might be faster, somehow?
                catch (Exception e) {
                    throw new IOException(e.toString()+"This occured inside of ParseBracket"); //But what happens if only ONE of these is -1?  In that case, I guess it gets ignored
                    //break; //Ignores certain cases of mismatched tags
                }
                Attribute userAttribute = myUser.returnAttribute(Name);
                if (userAttribute==null)  {
                    result.add(dataToAttribute(Name,"Attribute not found")); //Result+="Attribute not found";
                    result.get(result.size() - 1).tag="not found";
                } else {
                    //if (userAttribute.getType().equals("Text")) Result+=userAttribute.getData();
                    //else Result+=Integer.toString(userAttribute.getintData());
                    result.add(userAttribute);
                    result.get(result.size() - 1).tag="retrieved";
                }
                look=parseObject.indexOf(']',look)+1; //Plus one to keep from finding the same one over and over/
            } while (look!=0);
//            Result+=parseObject.substring(parseObject.length()-parseObject.,parseObject.length());
//        Result+=parseObject.substring(look,parseObject.length());
        result.add(dataToAttribute("nonunique,",parseObject.substring(look,parseObject.length())));
        result.get(result.size() - 1).tag="retrieved after brackets";
        return result;
    }

    private String composeAttributeList(List<Attribute> List,boolean throwError)throws Exception {
        String result="";
        for (Attribute list : List) {

            if (list.tag.equals("not found") && throwError) throw new Exception("An Attribute was not found"); //This will throw an error if is is NULL
            try {
                if (list.getType().equals("Numeric")) result+=Integer.toString(list.getintData());
                else result+=list.getData();
            } catch (Exception e) {
                throw new Exception("Error inside of composeAttributeList: " + e.toString());
            }
        }
        return result;
    }

    private String extractTag(String parseObject, char startTag, char endTag) throws Exception {
        //findCommand=Pattern.compile("\\*+?(.+)\\*+");
		  if (parseObject==null) throw new Exception("parseObject was null");			        
        int i=0;
        if (parseObject.charAt(i)!=startTag) throw new IOException("Something not a tag was treated as a tag");
        for (i=1; i<parseObject.length(); i++) {
            if (parseObject.charAt(i)==startTag) if (i < (parseObject.length()-1) && startTag != endTag) throw new IOException("Extranous tag: " + startTag);
            if (parseObject.charAt(i)==endTag) if (i < (parseObject.length()-1)) throw new IOException("Extranous tag: " + endTag);
        }
        return parseObject.substring(1,parseObject.length()-1);
    }

    private Attribute dataToAttribute(String attrName, String parseObject) {
        int result;
        try {
            result = Integer.parseInt(parseObject);
            //myUser.send_message("Trying to parse that int on + " + parseObject + "name was: " + attrName,"Server",Message.CHAT);
            return new Attribute("Numeric",attrName,null,result,result); //Type conversions
        } catch (NumberFormatException nfe) {
            return new Attribute("Text",attrName,parseObject,0,0);
        }
    }

    public String returnResult(String object) throws Exception{
        try {
        int laststart=0;
        int startcount=0;
        int endcount=0;
        for (int i=0; i<object.length(); i++){
            if (object.charAt(i)=='[') {
                laststart=i;
                startcount++;
            }

            if (object.charAt(i)==']'){
                if (startcount<endcount) throw new Exception("Bad startcount");
                String prepend="";
                String append="";
                //String replace=mywords.get(object.substring(laststart+1,i));
                String replace="";
                SerializeJSON.addLog("The attribute about to be passed in is: " + object.substring(laststart+1,i));
                Attribute replacement=myUser.returnAttribute(object.substring(laststart+1,i));
                if (replacement==null) throw new Exception("attribute not found in returnResult!");
                if (replacement.getType().equals("Text")) replace=replacement.getData();
                    else replace=Integer.toString(replacement.getintData());
                if (laststart!=0) prepend=object.substring(0,laststart);
                if (i!= object.length()-1) append=object.substring(i+1,object.length());
                object=prepend+replace+append;
                startcount=0;
                endcount=0;
                laststart=0;
                i=0;
            }
        }
        SerializeJSON.addLog("The object about to be returned is: " + object);
        return object;
    } catch (Exception e) {
        SerializeJSON.addLog("An exception has occured inside of returnResult: " + e.toString());
        throw new Exception();
    }
}

public void setsidePath(boolean result){
	sidePath=result;
}

public void gotoTrail(String file) throws Exception{
	SerializeJSON.addLog("Switching file to " + file + " old file was : " + currentFile);
	this.waiting=null;
	this.currentFile=file;
	br=setReader(filePath,this.currentFile);
	//my_blazes=TrailBlazes.INVALID; //This can cause the parser to stop inappropriately. Example: the goto is from a verb file
}

    public void readNextCommand() {
    	  SerializeJSON.addLog("Reading Next Command");
        String st;
        if (!this.advance(br)) return;
        /*try {
            st=br.readLine();
        } catch(Exception e) {
            my_blazes=TrailBlazes.INVALID;
            myUser.send_message("Probably end of file: "+ e.toString(),"Server",Message.CHAT);
            return;
        }
        */


        String command="NO COMMAND GIVEN";
        try {
            /*
                        if (st==null) {
                            //myUser.send_message("End of file","Server",Message.CHAT);
                            my_blazes=TrailBlazes.INVALID;
                            return;
                        }

                        if (st.equals("---")==false) {
                            myUser.send_message("malformed Result was: "+st,"Server: ",Message.CHAT);
                            my_blazes=TrailBlazes.INVALID;
                            return;
                        }
            */

            try {
                st=this.br.readLine();
                } catch (Exception e) {
                    command="command=this.br.readLine()";
                    throw new Exception(e.toString()+e.toString());
                }

            try {
            command = this.extractTag(st, '*','*');
                } catch (Exception e) { //We handle this differently since it occurs sometimes when returning from a verb for some reason  STOPGAP ONLY
                    command="command = this.extractTag(st, '*','*')"+e.toString()+" current file: " + this.currentFile;
                    //serializeJSON.addLog(command);
                    throw new Exception();
                }
            
            

            try {
                if (command.equals("PRINT")) { //Update concurrently with LOG!
                    st=this.br.readLine();
                    if (this.br.readLine().equals("---")) { //Send the message
                        //String myResult=this.composeAttributeList(this.parseBracket(st),false);
                        String myResult=returnResult(st);
                        if (myResult==null) throw new Exception("Result was null for some reason");
                        /*This needs to be given intermediary steps so that it can start to find world variables or other players variables*/
                        if (myResult!=null) myUser.send_message(myResult,"Server",Message.CHAT); else throw new Exception("Myresult was null for some reason");
                        //my_blazes=TrailBlazes.CONTINUE;
                        return;
                    }
                }
            } catch (Exception ee) {
                throw new Exception(ee.toString() + "Yes, this happened in the print block.");
            }
            /*Start of the SOCIAL command block*/
            //This should accept PHYSICAL, CHAT, and SOLO, meaning no chat functionality
            if (command.equals("SOCIAL")) {
                st=br.readLine();

                if (this.br.readLine().equals("---")) {
                    if (st.equals("CHAT"))myUser.in_public_chat=true;
                    if (st.equals("SOLO"))myUser.in_public_chat=false;
                    //my_blazes=TrailBlazes.CONTINUE;
                }
            }

            if (command.equals("SET") || command.equals("GET_ATTRIBUTE")) {
                SerializeJSON.addLog("Entering set Block");
                String attrName;
                String attrData;
                attrName=br.readLine();
                try { //If the first line does not generate an error, we need to ask the user what is up....
                    String aName;
                    //myUser.send_message(" Reading Tag " + attrName,"Server",Message.CHAT);
                    aName=this.extractTag(attrName,'<','>'); //This will error if there are no tags, going to the catch
                    SerializeJSON.addLog("aName: " + aName);                    
                    attrName=aName; //Negates any risk of loosing the last readLine()
                    attrData=br.readLine();
                    if (attrData.equals("---")) {
                    SerializeJSON.addLog("attrData is ---");                    
                        my_blazes=TrailBlazes.INQUIRE;
                        myLastQuestion=new storeQuestion(attrName,"Please enter a value for " + attrName,false,null);
                        SerializeJSON.addLog("storeQuestion has been saved with top block");
                        //this.sendQuestion(myLastQuestion);
                        myUser.askQuestion(attrName,"Please enter a value for " + attrName,false,null);
                        return;
                    } else {
                        List<String> Options=new ArrayList<String>();
                        while (attrData.equals("---")==false) {
                            Attribute createOption = this.dataToAttribute("noname",this.extractTag(attrData,'&','&'));
                            Options.add(createOption.getData());
                            if (attrData==null) myUser.send_message("Null result error.","Server",Message.CHAT);
                            attrData=br.readLine();
                        }
                        myLastQuestion=new storeQuestion(attrName,"Please enter an answer for " + attrName,true,Options);
                        SerializeJSON.addLog("storeQuestion has been saved with options");
                        //this.sendQuestion(myLastQuestion);
                        my_blazes=TrailBlazes.INQUIRE;
                        try {myUser.askQuestion(attrName,"Please enter an answer for " + attrName,true,Options);}
                       
                        catch (Exception e) {SerializeJSON.addLog("something went wrong iwth askQuestion."+e.toString());my_blazes=TrailBlazes.INVALID;}
                        SerializeJSON.addLog("askquestion has been saved with askquestions");
                        my_blazes=TrailBlazes.INQUIRE;
                        return;
                    }
                } catch (Exception e) { //For security reasons it might be wise to check WHICH error we have - we just assign the variable
                    attrData=br.readLine();
                    //attrName=composeAttributeList(this.parseBracket(attrName),false);
                    attrName=returnResult(attrName);
                    //attrData=this.composeAttributeList(this.parseBracket(attrData),false);
                    attrData=returnResult(attrData);
                    Attribute createAttr;
                    //myUser.send_message(" No tags foundattrName: " + attrName + " attrData: " + attrData,"Server",Message.CHAT);
                    createAttr=this.dataToAttribute(attrName,attrData);
                    myUser.setAttribute(createAttr);
                    String Choice=br.readLine();
                    if (Choice.equals("---")) {
                        //my_blazes=TrailBlazes.CONTINUE;
                        return;
                    } else throw new IOException();
                }
            }

            if (command.equals("CHATNAME")) {
                st=this.br.readLine();
                if (this.br.readLine().equals("---")) {
                    //String Result=this.composeAttributeList(this.parseBracket(st),false);
                    String Result=returnResult(st);
                    myUser.setChatName(Result);
                    //my_blazes=TrailBlazes.CONTINUE;
                    return;
                } else {throw new IOException("CHATNAME block contains invalid information or is malformed");}
            }

            /*---
            *SIMPLEMATH*
            *ADD*
            <zorkmids>
            [zorkmids]
            75
            ---*/

            if (command.equals("SIMPLEMATH")) {
                String next=br.readLine();
                String anAttribute;
                anAttribute=br.readLine();
                try {anAttribute=this.extractTag(anAttribute,'<','>');}
                catch (Exception e) {throw new Exception("Bad reference to attribute for calulation result");}
                next=this.extractTag(next,'*','*');
                Attribute result;
                /*Okay, so to update these attributes: first we check the list size.  The method here is to see if there is one or more than one.
                  If there is more than one bracket attribute, the only valid syntax is the specifier for WHICH player we are talking about
                  As a stop-gap, we are going to assume that the information in the first bracket is this even if it matches attribute names
                  Therefore, our attribute list should be updated to use names from "nonunique" to the original data
                  ...parseBrackets ahoy
                */

                Attribute first=this.parseBracket(br.readLine()).get(0); //Add some error checking such as making sure there IS only one attribute here
                Attribute second=this.parseBracket(br.readLine()).get(0);
                //Attribute first=this.dataToAttribute("first",this.parseBracket(br.readLine()));
                //Attribute second=this.dataToAttribute("second",this.parseBracket(br.readLine()));
                //myUser.send_message("first was: " + first.getData() + " tag was " + first.tag,"Server: ", Message.CHAT);
                //myUser.send_message("second was: " + second.getData() + " tag was " + second.tag,"Server: ", Message.CHAT);
                if (!first.getType().equals("Numeric") || !second.getType().equals("Numeric")) throw new Exception("Math entry is not a number: Name"+first.getName() + " Type: " +first.getType() + " Name: " + second.getName() + " type: " + second.getType()); //Should I be using and/or
                if (next.equals("ADD")) {
                    result=this.dataToAttribute(anAttribute,Integer.toString(first.getintData()+second.getintData()));
                } else if (next.equals("SUBTRACT")) {
                    result=this.dataToAttribute(anAttribute,Integer.toString(first.getintData()-second.getintData()));
                } else if (next.equals("MULTIPLY")) {
                    result=this.dataToAttribute(anAttribute,Integer.toString(first.getintData()*second.getintData()));
                } else if (next.equals("DIVIDE")) {
                    result=this.dataToAttribute(anAttribute,Integer.toString(first.getintData()/second.getintData()));
                } else throw new Exception("Unsupported mathematics");
                if (br.readLine().equals("---")) {
                    myUser.setAttribute(result);
                    return;
                } else throw new Exception("malformed SIMPLEMATH statement");
            }

            /*
                *SIMPLEMATH*
                *ADD*
                <result>
                [first]
                [second]
                ---
            */

            /*
            ---
                *IF*
                [hitpoints]
                (Will require mathematical operators) *<* *>* = <= =>
                (Will requre string operators) *isEqual* *isLonger*
                5 or [value]
                File For TRUE CONDITION
                File for FALSE CONIDTION
                ---
            */

            if (command.equals("IF")) {
            	boolean gotoType=true;
//                String firstCondition=this.parseBracket(br.readLine());
//                  String firstCondition=this.parseBracket(br.readLine()).get(0).
				    String returnType=br.readLine();
				    if (returnType.equals("*SIDEPATH*"))gotoType=false; //Must convert this back to extracttags
				    else if (!returnType.equals("*GOTO*")) throw new Exception("Could not process return type in if statement");
				    
					 String first=returnResult(br.readLine());                
                String operator;
                try {operator=this.extractTag(br.readLine(),'*','*');}
                catch (Exception e) { throw new Exception("Problem in IF statement");}
					 String second=returnResult(br.readLine());
                String trueResult=returnResult(br.readLine());
                String falseResult=returnResult(br.readLine());
                if (!br.readLine().equals("---")) throw new Exception("Problem finding --- after IF");
                if (operator.equals("=")) {
                    if (first.equals(second)) {
                    		myUser.executeVerb(trueResult,gotoType);
                    } else {
								myUser.executeVerb(falseResult,gotoType);
                    }
                    //my_blazes=TrailBlazes.CONTINUE;
                    return; //We do not have to read the last --- until we implement true returning branches
                }
                
                if (operator.equals("!=")) {
                    if (!first.equals(second)) {
								   myUser.executeVerb(trueResult,gotoType);
                    } else {
									myUser.executeVerb(falseResult,gotoType);
                    }
                    //my_blazes=TrailBlazes.CONTINUE;
                    return; //We do not have to read the last --- until we implement true returning branches
                }

                long firstParameter;
                long secondParameter;
				    try{
						firstParameter=Integer.parseInt(first);
						secondParameter=Integer.parseInt(second);			    
				    }catch(Exception e){
					    throw new Exception("Both variables must be numeric for numeric comparators");
				    }

                if (operator.equals(">")){
                    if (firstParameter>secondParameter){
								   myUser.executeVerb(trueResult,gotoType);
                    } else {
                        try {myUser.executeVerb(falseResult,gotoType);}
                        catch (Exception e) {throw new IOException("Problem in if block " + e.toString());}
                    }
            }
            
            if (operator.equals(">=")){
                 if (firstParameter>=secondParameter){
								   myUser.executeVerb(trueResult,gotoType);
                 } else {
									myUser.executeVerb(falseResult,gotoType);
                 }
         	}
         	
         	 if (operator.equals("<")){
                 if (firstParameter<secondParameter){
								   myUser.executeVerb(trueResult,gotoType);
                 } else {
									myUser.executeVerb(falseResult,gotoType);
                 }
         	}
         	
         	if (operator.equals("<=")){
                 if (firstParameter<=secondParameter){
								   myUser.executeVerb(trueResult,gotoType);
                 } else {
									myUser.executeVerb(falseResult,gotoType);
                 }
         	}
         	
         	
         	
            }

            if (command.equals("GOTO")) { //This is copied into IFEXISTS, and should be modified concurrently
                //br=setReader(filePath, this.parseBracket(br.readLine()).get(0).getData()); //Look at that - no error checking whatsoever.
                //this.currentFile=returnResult(br.readLine());
  				    gotoTrail(returnResult(br.readLine()));
                return;
            }

            if (command.equals("PICTURE_URL")) {
                st=br.readLine();
                if (br.readLine().equals("---")) {
                    //<img src="smiley.gif" alt="Smiley face" width="42" height="42">
                    String result=null;
                    try{
                        //String myResult=this.composeAttributeList(this.parseBracket(st),false);
                        String myResult=returnResult(st);
                        myUser.send_message(myResult,"Server",Message.HTML_IMAGE);
                    }catch (Exception e){
                        myUser.send_message("Something went wrong with the HTML parser.  "+e.toString(),"Server",Message.CHAT);
                        myUser.send_message(st,"Server",Message.HTML_IMAGE);
                    }                    
                    //myUser.send_message("we found a picture","Server",Message.CHAT);
                    //myUser.send_message(st,"Server",Message.HTML_IMAGE);
                }
                else throw new Exception("Problem in the picture block");
            }

            if (command.equals("RANDOM")) {
                myUser.send_message("beginning random","Server",Message.CHAT);
                //Attribute bottom = this.parseBracket(br.readLine()).get(0); //This is the problem!!  Prasebracket is not appropriate
                //Attribute top    = this.parseBracket(br.readLine()).get(0);
                String placeholder=returnResult(br.readLine());
                Attribute bottom = myUser.returnAttribute(placeholder);
                if (bottom==null){ //We need to assume that this is a number
						bottom=dataToAttribute("bottom",placeholder);          
                }
                placeholder=returnResult(br.readLine());
                Attribute top = myUser.returnAttribute(placeholder);
                if (top == null){
                	top=dataToAttribute("top",placeholder);
                }

                if (top.getType()!="Numeric" || bottom.getType()!="Numeric") throw new Exception("Something is wrong with a random number");
                //Attribute set=this.parseBracket(br.readLine()).get(0);
                String set = returnResult(br.readLine());
                Random rand = new Random();
                
                int result = rand.nextInt(top.getintData()-bottom.getintData())+bottom.getintData();
//              private Attribute dataToAttribute(String attrName, String parseObject) {
					//    public void setAttribute(String Type, String Name, String Data, int intData, float floatdata) {
                if (br.readLine().equals("---"))myUser.setAttribute("Numeric",set,null,result,0); //myUser.setAttribute(dataToAttribute(set.getName(),Integer.toString(result))); //....This is comically inefficient
                else throw new Exception("Syntax Error");
                myUser.send_message("commandblock result was: "+Integer.toString(result)+"bottom: "+Integer.toString(bottom.getintData())+"top: "+Integer.toString(top.getintData()),"Server",Message.CHAT);
                return;
            }
            /*
            *BROADCAST*
            *ALL* or ATTRIBUTE
            If Attribute:
            name
            value
            *CHAT*
            message
            */
//myUser.broadcast(attrName, value,st,Message.CHAT);
//myUser.broadcast(st, Message.CHAT);

            if (command.equals("BROADCAST")){
                st = br.readLine();
                if (st.equals("*ALL*")){
                    st = br.readLine();
                    if (st.equals("*CHAT*")){
                        //st=composeAttributeList(this.parseBracket(br.readLine()),false);
                        st=returnResult(br.readLine());
                        if (this.br.readLine().equals("---")) myUser.broadcast(st, Message.CHAT);
                        else throw new Exception("Problem in BROADCAST");
                    }
                }else { //instead of *ALL* we have attribute
                    SerializeJSON.addLog("Starting a private broadcast");
//                    String attrName=composeAttributeList(this.parseBracket(st),false);
                    String attrName=returnResult(br.readLine());
                    SerializeJSON.addLog("attrName: " + attrName);
//                    String value=composeAttributeList(this.parseBracket(br.readLine()),false);
                    String value=returnResult(br.readLine());
                    SerializeJSON.addLog("value: " + value);
                    if (br.readLine().equals("*CHAT*")){
                        SerializeJSON.addLog("entering *chat*");
//                        st=composeAttributeList(this.parseBracket(br.readLine()),false);
                          st=returnResult(br.readLine());
                        if (this.br.readLine().equals("---")) myUser.broadcast(attrName, value,st,Message.CHAT);
                        else throw new Exception("Problem in BROADCAST");
                    }
                }
            }

            if (command.equals("SIDEPATH")){ //This code is copied and pasted into ifexists!  Modify concurrently!  (consider making it a function);
                SerializeJSON.addLog("We are now inside of the sidepath");
                 String subroutine=returnResult(br.readLine());
                if (br.readLine().equals("---")) myUser.sidePath(subroutine); //sideTrail=new TrailBlazer(this.filePath,myUser,null,null,subroutine);
                my_blazes=TrailBlazes.WAIT; // Must be different from inquire since verbs can call this out of the blue
                return;
                //if (sideTrail!=null && sideTrail.my_blazes==TrailBlazes.GAMEOVER)my_blazes=TrailBlazes.GAMEOVER;
            }

            if (command.equals("GAMEOVER")){
                ///BUG!: We need to see how, exactly, this function get's called on failure.  Otherwise we would not have to have the if statement in the catch below
                my_blazes=TrailBlazes.GAMEOVER; //By itself, causes a lockup.. why?
                myUser.gameover();
                return;
            }

            if (command.equals("VERB")){
                String target=returnResult(br.readLine());
                String verb=returnResult(br.readLine());
                SerializeJSON.addLog("Verb called.  Target is: " + target + " and the verb is: " + verb);
                if (br.readLine().equals("---")) myUser.callVerb(target,verb);
            }

                    if (command.equals("LOG")) { //This should look exactly like print: except it spits the contents out to the log file.  Update concurrently with print!
                    		st=this.br.readLine();
                    		if (this.br.readLine().equals("---")) { //Send the message
	                        String myResult=returnResult(st);
	                        if (myResult==null) throw new Exception("Result was null for some reason");
	                        /*This needs to be given intermediary steps so that it can start to find world variables or other players variables*/
	                        SerializeJSON.addLog(myResult);
	                        //my_blazes=TrailBlazes.CONTINUE;
	                        return;
                    		}
                    		
                }
            
            /*
            	---
            	*IFEXISTS*
            	*GOTO* or *SIDEPATH*
            	[variable]
            	sometrail.trail
            	falsetrail.trail
            	---
            
            */

            if (command.equals("IFEXISTS")){
                String behave = this.extractTag(br.readLine(),'*','*'); //This can be either *GOTO* or *SIDETRAIL*
                SerializeJSON.addLog("Behavoir in ifexists is: " + behave);

                boolean exitEarly=false;
                try {
                    String attrName=returnResult(br.readLine()); //Checks for an object; if it does NOT exist it throws the error and goes into the catch
                    if (myUser.returnAttribute(attrName)==null) throw new Exception("Not a real expectation, triggering log file");
						  SerializeJSON.addLog("Exit Early will be False");
                }catch(Exception e){ //Reaching this should mean an error was thrown in returnResult - this means the thing does not exist!
                	  SerializeJSON.addLog("exitEarly will be true");
                    exitEarly=true; //This has to be seperate, because the ONLY error we don't throw back to the user is if this variable is not found.  *IFEXISTS*
                }
                String destination=returnResult(br.readLine());
                if (destination.equals("---")) throw new Exception("No file path stated"); // Because there needs to be at least one .trail file for the result
                String secondDestination=br.readLine();
                String finalLine;
                if (secondDestination.equals("---"))finalLine=secondDestination;
                else finalLine=br.readLine();
                if (finalLine!=null && finalLine.equals("---")){ //Need the null check because of possible errors in br.readLine()
                    if (exitEarly) {
                        if (secondDestination==finalLine) return;
                        else{
                         	if (secondDestination.equals("---")) return; //This means there IS no second destination, hence the variable read --- rather than something.trail
                         	destination=secondDestination; //Set the destination varibale to be equal to the failure case
                        }
                    }
                    if (behave.equals("GOTO")){ //This is copied from goto, and should be modified concurrently!!
                        gotoTrail(returnResult(br.readLine()));
                    }
                    
                    if (behave.equals("SIDEPATH")){ //This code was simply copied and pasted from Sidepath!  Modify Concurrently!!
    						SerializeJSON.addLog("We are now inside of the sidepath");
                		if (finalLine.equals("---")) myUser.sidePath(destination); //sideTrail=new TrailBlazer(this.filePath,myUser,null,null,subroutine);
                		my_blazes=TrailBlazes.WAIT; //Must be different from inquire since verbs can call this out of the blue
                		return;
                    }
                    

                }else throw new Exception("Invalid formatting near IFEXISTS");
            }


            
            if (command.equals("TIMER")) { //Possible memory leak!!! If "timer" doesn't exit on it's own, why would it's thread?
					SerializeJSON.addLog("Entering timer");										
					String type=br.readLine();
					SerializeJSON.addLog("type: "+type);					
					boolean gotoType=true;
					if (type.equals("*SIDEPATH*"))gotoType=false;
					else if (!type.equals("*GOTO*")) throw new Exception("Error reading TIMER");
					String secondsString=returnResult(br.readLine());
					long seconds;
					try {
						seconds =Long.parseLong(secondsString);
					}catch (Exception e){
						throw new Exception("Could not read the seconds value in TIMER");
					}
					SerializeJSON.addLog("seconds to delay: " + Long.toString(seconds));
					String nextFile = returnResult(br.readLine());
					SerializeJSON.addLog("nextfile: " +nextFile);
					if (br.readLine().equals("---"))new scriptTimer(myUser,nextFile,gotoType,seconds);
					else throw new Exception ("No --- found at end of TIMER");
				}

            if (command.equals("REMOVE")){ //NOT TESTED
                String Variable = returnResult(br.readLine());
                if (br.readLine().equals("---")) myUser.removeAttribute(Variable);
            }

            if (command.equals("CLEARGROUP") || command.equals("CLEARATTRIBUTES")){
                if (br.readLine().equals("---")) myUser.clearAttributes();
                else throw new Exception("Error in readline");
            }

            if (command.equals("CREATEGROUP")){
                String groupName=returnResult(br.readLine());
                boolean success=false;
                if (br.readLine().equals("---"))success= myUser.addGroup(groupName); else throw new Exception("Error in CREATEGROUP");
                if (success==false) throw new Exception ("ERROR: Group already created.");
                
            }

            if (command.equals("CHANGEGROUP")){ 
                String groupName=returnResult(br.readLine());
                boolean success=false;
                if (br.readLine().equals("---"))success= myUser.changeGroup(groupName); else throw new Exception("Error in CHANGEGROUP");
                if (success==false) throw new Exception ("ERROR: Group does not exist");
            }

            if (command.equals("INDEXREMOVE")){
                String index=returnResult(br.readLine());
                int result=-1;
                if (br.readLine().equals("---")){               
                    int i=0;
                    try {
                        i=Integer.parseInt(index);
                    } catch (Exception e){
                        throw new Exception("Invalid Index Number in indexremove");
                    }
                    //boolean succcess=userGroup.removeAttribute(i); What is should be, you can't remove something that is not there
                    myUser.removeAttribute(i);
                }else throw new Exception("Error in INDEXREMOVE");
            }

            if (command.equals("GROUPCOUNT"){
                String dest=returnResult(br.readLine());
                if (br.readLine().equals("---"){
                    myUser.assignGroupCount(dest);
                }
            }

            //New Commands CopyToGroup
            //CLEARGROUP | CHANGEGROUP | COPYTOGROUP | GROUPCOUNT
            //Arrays and 

            //All commands will 
				
        } catch(Exception e) { //If this happens, we may have a simple end of file.
            my_blazes=TrailBlazes.INVALID;
            SerializeJSON.addLog("Exception has occured while parsing"+ e.toString());
            //if (!command.equals("NO COMMAND GIVEN")) myUser.send_message("Exception has occured while parsing "+ e.toString() + "Player name was: " + myUser.returnAttribute("name").getData() + " last command was: " + command,"Server",Message.CHAT);
            return;
        }
    }



}
