import java.io.*;
import java.util.*;

/**
 * Uses Hoffman Tree method to compress and decompress text files
 * @author Rebecca Liu and Dylan Lawler, Spring 2021
 */

public class Huffman {
    private String PathName; // file name to be compressed
    private String compressedPathName; // location name of the compressed file
    private String decompressedPathName;  // location name of the decompressed file
    private TreeMap<Character, Integer> frequencyTable; // initializes a map of characters and their frequencies in the text
    private Comparator<BinaryTree<TreeData>> comparetrees = new TreeComparator(); // comparator class that allows trees to be compared by character frequency
    private PriorityQueue<BinaryTree<TreeData>> priorityQueue = new PriorityQueue<>(comparetrees); // priority queue that sorts based on character frequency
    private BinaryTree<TreeData> HuffmanTree;  // initializes a new tree
    private TreeMap<Character, String> coderetrieval; // map of characters and their frequencies

    /**
     * @param filename file being compressed
     * @param compressedPathName compressed name for the file
     * @param decompressedPathName decompressed name for the file
     */
    public Huffman(String filename, String compressedPathName, String decompressedPathName){
        this.PathName = filename;
        this.compressedPathName = compressedPathName;
        this.decompressedPathName = decompressedPathName;
    }

    /**
     * Uses a map to keep track of characters used in a text file and their frequencies
     * @param filename file being compressed
     * @return returns a map of characters and their frequencies throughout the text file
     */
    public TreeMap createFrequencyTable(String filename){
        frequencyTable = new TreeMap<Character, Integer>();  // initializes the frequency table
        try{
            BufferedReader input = new BufferedReader(new FileReader(PathName));  // uses buffered reader to read the given file from pathName
            int intchar = input.read(); // reads each character into an integer
            // while there are still characters left to read
            while(intchar!= -1){
                char c = (char)intchar;// casts the int back into a character
                // if the frequency table contains the character, iterate the frequency
                if(frequencyTable.containsKey(c)) {
                    Integer frequency = frequencyTable.get(c) + 1;
                    frequencyTable.put(c, frequency);
                }
                // if its not in the table, add it
                else{
                    frequencyTable.put(c, 1);
                }
                intchar = input.read();
            }
            // closes the file and returns the table
            input.close();
//            System.out.println(frequencyTable); // prints out frequency map
            return frequencyTable;
        }
        // ensures the given file exists
        catch(FileNotFoundException e){
            System.out.println("File does not exist");
            return frequencyTable;
        }
        // catches the possible in or out errors
        catch(IOException e){
            System.out.println("IO exception");
            return frequencyTable;
        }
    }
    /**
     * creates a priority queue that sorts the trees by their character frequencies
     */
    public void createPriorityQueue(){
        // initializes the priority queue of compare trees
        priorityQueue = new PriorityQueue<>(comparetrees);
        // loops through the frequency table, creates a new tree of the character and frequency, and adds the tree to the priority queue
        for (Character c: frequencyTable.keySet()){
            TreeData treedata = new TreeData(c, frequencyTable.get(c));
            BinaryTree<TreeData> tree = new BinaryTree<TreeData> (treedata);
            priorityQueue.add(tree);
        }
    }

    /**
     *
     * @return returns a tree of characters with the most frequent at the top and least at the bottom
     */
    public BinaryTree<TreeData> createTree(){
       if(priorityQueue.size()==1){  // if only one tree left
        return priorityQueue.peek();  // remove from the queue and return the tree
       }
        BinaryTree<TreeData> T1 = priorityQueue.remove();
        BinaryTree<TreeData> T2 = priorityQueue.remove();
        TreeData r = new TreeData('r',  T1.data.getFrequency() + T2.data.getFrequency());
        HuffmanTree = new BinaryTree<TreeData>(r, T1, T2);
        // inserts the new tree into the priority queue
        priorityQueue.add(HuffmanTree);
        return createTree();
    }

    /**
     *
     * @return a map of the characters and their given code sequences
     */
    public TreeMap<Character, String> codeRetrieval(){
        // initializes a n map of characters and their codes
        coderetrieval = new TreeMap<>();
        // if the tree size is only one, the code for that character is  zero
        if (createTree().size() == 1) {
            coderetrieval.put(createTree().data.getCharacter(), "0");
        }
        // if it is greater than one, calls the helper method to get the entire path code
        if(createTree().size()>1){
            if(createTree()!= null) {
                codeRetrievalHelp("", createTree(), coderetrieval);
            }
        }
//        System.out.println(coderetrieval); // prints out code map
        return coderetrieval;
    }

