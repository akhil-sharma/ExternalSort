import java.io.*;

public class BufferedReaderWrapper {
    private BufferedReader bufferedReader;
    private String line;

    public BufferedReaderWrapper(BufferedReader bufferedReader){
        this.bufferedReader = bufferedReader;
        readNextLine();
    }

    public String peek(){
        return this.line;
    }

    public String poll(){
        String answer = this.peek();
        readNextLine();
        return answer;
    }

    private void readNextLine(){
        try{
            this.line = bufferedReader.readLine();
            App.dskAccessCounter.incrementCounter();
        } catch(IOException e){
            System.out.println("Exception in BuferedReader Wrapper.");
            this.line = null;
        }
    }
    
    public void close() throws IOException{
        this.bufferedReader.close();
    }

}
