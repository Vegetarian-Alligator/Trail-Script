package mypackage;
 
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.*; 
//import java.util.List;
//import java.util.Map;
import java.io.IOException;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import org.json.simple.JSONObject;
//import org.json;
//A few of the above are not needed but oh well, enables wrter.println

@ServerEndpoint("/websocketendpoint")
public class WsServer extends HttpServlet{
//     List<String> user_list =new LinkedList<String>();
        //public static final Map<String, Session> sessions = new ConcurrentHashMap<>();
        public static final Map<String, User> Users = new ConcurrentHashMap<>();

        
    @OnOpen
    public void onOpen(Session session) throws IOException{
        System.out.println("Open Connection ..." + session.getId());
        //session.getBasicRemote().sendText(SerializeJSON.Serialize("chat","Welcome to the server, please enter your name"));
//        session.getBasicRemote().sendText("Please enter your name!");
        //user_list.add(session.getId());
        //TO-DO: Make sure the user is not already in the session, to prevent impersonation of other uses based on good guesses
//        User new_user = new User(
//        sessions.put(session.getId(),session);
    }
     
    @OnClose
    public void onClose(Session session){
        System.out.println("Close Connection ...");
        Users.remove(session.getId());
    }
     //SetTExt vs SendText?
    @OnMessage
    public void onMessage(Session session, String message) throws IOException{
        List<String> Input = new ArrayList<String>();
        Input=SerializeJSON.deserializeCommand(message);
        //session.getBasicRemote().sendText(SerializeJSON.Serialize("chat","message recieved was: " + message + "The type was: " + Input.get(0) + " The data was:" + Input.get(1)));
        message=SanatizeHTML.SanatizedHTML(Input.get(1)); // was message
//        message.replaceAll("[<].*[>]","tag"); //Very basic way to sanitize html - delete anything between <->.  Merciless.
        //System.out.println("Message from the client, deSerialized: " + Input.get(1)); // was message
        User this_user = Users.get(session.getId());

        if (this_user==null) {
            session.getBasicRemote().sendText(SerializeJSON.Serialize("chat","Welcome to the server"));
            //User a_user=new User(Input.get(1), session);// was message
            User a_user=new User("human",session);
            Users.put(session.getId(), a_user);
            return;
        }
           
            
        if (this_user.input(Input.get(0),Input.get(1)))
            for (String key : Users.keySet()) { 
                Users.get(key).send_message(Input.get(1),this_user.get_name(),Message.CHAT); //Was message
            } 
        this_user.Refresh();

        return;
    }
 
    @OnError
    public void onError(Throwable e){
        e.printStackTrace();
    }



public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        writer.println("<html>");
        writer.println("<head>");
        writer.println("<title>Sample Application Servlet Page</title>");
        writer.println("</head>");
        writer.println("<body bgcolor=white>");
        writer.println("<table border=\"0\">");
        writer.println("<tr>");
        writer.println("<td>");
        writer.println("<img src=\"images/tomcat.gif\">");
        writer.println("</td>");
        writer.println("<td>");
        writer.println("<h1>Sample Application Servlet</h1>");
        writer.println("This is the output of a file that SHOULD be ready to intercept web socket");
        writer.println("the Hello, World application.");
        writer.println("</td>");
        writer.println("</tr>");
        writer.println("</table>");
        writer.println("</body>");
        writer.println("</html>");
    }
 
};//        response.setContentType("text/html");
//        PrintWriter writer = response.getWriter();
//        writer.println("<html>");

