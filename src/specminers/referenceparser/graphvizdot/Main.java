/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.referenceparser.graphvizdot;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Otmar
 */
public class Main {

    final static String PATH_OPTION = "-p";
    final static String HELP_OPTION = "-h";
    final static String OUTPUT_OPTION = "-o";
    final static String DIRECT_CONVERSION_OPTION = "-d";

    private static Map<String, String> convertArgsToMap(String[] args) {
        Map<String, String> options = new HashMap();

        String lastOption = null;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("-")) {
                options.put(arg, "");
                lastOption = arg;
            } else {
                if (lastOption != null && options.containsKey(lastOption)) {
                    options.put(lastOption, arg);
                }
            }
        }

        return options;
    }

    public static void main(String[] args) throws IOException, TransformerException, ParserConfigurationException {
        Map<String, String> options = convertArgsToMap(args);

        if (options.containsKey(HELP_OPTION)) {
            displayHelp();
        }

        boolean argumentsOK = validateInputArguments(options);

        if (argumentsOK) {
            performConversion(options);
        }
    }

    public static void performConversion(Map<String, String> options) throws IOException, TransformerException, ParserConfigurationException {

        File dotFilesFolder = new File(options.get(PATH_OPTION));
        String[] extensions = new String[]{"dot"};
        boolean directConversion = options.containsKey(DIRECT_CONVERSION_OPTION);
        File outputDir = null;

        if (options.containsKey(OUTPUT_OPTION)) {
            outputDir = new File(options.get(OUTPUT_OPTION));
        }

        List<File> specFiles = FileUtils.listFiles(dotFilesFolder, extensions, true).stream().collect(Collectors.toList());

        for (File specFile : specFiles) {
            PradelsDotFilesToJffConverter converter = new PradelsDotFilesToJffConverter(specFile);
            converter.convert();
            File tempFile = File.createTempFile("jflap_", ".jff");

            if (directConversion) {

                try {
                    converter.saveToFile(tempFile.getAbsolutePath());
                    JffToRegexConverter regexGen = new JffToRegexConverter(tempFile);
                    String classSpec = regexGen.getRegularExpression();

                    if (outputDir != null && outputDir.exists()) {
                        File regexSpecFile;
                        regexSpecFile = java.nio.file.Paths.get(outputDir.getAbsolutePath(), specFile.getName().replace(".dot", "") + "_regex.txt").toFile();
                        FileUtils.writeStringToFile(regexSpecFile, classSpec);
                    } else {
                        System.out.println("Specification for file " + specFile.getName());
                        System.out.println(classSpec);
                    }
                } catch (RuntimeException exception) {
                    System.err.println("The automaton corresponding to the file " + specFile.getPath() + " seems to have some problems, like the lack of initial or final states, or multiple final states. Check this before trying to convert this file. Error message sent by the converter " + exception);
                    exception.printStackTrace();
                }
                catch (OutOfMemoryError t){
                    System.err.println("The automaton corresponding to the file " + specFile.getPath() + " seems to have some problems, like the lack of initial or final states, or multiple final states. Check this before trying to convert this file. Error message sent by the converter " + t);
                    t.printStackTrace();
                }
            } else {
                // Just want to save the jflap files
                if (outputDir != null && outputDir.exists()) {
                    File jffFile;
                    jffFile = java.nio.file.Paths.get(outputDir.getAbsolutePath(), specFile.getName().replace(".dot", "") + "_jflap_automaton.jff").toFile();
                    converter.saveToFile(jffFile.getAbsolutePath());
                } else {
                    System.out.println("JFlap file for dot file " + specFile.getName());
                    System.out.println(converter.getAsJffFormat());
                }
            }
        }
    }

    private static boolean validateInputArguments(Map<String, String> programOptions) {
        boolean ok = true;
        if (!programOptions.containsKey(PATH_OPTION)) {
            System.err.println("You must use the -p options to inform a valid path where to search for dot files.");
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

    private static void displayHelp() {
        System.out.println("In order to execute this program options:");
        System.out.println("-p <PATH> : Where to recursivelly search for file containing the specification statements. Mandatory argument");
        System.out.println("-o <PATH> : Mandatory folder path, where to save the converted files.");
        System.out.println("-d <PATH> : Optional argument. Indicates that the conversion will directly convert from .dot files to text files containing the regular expressions for each class.");
    }
}
