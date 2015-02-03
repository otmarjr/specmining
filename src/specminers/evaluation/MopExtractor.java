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

    public static final String MOP_FILES_EXTENSION = "mop";
    private final File mopFile;

    public MopExtractor(String mopFilePath) {
        this(new File(mopFilePath));
    }

    public MopExtractor(File mopFile) {
        this.mopFile = mopFile;
    }

    public boolean containsParseableSpec() {
        try {
            if (null == getMOPPropertiesAndHandlers() || !(getMOPPropertiesAndHandlers().get(0).getProperty() instanceof FormulaExt)) {
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
    Map<Character, String> eventsMappingMapping = new HashMap<>();

    // Regex components;
    List<String> components;

    // Original events corresponding to input alphabet of the original Mop regex
    Set<String> events;

    private String convertWordRegexToSingleCharRegex(String originalRegex) throws ParseException {

        this.components = Arrays.asList(originalRegex.split(" "));
        this.events = getJavaMopSpec().getEvents().stream()
                .map(ev -> ev.getId())
                .collect(Collectors.toSet());

        char letter = 'a';

        String simpleCharAlphabetRegex = originalRegex;

        for (String ev : this.events) {
            eventsMappingMapping.put(letter, ev);
            simpleCharAlphabetRegex = simpleCharAlphabetRegex.replace(ev, Character.toString(letter));
            letter++;
        }

        simpleCharAlphabetRegex = simpleCharAlphabetRegex.replaceAll(" ", "");
        return simpleCharAlphabetRegex;

    }

    private List<String> getRegexFormulaExpansions(String formulaRegex) throws ParseException {
        String simplifiedRegex = convertWordRegexToSingleCharRegex(formulaRegex);
        Generex g = new Generex(simplifiedRegex);

        Set<String> regexBasedSequence = g.getAllMatchedStringsViaStatePermutations();

        List<String> translatedSequence;

        translatedSequence = regexBasedSequence.stream().map(encodedSeq
                -> encodedSeq.chars().mapToObj(i -> this.eventsMappingMapping.get((char) i))
                .collect(Collectors.joining(" ")))
                .collect(Collectors.toList());

        return translatedSequence;
    }

    private List<String> removeRedundantSequences(List<String> potentiallyRedundant) {
        // Keeps only the sequences which are superstrings of oter sequences.
        return potentiallyRedundant.stream()
                .filter(seq
                        -> potentiallyRedundant
                        .stream()
                        .allMatch(seq2 -> seq.equals(seq2) || !seq.contains(seq2))
                ).collect(Collectors.toList());
    }

    public List<String> getForbiddenSequences() throws ParseException {
        List<String> forbidden = new LinkedList<>();
        if (containsParseableSpec()) {
            JavaMOPSpecExt spec = getMOPSpec().getSpecs().get(0);
            FormulaExt fext = (FormulaExt) spec.getPropertiesAndHandlers().get(0).getProperty();
            String formula = fext.getFormula(); // uniqueId of event may help in its expansion
            String parserRegex = convertWordRegexToSingleCharRegex(formula);
            forbidden.addAll(getRegexFormulaExpansions(parserRegex));
            forbidden = removeRedundantSequences(forbidden);
            forbidden = convertEventsToMethodSignatures(forbidden);
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

    private Map<String, String> correspondingMethodSignaturesOfEvents;
    private List<String> convertEventsToMethodSignatures(List<String> forbidden) {
         this.correspondingMethodSignaturesOfEvents = new HashMap<>();
         
         return forbidden;
    }
}
