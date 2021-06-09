package trails2;

import java.io.*;
import java.util.*;


class entity extends Thread{
    private String modifyName = "";
    private int lastIndex=-1;
    outputInterface myOut;
    LinkedList<data> myVars= new LinkedList<data>();
    int nest=1;
    
    entity(outputInterface myOut){
        this.myOut=myOut;
    }
    
    @Override
    public void run() {
        System.out.println("The new entity has loaded.");
        try {
            parseTrails();
        } catch (Exception e){
            e.printStackTrace();
            System.out.println(e);
        }
    }

    private boolean correctIndent(String content,int level){ //This should be modified to allow nested indentation, hence the useless parameter
       
       if (content.charAt(0)==9) return true;
       if (content.length() >= 4) if (content.substring(0,4).equals("    ")) return true;
       return false;
    }
    
    private void parseTrails() throws FileNotFoundException{
            File trailFile = new File("C:\\Users\\Mike\\Documents\\trails\\Trail-Script\\refact\\trailhead.trail");
            Scanner trailReader = new Scanner(trailFile);
            String nextCommand="";
            if (trailReader.hasNextLine())  nextCommand=trailReader.nextLine();
            while (trailReader.hasNextLine()) {
                if (nextCommand.equals("print")){
                    nextCommand=printTrails(trailReader);
                    continue;
                }
                
                if (nextCommand.equals("set")){
                    try{
                        setVariable(trailReader);
                    }catch (Exception e){
                        System.out.println("There was a problem trying to call set");
                    }
                    if (trailReader.hasNextLine()) nextCommand=trailReader.nextLine();
                    else nextCommand=null;
                    continue;
                }
                
                if (nextCommand.equals("ask")){
                    ask(trailReader);
                    if (trailReader.hasNextLine()) nextCommand=trailReader.nextLine();
                    else nextCommand=null;
                    continue;
                }
                if (nextCommand != null) System.out.println("incorrect command given: " + nextCommand);
            }
            if (nextCommand != null) System.out.println("incorrect command given: " + nextCommand);
            //else System.out.println("next command is " + nextCommand);
        }
        
        private void ask(Scanner trailReader){
        try {
            if (trailReader.hasNextLine()){
                String findVar=trailReader.nextLine();
                if (correctIndent(findVar,1)){
                    int index=-1;
                    index=arrayFinder(findVar);
                    findVar=finalizeValue(findVar);
                    String answer;
                    answer=myOut.ask();
                    data thisVar=pointToVariable(findVar.trim());
                    if (index==-1) {
                        thisVar.changeData(answer);
                    }else{
                        thisVar.changeData(answer,index);
                    }
                }
            }else throw new Exception("Error in ask function.");
        } catch (Exception e) {
            System.out.println("Error in ask function.");
        }
        }
        
        private void setVariable(Scanner trailReader){
            String variableName;
            String value;
            String error="";
            int index=-2;
            try {
                if (trailReader.hasNextLine()){
                    variableName=trailReader.nextLine();
                    //variableName=variableName.trim();
                    if (!(correctIndent(variableName,nest))) throw new Exception("Bad formatting in set tag");
                    variableName=finalizeValue(variableName); //This 
                    //System.out.println("Output from finalizeValue is \"" + variableName + "\"");
                    index=arrayFinder(variableName);
                    if (index > -1) variableName=modifyName;
                    //System.out.println("Name of the variable is: \"" + variableName + "\"");
                } else throw new Exception("document ended in set tag");
                
                if (trailReader.hasNextLine()){
                    value=trailReader.nextLine();
                    if (!(correctIndent(value,nest))) throw new Exception("Bad formatting in set tag");
                } else throw new Exception("document ended in set tag");
                //System.out.println("We are iterating over a variable.");
                data myVar=pointToVariable(variableName.trim());
                //System.out.println("The name of the variable is: " + myVar.getName() + " the value being added is " + value);
                if (index<0) myVar.changeData(value.trim());
                else myVar.changeData(value.trim(),index);
                //System.out.println("The name of the variable being accessed is: " + myVar.getName());
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("Exeception in setVariable " + e);
            }
        }
        
        private int arrayFinder(String variable){ //Bug - you can have a variable with no name at this point
            int open=-1;
            int close=-1;
            
            for (int i =0; i< variable.length(); i++){
                if (variable.charAt(i)=='('){
                    if (open==-1) open=i;
                    else {
                        System.out.println("More than one parens.  Multidimensional arrays may be supported in a future version.");
                        System.exit(1);
                    }
                }
            }
            
            for (int i =0; i< variable.length(); i++){
                if (variable.charAt(i)==')'){
                    if (close==-1) close=i;
                    else {
                        System.out.println("More than one parens.  Multidimensional arrays may be supported in a future version.");
                        System.exit(1);
                    }
                }
            }
            
            if (open>close){
                System.out.println("Parens out of order.");
                System.exit(1);
            }
            
            if ((open==-1 && close != -1) || (close==-1 && open !=-1 )){
                System.out.println("Mismatched parens");
                System.exit(1);
            }
            
            if (open == close && open ==-1 ) {
                return -1;
            }
            
            String target=variable.substring(open+1,close);
            target.trim();
            //System.out.println("Array target is: " + target);
            try {
                //System.out.println("The substring in question is: " + variable.substring(open,close+1));
                modifyName=variable.replace(variable.substring(open,close+1),"");
                modifyName=modifyName.trim();
                lastIndex=Integer.parseInt(target);
                //System.out.println("I have change variable to \"" + modifyName + "\"");
                return Integer.parseInt(target);
            }catch (NumberFormatException e){
                System.out.println("Bad array reference");
                System.exit(1);
            }
            return -1;
        }
        
