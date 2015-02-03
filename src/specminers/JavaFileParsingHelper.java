/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author otmarpereira
 */
public class JavaFileParsingHelper {
    static final String PACKAGE_DECLARATION_PATTERN =  "^package[\\s\\t]+([\\w\\.]+);";
    static final String DEFAULT_PACKAGE_NAME = "";
    
    public static String getPackageName(File javaFile) throws IOException {
        List<String> fileLines = FileHelper.getTrimmedFileLines(javaFile);
        
        Optional<String> packageNameLine = fileLines.stream()
                .filter(line -> line.matches(PACKAGE_DECLARATION_PATTERN))
                .findFirst();
        
        if (packageNameLine.isPresent()){
            return StringHelper.extractSingleValueWithRegex(packageNameLine.get(), PACKAGE_DECLARATION_PATTERN, 1);
        }
        
        return DEFAULT_PACKAGE_NAME;
    }
    
    public static String getFullClassName(File javaFile) throws IOException{
        String packageName = getPackageName(javaFile);
        String className = javaFile.getName().replace(".java", "");
        
        return  DEFAULT_PACKAGE_NAME.equals(packageName) ? className  : packageName + "." + className;
    }
}
