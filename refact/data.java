package trails2;

//This class holds all of the variables created by the user.
//It is designed so that each variable can be an array (a list) or a single variable, by default.

class data{

    String stringValue;
    double numericValue;
    String myName="";
    boolean isNumeric;
    
    //HashMap<String,String> stringValues = new HashMap<String,String>();
    //HashMap<String,Double> numericValues = new HashMap<String,Double>();
    //Seriously considered doing that, but we would have to implement so sort of an ordered hash in order to do so.  
    
    data (String name, String data){
        this.myName=name;
        changeData(data);
    }
    
    public String varName(){
        return myName;
    }
    
    public String getStringValue(){
        if (!(isNumeric)){
            return stringValue;
        }
        return Double.toString(numericValue);
    }
    
    public boolean isNumeric() {
        return this.isNumeric;
    }
    
    public String getName() {
        return myName;
    }
    
    public void changeData(String data){
        
        if (data != null) try {
            double d = Double.parseDouble(data);
            this.numericValue=d;
            isNumeric=true;
            this.stringValue=null;
        } catch (NumberFormatException nfe) {
            isNumeric=false;
            this.stringValue=data;
        }
    }
}
