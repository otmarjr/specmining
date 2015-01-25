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
        // Assuming test files folder is in current working dir!
        Path currentWorkingPath = Paths.get("");
        return Paths.get(currentWorkingPath.toFile().getAbsolutePath(), "testfiles").toFile(); 
    }
    
    public static File getTestFileFromTestsFolder(String folder, String fileName){
        return new File(TestsHelper.getTestFilesFolder(), folder +"/" + fileName);
    }
}
