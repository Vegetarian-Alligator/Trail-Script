package trails2;

import java.io.*;
import java.util.*;

//private class variable{
    
//}
    
class trails {
    
    public static void main(String[] args)throws FileNotFoundException{
        System.out.println("The system is booting up.  Currently, only console output is supported.");
        outputInterface consoleInt = new consoleOutput();
        Thread consolePlayer = new entity(consoleInt);
        consolePlayer.start();
    }
}
