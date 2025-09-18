/**
 * Compares tree node roots by the frequency of the character appearance
 * @author Rebecca Liu and Dylan Lawler, Spring 2021
 */
import java.util.Comparator;

public class TreeComparator implements Comparator<BinaryTree<TreeData>> {

    @Override
    public int compare(BinaryTree<TreeData> x, BinaryTree<TreeData> y) {
        // returns 1 if the first tree frequency is larger than the second
        if (x.data.getFrequency() > y.data.getFrequency()){
            return 1;
        }
        // returns 1 if the first tree frequency is equal the the second
        else if (x.data.getFrequency() == y.data.getFrequency()){
            return 0;
        }
        // returns -1 if first tree frequency is less than the second
        else {
            return -1;
        }
    }

}
