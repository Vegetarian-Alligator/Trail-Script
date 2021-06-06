package trails2;
import java.util.ArrayList;

//This class holds all of the variables created by the user.
//It is designed so that each variable can be an array (a list) or a single variable, by default.

class data{

    //String stringValue;
    //double numericValue;
    String myName="";
    //boolean isNumeric;
    ArrayList<String> stringVal = new ArrayList<String>();
    ArrayList<double> numericVal = new ArrayList<double>();
    ArrayList<boolean> isnumeric = new ArrayList<boolean>();
    data (String name, String data){
        this.myName=name;
        changeData(data);
    }
    
    public String varName(){
        return myName;
    }
    
    public String getStringValue(){
        if (!(isNumeric.get(0)){
            return stringVal.get(0);
        }
        return Double.toString(numericVal.get(0));
    }

    public String getStringValue(int index){
        try {
            if (!(isNumeric.get(index)){
                return stringVal.get(index);
            }
            return Double.toString(numericVal.get(index));
        } catch (Exception e){
            System.out.println("Fatal Error. Array likely out of bounds.");
            System.exit(1);
        }

    
    public boolean isNumeric() {
        return this.isNumeric;
    }
    
    public String getName() {
        return myName;
    }
    

    public void changeData(String data, int index) {

    }

    public void changeData(String data){
        if (data != null) try {
            double d = Double.parseDouble(data);
            if(numericVal.length()!=0) this.numericVal.get(0)=d;
            else this.numericVal.add(d);
            isNumeric=true;
            this.stringValue=null;
        } catch (NumberFormatException nfe) {
            isNumeric=false;
            if (stringVal.length()!=0) this.stringVal.get(0)=data;
            else stringVal.add(data);
        }
    }
}
