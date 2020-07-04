package mypackage;

public class Attribute implements Cloneable {
    private String myType;
    private String myName;
    public String myData;
    float myfloatData;
    public int myintData;
    public boolean isNumeric;    //Not Used
    boolean floaty;
    public String tag=null;
    //Tag will be used really only in the attribute function, for example
    //"not found" tell the function that the string to be used was not found
    //"retrieved" - meaning we got this from an attribute
    //"Typed" String literal provided by the user

    //I might want to initilize this, but its going to be null
    //Except in trailblazer

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

    public String getType() {
        return this.myType;
    }

    public String getData () {
         return this.myData;
    }

    public int getintData() {
        return this.myintData;
    }

    public void setName(String Name) { //This function only exists to make it easier to verbs to transfer names from file to file
        this.myName=Name;
    }

    public Object clone() throws
                       CloneNotSupportedException 
        { 
            return super.clone(); 
        } 
}
