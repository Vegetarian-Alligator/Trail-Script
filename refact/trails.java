import java.io.*;
import java.util.*;

//private class variable{
    
//}
    
class trails {
    public static void main(String[] args)throws FileNotFoundException{
        parseTrails();
    }
    
    private static void parseTrails() throws FileNotFoundException{
        File trailFile = new File("C:\\Users\\Mike\\Documents\\trails\\trailhead.trail");
        Scanner trailReader = new Scanner(trailFile);
        String nextCommand="";
        if (trailReader.hasNextLine())  nextCommand=trailReader.nextLine();
        while (trailReader.hasNextLine()) {
            if (nextCommand.equals("print")){
                nextCommand=printTrails(trailReader);
                continue;
            }
            if (nextCommand != null) System.out.println("incorrect command given: " + nextCommand);
        }
        if (nextCommand != null) System.out.println("incorrect command given: " + nextCommand);
        else System.out.println("next command is " + nextCommand);
    }
    
    private static String printTrails(Scanner trailReader){
        System.out.println("entering print");
        short itemCount=0;
        String result="";
        String content="";
        while (trailReader.hasNextLine()){
            content=trailReader.nextLine();
            //System.out.println("Content is: " + content + " and the first character is " + (int)content.charAt(0) );
            if (content.charAt(0)==9 || content.substring(0,4).equals("    ")) { //This is not very reliable, encoding issues?
                result+=content.substring(1);
                itemCount+=1;
            }else{
                break;
            }
        }
        
        if (itemCount!=0) {
            System.out.println(result);
            System.out.println("returning " + content);
            return content;
        }
        
        System.out.println("Failure in printTrails, exiting now");
        System.exit(1);
        return null;
    }
}
