/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.evaluation;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import specminers.FileHelper;

/**
 *
 * @author Otmar
 */
public class MinedSpecsFilter {

    File unitTestFile;
    List<String> lines;
    String sourceCode;
    
    public MinedSpecsFilter(File testFile) {
        this.unitTestFile = testFile;
    }

    private boolean checkIfCatchSectionSwallowsException(String catchBlockStartLine) {
        int startIndex = this.lines.indexOf(catchBlockStartLine);
        
        // contains return, continue or does not throw.
        Deque<String> blocks = new LinkedList<>();
        blocks.add(catchBlockStartLine);
        
        Deque<Integer> blockStartPositions = new LinkedList<>();
        blockStartPositions.add(sourceCode.indexOf(catchBlockStartLine) + catchBlockStartLine.indexOf("{"));
        
        boolean throwFound = false;
        
        int currentBlockStart = blockStartPositions.peekFirst();
        int currentIndexInFile = currentBlockStart;
        for (int i = startIndex; i < lines.size(); i++) {
            String line = lines.get(i);
            if (!blocks.isEmpty()) {
                if (line.contains("{") && i!=startIndex) {
                    blocks.add(line);
                    blockStartPositions.add(sourceCode.indexOf(line.trim()));
                }
                
                if (line.contains("}") && line.indexOf("}") + currentIndexInFile > currentBlockStart) {
                    blocks.pop();
                    currentBlockStart = blockStartPositions.pop();
                }
                if (line.contains("return ") || line.contains("return;") || line.contains("continue;")) {
                    return true;
                }

                if (line.contains("throw ")) {
                    throwFound = true;
                }
            }
            currentIndexInFile += line.length();
        }
        
        return !throwFound;
    }

    // Checks if this test is intended for the presence of exceptions being
    // thrown when certain conditions are met. These scenarios are intended for
    // regression tests, and are not intended for consumers of the library 
    // replicating the patterns found on the test.
    private boolean testsLibraryProtectionViaExceptionThrowing() throws IOException {
        lines = FileHelper.getTrimmedFileLines(unitTestFile);
        sourceCode = FileUtils.readFileToString(unitTestFile);

        final String catchPattern = "catch[\\s\\t]*\\([\\s\\t]*[\\$_\\w\\<\\>]+[\\s\\t]+[\\$_\\w\\<\\>]+[\\s\\t]*\\)";
        Pattern p = Pattern.compile(catchPattern);
        List<String> linesCatchingExceptions = lines.stream().filter(l -> p.matcher(l).find())
                .collect(Collectors.toList());

        if (!linesCatchingExceptions.isEmpty()){
            return linesCatchingExceptions
                    .stream()
                    .anyMatch(l -> checkIfCatchSectionSwallowsException(l));
        }

        return false;
    }
    
   

    public boolean isStandAloneTest() throws IOException {
        // Inner classes of tests are marked with $ on their names!
        return !unitTestFile.getName().contains("$") && !FileUtils.readFileToString(unitTestFile).contains("$");
    }
    public boolean containsExternalAPITest() throws IOException {
        return !testsLibraryProtectionViaExceptionThrowing();
    }
}
