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
    
     public static void main(String[] args) throws IOException, ParseException{
        Map<String, String> options = ExecutionArgsHelper.convertArgsToMap(args);

        // Sample run args: -u "E:\openjdk-6-src-b33-14_oct_2014.tar\jdk\test\java\net" -t "C:\Users\Otmar\Google Drive\Mestrado\SpecMining\dataset\mute_log\java\net" -o "C:\Users\Otmar\Google Drive\Mestrado\SpecMining\dataset\mute_log\filtered\net"
        
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

    private static void filterUnitTestExeceutionTraces(Map<String, String> options)  throws IOException, ParseException {
        
        File testFilesFolder = new File(options.get(UNIT_TEST_FILES_CODE_PATH_OPTION));
        String[] extensions = new String[]{"java"};
        
        
        List<File> files = FileUtils.listFiles(testFilesFolder, extensions, true).stream()
                .collect(Collectors.toList());

        // Key: class name Value: list of trace files containing filtered
        // unit test traces.
        Map<String, Set<File>> filteredTraceFiles = new HashMap<>();
        
        for (File f : files) {
            
            File curFile = f;
            
            while (!curFile.getParentFile().equals(testFilesFolder)){
                curFile = curFile.getParentFile();
            }
                    
            String testedClass = curFile.getName();
            
            MinedSpecsFilter filter = new MinedSpecsFilter(f);
            if (filter.containsExternalAPITest()){
                if (!filteredTraceFiles.containsKey(testedClass)){
                    filteredTraceFiles.put(testedClass, new HashSet<>());
                }
                
                filteredTraceFiles.get(testedClass).add(f);
            }
        }
        
        
        for (String clazz : filteredTraceFiles.keySet()){
            File parentFolder = Paths.get(options.get(TRACES_PATH_OPTION), clazz).toFile();
            Path filteredOutputFolder = Paths.get(options.get(OUTPUT_OPTION), clazz).toAbsolutePath();
            
            if (!Files.exists(filteredOutputFolder, LinkOption.NOFOLLOW_LINKS)){
                Files.createDirectory(filteredOutputFolder);
            }
            
            for (File testFile : filteredTraceFiles.get(clazz)){
                String traceFileName = testFile.getName().replace(".java", "_client_calls_trace.txt");
                File traceFile = Paths.get(parentFolder.getAbsolutePath(),traceFileName).toFile();
                
                if (traceFile.exists()){
                    FileUtils.copyFileToDirectory(traceFile, filteredOutputFolder.toFile());
                }
            }
            
            if (filteredOutputFolder.toFile().list().length == 0){
                FileUtils.deleteDirectory(filteredOutputFolder.toFile());
            }
        }
    }
}
