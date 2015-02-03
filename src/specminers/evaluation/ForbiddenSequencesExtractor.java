/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.evaluation;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javamop.parser.main_parser.ParseException;
import jdk.nashorn.internal.runtime.ParserException;
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

        if (validateOptions(options)) {
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
        File mopFilesFolder = new File(options.get(INPUT_PATH_OPTION));
        String[] extensions = new String[]{"mop"};
        List<File> files = FileUtils.listFiles(mopFilesFolder, extensions, true).stream()
                .collect(Collectors.toList());

        List<String> forbiddenSequences = new LinkedList<>();

        for (File f : files) {
            MopExtractor extractor = new MopExtractor(f);

            if (extractor.containsParseableSpec()) {
                forbiddenSequences.addAll(extractor.getForbiddenSequences());
            }
        }

        File outputDir = new File(options.get(OUTPUT_OPTION));
        
        if (outputDir == null || !outputDir.exists()) {
            forbiddenSequences.forEach(l -> System.out.println(l));
        } else {
            File forbiddenSeqsFile;
            forbiddenSeqsFile = java.nio.file.Paths.get(outputDir.getAbsolutePath(), "forbidden_sequences.txt").toFile();
            FileUtils.writeLines(forbiddenSeqsFile, forbiddenSequences);

        }
    }
}