        private String finalizeValue(String process){
            int opens=0;
            int closes=0;
            int lastopen=-1;
            int lastclose=-1;
            
            for (int i =0; i< process.length(); i++){
                if (process.charAt(i)=='['){
                    if (i > 0) {if (process.charAt(i-1)!='/') {opens+=1;lastopen=i;}}
                    else {opens+=1;lastopen=i;}
                }
            }
            
            //System.out.println("total opens found was " + opens);
            for (int i =0; i< process.length(); i++){
                if (process.charAt(i)=='['){
                    if (i > 0) {if (process.charAt(i-1)!='/') {closes+=1;lastclose=i;}}
                    else {System.out.println("You can't open with a ] without an escape character");System.exit(1);}
                }
            }
            
            if (opens != closes || lastopen > lastclose) { //Cathes incoomplete bracket statements or the obvious closing bracket before opening bracket.
                System.out.println("Fatal Error - excess closed or excess open brackets");
                System.exit(1);
            }
            
            while (opens !=0 && closes !=0){
                //System.out.println("Open the main loop.");
                for (int i = lastopen;i<process.length();i++){
                    if (process.charAt(i)==']' && process.charAt(i-1)!='/') {
                        String nextName=process.substring(lastopen+1,i);
                        int variable=arrayFinder(nextName);
                        //if (variable==-1) variable=0;
                    
                        if (variable!=-1) {
                            nextName=modifyName;
                        }
                        
                        data myVar = pointToVariable(nextName);
                        String replacement="";
                        if (variable==-1) if (myVar.getStringValue() == null) { //If the variable does not *have* a value, then we just write NULL.
                            replacement=" NULL ";
                        } else {
                            replacement=myVar.getStringValue();
                        }
                        
                        if (variable!=-1){
                        if (myVar.getStringValue(variable) == null) { //If the variable does not *have* a value, then we just write NULL.
                            replacement=" NULL ";
                        } else {
                            replacement=myVar.getStringValue(variable);
                        }
                        
                        }
                        //System.out.println("The value of replacement is: " + replacement);
                        process=process.substring(0,lastopen) + replacement + process.substring(i+1,process.length());
                        //System.out.println("process is: " + process);
                        opens-=1;
                        closes-=1;
                        
                        if (opens !=0) for (int z =0; z< process.length(); z++){
                            if (process.charAt(z)=='['){
                                if (process.charAt(z-1)!='/') lastopen=z;
                                else lastopen=z;
                            }
                        }
                        //System.out.println("breaking");
                        break; //This should send us back to search for the next group
                    }
                }
                    //then you have a mismatch
            }
            if (opens != 0) {System.out.println("Exiting - you have mismatched parenthesis.  Result was: " + process);
            System.exit(1);} //If you iterate through the whole of the string and don't find a matching '['
            //System.out.println("The final result of the parsing is: " + process);
            return process;
            

        }
        
        private data pointToVariable(String name){ //This creates a variable or points to a variable of the given name.
                Iterator<data>  itr = myVars.iterator();
                data inspect;
                while (itr.hasNext()) {
                    inspect = itr.next();
                    if (inspect.myName.equals(name)){
                        return inspect;
                    }
                }
                data newData=new data(name,null);
                myVars.add(newData);
                return newData;
        }
        
        private String printTrails(Scanner trailReader){
            //System.out.println("entering print");
            short itemCount=0;
            String result="";
            String content="";
            while (trailReader.hasNextLine()){
                content=trailReader.nextLine();
                //System.out.println("Content is: " + content + " and the first character is " + (int)content.charAt(0) );
                if (correctIndent(content, nest)) { //This is not very reliable, encoding issues?
                    result+=content.substring(1);
                    //result+="\n";
                    itemCount+=1;
                }else{
                    //System.out.println("We are breaking now; the value of content is: " + content);
                    break;
                }
            }
            if (trailReader.hasNextLine()==false) content=null;
            if (itemCount!=0) {
                result=finalizeValue(result);
                myOut.print(result);
                //System.out.println("returning " + content);
                return content;
            }
            
            System.out.println("Failure in printTrails, exiting now");
            System.exit(1);
            return null;
        }
    }
