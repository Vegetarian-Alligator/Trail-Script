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
        public static Park myPark=null;

    @OnOpen
    public void onOpen(Session session) throws IOException{
        System.out.println("Open Connection ..." + session.getId());
        session.getBasicRemote().sendText(SerializeJSON.Serialize("chat","Welcome to the server"));
        if (myPark==null) { // Create the whole world on the connection of the first user
            myPark=new Park("Trailscript game");
            session.getBasicRemote().sendText(SerializeJSON.Serialize("chat","We have created a new Park."));
        } 
        myPark.addUser(session.getId(),true,session);        
        return;
    }
     
    @OnClose
    public void onClose(Session session){
        System.out.println("Close Connection ...");
        if (myPark != null) myPark.removeUser(session.getId());
        else SerializeJSON.addLog("It is unkown why this is disconnected.");
        
    }
     //SetTExt vs SendText?
    @OnMessage
    public void onMessage(Session session, String message) throws IOException{
        //entityInteract
        myPark.entityInteract(message,session);
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

