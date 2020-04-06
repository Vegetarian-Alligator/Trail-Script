package mypackage;

public class Attribute {
    private String myType; //Not Used
    private String myName;
    public String myData;
    float myfloatData;
    public int myintData;
        
    Attribute(String Type,String Name, String Data, float floatdata, int intData) {
        this.setAttributes(Type, Name, Data, floatdata, intData);
    }

    public void setAttributes(String Type,String Name, String Data, float floatdata, int intData) { //Not reason to return a class as with Attributes
        this.myType=Type;
        this.myData=Data;
        this.myfloatData=floatdata;
        this.myintData=intData;
        this.myName=Name;
    }

     public String getName() {
        return this.myName;
        //return Integer.toString(myintData);
    }

    public String getData () {
       return this.myData;
    }
}
