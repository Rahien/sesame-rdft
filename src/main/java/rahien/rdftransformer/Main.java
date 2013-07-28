package rahien.rdftransformer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: karel
 * Date: 7/28/13
 * Time: 10:59 AM
 */
public class Main {
    private static StoreInterface storeIf;
    private static String query;
    private static String outputFile;
    public static void main(String[] args){
        storeIf = new StoreInterface();
        boolean moveOn;
        try{
            moveOn=parseArguments(args);
        }catch(Exception e){
            e.printStackTrace();
            printUsage();
            storeIf.cleanup();
            return;
        }

        if(moveOn){
            OutputStream stream=null;
            if(outputFile!=null){
                try {
                    stream=new FileOutputStream(new File(outputFile));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            // retesting so we can still print to standard out if file creation failed
            if(outputFile==null){
                System.out.println("Query result\n");
                stream=System.out;
            }
            if(query.toLowerCase().trim().startsWith("select")){
                storeIf.selectQuery(query,stream);
            }else if(query.toLowerCase().trim().startsWith("construct")){
                storeIf.graphQuery(query,stream);
            }
        }
        storeIf.cleanup();
    }

    //* parses the given arguments. returns false if program should exit in stead of continuing
    private static boolean parseArguments(String[] args){
        if(args.length>0 && args[0].startsWith("-")){
            return parseNormalArguments(args);
        }else{
            return parseShortHandArguments(args);
        }
    }

    private static boolean parseNormalArguments(String[] args) {
        int index =0;
        while (index < args.length){
            String currentArg=args[index];
            index++;
            if(currentArg.equals("-f")){
                String filename=args[index];
                storeIf.loadFile(filename);
                index++;
            }else if(currentArg.equals("-o")){
                outputFile=args[index];
                index++;
            }else if(currentArg.equals("-q")){
                query= args[index];
                index++;
            }else{
                printUsage();
                return false;
            }
        }

        return true;
    }

    private static boolean parseShortHandArguments(String[] args){
        try{
            query=args[0];
            storeIf.loadFile(args[1]);
            if(args.length>2){
                outputFile=args[2];
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            printUsage();
            return false;
        }
    }

    private static void printUsage(){
        System.out.print("Java program for quick execution of sparl on an rdf file.\n"+
                "Usage: java -jar rdftransform.jar -f file -q \"query\"\n"+
                "-f <filename> : load the given file into the repository\n" +
                "-q \"<sparql query>\" : the query to execute\n" +
                "-o <filename> : the output file to use (only the last one will be kept). If not present, will write to system out.\n" +
                "-h : print this help message\n"+
                "Shorthand: java -jar rdftransform.jar \"query\" file output\n"
        );
    }
}
