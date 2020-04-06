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
            //if (st.equals("---")==false){ //Malformed
            if (st==null) {
                //myUser.send_message("End of file","Server",Message.CHAT);
                my_blazes=TrailBlazes.INVALID;
                return;
            }
    
             if (st.equals("---")==false) {
                myUser.send_message("malformed","Server",Message.CHAT);
                my_blazes=TrailBlazes.INVALID;
                return;
            }
                       
                //Errors: More than one * will screw up the Syntax!  It should be any number!
                st=this.br.readLine();
                //buffer=sc.nextLine().replaceAll("(?<=\\*)(.*?)(?=\\*)","this was the command");

                //This SHOULD be the next command at this point.
                Pattern findCommand;
                findCommand=Pattern.compile("\\*+?(.+)\\*+");
                Matcher extractCommand = findCommand.matcher(st);
                if (extractCommand.matches()==false) {my_blazes=TrailBlazes.INVALID;return;}            
                String command = extractCommand.group(1);
                /* Start of the PRINT command block */                
                /*I really can't believe this worked the first time I built it.*/
                if (command.equals("PRINT")) { //The User 
                    //myUser.send_message("Entering the print subroutine","Server",Message.CHAT);
                    st=this.br.readLine();
                    if (this.br.readLine().equals("---")) { //Send the message
                        int count=-1;
                        int closecount=-1;
                        boolean open=false; 
                        int copyHead=0;
                        String Result="";                       
                        for (int i = 0; i < st.length(); i++) {
                            if (st.charAt(i) == '[') {
                                if (open) throw new IOException("Bracket read error.");
                                count=i;
                                open=true;
                            }
                            if (st.charAt(i) == ']') {
                                
                                if (open == false) throw new IOException("Bracket read error.");                                
                                open=false; //Why didn't \" WORK ?!
                                //if (closecount==-1)closecount++;                                
                                Result+=st.substring(copyHead,count);
                                //Result+="INSERT DATA RETREIVAL HERE";
                                //myUser.send_message("Retrieving Attributes: *"+st.substring(count+1,i)+"*","Server",Message.CHAT);
                                Attribute userAttribute;
                                userAttribute = myUser.returnAttribute(st.substring(count+1,i));
                                if (userAttribute==null) Result+="Attribute not found";
                                else {
                                    Result+=userAttribute.getData();
                                }
                                copyHead=i+1;
                                closecount=i;
                            }
                        }
                        if (open) if (closecount==-1) throw new IOException("Bracket read error - brackets opened but not closed");                        
                        //if (count==-1) {
                            //myUser.send_message(st,"Server",Message.CHAT);
                            if (copyHead!=st.length()) Result+=st.substring(copyHead,st.length());
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
                        try {
                        //myUser.send_message("Trying to parse that int on + " + attrData,"Server",Message.CHAT);
                            result = Integer.parseInt(attrData);
                            myUser.setAttribute("Number",attrName.substring(0,attrName.length()),null,result,result); //Type conversions
                        } catch (NumberFormatException nfe) {
                            myUser.setAttribute("Text",attrName.substring(0,attrName.length()),attrData,0,0);
                        }
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
                             for (int i = 0; i < attrData.length(); i++) {
                                if (attrData.charAt(i) == '&') count++; //Why didn't \" WORK ?!
                             }
                             if (count != 2) throw new IOException("Bad Parsing inside of an attribute statement");
                             attrData=attrData.substring(1,attrData.length()-1);
                             try {
                                int result;
                                result = Integer.parseInt(attrData);
                             } catch (NumberFormatException nfe) {
                                numeric=false;
                             }
                             Options.add(attrData);
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
