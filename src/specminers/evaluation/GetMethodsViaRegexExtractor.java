/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.evaluation;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import specminers.StringHelper;

/**
 *
 * @author Otmar
 */
public class GetMethodsViaRegexExtractor {

    File javaFile;

    public GetMethodsViaRegexExtractor(String javaFilePath) {
        this(new File(javaFilePath));
    }

    public GetMethodsViaRegexExtractor(File javaFile) {
        this.javaFile = javaFile;
    }

    public List<String> getMatches() throws IOException {
        List<String> matches = new LinkedList<>();

        List<String> fileLines = FileUtils.readLines(javaFile);
        String methodSigPattern = "^((public|private|protected|static|final|native|synchronized|abstract|threadsafe|transient)+\\s)+[\\$_\\w\\<\\>\\[\\]]*\\s+(get[\\$_\\w]+)\\([^\\)]*\\)?\\s*\\{?[^\\}]*\\}?.+$";
        String packagePattern = "^package[\\s\\t]+([\\w\\.]+);";
        
        String packageNameLine = fileLines.stream().filter(line -> line.trim().matches(packagePattern)).findFirst().get();
        String packageName = StringHelper.extractSingleValueWithRegex(packageNameLine, packagePattern, 1);
        fileLines.stream().filter((line) -> 
                (line.trim().matches(methodSigPattern) && !line.contains("private") && !line.contains("protected")))
                .map((line) -> packageName + "." + javaFile.getName().replace(".java", "") + "." + StringHelper.extractSingleValueWithRegex(line, methodSigPattern, 3) + "()")
                .forEach((matching) -> {
                    matches.add(matching);
                });

        return matches;
    }
}
