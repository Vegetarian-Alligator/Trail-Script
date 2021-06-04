package trails2;

import java.io.*;
import java.util.*;


class entity extends Thread{


    LinkedList<data> myVars= new LinkedList<data>();

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
                if (nextCommand != null) System.out.println("incorrect command given: " + nextCommand);
            }
            if (nextCommand != null) System.out.println("incorrect command given: " + nextCommand);
            //else System.out.println("next command is " + nextCommand);
        }
        
        private void setVariable(Scanner trailReader){
            String variableName;
            String value;
            String error="";
            try {
                if (trailReader.hasNextLine()){
                    variableName=trailReader.nextLine();
                    if (!(correctIndent(variableName,1))) throw new Exception("Bad formatting in set tag");
                } else throw new Exception("document ended in set tag");
                
                if (trailReader.hasNextLine()){
                    value=trailReader.nextLine();
                    if (!(correctIndent(value,1))) throw new Exception("Bad formatting in set tag");
                } else throw new Exception("document ended in set tag");
                System.out.println("We are iterating over a variable.");
                Iterator<data>  itr = myVars.iterator();
                data inspect;
                while (itr.hasNext()) {
                    inspect = itr.next();
                    if (inspect.myName.equals(variableName)){
                        inspect.changeData(value);
                        return;
                    }
                }
                myVars.add(new data(variableName,value));
            }catch (Exception e){
                System.out.println("Exeception in setVariable");
            }
        }
        
        private String printTrails(Scanner trailReader){
            //System.out.println("entering print");
            short itemCount=0;
            String result="";
            String content="";
            while (trailReader.hasNextLine()){
                content=trailReader.nextLine();
                //System.out.println("Content is: " + content + " and the first character is " + (int)content.charAt(0) );
                if (correctIndent(content, 1)) { //This is not very reliable, encoding issues?
                    result+=content.substring(1);
                    result+="\n";
                    itemCount+=1;
                }else{
                    //System.out.println("We are breaking now; the value of content is: " + content);
                    break;
                }
            }
            if (trailReader.hasNextLine()==false) content=null;
            if (itemCount!=0) {
                System.out.println(result);
                //System.out.println("returning " + content);
                return content;
            }
            
            System.out.println("Failure in printTrails, exiting now");
            System.exit(1);
            return null;
        }
    }
