package trails2;

//This class holds all of the variables created by the user.

class data{
    String stringValue;
    double numericValue;
    String myName="";
    boolean isNumeric;
    
    
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
