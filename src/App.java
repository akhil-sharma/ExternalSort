import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.*;

public class App 
{
    public static void main(String[] args) throws Exception 
    {
    	
    	long startTime = System.nanoTime();
        // get this argument from the cmd arguments.
        //String inputFileName = "fileSmall.txt";
        String inputFileName = args[0];

        File inputFile = new File(Constants.INPUT_PATH + inputFileName);
        BufferedReader inputFileReader = new BufferedReader(new FileReader(inputFile));

        // Basically the size of a file.
        long runSize = Utils.estimateBestRunSize(Utils.getApproximateMemoryAvailable(), inputFile.length());       
        
        // Save the pointers to all the files.
        ArrayList<File> files = new ArrayList<>();

        // buffer for individual files.
        ArrayList<String> buffer = new ArrayList<>();

        int bufferRecordCount = 0; // hack, will be used phase 2.
        
        try{
            String currentRecord = ""; 
            while (currentRecord != null){
                long bufferSize = 0;
                while ((bufferSize < runSize) && ((currentRecord = inputFileReader.readLine()) != null)){
                    buffer.add(currentRecord);
                    bufferSize += Constants.RECORD_SIZE_IN_BYTES;
                }
                
                files.add(App.writeTmpRecordToFile(buffer));
                if (bufferRecordCount == 0){
                    bufferRecordCount = buffer.size();
                }
                //reset the buffer.
                buffer = new ArrayList<>();
            }

        } finally {
            inputFileReader.close();
        }
        



        // int batch = 0;
        // String fileName = "";
        // for (int i = 1; i <= 50; i++){
        //     if (i % 10 == 0){
        //         batch++;
        //         fileName = "C:\\Users\\akhil\\Desktop\\ExternalSort\\Output\\" + String.valueOf(batch) + "tmp.txt";
        //         buffer.add(reader.readLine());
        //         App.writeLinesToFile(buffer, fileName);
        //         buffer = new ArrayList<>();            
            
        //     } else {
        //         buffer.add(reader.readLine());
        //     }
        // }

        // reader.close();
        
        
        // At this point we have sorted bins, if I may.
        // So I will have to get a hold of all the files.
        // PHASE : 2

        /////
        // I now have a list of Files.

        PriorityQueue<BufferedReaderWrapper> pq = new PriorityQueue<>(new BufferedReaderComparator());

        // for (int i = 1; i <= totalFiles; i++){
        //     String fileAddress = baseAddress + String.valueOf(i) + "tmp.txt";
        //     BufferedReaderWrapper bwr = new BufferedReaderWrapper(new BufferedReader(new FileReader(fileAddress)));
        //     pq.add(bwr);
        // }

        // Add the bufferedRecordWrappers to the priorityQueue
        for (File tmpF: files){
            BufferedReaderWrapper bwr = new BufferedReaderWrapper(new BufferedReader(new FileReader(tmpF)));
            pq.add(bwr);
        }

        // Now the priority queue has all the readers based on the first elements.
        // The merging will now start.
        // ArrayList<String> mergeBuffer = new ArrayList<>();
        // use the same buffer.

        //String finalFileName = "C:\\Users\\akhil\\Desktop\\ExternalSort\\Output\\final_file.txt";
        File outputFile = new File(Constants.OUTPUT_PATH, Constants.OUTPUT_FILE_NAME);
        String outputFileName = outputFile.toPath().toAbsolutePath().toString();

        String duplicateHolder = "";

        while (pq.size() > 0){
            BufferedReaderWrapper b = pq.poll();
            String record = b.poll();
            if(!duplicateHolder.equals(record)){
                buffer.add(record);
                duplicateHolder = record;
            }            

            if(b.peek() != null){
                // if the bufferReaderwrapper is not empty, add it back.
                pq.add(b);
            
            } else {
                // just close the BufferedReaders associated with the wrapper.
                b.close();
            }

            if (buffer.size() == bufferRecordCount){
                App.writeLinesToFile(buffer, outputFileName, true);
                // reset the merge buffer.
                buffer = new ArrayList<>();
            }

            if (pq.peek() == null && buffer.size() > 0){
                App.writeLinesToFile(buffer, outputFileName, true);
            }
        }
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        System.out.println("Execution time in nanoseconds: " + timeElapsed);
        System.out.println("Execution time in milliseconds: " + timeElapsed / 1000000);
    }


    public static void writeLinesToFile(ArrayList<String> recordList, String fileName, boolean append){
        
        String finalAddress = fileName;

        try {
            FileWriter fw = new FileWriter(finalAddress, append);
            BufferedWriter bw = new BufferedWriter(fw);
            recordList.sort(new RecordComparator());
            for (String record: recordList){
                bw.write(record);
                bw.newLine();
            }
         
            bw.close();
        } catch (Exception e) {
            System.out.println("Detected an exception in the writeLinesToFile method... fk me, i guess.");
        }
    }

    public static File writeTmpRecordToFile(ArrayList<String> recordList) throws IOException{
        File tmpDirectory = new File(Constants.TEMP_FILE_PATH);
        tmpDirectory.mkdirs();
        File tmpRecordFile = File.createTempFile("tmp", ".txt", tmpDirectory);
        tmpRecordFile.deleteOnExit();

        OutputStream out = new FileOutputStream(tmpRecordFile);
        
        try(BufferedWriter tempFileWriter = new BufferedWriter(new OutputStreamWriter(out))){          
            // sort the run here and write all the lines to a tmp file.
            recordList.sort(new RecordComparator());
        
            for (String record: recordList){
                tempFileWriter.write(record);
                tempFileWriter.newLine();
            }
        }
        
        return tmpRecordFile;
    }
}
