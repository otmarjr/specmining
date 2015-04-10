/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.evaluation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import specminers.FileHelper;
import specminers.JavaFileParsingHelper;
import specminers.StringHelper;

/**
 *
 * @author Otmar
 */
public class RandoopGeneratedTestParser {

    File javaFile;
    List<String> lines;

    public RandoopGeneratedTestParser(String javaFilePath) throws IOException {
        this(new File(javaFilePath));
    }

    public RandoopGeneratedTestParser(File javaFile) throws IOException {
        this.javaFile = javaFile;
        this.lines = FileHelper.getTrimmedFileLines(javaFile);
        this.loadTestMethods();
    }

    List<String> testMethods;
    Map<String, Map<String, String>> testMethodDetails;

    public Map<String, Map<String, String>> getTestMethodDetails(){
        return this.testMethodDetails;
    }
    
    private void loadTestMethods() {
        this.testMethods = new LinkedList<>();
        this.testMethodDetails = new HashMap<>();
        String testMethodRegularExpressionDeclaration = "^public\\svoid\\stest(\\d+)\\(\\).+$";

        Deque<String> openBraces = new ArrayDeque<String>();
        String currentMethod = null;
        List<String> statementsBeforeTryCatch = null;
        String currentClass = "";
        boolean foundTryCatchForCurrentTestMethod = false;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            if (line.matches(testMethodRegularExpressionDeclaration)) {
                openBraces.add("{");
                currentMethod = StringHelper.extractSingleValueWithRegex(line, testMethodRegularExpressionDeclaration, 1);
                statementsBeforeTryCatch = new LinkedList<>();
                foundTryCatchForCurrentTestMethod = false;
            } else {
                if (currentMethod != null) {
                    if (line.contains("try {")) {
                        foundTryCatchForCurrentTestMethod = true;
                    }

                    if (!line.contains("if (debug) { System.out.println();")) {
                        if (line.contains("var0")) {
                            if (!foundTryCatchForCurrentTestMethod) {
                                if (line.contains("var0 = new java.")) {
                                    int startIndex = line.indexOf(("var0 = new java."));
                                    int endIndex = line.indexOf("(", startIndex);
                                    currentClass = line.substring(startIndex, endIndex);
                                    statementsBeforeTryCatch.add(currentClass + ".init<>()");
                                } else {
                                    int startIndex = line.lastIndexOf("var0.");
                                    int endIndex = line.lastIndexOf("(");
                                    String calledMethod = line.substring(startIndex, endIndex);
                                    statementsBeforeTryCatch.add(currentClass + calledMethod + "()");
                                }
                            }
                        }
                        for (int j = 0; j < line.length(); j++) {
                            if (line.charAt(j) == '{') {
                                openBraces.add("{");
                            }
                            if (line.charAt(j) == '}') {
                                openBraces.remove();
                            }
                        }
                    }

                    if (openBraces.isEmpty()) {
                        String testMethodStatements = statementsBeforeTryCatch.stream()
                                .collect(Collectors.joining(""));
                        Map<String, String> currentTestDetails = new HashMap<>();
                        currentTestDetails.put("foundTryCatch", foundTryCatchForCurrentTestMethod + "");
                        currentTestDetails.put("statementsBeforeTryCatch", testMethodStatements);
                            testMethodDetails.put(currentMethod, currentTestDetails);
                        currentMethod = "";
                        statementsBeforeTryCatch.clear();;
                        foundTryCatchForCurrentTestMethod = false;
                        // Prepare for new method
                    }
                }
            }
        }

    }
}
