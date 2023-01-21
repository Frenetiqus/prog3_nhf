package components;
import java.util.HashMap;
import java.util.Map;
import java.io.*;

public class PerformanceCounter{
    static HashMap<String, Time> funcTimes;
    private String funcName;
    long startTime, elapsedTime;

    public class Time{
        long min;
        long max;
        double avg;
        long elemCount;

        public Time(){
            min = Long.MAX_VALUE-1;
            max = 0;
            avg = 0;
            elemCount = 0;
        }
        public void saveElem(long newElem){
            if(newElem > max){
                max = newElem;
            }
            if(newElem < min){
                min = newElem;
            }
            calcAvg(newElem);
        }
        private void calcAvg(long newElem){
            if(avg == 0){
                avg = newElem;
                elemCount = 1;
                return;
            }
            avg = (avg*elemCount+newElem)/(elemCount+1);
            elemCount = elemCount + 1;
        }
        public double getAvg() {
            return avg;
        }
        public long getMax() {
            return max;
        }
        public long getMin() {
            return min;
        }
    }

    static{
        funcTimes = new HashMap<>();
    }

    public PerformanceCounter(String funcName){
        this.funcName = funcName;
    }

    public void countStart(){
        elapsedTime = 0;
        startTime = System.nanoTime();
        if(!funcTimes.containsKey(funcName)){
            Time newTime = new Time();
            funcTimes.put(funcName, newTime);
        }
    }

    public void countStop(){
        if(funcTimes.containsKey(funcName)){
            long endTime = System.nanoTime();
            elapsedTime = endTime - startTime;
            funcTimes.get(funcName).saveElem(elapsedTime);
        }
    }

    public static void writeResults(FileWriter writer) throws IOException{
        try{
            if(!funcTimes.isEmpty()){
                for (Map.Entry<String,Time> entry : funcTimes.entrySet()) {
                    String funcName = entry.getKey();
                    Time result = entry.getValue();
                    writer.write("-----"+funcName+"-----\n");
                    writer.write("min: " + (double)result.getMin()/1000000 + " ms\n");
                    writer.write("max: " + (double)result.getMax()/1000000 + " ms\n");
                    writer.write("avg: " + (double)result.getAvg()/1000000 + " ms\n");
                }
            }
            
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    
}

