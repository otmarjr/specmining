/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.evaluation;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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
    
    private boolean containsOnlyCallsToTargetClassPackage() throws IOException{
        return Stream.of(FileUtils.readFileToString(file).split("\\)"))
                .allMatch(call -> call.startsWith(this.classPackage));
    }
    
    private boolean containsAtLeastOneCallToTargetClass() throws IOException{
        return Stream.of(FileUtils.readFileToString(file).split("\\)"))
                .anyMatch(call -> call.startsWith(this.testedClass + ".") );
    }
    
    public boolean isValidTrace(){
        try {
            return containsOnlyCallsToTargetClassPackage() 
                    && containsAtLeastOneCallToTargetClass();
        } catch (IOException ex) {
            Logger.getLogger(TestTraceFilter.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public static Map<String, Set<File>> removeSubTraces(Map<String, Set<File>> traces, String packageName) throws IOException {
        Map<String, Set<File>> trimmedTraces = new HashMap<>();
        Map<String, Set<String>> testedClassOnlySequeces = new HashMap<>();
        Map<File, String> filePureSequences = new HashMap<>();
        
        
        for (String clazz : traces.keySet()){
            String fullName = String.format("%s.%s", packageName, clazz);
            Set<String> clazzOnlySequences = new HashSet<>();
            for (File trace : traces.get(clazz)){
                String origSeq = FileUtils.readFileToString(trace);
                List<String> origSeqParts = Stream.of(origSeq
                        .split("\\)"))
                        .map(sig -> sig + ")")
                        .collect(Collectors.toList());
                
                String pureSeq = origSeqParts.stream()
                        .filter(l -> l.toLowerCase().startsWith(fullName.toLowerCase()))
                        .collect(Collectors.joining(""));
                clazzOnlySequences.add(pureSeq);
                filePureSequences.put(trace, pureSeq);
            }
            
            testedClassOnlySequeces.put(clazz, clazzOnlySequences);
        }
        
        for (String clazz : traces.keySet()){
            for (File trace : traces.get(clazz)){
                String fileSeq = filePureSequences.get(trace);
                
                if (!testedClassOnlySequeces.get(clazz).stream()
                        .anyMatch(superseq -> !superseq.equals(fileSeq) && superseq.contains(fileSeq))){
                    if (trimmedTraces.get(clazz) == null){
                        trimmedTraces.put(clazz, new HashSet<>());
                    }
                    
                    trimmedTraces.get(clazz).add(trace);
                }
            }
        }
        
        return trimmedTraces;
    }
}