    /**
     *
     * @param pathStart an empty string to add to to create the path string
     * @param tree the character tree that's being traversed over
     * @param codeMap the code map thats storing the characters and their codes
     */
    public void codeRetrievalHelp(String pathStart, BinaryTree<TreeData> tree, TreeMap<Character, String> codeMap){
        // keeps track of the path, starts empty
        String pathSoFar = pathStart;

        // if the node is a leaf, put the character and its code into the map
        if (!tree.hasLeft() && !tree.hasRight()){
            codeMap.put(tree.data.getCharacter(), pathSoFar);
        }
        // the the node has a right child, add a one to the code and recurse down
        if (tree.hasRight()){

            codeRetrievalHelp(pathSoFar + "1", tree.getRight(), codeMap);
        }

        //if the node has a left child, add a zero to the code and recurse down
        if (tree.hasLeft()){

            codeRetrievalHelp(pathSoFar + "0", tree.getLeft(), codeMap);

        }
    }

    /**
     * compresses a file
     */
    public void compress(){
        BufferedReader input;
        BufferedBitWriter bitOutput;
        createFrequencyTable(PathName);
        createPriorityQueue();
        try{
            createTree();
//            System.out.println(createTree());// prints out code tree
        }
        catch(NoSuchElementException e){
            System.out.println("compressing empty file.");  // prints an exception for an empty file
            return;
        }
        // set variable codeMap to the TreeMap returned by codeRetrieval method
        TreeMap<Character, String> codeMap = codeRetrieval();
        try{
            input = new BufferedReader(new FileReader(PathName));
            bitOutput = new BufferedBitWriter(compressedPathName);
            // set variable to read the first character in input
            int intchar = input.read();
            while(intchar!= -1){// while the file is not empty
                //get the code for the character from the codeMap
                String code = codeMap.get((char)intchar);
                for(int i = 0; i< code.length(); i++){
                    if (code.charAt(i)=='0'){
                        //set boolean value to false if 0
                        bitOutput.writeBit(false);
                    } else if(code.charAt(i)=='1'){
                        // set boolean value to true if 1
                        bitOutput.writeBit(true);
                    }
                }
                // read the next character in input
                intchar = input.read();

            }
            //close files
            input.close();
            bitOutput.close();
        } catch(FileNotFoundException e){
            //catch exception if file not found
            System.out.println("File does not exist");
        }
        catch(IOException e){
            //catch IO exception
            System.out.println("IO exception");
        }
    }

    /**
     * decompresses a file
     */
    public void decompress(){
        BufferedWriter output;
        BufferedBitReader bitInput;
        try{
            // set a variable tree to the BinaryTree created by createTree method
            BinaryTree<TreeData> tree = createTree();
            output = new BufferedWriter(new FileWriter(decompressedPathName));
            bitInput = new BufferedBitReader(compressedPathName);
            while (bitInput.hasNext()) {// while there's still a bit left to read
                boolean bit = bitInput.readBit();
                if(!bit){// if bit is false
                    if(tree.hasLeft()){
                        tree = tree.getLeft();
                    }
                }
                else if(tree.hasRight()){
                        tree = tree.getRight();
                }
                if(tree.isLeaf()){
                    // get character from current tree node's data and write to output file
                    output.write(tree.data.getCharacter());
                    // go back to root of tree
                    tree = createTree();
                }
            }
            //close output file
            bitInput.close();
            output.close();
        }

        catch(FileNotFoundException e){
            // catch exception if file not found
            System.out.println("File does not exist");
        }
        catch (IOException e){
            // catch IO exception
            System.out.println("IO exception" + e.getMessage());
        }
        catch(NoSuchElementException e){
            // catch exception if empty file
            System.out.println("decompressing empty file");
        }

    }

    public static void main(String[] args){
        //testing random characters
        Huffman test = new Huffman("inputs/test", "test_compressed.txt", "test_decompressed.txt");
        test.compress();
        test.decompress();

        // testing a single character
        Huffman test1 = new Huffman("inputs/test1", "test1_compressed.txt", "test1_decompressed.txt");
        test1.compress();
        test1.decompress();

        //testing multiple of the same character
        Huffman test2 = new Huffman("inputs/test2", "test2_compressed.txt", "test2_decompressed.txt");
        test2.compress();
        test2.decompress();


        //testing an empty file
        Huffman test3 = new Huffman("inputs/test3", "test3_compressed.txt", "test3_decompressed.txt");
        test3.compress();
        test3.decompress();

        //compressing and decompressing constitution
        Huffman constitution= new Huffman("inputs/USConstitution.txt", "USConstitution_compressed.txt", "USConstitution_decompressed.txt");
        constitution.compress();
        constitution.decompress();

        // compressing war and peace
        Huffman warAndPeace= new Huffman("inputs/WarAndPeace.txt", "WarAndPeace_compressed.txt", "WarAndPeace_decompressed.txt");
        warAndPeace.compress();

    }

}
