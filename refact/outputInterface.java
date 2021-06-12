package trails2;
import java.util.ArrayList;

public abstract class outputInterface{
    public abstract boolean print(String text); //All output interfaces must have a way to print text to the screen 
    public abstract String ask();
    public abstract int askMultiple(ArrayList<String> myOptions);
}
