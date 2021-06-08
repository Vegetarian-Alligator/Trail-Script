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
            System.out.println("Fatal Error. Array likely out of bounds.");
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
        if (data != null) try {
            Double d = Double.parseDouble(data);
            if (!(index > stringVal.size())){
                if(numericVal.size() > index) this.numericVal.set(index,d); //this.numericVal.get(0)= d;
                else this.numericVal.add(d);
            }
            else throw new Exception("You can use a number in an array that is bigger than the number of items in the array.");
            //isNumeric.get(0)=true;
            if (isNumeric.size()>index) isNumeric.set(index,true);
            else isNumeric.add(true);
            this.stringVal=null;
        } catch (NumberFormatException nfe) { //Keeps from catching the other random exceptions in the above
            //isNumeric.get(0)=false;
            //isNumeric.set(0,false);
            //System.out.println("In " + myName + "Size of the array is " + stringVal.size() +  "the value of index is  " + index);
            if (!(index > stringVal.size())) {
                if(stringVal.size() > index) {this.stringVal.set(index,data);} //this.numericVal.get(0)= d;
                else {this.stringVal.add(data);}
            }
            else throw new Exception("You can't use a number in an array that is bigger than the number of items in the array.  Index was" + index + " size is " + stringVal.size());
            if (isNumeric.size()> index) isNumeric.set(index,false);
            else isNumeric.add(false);
        }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("The error was in changeData(data,index): " + e);
            System.exit(1);
        }
    }

    public void changeData(String data){
        if (data != null) try {
            Double d = Double.parseDouble(data);
            if(numericVal.size()!=0) this.numericVal.set(0,d); //this.numericVal.get(0)= d;
            else this.numericVal.add(d);
            //isNumeric.get(0)=true;
            if (isNumeric.size()!= 0) isNumeric.set(0,true);
            else isNumeric.add(true);
            this.stringVal=null;
        } catch (NumberFormatException nfe) {
            //isNumeric.get(0)=false;
            //isNumeric.set(0,false);
            if (isNumeric.size()!= 0) isNumeric.set(0,false);
            else isNumeric.add(false);
            if (stringVal.size()!=0) this.stringVal.set(0,data);//this.stringVal.get(0)=data;
            else stringVal.add(data);
        }
    }
}
