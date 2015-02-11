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
public class GetMethodsViaRegexExtractor {

    File javaFile;

    public GetMethodsViaRegexExtractor(String javaFilePath) {
        this(new File(javaFilePath));
    }

    public GetMethodsViaRegexExtractor(File javaFile) {
        this.javaFile = javaFile;
    }

    private String fullClassName;

    public String getFullClassName() throws IOException {
        if (StringUtils.isBlank(fullClassName)) {
            fullClassName = JavaFileParsingHelper.getFullClassName(javaFile);
        }
        return fullClassName;
    }

    private List<String> getPublicMethodsViaRegex(String regex) throws IOException {
        List<String> matches = new LinkedList<>();
        List<String> fileLines = FileUtils.readLines(javaFile);

        this.getFullClassName();

        fileLines.stream().filter((line)
                -> (line.trim().matches(regex) && !line.contains("private")))
                .map((line) -> this.fullClassName + "." + StringHelper.extractSingleValueWithRegex(line, regex, 3) + "()")
                .forEach((matching) -> {
                    matches.add(matching);
                });

        return matches;
    }

    public List<String> getReadOnlyMethods() throws IOException {
        String methodSigPattern = "^((public|private|protected|static|final|native|synchronized|abstract|threadsafe|transient)+\\s)+[\\$_\\w\\<\\>\\[\\]\\,]*\\s+(get[\\$_\\w]+)\\([^\\)]*\\)?\\s*\\{?[^\\}]*\\}?.+$";
        return getPublicMethodsViaRegex(methodSigPattern);
    }

    public String getBaseClass() throws IOException {
        String extendsPattern = "[\\s\\t]+extends[\\s\\t]([A-Z_]($[A-Z_]|[\\w_])*)";

        List<String> fileTrimmedLines = FileHelper.getTrimmedFileLines(javaFile);
        Pattern p = Pattern.compile(extendsPattern);
        Optional<String> baseClassDeclaration = fileTrimmedLines.stream().filter(l -> {
            Matcher m = p.matcher(l);
            return m.find();
        }).findFirst();

        if (baseClassDeclaration.isPresent()) {
            Matcher m = p.matcher(baseClassDeclaration.get());
            if (m.find()) {
                String baseClass = m.group(1);
                return baseClass;
            }
        }
        return null;
    }

    public List<String> getAllMethods() throws IOException {
        String allPublicMethodsPattern = "^((public|private|protected|static|final|native|synchronized|abstract|threadsafe|transient)+[\\s\\t]+)+[\\$_\\w\\<\\>\\[\\]\\,]*\\s+([\\$_\\w]+)[\\t\\s]*\\([^\\)]*\\)?\\s*\\{?[^\\}]*\\}?.+$";
        List<String> matches = getPublicMethodsViaRegex(allPublicMethodsPattern);
        // Add the constructor according to the parser format:
        matches.add(fullClassName + ".<init>()");
        return matches;
    }
}
