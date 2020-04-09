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

    User(String name, Session my_session)
    {
        this.name=name;
        this.chatName=name;
        ask=false;
        Waiting=null;
        this.session=my_session;
        this.in_public_chat=true;
        String rootdir = "/var/lib/tomcat9/webapps/myapp-0.1-dev/";
        myTrail=new TrailBlazer(rootdir,this);
        this.in_public_chat=true;
        myAttributes.add(new Attribute("Text", "Name", name, 0, 0));
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
        if (Type.equals("CHAT")) if (ask==false)
            {
                return true;
            }
        if (ask) {
            if (attrChoice==null) {
                try {
                    this.setAttribute("Numeric",Waiting,null,Integer.parseInt(message),0);
                    //this.send_message(" Numeric message sent: "+Waiting,"Server",Message.CHAT);
                } catch (Exception e) {
                    this.setAttribute("Text",Waiting,message,0,0);
                    //this.send_message(" Text message sent " + Waiting,"Server",Message.CHAT);
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
        }
        return true;
    }
    public boolean askQuestion(String attrName,String Question,boolean isNumeric,List<String> Options) {
        ask=true;
        Waiting=attrName;
        if (Options==null) {
//            this.send_message(Question,"Server",Message.CHAT);
            return ask;
        }
        //If we reach this point in the code, we have several options to choose from.
        //For the poruporses of this initial test, we do not enumerate options to the user.  This
        //Should really be done with a "Print" Command, and collection of the data should be silent

        this.attrChoice=Options;
        //this.send_message(Question,"Server",Message.CHAT);
        return ask;
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
                String Dispatch=new String();
                //SerializeJSON.Serialize("chat",sender +": " + message);
                this.session.getBasicRemote().sendText(SerializeJSON.Serialize("chat",sender +": " + message));
                return;

            }
        } catch (IOException e) {

        }
    }

    public String get_name() {
        return this.name;
    }

}
