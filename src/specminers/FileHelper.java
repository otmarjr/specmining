/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author otmarpereira
 */
public class FileHelper {

    public static List<String> getTrimmedFileLines(File file) throws IOException {
        return FileUtils.readLines(file)
                .stream()
                .map(l -> l.trim())
                .collect(Collectors.toList());
    }
    
    /*
    public static String getClassNameFromFolderHierarchy(File file) {
        String fullPath = file.getAbsolutePath();
        
        if (!fullPath.contains("java"))
            return null;
        else
        {
            String classNamePattern = "^\\w[a-zA-Z0-9\\$";
        }
    }*/
}
