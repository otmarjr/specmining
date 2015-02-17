/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.evaluation;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Otmar
 */
public class TestTraceFilter {
    private final File file;
    private final String testedClass;
    private final String classPackage;
    
    
    public TestTraceFilter(File traceFile, String testedClassName){
        this.file = traceFile;
        this.testedClass = testedClassName;
        this.classPackage = testedClass.substring(0, testedClass.lastIndexOf("."));
    }
    
    private boolean containsOnlyCallsToTargetClass() throws IOException{
        return Stream.of(FileUtils.readFileToString(file).split("\\)"))
                .allMatch(call -> call.startsWith(this.classPackage));
    }
    
    public boolean isValidTrace(){
        try {
            return containsOnlyCallsToTargetClass();
        } catch (IOException ex) {
            Logger.getLogger(TestTraceFilter.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
