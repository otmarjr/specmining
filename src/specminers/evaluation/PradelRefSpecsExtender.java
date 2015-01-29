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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import specminers.ExecutionArgsHelper;

/**
 *
 * @author otmarpereira
 */
public class PradelRefSpecsExtender {
    private final static String JFLAP_PATH_OPTION = "-j";
    private final static String SOURCE_CODE_PATH_OPTION = "-s";
    private final static String HELP_OPTION = "-h";
    private final static String OUTPUT_OPTION = "-o";
    
    
    public static void main(String[] args) throws IOException{
        Map<String, String> options = ExecutionArgsHelper.convertArgsToMap(args);

        if (options.containsKey(HELP_OPTION)) {
            ExecutionArgsHelper.displayHelp(Arrays.asList(
                    "In order to execute this program options:",
                    "-j <PATH> : Where to recursivelly search for jflap files equivalent to Pradel's reference automata.",
                    "-s <PATH> : Folder where source code corresponding to the FSM model can be found.",
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
        
        if (!programOptions.containsKey(SOURCE_CODE_PATH_OPTION)) {
            System.err.println("You must use the -s option to inform a valid path where to search for classes'source code.");
            ok = false;
        } else {
            File f = new File(programOptions.get(SOURCE_CODE_PATH_OPTION));

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

    private static Map<String, Set<String>> getClassesPublicMethods(String sourceCodeRootPath) throws IOException {
        Map<String, Set<String>> publicAPI = new HashMap<>();
        File javaFilesFolder = new File(sourceCodeRootPath);
        String[] extensions = new String[]{"java"};
        
        List<File> files = FileUtils.listFiles(javaFilesFolder, extensions, true).stream().collect(Collectors.toList());
        
        for (File sourceFile : files) {
            GetMethodsViaRegexExtractor extractor = new GetMethodsViaRegexExtractor(sourceFile);

            Set<String> result = new HashSet<>(extractor.getAllMethods());
            publicAPI.put(extractor.getFullClassName(), result);
        }
        
        return publicAPI;
    }
    private static void extendedOriginalSpecification(Map<String, String> options)  throws IOException {
        Map<String, Set<String>> publicAPI = getClassesPublicMethods(options.get(SOURCE_CODE_PATH_OPTION));
        
        File originalSpecsFolder = new File(options.get(JFLAP_PATH_OPTION));
        String[] extensions = new String[]{"jff"};
        File outputDir = null;

        if (options.containsKey(OUTPUT_OPTION)) {
            outputDir = new File(options.get(OUTPUT_OPTION));
        }

        List<File> originalSpecFiles = FileUtils.listFiles(originalSpecsFolder, extensions, true).stream().collect(Collectors.toList());

        for (File file : originalSpecFiles) {
            JflapFileManipulator jffManipulator = new JflapFileManipulator(file);
            jffManipulator.includeTransitions(publicAPI, false);
            String extendedSpecPath = Paths.get(outputDir.getPath(), file.getName().replace(".jff", "_package_extended.jff")).toFile().getAbsolutePath();
            jffManipulator.saveToFile(extendedSpecPath);
        }
    }
}
