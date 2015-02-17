/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.evaluation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javamop.parser.main_parser.ParseException;
import org.apache.commons.io.FileUtils;
import specminers.ExecutionArgsHelper;

/**
 *
 * @author Otmar
 */
public class PrecisionEvaluator {

    private final static String JFLAP_FULL_REFERENCE_SPECS_PATH = "-j";
    private final static String JFLAP_ORIGINAL_REFERENCE_SPECS_PATH = "-r";
    private final static String TRACES_PATH_OPTION = "-t";
    private final static String HELP_OPTION = "-h";
    private final static String OUTPUT_OPTION = "-o";

    public static void main(String[] args) throws IOException, ParseException {
        Map<String, String> options = ExecutionArgsHelper.convertArgsToMap(args);

        // Sample run args: -j "C:\Users\Otmar\Google Drive\Mestrado\SpecMining\dataset\specs\pruned_experimental\net" -t "C:\Users\Otmar\Google Drive\Mestrado\SpecMining\dataset\mute_log\dissertation-traces\filtered-net-pradel" -r "C:\Users\Otmar\Google Drive\Mestrado\SpecMining\dataset\specs\jflap\net" -o "C:\Users\Otmar\Google Drive\Mestrado\SpecMining\dataset\precision\net"
        if (options.containsKey(HELP_OPTION)) {
            ExecutionArgsHelper.displayHelp(Arrays.asList(
                    "In order to execute this program options:",
                    "-j <PATH> : Where to recursivelly search for JFLAP full reference specifications.",
                    "-t <PATH> : Folder contaning trace calls to compare their precision against reference specs.",
                    "-o <PATH> : Folder where precisions statistics per classes will be saved."
            ));
        }

        if (validateInputArguments(options)) {
            collectPrecisionStatisticsPerClass(options);
        }
    }

    private static boolean validateInputArguments(Map<String, String> programOptions) {
        boolean ok = true;
        if (!programOptions.containsKey(JFLAP_FULL_REFERENCE_SPECS_PATH)) {
            System.err.println("You must use the -j option to inform a valid path where to search for reference specification files.");
            ok = false;
        } else {
            File f = new File(programOptions.get(JFLAP_FULL_REFERENCE_SPECS_PATH));

            if (!f.exists()) {
                System.err.println("The supplied jflap reference specs folder does not exist.");
                ok = false;
            }
        }
        
        if (!programOptions.containsKey(JFLAP_ORIGINAL_REFERENCE_SPECS_PATH)) {
            System.err.println("You must use the -r option to inform a valid path where to search for original reference specification files.");
            ok = false;
        } else {
            File f = new File(programOptions.get(JFLAP_ORIGINAL_REFERENCE_SPECS_PATH));

            if (!f.exists()) {
                System.err.println("The supplied jflap reference specs folder does not exist.");
                ok = false;
            }
        }

        if (!programOptions.containsKey(TRACES_PATH_OPTION)) {
            System.err.println("You must use the -t option to inform a valid path where to search for unit tests execution traces.");
            ok = false;
        } else {
            File f = new File(programOptions.get(TRACES_PATH_OPTION));

            if (!f.exists()) {
                System.err.println("The supplied unit tests' trace path does not exist.");
                ok = false;
            }
        }

        if (!programOptions.containsKey(OUTPUT_OPTION)) {
            System.out.println("WARNING: No output file informed. Specification will be printed on standard output.");
        }

        return ok;
    }

    static class TracePrecisionStatistics {

        public String className;
        public int numberOfExternalTraces;
        public int numberOfAcceptedExternalTraces;
        public int numberOfInternalTraces;
        public int numberOfAcceptedInternalTraces;
        public double recall;
    }

