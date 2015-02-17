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
 * @author Otmar
 */
public class TracesFilter {

    private final static String UNIT_TEST_FILES_CODE_PATH_OPTION = "-u";
    private final static String TRACES_PATH_OPTION = "-t";
    private final static String HELP_OPTION = "-h";
    private final static String OUTPUT_OPTION = "-o";
    private final static String TARGET_PACKAGE = "-p";

    public static void main(String[] args) throws IOException, ParseException {
        Map<String, String> options = ExecutionArgsHelper.convertArgsToMap(args);

        // Sample run args: -u "C:\Users\Otmar\Google Drive\Mestrado\SpecMining\dataset\mute_log\dissertation-unit-tests\net-pradel" -t "C:\Users\Otmar\Google Drive\Mestrado\SpecMining\dataset\mute_log\dissertation-traces\net-pradel" -o "C:\Users\Otmar\Google Drive\Mestrado\SpecMining\dataset\mute_log\dissertation-traces\filtered-net-pradel" -p java.net
        if (options.containsKey(HELP_OPTION)) {
            ExecutionArgsHelper.displayHelp(Arrays.asList(
                    "In order to execute this program options:",
                    "-u <PATH> : Where to recursivelly search for unit tests source code.",
                    "-t <PATH> : Folder contaning trace calls.",
                    "-o <PATH> : Folder where filtered traced unit test executions should be saved."
            ));
        }

        if (validateInputArguments(options)) {
            filterUnitTestExeceutionTraces(options);
        }
    }

    private static boolean validateInputArguments(Map<String, String> programOptions) {
        boolean ok = true;
        if (!programOptions.containsKey(UNIT_TEST_FILES_CODE_PATH_OPTION)) {
            System.err.println("You must use the -u option to inform a valid path where to search for original unit test files.");
            ok = false;
        } else {
            File f = new File(programOptions.get(UNIT_TEST_FILES_CODE_PATH_OPTION));

            if (!f.exists()) {
                System.err.println("The supplied unit tests files path does not exist.");
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

    private static void copyTraceFilesToOutput(Map<String, Set<File>> filteredTests,
            String testsType, Map<String, String> programOptions) throws IOException {
        for (String clazz : filteredTests.keySet()) {
            String fullyQualifiedClassName = String.format("%s.%s", programOptions.get(TARGET_PACKAGE), clazz);
            File parentFolder = Paths.get(programOptions.get(TRACES_PATH_OPTION), clazz).toFile();
            Path filteredOutputFolder = Paths.get(programOptions.get(OUTPUT_OPTION), clazz, testsType).toAbsolutePath();

            filteredOutputFolder.toFile().delete();
            filteredOutputFolder.toFile().mkdirs();
            if (!Files.exists(filteredOutputFolder, LinkOption.NOFOLLOW_LINKS)) {
                Files.createDirectory(filteredOutputFolder);
            }

            for (File testFile : filteredTests.get(clazz)) {
                String traceFileName = testFile.getName().replace(".java", "_client_calls_trace.txt");
                File traceFile = Paths.get(parentFolder.getAbsolutePath(), traceFileName).toFile();

                if (traceFile.exists()) {
                    TestTraceFilter traceFilter = new TestTraceFilter(traceFile, fullyQualifiedClassName);
                    if (traceFilter.isValidTrace()) {
                        FileUtils.copyFileToDirectory(traceFile, filteredOutputFolder.toFile());
                    }
                }
            }

            if (filteredOutputFolder.toFile().list().length == 0) {
                FileUtils.deleteDirectory(filteredOutputFolder.toFile());
            }
        }
    }

    private static void filterUnitTestExeceutionTraces(Map<String, String> options) throws IOException, ParseException {

        File testFilesFolder = new File(options.get(UNIT_TEST_FILES_CODE_PATH_OPTION));
        String[] extensions = new String[]{"java"};

        List<File> files = FileUtils.listFiles(testFilesFolder, extensions, true).stream()
                .collect(Collectors.toList());

        // Key: class name Value: list of trace files containing filtered
        // unit test traces.
        Map<String, Set<File>> externalFilteredTests = new HashMap<>();
        Map<String, Set<File>> internalFilteredTests = new HashMap<>();

        for (File f : files) {

            File curFile = f;

            while (!curFile.getParentFile().equals(testFilesFolder)) {
                curFile = curFile.getParentFile();
            }

            String testedClass = curFile.getName();

            MinedSpecsFilter filter = new MinedSpecsFilter(f);
            if (!filter.isStandAloneTest()) {
                continue;
            }
            if (filter.containsExternalAPITest()) {
                if (!externalFilteredTests.containsKey(testedClass)) {
                    externalFilteredTests.put(testedClass, new HashSet<>());
                }

                externalFilteredTests.get(testedClass).add(f);
            } else {
                if (!internalFilteredTests.containsKey(testedClass)) {
                    internalFilteredTests.put(testedClass, new HashSet<>());
                }

                internalFilteredTests.get(testedClass).add(f);
            }
        }

        copyTraceFilesToOutput(externalFilteredTests, "external", options);
        copyTraceFilesToOutput(internalFilteredTests, "internal", options);
        
    }
}
