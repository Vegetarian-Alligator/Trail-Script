package trails2;
import java.util.ArrayList;
import java.text.*;

//This class holds all of the variables created by the user.
//It is designed so that each variable can be an array (a list) or a single variable, by default.

class data{

    //String stringValue;
    //double numericValue;
    String myName="";
    //boolean isNumeric;
    ArrayList<String> stringVal = new ArrayList<String>();
    ArrayList<Double> numericVal = new ArrayList<Double>();
    ArrayList<Boolean> isNumeric = new ArrayList<Boolean>();
    
    data (String name, String data){
        this.myName=name;
        changeData(data);
    }
    
    public int getSize(){
        return stringVal.size() + numericVal.size();
    }
    
    public String varName(){
        return myName;
    }
    
    public String getStringValue(){
        if (isNumeric.get(0)){
            DecimalFormat format = new DecimalFormat("0.#");
            return format.format(numericVal.get(0));
        }
        return stringVal.get(0);
    }

    public String getStringValue(int index){
        try {
            if (!(isNumeric.get(index))){
                return stringVal.get(index);
            }
            DecimalFormat format = new DecimalFormat("0.#");
            return format.format(numericVal.get(index));
        } catch (Exception e){
            System.out.println("Fatal Error. Array likely out of bounds." + e);
            System.exit(1);
        }
        return null; //Should never execute; it should always be one of the top two returns?
    }

    public boolean isNumeric() {
        return this.isNumeric.get(0);
    }
    
    public String getName() {
        return myName;
    }
    
    public void changeData(String data, int index) {
        
        try{
            if (data != null) if (!(index > stringVal.size())) {
                if(stringVal.size() > index) {this.stringVal.set(index,data);} //this.numericVal.get(0)= d;
                else {this.stringVal.add(data);}
            } else throw new Exception("You can't use a number in an array that is bigger than the number of items in the array.  Index was" + index + " size is " + stringVal.size());
            if (isNumeric.size()> index) isNumeric.set(index,false);
            else isNumeric.add(false);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("The error was in changeData(data,index): " + e);
            System.exit(1);
        }
        
    }

    public void changeData(String data){
    
        if (data != null) {
        
            //isNumeric.get(0)=false;
            //isNumeric.set(0,false);
            if (isNumeric.size()!= 0) isNumeric.set(0,false);
            else isNumeric.add(false);
            if (stringVal.size()!=0) this.stringVal.set(0,data);//this.stringVal.get(0)=data;
            else stringVal.add(data);
        }   
    }
}
