import java.util.Comparator;

public class RecordComparator implements Comparator<String>{
    public int compare(String t1, String t2){
        // Comparing only the first 8 characters.
        // return t1.substring(0, 8).compareTo(t2.substring(0, 8));

        // Comparing the entire record.
        return t1.compareTo(t2);
    }    
}
