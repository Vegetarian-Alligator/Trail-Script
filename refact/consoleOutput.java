package trails2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class consoleOutput extends outputInterface{
    public boolean print(String text){
        System.out.println(text);
        return true;
    }
    
    public String ask() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println();
        System.out.print("(Enter your answer here): " );
        try {return reader.readLine();}
        catch(Exception e){
            System.out.println("Error getting user input.");
            System.exit(1);
        }
        return ""; //Should be unreachable
    }
}
