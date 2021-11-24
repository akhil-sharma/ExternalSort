public class Utils {

    /**
     * Calculates the memory available before getting 
     * an out of bound error.
     * 
     * http://stackoverflow.com/questions/12807797/java-get-available-memory
     * 
     * @return The presumably free memory.
     */
    public static long getApproximateMemoryAvailable(){
        long maxMemory = Runtime.getRuntime().maxMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();

        return maxMemory - (totalMemory - freeMemory);
    }

    public static long estimateBestRunSize(long maxMemory, long fileSize){
        // Limit the number of temp files to the MAX_TEMP_FILES (1024)
        // But if the run size if too small when compared to the main
        // memory, we change it to maxMemory / 2;
        long integralRunSize = fileSize / Constants.MAX_TEMP_FILES;
        long fractionalRunSize = fileSize % Constants.MAX_TEMP_FILES == 0 ? 0: 1;
        long runSize = integralRunSize + fractionalRunSize;
        
        runSize = runSize < (maxMemory / 2) ? (maxMemory / 2) : runSize;

        return runSize;
    }
}
