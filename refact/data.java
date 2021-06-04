package trails2;

//This class holds all of the variables created by the user.

class data{
    String stringValue="";
    double numericValue;
    short type=-1;
    String myName="";
    
    data (String name, String data){
        this.myName=name;
        changeData(data);
    }
    
    public String varName(){
        return myName;
    }
    
    public void changeData(String data){
        try {
            double d = Double.parseDouble(data);
            this.numericValue=d;
        } catch (NumberFormatException nfe) {
            this.stringValue=data;
        }
    }
}
