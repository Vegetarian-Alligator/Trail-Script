package trails2;

class consoleOutput extends outputInterface{
    public boolean print(String text){
        System.out.println(text);
        return true;
    }
}
