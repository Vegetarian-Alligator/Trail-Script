package mypackage;
import java.io.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import java.util.*;

public class TrailBlazer {
    protected TrailBlazes my_blazes; //The status of this trail
    protected String Command;
    private String filePath;
    private BufferedReader br;
    User myUser;
    TrailBlazer (String startPath, User calling_user) {
        myUser=calling_user;
        //myUser.send_message("Testing the new Trailblazer system","Server",Message.CHAT);


        try {
            //this.br = new BufferedReader(new FileReader(startPath+"trailhead.trail"));
              this.br = this.setReader(startPath,"trailhead.trail");
              this.filePath=startPath;
            my_blazes=TrailBlazes.CONTINUE;
            this.Parse();

        } catch (IOException e) { //NoSuchElementException
            //myUser.send_message("IO Exception has occured "+ e.toString(),"Server",Message.CHAT);
        }
        //while ((st = br.readLine()) != null)


    }
    
    private BufferedReader setReader(String startPath, String startFile) throws IOException {
        return new BufferedReader(new FileReader(startPath+startFile)); // Returns instead of sets br in case we ever implement "return" branches
    }

    public void Parse() {
        while (my_blazes==TrailBlazes.CONTINUE) {
            this.readNextCommand();
        }
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
        if (parseObject.indexOf('[') != -1)
            do {
                try {
                    try {
                        //Result+=parseObject.substring(look,parseObject.indexOf('[',look));   //Because the solution will be behind the start, exception is thrown
//                        public void setAttributes(String Type,String Name, String Data, float floatdata, int intData) { //Not reason to return a class as with Attributes
                        //private Attribute dataToAttribute(String attrName, String parseObject) {
                          result.add(dataToAttribute("nonunique",parseObject.substring(look,parseObject.indexOf('[',look))));
                          result.get(result.size() - 1).tag="typed";  
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
                }else {
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
        return result;
    }

    private String composeAttributeList(List<Attribute> List,boolean throwError)throws Exception {
        String result="";
        for (Attribute list : List) {
            if (list.tag.equals("not found") && throwError) throw new Exception("An Attribute was not found");
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
            //myUser.send_message("Trying to parse that int on + " + parseObject,"Server",Message.CHAT);
            return new Attribute("Numeric",attrName,null,result,result); //Type conversions
        } catch (NumberFormatException nfe) {
            return new Attribute("Text",attrName,parseObject,0,0);
        }
    }

    public void readNextCommand() {
        String st;
        try {
            st=br.readLine();
        } catch(Exception e) {
            my_blazes=TrailBlazes.INVALID;
            myUser.send_message("Probably end of file: "+ e.toString(),"Server",Message.CHAT);
            return;
        }


        try {
            if (st==null) {
                //myUser.send_message("End of file","Server",Message.CHAT);
                my_blazes=TrailBlazes.INVALID;
                return;
            }

            if (st.equals("---")==false) {
                myUser.send_message("malformed Result was: ","Server: "+st,Message.CHAT);
                my_blazes=TrailBlazes.INVALID;
                return;
            }


            st=this.br.readLine();


            String command = this.extractTag(st, '*','*');

            if (command.equals("PRINT")) { //The User
                //myUser.send_message("Entering the print subroutine","Server",Message.CHAT);
                st=this.br.readLine();
                if (this.br.readLine().equals("---")) { //Send the message
                    //String Result=this.parseBracket(st);
                    //myUser.send_message(Result,"Server",Message.CHAT); //This line of code Actually send the message!
                    String myResult=this.composeAttributeList(this.parseBracket(st),false);
                    /*This needs to be given intermediary steps so that it can start to find world variables or other players variables*/                    
                    myUser.send_message(myResult,"Server",Message.CHAT);
                    my_blazes=TrailBlazes.CONTINUE;
                    return;
                }
            }

            /*Start of the SOCIAL command block*/
            //This should accept PHYSICAL, CHAT, and SOLO, meaning no chat functionality
            if (command.equals("SOCIAL")) {
                st=br.readLine();

                if (this.br.readLine().equals("---")) {
                    if (st.equals("CHAT"))myUser.in_public_chat=true;
                    if (st.equals("SOLO"))myUser.in_public_chat=false;
                    my_blazes=TrailBlazes.CONTINUE;
                }
            }

            if (command.equals("GET_ATTRIBUTE")) {
                String attrName;
                String attrData;
                attrName=br.readLine();
                try { //If the first line does not generate an error, we need to ask the user what is up....
                    String aName;                    
                    //myUser.send_message(" Reading Tag " + attrName,"Server",Message.CHAT);                    
                    aName=this.extractTag(attrName,'<','>'); //This will error if there are tags, going to the catch
                    attrName=aName; //Negates any risk of loosing the last readLine()
                    attrData=br.readLine();
                    if (attrData.equals("---")) {
                        my_blazes=TrailBlazes.INQUIRE;
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
                        myUser.askQuestion(attrName,"Please enter an answer for " + attrName,true,Options);

                        my_blazes=TrailBlazes.INQUIRE;
                        return;
                    }
                } catch (Exception e) { //For security reasons it might be wise to check WHICH error we have - we just assign the variable
                    attrData=br.readLine();
                    Attribute createAttr;
                    //myUser.send_message(" No tags foundattrName: " + attrName + " attrData: " + attrData,"Server",Message.CHAT);
                    createAttr=this.dataToAttribute(attrName,attrData);
                    myUser.setAttribute(createAttr);
                    String Choice=br.readLine();
                    if (Choice.equals("---")) {
                        my_blazes=TrailBlazes.CONTINUE;
                        return;
                    } else throw new IOException();
                }
            }

            if (command.equals("CHATNAME")) {
                st=this.br.readLine();
                if (this.br.readLine().equals("---")) {
                    String Result=this.composeAttributeList(this.parseBracket(st),false);
                    myUser.setChatName(Result);
                    my_blazes=TrailBlazes.CONTINUE;
                    return;
                }else {throw new IOException("CHATNAME block contains invalid information or is malformed");}
            }

            if (command.equals("SIMPLEMATH")){
                String next=br.readLine();
                String anAttribute;
                anAttribute=br.readLine();
                try {anAttribute=this.extractTag(anAttribute,'<','>');}catch (Exception e){throw new Exception("Bad reference to attribute for calulation result");}
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
                    
                    if (!first.getType().equals("Numeric") || !second.getType().equals("Numeric")) throw new Exception("Math entry is not a number"); //Should I be using and/or
                    if (next.equals("ADD")){
                        result=this.dataToAttribute(anAttribute,Integer.toString(first.getintData()+second.getintData()));
                     }else if (next.equals("SUBTRACT")){
                        result=this.dataToAttribute(anAttribute,Integer.toString(first.getintData()-second.getintData()));
                     }else if (next.equals("MULTIPLY")){
                        result=this.dataToAttribute(anAttribute,Integer.toString(first.getintData()*second.getintData()));
                     }else if (next.equals("DIVIDE")){
                        result=this.dataToAttribute(anAttribute,Integer.toString(first.getintData()/second.getintData()));
                    }else throw new Exception("Unsupported mathematics");
                    
                    if (br.readLine().equals("---")){
                        myUser.setAttribute(result);
                        return;
                    }else throw new Exception("malformed SIMPLEMATH statement");
            }

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

            if (command.equals("IF")){
//                String firstCondition=this.parseBracket(br.readLine());
//                  String firstCondition=this.parseBracket(br.readLine()).get(0).
                Attribute first=this.parseBracket(br.readLine()).get(0); //Again add debugging based on list size
                String operator;
                try {operator=this.extractTag(br.readLine(),'*','*');} catch (Exception e){ throw new Exception("Problem in IF statement");}
//                String secondCondition=this.parseBracket(br.readLine());
                Attribute second=this.parseBracket(br.readLine()).get(0);      
                //Attribute first = this.dataToAttribute("First",firstCondition);
                //Attribute second = this.dataToAttribute("Second",secondCondition);
                String trueResult=br.readLine();
                String falseResult=br.readLine();
                if (operator.equals("=")) {
                    String firstParameter;
                    String secondParameter;
                    if (first.getType().equals("Text")) firstParameter=first.getData();else firstParameter=Integer.toString(first.getintData());              
                    if (second.getType().equals("Text")) secondParameter=second.getData();else secondParameter=Integer.toString(second.getintData());
                    if (firstParameter.equals(secondParameter)) {
                        try {br=setReader(filePath, trueResult);} 
                        catch (Exception e) {throw new IOException("Problem in if block " + e.toString());}
                    } else {
                        try {br=setReader(filePath, falseResult);} 
                        catch (Exception e) {throw new IOException("Problem in if block " + e.toString());}
                    }
                    my_blazes=TrailBlazes.CONTINUE;
                    return; //We do not have to read the last --- until we implement true returning branches
                }
            }
            
           if (command.equals("GOTO")){
            br=setReader(filePath, this.parseBracket(br.readLine()).get(0).getData()); //Look at that - no error checking whatsoever.
            my_blazes=TrailBlazes.CONTINUE;
            return;
           }
            
        } catch(Exception e) { //If this happens, we may have a simple end of file.
            my_blazes=TrailBlazes.INVALID;
            myUser.send_message("Unkown Exception has occured "+ e.toString(),"Server",Message.CHAT);
            return;
        }
    }



}
