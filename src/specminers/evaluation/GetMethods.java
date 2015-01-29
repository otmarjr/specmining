/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.evaluation;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import specminers.ExecutionArgsHelper;

/**
 *
 * @author Otmar
 */
public class GetMethods {

    private final static String PATH_OPTION = "-p";
    private final static String HELP_OPTION = "-h";
    private final static String OUTPUT_OPTION = "-o";
    private final static String GRABBING_TYPE = "-t";
    
    public static void main(String[] args) throws IOException {
        Map<String, String> options = ExecutionArgsHelper.convertArgsToMap(args);

        if (options.containsKey(HELP_OPTION)) {
            ExecutionArgsHelper.displayHelp(Arrays.asList(
                    "In order to execute this program options:",
                    "-p <PATH> : Where to recursivelly search for java files matching the supplied regex",
                    "-o <PATH> : Folder path indicating where to save text file containing extracted regex. If not defined, output will be printed on standard output.",
                    "-r <REGEX> : Regex to be applied to java files. Mandatory argument"
            ));
        }

        if (validateInputArguments(options)) {
            extractRegexFromJavaFiles(options);
        }
    }

    private static boolean validateInputArguments(Map<String, String> programOptions) {
        boolean ok = true;
        if (!programOptions.containsKey(PATH_OPTION)) {
            System.err.println("You must use the -p options to inform a valid path where to search for java files.");
            ok = false;
        } else {
            File f = new File(programOptions.get(PATH_OPTION));

            if (!f.exists()) {
                System.err.println("The supplied path to look for dot files does not exist.");
                ok = false;
            }
        }

        if (!programOptions.containsKey(OUTPUT_OPTION)) {
            System.out.println("WARNING: No output file informed. Specification will be printed on standard output.");
        }

        return ok;
    }

    private static void extractRegexFromJavaFiles(Map<String, String> options) throws IOException {
        File javaFilesFolder = new File(options.get(PATH_OPTION));
        String[] extensions = new String[]{"java"};
        File outputDir = null;

        if (options.containsKey(OUTPUT_OPTION)) {
            outputDir = new File(options.get(OUTPUT_OPTION));
        }

        List<File> sourceFiles = FileUtils.listFiles(javaFilesFolder, extensions, true).stream().collect(Collectors.toList());

        for (File sourceFile : sourceFiles) {
            GetMethodsViaRegexExtractor extractor = new GetMethodsViaRegexExtractor(sourceFile);

            // Set<String> result = new HashSet<>(extractor.getReadOnlyMethods());
            Set<String> result = new HashSet<>(extractor.getAllMethods());
            
            if (outputDir != null && outputDir.exists()) {
                File regexesFile;
                regexesFile = java.nio.file.Paths.get(outputDir.getAbsolutePath(), sourceFile.getName().replace(".java", "") + "_read_operations.txt").toFile();
                FileUtils.writeLines(regexesFile, result);

            } else {
                System.out.println("Read operations found on file "  + sourceFile.getName());
                result.forEach(l -> System.out.println(l));
            }
        }
    }
}
