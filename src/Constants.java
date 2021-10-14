public class Constants {
    public static String OUTPUT_PATH = "./output/";
    public static String TEMP_FILE_PATH = "./output/tmp/";
    public static String INPUT_PATH = "./input/";
    public static String OUTPUT_FILE_NAME = "SORTED_DISTINCT_OUTPUT.txt";
    // The size of the record is calculated assuming the
    // machine has a 64bit architecture, leading to a 
    // character size of 2-bytes and a conservative obj 
    // overhead of 60 bytes. (2 * 100) + 60; 
    public static int RECORD_SIZE_IN_BYTES = 260;
    public static String FINAL_FILE_NAME = "FINAL_RELATION";
    
    // This is the number of temporary "sorted" files which 
    // will be created at the end of phase one.
    public static int MAX_TEMP_FILES = 1024;
}
