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
import specminers.ExecutionArgsHelper;

/**
 *
 * @author otmarpereira
 */
public class MergedSpecGenerator {
    private final static String JFLAP_PATH_OPTION = "-j";
    private final static String MOP_FILES_CODE_PATH_OPTION = "-m";
    private final static String HELP_OPTION = "-h";
    private final static String OUTPUT_OPTION = "-o";
    
    
    public static void main(String[] args) throws IOException, ParseException{
        Map<String, String> options = ExecutionArgsHelper.convertArgsToMap(args);

        // Sample run args: -m "C:\Users\Otmar\Google Drive\Mestrado\SpecMining\annotated-java-api\properties\java\net" -j "C:\Users\Otmar\Google Drive\Mestrado\SpecMining\dataset\specs\jflap_extended\net" -o "C:\Users\Otmar\Google Drive\Mestrado\SpecMining\dataset\specs\jflap_pruned\net"
        // or  -m "/Users/otmarpereira/Documents/mute_dataset/annotated-java-api/properties/java/util" -j "/Users/otmarpereira/Downloads/jflap_extended 2/util" -o "/Users/otmarpereira/Documents/mute_dataset/specs/jflap_pruned/util"
        if (options.containsKey(HELP_OPTION)) {
            ExecutionArgsHelper.displayHelp(Arrays.asList(
                    "In order to execute this program options:",
                    "-j <PATH> : Where to recursivelly search for jflap files equivalent to Pradel's reference automata extended with public API.",
                    "-m <PATH> : Folder where source code corresponding to mop files containing forbidden method sequences.",
                    "-s <PATH> : Folder containing teste classes source code.",
                    "-o <PATH> : Folder where automata with extended transitions will be saved."
            ));
        }

        if (validateInputArguments(options)) {
            extendedOriginalSpecification(options);
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
        
        if (!programOptions.containsKey(MOP_FILES_CODE_PATH_OPTION)) {
            System.err.println("You must use the -s option to inform a valid path where to search for classes'source code.");
            ok = false;
        } else {
            File f = new File(programOptions.get(MOP_FILES_CODE_PATH_OPTION));

            if (!f.exists()) {
                System.err.println("The supplied source code path does not exist.");
                ok = false;
            }
        }

        if (!programOptions.containsKey(OUTPUT_OPTION)) {
            System.out.println("WARNING: No output file informed. Specification will be printed on standard output.");
        }

        return ok;
    }

    private static void extendedOriginalSpecification(Map<String, String> options)  throws IOException, ParseException {
        
        File mopFilesFolder = new File(options.get(MOP_FILES_CODE_PATH_OPTION));
        String[] extensions = new String[]{MopExtractor.MOP_FILES_EXTENSION};
        List<File> files = FileUtils.listFiles(mopFilesFolder, extensions, true).stream()
                .collect(Collectors.toList());

        List<String> forbiddenSequences = new LinkedList<>();

        for (File f : files) {
            MopExtractor extractor = new MopExtractor(f);

            if (extractor.containsParseableSpec()) {
                List<String> newLines = extractor.getForbiddenSequences();
                forbiddenSequences.addAll(newLines);
                
            }
        }
        
        File originalSpecsFolder = new File(options.get(JFLAP_PATH_OPTION));
        extensions = new String[]{"jff"};
        File outputDir = null;

        if (options.containsKey(OUTPUT_OPTION)) {
            outputDir = new File(options.get(OUTPUT_OPTION));
        }

        List<File> originalSpecFiles = FileUtils.listFiles(originalSpecsFolder, extensions, true).stream().collect(Collectors.toList());

        for (File file : originalSpecFiles) {
            JflapFileManipulator jffManipulator = new JflapFileManipulator(file);
            jffManipulator.removeInvalidSequences(forbiddenSequences);
            String extendedSpecPath = Paths.get(outputDir.getPath(), file.getName().replace(".jff", "_package_full_merged_spec.jff")).toFile().getAbsolutePath();
            jffManipulator.saveToFile(extendedSpecPath);
        }
    }
    
}
