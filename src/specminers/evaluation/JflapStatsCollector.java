/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.evaluation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javamop.parser.main_parser.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import specminers.ExecutionArgsHelper;
import specminers.StringHelper;

/**
 *
 * @author otmarpereira
 */
public class JflapStatsCollector {
    private final static String JFLAP_PATH_OPTION = "-j";
    private final static String HELP_OPTION = "-h";
    private final static String OUTPUT_OPTION = "-o";
    
    
    public static void main(String[] args) throws IOException, ParseException{
        Map<String, String> options = ExecutionArgsHelper.convertArgsToMap(args);

        // Sample run args: -j "C:\Users\Otmar\Dropbox\SpecMining\dataset\specs\jflap_pruned\net_v2.1" -o "C:\Users\Otmar\Dropbox\SpecMining\dataset\specs\"
        // Sample run args: -j "C:\Users\Otmar\Dropbox\SpecMining\dataset\specs\jflap\net" -o "C:\Users\Otmar\Dropbox\SpecMining\dataset\specs\"
        // Sample run args: -j "C:\Users\Otmar\Dropbox\SpecMining\dataset\specs\jflap\\util" -o "C:\Users\Otmar\Dropbox\SpecMining\dataset\specs\"
        //  -j "C:\Users\Otmar\Dropbox\SpecMining\dataset\specs\jflap_pruned\\util_v2.0" -o "C:\Users\Otmar\Dropbox\SpecMining\dataset\specs\jflap_pruned\\util_v2.0"
        // or  -j "/Users/otmarpereira/Downloads/jflap_extended 2/util" -o "/Users/otmarpereira/Documents/mute_dataset/specs/jflap_pruned/util"
        if (options.containsKey(HELP_OPTION)) {
            ExecutionArgsHelper.displayHelp(Arrays.asList(
                    "In order to execute this program options:",
                    "-j <PATH> : Where to recursivelly search for jflap files equivalent to Pradel's reference automata extended with public API.",
                    "-o <PATH> : Folder where automata with extended transitions will be saved."
            ));
        }

        if (validateInputArguments(options)) {
            collectMethodsStatistics(options);
        }
    }

    private static boolean validateInputArguments(Map<String, String> programOptions) {
        boolean ok = true;
        if (!programOptions.containsKey(JFLAP_PATH_OPTION)) {
            System.err.println("You must use the -j option to inform a valid path where to search for original jflap files.");
            ok = false;
        } else {
            File f = new File(programOptions.get(JFLAP_PATH_OPTION));

            if (!f.exists()) {
                System.err.println("The supplied jflap files path does not exist.");
                ok = false;
            }
        }
        

        if (!programOptions.containsKey(OUTPUT_OPTION)) {
            System.out.println("WARNING: No output file informed. Specification will be printed on standard output.");
        }

        return ok;
    }
    
    
     private static void collectMethodsStatistics(Map<String, String> options)  throws IOException, ParseException {
        
        File originalSpecsFolder = new File(options.get(JFLAP_PATH_OPTION));
        String[] extensions = new String[]{"jff"};
        File outputDir = null;

        if (options.containsKey(OUTPUT_OPTION)) {
            outputDir = new File(options.get(OUTPUT_OPTION));
        }
        
        List<File> originalSpecFiles = FileUtils.listFiles(originalSpecsFolder, extensions, true).stream().collect(Collectors.toList());

        
        List<String> lines = new LinkedList<>();
        for (File file : originalSpecFiles) {
            String classname = file.getName().replace("_jflap_automaton.jff", "");
            JflapFileManipulator jffManipulator = new JflapFileManipulator(file);
            AutomataStats stats = jffManipulator.getAutomataMethodsStats(classname);
            String line = FilenameUtils.removeExtension(file.getName()) + ";" + 
                    stats.getNumberOfPublicMethods() + ";"
                    + stats.getNumberOfRelevantMethods() + ";"
                    + stats.getNumberOfComplexRelevantMethods()
            + ";" + stats.getComplexRelevantMethods().stream().collect(Collectors.joining(","));
            lines.add(line);
        }
        
        String statsPath = Paths.get(outputDir.getPath(), StringHelper.getCurrentDateTimeStamp() + "_" + originalSpecsFolder.getName() + "automatas_method_statistics.txt").toFile().getAbsolutePath();
        lines.add(0, "Class name;# of public methods;# of relevant methods;# of complex relevant methods;Complex method signatures");
        FileUtils.writeLines(new File(statsPath), lines);
    }

    private static void collectScenariosStatistics(Map<String, String> options)  throws IOException, ParseException {
        
        File originalSpecsFolder = new File(options.get(JFLAP_PATH_OPTION));
        String[] extensions = new String[]{"jff"};
        File outputDir = null;

        if (options.containsKey(OUTPUT_OPTION)) {
            outputDir = new File(options.get(OUTPUT_OPTION));
        }
        
        List<File> originalSpecFiles = FileUtils.listFiles(originalSpecsFolder, extensions, true).stream().collect(Collectors.toList());

        
        List<String> lines = new LinkedList<>();
        for (File file : originalSpecFiles) {
            JflapFileManipulator jffManipulator = new JflapFileManipulator(file);
            AutomataStats stats = jffManipulator.getAutomataScenariosStats();
            String line = FilenameUtils.removeExtension(file.getName()) + ";" + 
                    stats.getShortestScenario() + ";" + stats.getShortestScenarioExample()
                    + ";" + stats.getLongestScenario() + ";"
                    + stats.getLongestScenarioExample()
                    + ";" + stats.getNumberOfScenarios();
            lines.add(line);
        }
        
        String statsPath = Paths.get(outputDir.getPath(), StringHelper.getCurrentDateTimeStamp() + "_" + originalSpecsFolder.getName() + "automata_statistics.txt").toFile().getAbsolutePath();
        FileUtils.writeLines(new File(statsPath), lines);
    }
    
}
