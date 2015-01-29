/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.evaluation;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javamop.parser.main_parser.ParseException;
import org.apache.commons.io.FileUtils;
import specminers.ExecutionArgsHelper;

/**
 *
 * @author Otmar
 */
public class ForbiddenSequencesExtractor {

    private final static String INPUT_PATH_OPTION = "-p";
    private final static String HELP_OPTION = "-h";
    private final static String OUTPUT_OPTION = "-o";

    public static void main(String[] args) throws IOException, ParseException {
        Map<String, String> options = ExecutionArgsHelper.convertArgsToMap(args);

        if (options.containsKey(HELP_OPTION)) {
            ExecutionArgsHelper.displayHelp(Arrays.asList(
                    "In order to execute this program options:",
                    "-p <PATH> : Where to recursivelly search for mop files containing forbidden sequences.",
                    "-o <PATH> : Folder where automata with extended transitions will be saved."
            ));
        }
        
        if (validateOptions(options)){
            extractInvalidSequences(options);
        }
    }

    private static boolean validateOptions(Map<String, String> programOptions) {
        boolean ok = true;
        if (!programOptions.containsKey(INPUT_PATH_OPTION)) {
            System.err.println("You must use the -p option to inform a valid path where to search for mop files.");
            ok = false;
        } else {
            File f = new File(programOptions.get(INPUT_PATH_OPTION));

            if (!f.exists()) {
                System.err.println("The supplied mop files path does not exist.");
                ok = false;
            }
        }


        return ok;
    }

    
    private static void extractInvalidSequences(Map<String, String> options) throws IOException, ParseException {
        File mopPackagesRootFolder = new File(options.get(INPUT_PATH_OPTION));
        String[] extensions = new String[] {"mop"};
        List<File> mopFiles = FileUtils.listFiles(mopPackagesRootFolder, extensions, true).stream().collect(Collectors.toList());
        
        for (File file : mopFiles){
            MopExtractor extractor = new MopExtractor(file);
            
            if (extractor.containsExtendedRegularExpression()){
                String regex = extractor.getExtendedRegularExpression();
                
                System.out.println("Found regex " + regex + " expanded to " + extractor.getExpandedRegularExpression() + " for class " + extractor.getTargetClassName() + " on file " + file.getAbsolutePath());
            }
        }
    }
}
