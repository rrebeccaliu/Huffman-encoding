/**
 * TreeData class that stores character and frequency variables
 * @author Rebecca Liu and Dylan Lawler, Spring 2021
 */
public class TreeData {
    char character;
    int frequency;

    //constructor
    public TreeData(char c, int frequency){
        this.character = c;
        this.frequency = frequency;
    }

    // set methods
    public void setCharacter(char c){
        this.character = c;
    }
    public void setFrequency(int frequency){
        this.frequency = frequency;
    }

    // get methods
    public char getCharacter(){
        return character;
    }
    public int getFrequency(){
        return frequency;
    }

    public String toString(){
        return "c:"+ character + "frequency:" + frequency;
    }
}
