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
        this.lines = FileHelper.getTrimmedFileLines(javaFile)
                .stream().filter(l -> StringUtils.isNotBlank(l))
                .collect(Collectors.toList());
        this.loadTestMethods();
    }

    List<String> testMethods;
    Map<String, Map<String, String>> testMethodDetails;

    public Map<String, Map<String, String>> getTestMethodDetails() {
        return this.testMethodDetails;
    }

    private void loadTestMethods() {
        this.testMethods = new LinkedList<>();
        this.testMethodDetails = new HashMap<>();
        String testMethodRegularExpressionDeclaration = "^public\\svoid\\stest(\\d+)\\(\\).+$";

        Deque<String> openBraces = new ArrayDeque<>();
        String currentMethod = null;
        List<String> statementsBeforeTryCatch = null;
        String currentClass = "";
        boolean foundTryCatchForCurrentTestMethod = false;
        String firstVarFound = "";
        String varRegex = "var\\d+\\W";
        Pattern p = Pattern.compile(varRegex);

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
                        Matcher m = p.matcher(line);

                        if (m.find()) {
                            if (!foundTryCatchForCurrentTestMethod) {
                                if (StringUtils.isEmpty(firstVarFound)) {
                                    firstVarFound = m.group(0).trim();
                                }

                                if (line.contains(firstVarFound)) {

                                    if (line.contains(firstVarFound + " = new java.")) {
                                        int startIndex = line.indexOf("new") + 3;
                                        int endIndex = line.indexOf("(", startIndex);
                                        currentClass = line.substring(startIndex, endIndex);
                                        statementsBeforeTryCatch.add((currentClass + ".init<>()").trim());
                                    } else {
                                        if (line.contains(firstVarFound + ".")) {
                                            int startIndex = line.indexOf(firstVarFound + ".") + 4;
                                            int endIndex = line.lastIndexOf("(");
                                            String calledMethod = "";
                                            calledMethod = line.substring(startIndex, endIndex);
                                            statementsBeforeTryCatch.add(currentClass + (calledMethod.endsWith("(") ? "" : "(") +")");

                                        }
                                    }
                                }
                            }
                        }
                        for (int j = 0; j < line.length(); j++) {
                            if (line.charAt(j) == '{') {
                                openBraces.add("{");
                            }
                            if (line.charAt(j) == '}') {
                                if (!openBraces.isEmpty()) {
                                    openBraces.pop();
                                }
                            }
                        }
                    }

                    if (openBraces.isEmpty()) {
                        String testMethodStatements = statementsBeforeTryCatch.stream()
                                .map(st -> st.trim())
                                .collect(Collectors.joining(""));
                        Map<String, String> currentTestDetails = new HashMap<>();
                        currentTestDetails.put("foundTryCatch", foundTryCatchForCurrentTestMethod + "");
                        currentTestDetails.put("statementsBeforeTryCatch", testMethodStatements);

                        if (StringUtils.isNotBlank(currentMethod)) {
                            testMethodDetails.put(currentMethod, currentTestDetails);
                        }

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
