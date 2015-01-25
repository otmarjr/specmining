/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Otmar
 */
public class TestsHelper {
    public static File getTestFilesFolder() {
        String pathFromArgument = System.getProperty("java.testfiles.path");
        File testFilesFolder;
        if (StringUtils.isNotBlank(pathFromArgument) && (testFilesFolder = new File(pathFromArgument)).exists()){
            return testFilesFolder;
        }
        else{
            Path currentWorkingPath = Paths.get("");
            return currentWorkingPath.toFile(); // Assume all files are in current working dir!
        }
    }
}