    private static void collectPrecisionStatisticsPerClass(Map<String, String> options) throws IOException, ParseException {

        File testFilesFolder = new File(options.get(JFLAP_FULL_REFERENCE_SPECS_PATH));
        File originalReferenceSpecFolder = new File(options.get(JFLAP_ORIGINAL_REFERENCE_SPECS_PATH));
        String[] extensions = new String[]{"jff"};
        String[] traceFileExtension = new String[]{"txt"};

        List<File> files = FileUtils.listFiles(testFilesFolder, extensions, true).stream()
                .collect(Collectors.toList());
        
        List<File> originalSpecFiles = FileUtils.listFiles(originalReferenceSpecFolder, extensions, true)
                .stream().collect(Collectors.toList());

        // Key: class name Value: list of trace files containing filtered
        // unit test traces.
        Map<String, TracePrecisionStatistics> testTraces = new HashMap<>();

        for (File f : files) {

            String testedClass = f.getName().replace("_jflap_automaton_package_extended_package_full_merged_spec.jff", "");
            String testedClassSimpleName = testedClass.substring(testedClass.lastIndexOf(".") + 1);
            File tracesFolder = Paths.get(options.get(TRACES_PATH_OPTION), testedClassSimpleName).toFile();
            Collection<File> traces = FileUtils.listFiles(tracesFolder, traceFileExtension, true);
            
            TracePrecisionStatistics statistics = new TracePrecisionStatistics();
            statistics.className = testedClass;
            statistics.numberOfExternalTraces = 0;
            statistics.numberOfAcceptedExternalTraces = 0;
            statistics.numberOfInternalTraces = 0;
            statistics.numberOfAcceptedInternalTraces = 0;

            JflapFileManipulator jff = new JflapFileManipulator(f);
            Set<List<String>> minedSeqs;
            minedSeqs = new HashSet<>();
            int numberOfAccepted = 0;

            for (File t : traces) {
                String fullTrace = FileUtils.readFileToString(t);
                boolean isExternalTest = t.getAbsolutePath().contains("external");

                List<String> traceCalls = Stream.of(fullTrace.split("\\)"))
                        .map(call -> call + ")")
                        .collect(Collectors.toList());

                minedSeqs.add(traceCalls);
                
                if (isExternalTest) {
                    statistics.numberOfExternalTraces++;
                    if (jff.acceptsSequence(traceCalls)) {
                        statistics.numberOfAcceptedExternalTraces++;
                        numberOfAccepted++;
                    }
                } else {
                    statistics.numberOfInternalTraces++;
                    // Tests for protection via exceptions being thrown
                    // should not lead to an accepting state!
                    if (!jff.acceptsSequence(traceCalls)) {
                        statistics.numberOfAcceptedInternalTraces++;
                        numberOfAccepted++;
                    }
                }
            }
            
            File originalReferenceSpec;
            originalReferenceSpec = originalSpecFiles.stream()
                    .filter(refFile -> refFile.getName().equals(testedClass + "_jflap_automaton.jff")
                    ).findFirst().get();
            
            JflapFileManipulator refRecall = new JflapFileManipulator(originalReferenceSpec);
            statistics.recall = refRecall.calculateSequencesRecall(minedSeqs);
            testTraces.put(testedClass, statistics);
        }

        List<String> allStats = new LinkedList<>();
        String header = "Class;External Traces;Accepted External Traces;External Precision;Internal Traces;Accepted Internal Traces;Internal Precision;Recall";
        for (String clazz : testTraces.keySet()) {
            File statsFile = Paths.get(options.get(OUTPUT_OPTION), clazz + "_statistics.txt").toFile();
            TracePrecisionStatistics st = testTraces.get(clazz);
            double externalPrecision = st.numberOfAcceptedExternalTraces * 1D / st.numberOfExternalTraces;
            double internalPrecision = st.numberOfAcceptedInternalTraces * 1D / st.numberOfInternalTraces;
            String stats = String.format("%s;%s;%s;%f;%s;%s;%f;%f", st.className, st.numberOfExternalTraces, st.numberOfAcceptedExternalTraces, externalPrecision
                    ,st.numberOfInternalTraces, st.numberOfAcceptedInternalTraces, internalPrecision,
                    st.recall);

            List<String> lines = new LinkedList();
            lines.add(header);
            lines.add(stats);
            FileUtils.writeLines(statsFile, lines);

            allStats.add(stats);
        }

        allStats.add(0,header);
        File allStatsFile = Paths.get(options.get(OUTPUT_OPTION), "full_statistics.txt").toFile();

        FileUtils.writeLines(allStatsFile, allStats);

    }
}
