package mypackage;
import java.io.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import java.util.*;
/*
This is a file for reading "trail" scripts.  It will systematically iterate through the game at the request of whatever is calling it.
The functions returned will suggest an action to the caller.  These actions can be:

CONTINUE: Ready to advance to the next object
INQUIRE: I don't have 

*/
/*
final class dataReturn
{
   public String myData;
   public int    myInt;
   public float  myFloat;
   public boolean stringData; 
   public dataReturn(String myData, int, myInt, float myFloat,boolean stringData){
        this.myD
   }
}
*/

public class TrailBlazer {
protected TrailBlazes my_blazes; //The status of this trail
protected String Command;
private BufferedReader br;
User myUser;
    TrailBlazer (String startPath, User calling_user){
        myUser=calling_user;
        //myUser.send_message("Testing the new Trailblazer system","Server",Message.CHAT);
         
     
        try {    
            this.br = new BufferedReader(new FileReader(startPath+"trailhead.trail")); 
            my_blazes=TrailBlazes.CONTINUE;
            this.Parse();

        } catch (IOException e) { //NoSuchElementException
            //myUser.send_message("IO Exception has occured "+ e.toString(),"Server",Message.CHAT);
        }
        //while ((st = br.readLine()) != null) 

         
    }

    public void Parse(){
        while (my_blazes==TrailBlazes.CONTINUE) {
            this.readNextCommand();
        }
        //try{myUser.send_message("IT seems we are done parsing with my_blazes="+my_blazes,"Server",Message.CHAT);
        //}catch (IOException ee) {}
    }

    private String getAttribute() {
        return null;
    }

    private String parseBracket(String parseObject) throws Exception { //Can only be called from readNextCommand, therefore throwing is acceptable
        /*I really can't believe this worked the first time I built it - see file "cool"*/        

        String Result="";
        String Name;//=""; //Initilization Unneeded
        int look=0;// = parseObject.indexOf('[');
        if (parseObject.indexOf('[') != -1) 
            do {
                try {                
                try{Result+=parseObject.substring(look,parseObject.indexOf('['));} catch (Exception e){break;} //Because the solution will be behind the start, exception is thrown              
                Name = parseObject.substring(parseObject.indexOf('[',look)+1,parseObject.indexOf(']',look));
                } //"Consuming" the string might be faster, somehow?
                catch (Exception e){
                    throw new IOException(e.toString()+"This occured inside of ParseBracket"); //But what happens if only ONE of these is -1?  In that case, I guess it gets ignored
                    //break; //Ignores certain cases of mismatched tags
                }
                Attribute userAttribute = myUser.returnAttribute(Name);
                if (userAttribute==null) Result+="Attribute not found";
                else {
                    Result+=userAttribute.getData();
                }
                look=parseObject.indexOf(']',look)+1; //Plus one to keep from finding the same one over and over/
            }while (look!=0);
        
            
//            Result+=parseObject.substring(parseObject.length()-parseObject.,parseObject.length());
              Result+=parseObject.substring(look,parseObject.length());
              return Result;   
        }

    private String extractTag(String parseObject, char startTag, char endTag) throws Exception{         
        //findCommand=Pattern.compile("\\*+?(.+)\\*+");       
        int i=0;
        if (parseObject.charAt(i)!=startTag) throw new IOException("Something not a tag was treated as a tag");
        for (i=1;i<parseObject.length();i++) {
            if (parseObject.charAt(i)==startTag) if (i < (parseObject.length()-1) && startTag != endTag) throw new IOException("Extranous tag: " + startTag);
            if (parseObject.charAt(i)==endTag) if (i < (parseObject.length()-1)) throw new IOException("Extranous tag: " + endTag);
        }
        return parseObject.substring(1,parseObject.length()-1);
    }

    private Attribute dataToAttribute(String attrName, String parseObject) {
        int result;        
        try {
             //myUser.send_message("Trying to parse that int on + " + attrData,"Server",Message.CHAT);
             result = Integer.parseInt(parseObject);
             return new Attribute("Number",attrName.substring(0,attrName.length()),null,result,result); //Type conversions
        } catch (NumberFormatException nfe) {
             return new Attribute("Text",attrName.substring(0,attrName.length()),parseObject,0,0);
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
                        String Result=this.parseBracket(st);                        
                        myUser.send_message(Result,"Server",Message.CHAT); //This line of code Actually send the message!
                        my_blazes=TrailBlazes.CONTINUE;
                        return;
                        }
                   }
                        
