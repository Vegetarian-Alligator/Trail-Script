package mypackage;
import javax.websocket.Session;
import java.io.*;
import java.util.*;

class Group{
private int size;
private List<Attribute> myAttributes=new ArrayList<Attribute>(); //Does private vs public effect return types?

Group(){
    this.size=0;
}
 
public int getCount(){
    return myAttributes.size();
} 

public Attribute returnAttribute(int count){
    if (count < 0  || count >= myAttributes.size() || myAttributes.size()==0) return null;
        int i =0;
        for (Attribute key : myAttributes) {
            if (i==count) return key;
            i++;
        }
        return null;
}

public void removeAttribute(int count){ //Removes attributes based on the order they were put in
		int i=0;
			for (i=0;i<myAttributes.size();i++){
				if (myAttributes.get(i)!=null) {if (i==count)myAttributes.remove(i);return;}
			}
}

public void clearAttributes(){ //This function should probably be synchronized
    myAttributes.clear();
    this.size=0;
}

 public Attribute returnAttribute(String Name) {
        for (Attribute key : myAttributes) {
            if (key.getName().equals(Name)) return key;
        }
        return null;
    }
    
	 public void removeAttribute(String name){ //This should be a boolean to reveal failure conditions.
		int i=0;
			for (i=0;i<myAttributes.size();i++){
				if (myAttributes.get(i)!=null) if (myAttributes.get(i).getName().equals(name))myAttributes.remove(i);
			}
	 }    
    
//                            myUser.setAttribute("Text",attrName,attrData,0,0);
    public void setAttribute(String Type, String Name, String Data, int intData, float floatdata) {
        Attribute manipular;
        manipular=null; //Since it may not be set in the case of an exception
        try {
            manipular=returnAttribute(Name);
        } catch(Exception e) {
            manipular=null;
        }

        //manipular=null;
        if (manipular==null) {
            myAttributes.add(new Attribute(Type, Name, Data, floatdata, intData));
        } else {
            manipular.setAttributes(Type, Name, Data, floatdata, intData);
        }
    }

    public void setAttribute(Attribute newAttribute) { //For thread safety: this MUST be in a synchronized statement!
        Attribute manipular;
        manipular=null; //Since it may not be set in the case of an exception
        try {
            manipular=returnAttribute(newAttribute.getName());
        } catch(Exception e) {
            manipular=null;
        }

        //manipular=null;
        if (manipular==null) {
            myAttributes.add(newAttribute);
        } else {
//            myAttributes.remove(manipular); //Does this really work?
            this.removeAttribute(manipular.getName());
            myAttributes.add(newAttribute);
        }
    }

}
