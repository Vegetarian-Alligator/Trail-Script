package trails2;
//For the ask subroutine
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
//

//For askMultiple
import java.util.ArrayList;
//

import java.io.*;

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
            System.out.println("consoleOutput: Error getting user input.");
            System.exit(1);
        }
        return ""; //Should be unreachable
    }
    
    public int askMultiple(ArrayList<String> myOptions){
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int option=1;
        if (myOptions.size() == 0){
            System.out.println("consoleOutput: No items in list.");
            System.exit(1);
        }
        
        for (int i=0; i<myOptions.size(); i++) {
            System.out.println(option + "). " + myOptions.get(i));
        }
        String rawAnswer="";
        int answer=-1;
        do{
            System.out.print("(Enter your answer here): " );
            try{rawAnswer=reader.readLine();}catch(Exception e){e.printStackTrace();System.exit(1);}
            try{answer=Integer.parseInt(rawAnswer);}catch(Exception e){System.out.println("");continue;};
            
        }while(answer < 0 || answer > myOptions.size());
        //https://stackoverflow.com/questions/35370811/java-how-to-set-input-range-validation-for-user-input
        
        return answer;
    }
}

//Phouebus Cartel
