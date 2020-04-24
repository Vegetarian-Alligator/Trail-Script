package mypackage;
import javax.websocket.Session;
import java.io.*;
import java.util.*;

public class User { // implements Runnable {
    private String name=null;
    private Session session; //Doubt we need the static keyword, I will doublecheck
    public boolean in_public_chat;
    private boolean ask;
    String Waiting;
    private TrailBlazer myTrail;
    List<Attribute> myAttributes=new ArrayList<Attribute>();
    List<String> attrChoice;
    private String chatName;
    boolean AI=false;
    private Park myPark;
    private String lastQuestion;
    private boolean lastNumeric;
    private List<String> lastOptions;

    User(String name, Session my_session, Park myPark)
    {
        this.myPark=myPark;
        this.AI=false;
        this.name=name;
        this.chatName=name;
        ask=false;
        Waiting=null;
        this.session=my_session;
        this.in_public_chat=true;
        String rootdir = "/var/lib/tomcat9/webapps/myapp-0.1-dev/";
        myTrail=new TrailBlazer(rootdir,this,null);
    }

    public void setChatName(String myNewChatName) {
        this.chatName=myNewChatName;
    }

    public String getChatName() {
        return this.chatName;
    }

    public void Refresh() {
        myTrail.Parse();
    }

    public boolean input(String Type, String message) {
        if (Type.equals("CHAT"))
            {
                if (this.in_public_chat==true) return true;else return false;
            }
        
        if (ask) { //Type should equal "COMMAND" at this point; this "ask" statement must be removed in order for unprompted commands to happen
            if (myPark.uniqueAttributeAllowed(Waiting,message)) {
                if (attrChoice==null) {
                    try {
                        this.setAttribute("Numeric",Waiting,null,Integer.parseInt(message),0);
                        //this.send_message(" Numeric message sent: "+Waiting+":"+message,"Server",Message.CHAT);
                    } catch (Exception e) {
                        this.setAttribute("Text",Waiting,message,0,0);
                        //this.send_message(" Text message sent " + Waiting+":"+message,"Server",Message.CHAT);
                    }
                    Waiting=null;
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
                            this.setAttribute("Numeric",Waiting,null,Integer.parseInt(message),0);

                        } catch (Exception e) {
                            this.setAttribute("Text",Waiting,message,0,0);
                        }
                        Waiting=null;
                        ask=false;
                        attrChoice=null;
                        //this.send_message("Thank you for setting the attribute","Server",Message.CHAT);
                        myTrail.my_blazes=TrailBlazes.CONTINUE;
                        return false;
                    }
                }
                this.send_message("Answer not Listed","Server",Message.CHAT);
                return false;
            } else {
                this.send_message("It appears that someone else has already chosen that.  Can you try a different one?","Server",Message.CHAT);
                this.askQuestion(Waiting,lastQuestion,lastNumeric,lastOptions);
                return false;
            }
        }
    return false; //We shouldn't really get to this statement UNLESS someone sends a bad chat command "type" that we do not recognize
    }

    public boolean askQuestion(String attrName,String Question,boolean isNumeric,List<String> Options) {
        if (!AI) {
            //this.send_message("We are in the ask question subroutine","Server",Message.CHAT);
            ask=true;
            this.lastQuestion=Question;
            this.lastNumeric=isNumeric;
            this.lastOptions= Options;
            Waiting=attrName;
            if (Options==null) {
                this.send_message(Question,"Server",Message.COMMAND);
                return ask;
            }
            //If we reach this point in the code, we have several options to choose from.
            //For the poruporses of this initial test, we do not enumerate options to the user.  This
            //Should really be done with a "Print" Command, and collection of the data should be silent

            this.attrChoice=Options;
            this.send_message(Question,"Server",Message.COMMAND);
            return ask;
        }
        return false;
    }

    public boolean get_public_chat() {
        return in_public_chat;
    }

    public Attribute returnAttribute(String Name) {
        for (Attribute key : myAttributes) {
            if (key.getName().equals(Name)) return key;
        }
        return null;
    }
//                            myUser.setAttribute("Text",attrName,attrData,0,0);
    public void setAttribute(String Type, String Name, String Data, int intData, float floatdata) {
        Attribute manipular;
        manipular=null; //Since it may not be set in the case of an exception
        try {
            manipular=returnAttribute(Name);
        } catch(Exception e) {
            manipular=null;
        }

        //manipular=null;
        if (manipular==null) {
            myAttributes.add(new Attribute(Type, Name, Data, floatdata, intData));
            //this.send_message("Adding new Attribute: *" + Name+"*","Server",Message.CHAT);
        } else {
            manipular.setAttributes(Type, Name, Data, floatdata, intData);
        }
    }

    public void setAttribute(Attribute newAttribute) { //For thread safety: this MUST be in a synchronized statement!
        Attribute manipular;
        manipular=null; //Since it may not be set in the case of an exception
        try {
            manipular=returnAttribute(newAttribute.getName());
        } catch(Exception e) {
            manipular=null;
        }

        //manipular=null;
        if (manipular==null) {
            myAttributes.add(newAttribute);
        } else {
            myAttributes.remove(manipular);
            myAttributes.add(newAttribute);
        }
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
        } catch (Exception e) {
            try{this.send_message("EXCEPTION in send_message: " + e.toString(),"Server",Message.CHAT);}catch (Exception ee) {}
        }
    }

    public String get_name() {
        return this.name;
    }
}
