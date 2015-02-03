/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.evaluation;

import com.mifmif.common.regex.Generex;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javamop.parser.astex.MOPSpecFileExt;
import javamop.parser.astex.mopspec.FormulaExt;
import javamop.parser.astex.mopspec.JavaMOPSpecExt;
import javamop.parser.astex.mopspec.PropertyAndHandlersExt;
import javamop.parser.main_parser.JavaMOPParser;
import javamop.parser.main_parser.ParseException;

/**
 *
 * @author Otmar
 */
public class MopExtractor {
    File mopFile;
    
    public MopExtractor(String mopFilePath){
        this(new File(mopFilePath));
    }

    public MopExtractor(File mopFile) {
        this.mopFile = mopFile;
    }

    public boolean containsParseableSpec() {
        try {
            if (null == getMOPPropertiesAndHandlers() || !(getMOPPropertiesAndHandlers().get(0).getProperty() instanceof FormulaExt)){
                return false;
}
            
            FormulaExt formula = (FormulaExt) getMOPPropertiesAndHandlers().get(0).getProperty();
            
            return formula.getType().equals("ere");
        } catch (ParseException ex) {
            Logger.getLogger(MopExtractor.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    // Since the library used to handle regexes only accepts single char
    // for transition's labels, it is necessary to map event names used
    // in mop regexes to a single char.
    Map<Character, String> componentsMapping = new HashMap<>();

    // Regex components;
    List<String> components;

    private String convertWordRegexToSingleCharRegex(String originalRegex) {
        this.components = Arrays.asList(originalRegex.split(" "));

        int currentCompIndex = 0;

        char letter = 'a';

        String simpleCharAlphabetRegex = originalRegex;

        while (currentCompIndex < components.size()) {
            if (!componentsMapping.containsValue(components.get(currentCompIndex))) {
                componentsMapping.put(letter, components.get(currentCompIndex));
                simpleCharAlphabetRegex = simpleCharAlphabetRegex.replace(components.get(currentCompIndex), Character.toString(letter));
                letter++;
            }
            currentCompIndex++;
        }

        simpleCharAlphabetRegex = simpleCharAlphabetRegex.replaceAll(" ", "");
        return simpleCharAlphabetRegex;

    }

    private List<String> getRegexFormulaExpansions(String formulaRegex) {
        String simplifiedRegex = convertWordRegexToSingleCharRegex(formulaRegex);
        Generex g = new Generex(simplifiedRegex);

        Set<String> regexBasedSequence = g.getAllMatchedStringsViaStatePermutations();

        List<String> translatedSequence;

        translatedSequence = regexBasedSequence.stream().map(encodedSeq
                -> encodedSeq.chars().mapToObj(i-> this.componentsMapping.get((char)i))
                        .collect(Collectors.joining(" ")))
                .collect(Collectors.toList());

        return translatedSequence;
    }

    public List<String> getForbiddenSequences() throws ParseException {
        List<String> forbidden = new LinkedList<>();
        if (containsParseableSpec()) {
            JavaMOPSpecExt spec = getMOPSpec().getSpecs().get(0);
            FormulaExt fext = (FormulaExt) spec.getPropertiesAndHandlers().get(0).getProperty();
            String formula = fext.getFormula(); // uniqueId of event may help in its expansion
            String parserRegex = convertWordRegexToSingleCharRegex(formula);
            forbidden.addAll(getRegexFormulaExpansions(parserRegex));
        }

        return forbidden;
    }

    private List<PropertyAndHandlersExt> getMOPPropertiesAndHandlers() throws ParseException {
        if (getJavaMopSpec() == null) {
            return null;
        }
        if (getJavaMopSpec().getPropertiesAndHandlers().isEmpty()) {
            return null;
        }

        return getJavaMopSpec().getPropertiesAndHandlers();
    }

    private JavaMOPSpecExt getJavaMopSpec() throws ParseException {
        if (getMOPSpec().getSpecs().isEmpty()) {
            return null;
        }
        return getMOPSpec().getSpecs().get(0);
    }

    private MOPSpecFileExt getMOPSpec() throws ParseException {
        return JavaMOPParser.parse(this.mopFile);
    }
}
