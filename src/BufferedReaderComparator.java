import java.util.Comparator;

public class BufferedReaderComparator implements Comparator<BufferedReaderWrapper>{
    
    public int compare(BufferedReaderWrapper b1, BufferedReaderWrapper b2){
        return b1.peek().substring(0, 8).compareTo(b2.peek().substring(0, 8));
    }  
}