                        /*The following section needs to return the attribute value.. then print that
                    } else throw new IOException("Error somewhere in the printing file.");//{my_blazes=TrailBlazes.INVALID;return;}
                }
                
                /*Start of the SOCIAL command block*/
                //This should accept PHYSICAL, CHAT, and SOLO, meaning no chat functionality
                if (command.equals("SOCIAL")){
                    st=br.readLine();
                    
                    if (this.br.readLine().equals("---")) {
                        if (st.equals("CHAT"))myUser.in_public_chat=true;
                        if (st.equals("SOLO"))myUser.in_public_chat=false;
                        my_blazes=TrailBlazes.CONTINUE;
                    }
                }

                if (command.equals("GET_ATTRIBUTE")) {
                    //myUser.send_message("Entering Get Attribute","Server", Message.CHAT);
                    String attrName;
                    String attrData;                    
                    attrName=br.readLine();
                    int count=0;
                    int closecount=0;
                    for (int i = 0; i < attrName.length(); i++) {
                        if (attrName.charAt(i) == '<') count++;
                        if (attrName.charAt(i) == '>') closecount++;
                    }
                    //myUser.send_message("attrName: " + attrName,"Server",Message.CHAT);
                    if (closecount!=count) System.out.println("Parsing Error!  Mismatched brackets");
                    if (closecount==0){ //We just assign the variable
                        //myUser.send_message("Reading line...","Server",Message.CHAT);                        
                        attrData=br.readLine(); 
                        //myUser.send_message("attrData: " + attrData,"Server",Message.CHAT);
                        int result;                        
                    /*
                    try {
                        //myUser.send_message("Trying to parse that int on + " + attrData,"Server",Message.CHAT);
                            result = Integer.parseInt(attrData);
                            myUser.setAttribute("Number",attrName.substring(0,attrName.length()),null,result,result); //Type conversions
                        } catch (NumberFormatException nfe) {
                            myUser.setAttribute("Text",attrName.substring(0,attrName.length()),attrData,0,0);
                        }
                     */
                        Attribute createAttr;
                        createAttr=this.dataToAttribute(attrName,attrData);
                        myUser.setAttribute(createAttr);
                        String Choice=br.readLine();
                        if (Choice.equals("---")){
                            my_blazes=TrailBlazes.CONTINUE;
                            return;
                        } else throw new IOException();
                    }
                
                if (closecount > 1) throw new IOException("Too many brackets");//System.out.println("Parsing Error: Too many Brackets");
                if (closecount == 1) { //Get the variable from the user - should probably use regex to make sure the brackets make sense, we don't yet
                    attrData=br.readLine();
                    if (attrData.equals("---")) {
                        my_blazes=TrailBlazes.INQUIRE;
                        myUser.askQuestion(attrName.substring(1,attrName.length()-1),"Please enter a value for " + attrName.substring(1,attrName.length()-1),false,null);
                        return;
                    } else {
                         boolean numeric=true;
                         //myUser.send_message("We are creating the list of multiple options: " + attrData,"Server",Message.CHAT);
                         List<String> Options=new ArrayList<String>();                           
                         while (attrData.equals("---")==false) {
                             count=0;
                             /*
                             for (int i = 0; i < attrData.length(); i++) {
                                if (attrData.charAt(i) == '&') count++; //Why didn't \" WORK ?!
                             }
                             if (count != 2) throw new IOException("Bad Parsing inside of an attribute statement");
                             attrData=attrData.substring(1,attrData.length()-1);
                             */
                             attrData=this.extractTag(attrData,'&','&');
                            /*
                             try {
                                int result;
                                result = Integer.parseInt(attrData);
                             } catch (NumberFormatException nfe) {
                                numeric=false;
                             }
                             Options.add(attrData);
                             */
                             Attribute createOption = this.dataToAttribute("noname",attrData);
                             Options.add(createOption.getData());
                             if (attrData==null) myUser.send_message("Null result error.","Server",Message.CHAT);
                             attrData=br.readLine();
                         }
                         if (numeric==false) myUser.askQuestion(attrName.substring(1,attrName.length()-1),"Please enter an answer for " + attrName.substring(1,attrName.length()-1),false,Options);
                         else myUser.askQuestion(attrName.substring(1,attrName.length()-1),"Please enter an answer for " + attrName.substring(1,attrName.length()-1),true,Options);
                        
                         my_blazes=TrailBlazes.INQUIRE;
                         return;
                    }
                }
            }
        }catch(Exception e){ //If this happens, we may have a simple end of file.
                        
            my_blazes=TrailBlazes.INVALID;
            
            myUser.send_message("Unkown Exception has occured "+ e.toString(),"Server",Message.CHAT);
            
            return;
            }
    }


    
}
