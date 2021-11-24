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

public class App{

    // A static disk IO counter. It will be used everywhere we read 
    // or write a line to a file.
    public static IOCounter dskAccessCounter = new IOCounter();
    public static void main(String[] args) throws Exception 
    {
        // Clean the output directory before starting.
    	App.cleanOutputDirectory();
        
        String inputFileName = args[0];
        File inputFile = new File(Constants.INPUT_PATH + inputFileName);
        BufferedReader inputFileReader = new BufferedReader(new FileReader(inputFile));
        
        // Testing purposes.
        boolean verbose = false;
        if (args.length == 2 && args[1].equals("v")){
            verbose = true;
        }

        // Timing starts here.
    	long startTime = System.nanoTime();
        
        // Calculating the size of a tmp file or run.
        long runSize = Utils.estimateBestRunSize(Utils.getApproximateMemoryAvailable(), inputFile.length()); 
        
        System.out.println("Runsize: " + runSize);
        
        // Save the pointers to all the tmp files generated in phase 1.
        List<File> files = new ArrayList<>();

        // buffer for individual runs.
        List<String> buffer = new ArrayList<>();

        // hack, will be used phase 2.
        // Do not remove this variable or 
        // phase two will fail.
        int bufferRecordCount = 0;
        
        try{
            String currentRecord = ""; 
            while (currentRecord != null){
                long bufferSize = 0;
                while ((bufferSize < runSize) && ((currentRecord = inputFileReader.readLine()) != null)){
                    // increment for the readLine.
                    App.dskAccessCounter.incrementCounter();

                    buffer.add(currentRecord);
                    bufferSize += Constants.RECORD_SIZE_IN_BYTES;
                }
                
                files.add(App.writeTmpRecordToFile(buffer));
                if (bufferRecordCount == 0){
                    bufferRecordCount = buffer.size();
                }
                //reset the buffer.
                buffer.clear();
            }

        } finally {
            inputFileReader.close();
        }

        // At this point we have sorted bins, if I may.
        // In addition, I also have a List of references 
        // to all the temp files.
        // PHASE : 2

        // This priority queue will be used for merging the sorted runs stored in the tmp files.
        PriorityQueue<BufferedReaderWrapper> pq = new PriorityQueue<>(new BufferedReaderComparator());
        ListIterator<File> iter = files.listIterator();
        BufferedReaderWrapper bwr = null;
        while(iter.hasNext()){
            bwr = new BufferedReaderWrapper(new BufferedReader(new FileReader(iter.next())));
            if (bwr.peek() != null){
                pq.add(bwr);
            
            } else{
                // The file held by this buffer does not have any records.
                bwr.close();
            }
            
            //finally remove the File from the `files` list. (Maybe it helps in th saving the space. Maybe.)
            iter.remove();
        }

        // the priority queue will now have all the BufferedReaders sorted based on the
        // next lines in their respective first records.

        File outputFile = new File(Constants.OUTPUT_PATH, Constants.OUTPUT_FILE_NAME);
        String outputFileName = outputFile.toPath().toAbsolutePath().toString();

        String duplicateHolder = "";
        int duplicateCount = 0;
        while (pq.size() > 0){
            BufferedReaderWrapper b = pq.poll();
            String record = b.poll();
            
            if(!duplicateHolder.equals(record)){
                buffer.add(record);
                duplicateHolder = record;
            } else {
                duplicateCount++;
                // print the duplicate records if the verbose key was added.
                if (verbose){
                    System.out.println("    >>Found Duplicate record: " + record);
                }
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
                buffer.clear();
            }
        }

        // For the last buffer that may not be full.
        if (buffer.size() > 0){
            App.writeLinesToFile(buffer, outputFileName, true);
            buffer.clear();
        }

        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        System.out.println("Duplicate records found: " + duplicateCount);
        System.out.println("==========================");
        System.out.println("Execution Complete:");
        System.out.println("Execution time in milliseconds: " + timeElapsed / 1000000);
        System.out.println("Number of disk IOs: " + App.dskAccessCounter.getCounter());
        System.out.println("==========================");
    }

    public static void cleanOutputDirectory(){
        cleanOutputDirectory(false);
    }

    public static void cleanOutputDirectory(boolean tmp){
        System.out.println("==========================");
        System.out.println("Cleaning artifacts from previous executions...");
        File tmpFile = null;
        if (tmp){
            tmpFile = new File(Constants.TEMP_FILE_PATH);
            if (tmpFile.delete()){
                System.out.println(">Removed the tmp directory.");
            }
        }

        tmpFile = new File(Constants.OUTPUT_PATH, Constants.OUTPUT_FILE_NAME);
        if (tmpFile.delete()){
            System.out.println(">Removed the stale output file.");
        }
        System.out.println("Clean up complete...");
        System.out.println("==========================");
    }


    public static void writeLinesToFile(List<String> recordList, String fileName, boolean append){
        
        String finalAddress = fileName;

        try {
            FileWriter fw = new FileWriter(finalAddress, append);
            BufferedWriter bw = new BufferedWriter(fw);
            recordList.sort(new RecordComparator());
            for (String record: recordList){
                bw.write(record);
                bw.newLine();
                App.dskAccessCounter.incrementCounter();
            }
         
            bw.close();
        } catch (Exception e) {
            System.out.println("Detected an exception in the writeLinesToFile method... fk me, i guess.");
        }
    }

    public static File writeTmpRecordToFile(List<String> recordList) throws IOException{
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
                App.dskAccessCounter.incrementCounter();
            }
        }
        
        return tmpRecordFile;
    }
}
