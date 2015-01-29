/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.evaluation;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javamop.parser.ast.MOPSpecFile;
import javamop.parser.astex.MOPSpecFileExt;
import javamop.parser.astex.mopspec.JavaMOPSpecExt;
import javamop.parser.main_parser.JavaMOPParser;
import javamop.parser.main_parser.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import specminers.StringHelper;

/**
 *
 * @author Otmar
 */
public class MopExtractor {
    File mopSpecFile;
    private String extendedRegularExpression;
    private static final String ERE_PATTERN = "^ere[\\s\\t]+\\:[\\s\\t]+(.+)$";
    MOPSpecFileExt parsedMopSpecFile;
    MOPSpecFile translatedMopFile;
    
    public MopExtractor(String mopFilePath){
        this(new File(mopFilePath));
    }
    
    
    public MopExtractor(File mopFile){
        this.mopSpecFile = mopFile;
    }
    
    private String getMatchingLine() throws IOException {
        return getLinesInFile()
                .stream()
                .filter(l -> l.matches(ERE_PATTERN))
                .findFirst()
                .orElse(null);
    }
    
    private List<String> getLinesInFile() throws IOException {
        return FileUtils.readLines(mopSpecFile)
                .stream()
                .map(l -> l.trim())
                .collect(Collectors.toList());
    }
        
    public boolean containsExtendedRegularExpression() throws IOException {
        return getMatchingLine() != null;
    }
    
    public String getExtendedRegularExpression() throws IOException {
        if (!containsExtendedRegularExpression())
            return null;
        else {
            if (StringUtils.isBlank(extendedRegularExpression)){
                String matchingLine = getMatchingLine(); 
                extendedRegularExpression =  StringHelper.extractSingleValueWithRegex(matchingLine, ERE_PATTERN, 1);
            }
            
            return extendedRegularExpression;
            
        }
    }
    
    private MOPSpecFileExt getInputMopFileSpec() throws ParseException{
        if (this.parsedMopSpecFile == null){
            this.parsedMopSpecFile = JavaMOPParser.parse(this.mopSpecFile);
        }
        
        return this.parsedMopSpecFile;
    }
    
    /*
    private MOPSpecFile getTranslatedMopSpec() throws ParseException {
        if (this.translatedMopFile == null){
            this.translatedMopFile = JavaMOPExtender.translateMopSpecFile(getInputMopFileSpec());
        }
        
        return this.translatedMopFile;
    }
    */ 
    
    public String getTargetClassName() throws ParseException {
        //return this.translatedMopFile.getSpecs().get(0).getParameters().get(0).getType().toString();
        if (this.getInputMopFileSpec().getSpecs().get(0).getParameters().toList().isEmpty()){
            return "";
        }
        return this.getInputMopFileSpec().getSpecs().get(0).getParameters().get(0).getType().toString();
    }
    
    private JavaMOPSpecExt getMopSpec() throws ParseException {
        if (this.getInputMopFileSpec().getSpecs().size() > 1){
            System.out.println("MUST HANDLE MULTIPLE SPECS!");
            throw new RuntimeException("Multiple specs found on file!");
        }
        
        return this.getInputMopFileSpec().getSpecs().get(0);
    }
    
    public String getExpandedRegularExpression() throws ParseException {
        String expansion = "";
        getMopSpec().getPropertiesAndHandlers().get(0);
        return expansion;
    }
}
