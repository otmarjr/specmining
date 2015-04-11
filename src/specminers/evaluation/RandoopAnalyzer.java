/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.evaluation;

import java.io.File;
import java.io.FileFilter;
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
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.StringUtils;
import specminers.ExecutionArgsHelper;

/**
 *
 * @author otmarpereira
 */
public class RandoopAnalyzer {

    private final static String RANDOOP_TESTS_FOLDER = "-t";
    private final static String HELP_OPTION = "-h";
    private final static String OUTPUT_OPTION = "-o";

    public static void main(String[] args) throws IOException, ParseException {
        Map<String, String> options = ExecutionArgsHelper.convertArgsToMap(args);

        // Sample run args: -t E:\randoop\gerados\ -o "C:\Windows\Temp\randoop_analyzsis\"
        // -m "C:\Users\Otmar\Google Drive\Mestrado\SpecMining\annotated-java-api\properties\java\\util" -j "C:\Users\Otmar\Dropbox\SpecMining\dataset\specs\jflap_extended\\util_v2.0" -o "C:\Users\Otmar\Dropbox\SpecMining\dataset\specs\jflap_pruned\\util_v2.0"
        // or  -m "/Users/otmarpereira/Documents/mute_dataset/annotated-java-api/properties/java/util" -j "/Users/otmarpereira/Downloads/jflap_extended 2/util" -o "/Users/otmarpereira/Documents/mute_dataset/specs/jflap_pruned/util"
        if (options.containsKey(HELP_OPTION)) {
            ExecutionArgsHelper.displayHelp(Arrays.asList(
                    "In order to execute this program options:",
                    "-t <PATH> : Where to recursivelly search for jflap files equivalent to Pradel's reference automata extended with public API.",
                    "-o <PATH> : Folder where automata with extended transitions will be saved."
            ));
        }

        if (validateInputArguments(options)) {
            extendedOriginalSpecification(options);
        }
    }

    private static boolean validateInputArguments(Map<String, String> programOptions) {
        boolean ok = true;
        if (!programOptions.containsKey(RANDOOP_TESTS_FOLDER)) {
            System.err.println("You must use the -j option to inform a valid path where to search for original jflap files.");
            ok = false;
        } else {
            File f = new File(programOptions.get(RANDOOP_TESTS_FOLDER));

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

    private static void extendedOriginalSpecification(Map<String, String> options) throws IOException, ParseException {

        File testsFolder = new File(options.get(RANDOOP_TESTS_FOLDER));
        String[] extensions = new String[]{"java"};
        File outputDir = null;

        if (options.containsKey(OUTPUT_OPTION)) {
            outputDir = new File(options.get(OUTPUT_OPTION));
        } else {
            outputDir = FileUtils.getTempDirectory();
            System.out.println("Files will be generated at folder " + outputDir.getAbsolutePath());
        }

        for (File classFolder : testsFolder.listFiles()) {
            if (classFolder.isDirectory()) {
                List<File> timeIntervalFolders;
                timeIntervalFolders = Arrays
                        .asList(classFolder.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY));

                List<String> classDetails = new LinkedList<>();
                Set<String> classSequences = new HashSet<>();

                for (File containingFolder : timeIntervalFolders) {
                    List<String> lines = new LinkedList<>();
                    Set<String> sequenceLines = new HashSet<>();
                    List<File> genTests = FileUtils
                            .listFiles(containingFolder, extensions, true)
                            .stream().collect(Collectors.toList());

                    for (File file : genTests) {
                        if (file.getName().equals("RandoopTest.java")) {
                            continue;
                        }
                        RandoopGeneratedTestParser ra = new RandoopGeneratedTestParser(file);

                        for (String test : ra.getTestMethodDetails().keySet()) {

                            String details = "";

                            for (String det : ra.getTestMethodDetails().get(test).keySet()) {
                                details += det + ";" + ra.getTestMethodDetails().get(test).get(det);
                            }

                            String line = test + ";" + details;
                            lines.add(line);

                            String sequence = ra.getTestMethodDetails().get(test).get("statementsBeforeTryCatch");
                            if (!StringUtils.isEmpty(sequence)) {
                                sequenceLines.add(sequence.trim());
                            }
                        }
                    }

                    String statsPath = Paths.get(outputDir.getPath(), classFolder.getName() + "_" + containingFolder.getName() + "_test_statistics.txt").toFile().getAbsolutePath();
                    String sequencesPath = Paths.get(outputDir.getPath(), classFolder.getName() + "_" + containingFolder.getName() + "_sequences.txt").toFile().getAbsolutePath();
                    FileUtils.writeLines(new File(statsPath), lines);
                    FileUtils.writeLines(new File(sequencesPath), sequenceLines);

                    classDetails.addAll(lines);
                    classSequences.addAll(sequenceLines);
                }

                String classStatsPath = Paths.get(outputDir.getPath(), classFolder.getName() + "_" + "all_tests_statistics.txt").toFile().getAbsolutePath();
                String classSequencesPath = Paths.get(outputDir.getPath(), classFolder.getName() + "_all_sequences.txt").toFile().getAbsolutePath();

                FileUtils.writeLines(new File(classStatsPath), classDetails);
                FileUtils.writeLines(new File(classSequencesPath), classSequences);

            }
        }
        List<File> originalSpecFiles = FileUtils.listFiles(testsFolder, extensions, true).stream().collect(Collectors.toList());

        List<String> lines = new LinkedList<>();
        for (File file : originalSpecFiles) {
            if (file.getName().equals("RandoopTest.java")) {
                continue;
            }
            RandoopGeneratedTestParser ra = new RandoopGeneratedTestParser(file);

            for (String test : ra.getTestMethodDetails().keySet()) {

                String details = "";

                for (String det : ra.getTestMethodDetails().get(test).keySet()) {
                    details += det + ";" + ra.getTestMethodDetails().get(test).get(det);
                }

                String line = test + ";" + details;
                lines.add(line);
            }
        }

        String statsPath = Paths.get(outputDir.getPath(), "test_details.txt").toFile().getAbsolutePath();
        FileUtils.writeLines(new File(statsPath), lines);
    }

}
