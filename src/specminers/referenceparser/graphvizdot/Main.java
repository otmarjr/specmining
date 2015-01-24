/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.referenceparser.graphvizdot;

import specminers.referenceparser.javamop.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Otmar
 */
public class Main {
    final static String PATH_OPTION = "-p";
    final static String HELP_OPTION = "-h";
    final static String OUTPUT_OPTION = "-o";
    
    private static Map<String, String> convertArgsToMap(String[] args){
        Map<String, String> options = new HashMap();
        
        String lastOption = null;
        
        for (int i=0;i<args.length;i++){
            String arg = args[i];
            if (arg.startsWith("-")){
                options.put(arg, "");
                lastOption = arg;
            }
            else {
                if (lastOption !=null && options.containsKey(lastOption)){
                    options.put(lastOption, arg);
                }
            }
        }
        
        return options;
    }
    
    public static void main(String[] args) throws IOException, TransformerException, ParserConfigurationException{
        Map<String,String> options = convertArgsToMap(args);
        
        if (options.containsKey(HELP_OPTION)){
            displayHelp();
        }
        
        boolean argumentsOK = validateInputArguments(options);
        
        if (argumentsOK){
            PradelsDotFilesToJffConverter converter = new PradelsDotFilesToJffConverter(options.get(PATH_OPTION));
            converter.convert();
            
            if (options.containsKey(OUTPUT_OPTION) && StringUtils.isNotBlank(options.get(OUTPUT_OPTION))){
                converter.saveToFile(options.get(OUTPUT_OPTION));
            }
            else{
                System.out.println(converter.getAsJffFormat());
            }
        }
    }
    
    private static boolean validateInputArguments(Map<String,String> programOptions){
        boolean ok = true;
        if (!programOptions.containsKey(PATH_OPTION)){
            System.err.println("You must use the -p options to inform a valid path where to search for dot files.");
            ok = false;
        }
        else{
            File f = new File(programOptions.get(PATH_OPTION));
            
            if (!f.exists()){
                System.err.println("The supplied path to look for dot files does not exist.");    
                ok = false;
            }
        }
        
        if (!programOptions.containsKey(OUTPUT_OPTION)){
            System.out.println("WARNING: No output file informed. Specification will be printed on standard output.");
        }
        
        return ok;
    }
    
    private static void displayHelp(){
        System.out.println("In order to execute this program options:");
        System.out.println("-p <PATH> : Where to recursivelly search for file containing the specification statements. Mandatory argument");
        System.out.println("-o <PATH> : Mandatory file path, where to save the collected specification.");
    }
}
