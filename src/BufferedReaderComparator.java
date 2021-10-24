import java.util.Comparator;

public class BufferedReaderComparator implements Comparator<BufferedReaderWrapper>{
    
    public int compare(BufferedReaderWrapper b1, BufferedReaderWrapper b2){
        // Comparing only the first 8 characters.
        // return b1.peek().substring(0, 8).compareTo(b2.peek().substring(0, 8));

        // Comparing the entire string.
        return b1.peek().compareTo(b2.peek());
    }  
}
