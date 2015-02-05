/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.evaluation;

import com.mifmif.common.regex.Generex;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javamop.parser.ast.aspectj.CombinedPointCut;
import javamop.parser.ast.aspectj.MethodPointCut;
import javamop.parser.ast.aspectj.PointCut;
import javamop.parser.ast.aspectj.TargetPointCut;
import javamop.parser.astex.MOPSpecFileExt;
import javamop.parser.astex.mopspec.EventDefinitionExt;
import javamop.parser.astex.mopspec.FormulaExt;
import javamop.parser.astex.mopspec.HandlerExt;
import javamop.parser.astex.mopspec.JavaMOPSpecExt;
import javamop.parser.astex.mopspec.PropertyAndHandlersExt;
import javamop.parser.main_parser.JavaMOPParser;
import javamop.parser.main_parser.ParseException;
import specminers.FileHelper;
import specminers.StringHelper;

/**
 *
 * @author Otmar
 */
public class MopExtractor {

    public static final String MOP_FILES_EXTENSION = "mop";
    private static final String PARSEABLE_FORMULA_TYPE = "ere";
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

            return formula.getType().equals(PARSEABLE_FORMULA_TYPE);
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

    private boolean isUsingFailHandler() throws ParseException{
        List<HandlerExt> handlers = getMOPPropertiesAndHandlers()
                .stream().flatMap(ph -> ph.getHandlerList().stream())
                .collect(Collectors.toList());
        
        return handlers.stream().anyMatch(h -> h.getState().equals("fail"));
    }
    private List<String> getRegexFormulaExpansions(String formulaRegex) throws ParseException {
        String simplifiedRegex = convertWordRegexToSingleCharRegex(formulaRegex);
        boolean shouldGetRegexComplement = isUsingFailHandler();
        Generex g = new Generex(simplifiedRegex,shouldGetRegexComplement);

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

    private Map<String, List<String>> correspondingMethodSignaturesOfEvents;

    private String getTestedClassPackage() {
        String regex = "^import (java\\.\\w+)\\.\\*;$";
        
        try {
            String matchingLine = FileHelper.getTrimmedFileLines(mopFile)
                    .stream().filter(l -> l.matches(regex))
                    .findFirst()
                    .orElse("");
            
            return StringHelper.extractSingleValueWithRegex(matchingLine, regex, 1);
        } catch (IOException ex) {
            Logger.getLogger(MopExtractor.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
                
    }
    private String getFormattedMethodSignature(MethodPointCut methodPointCut){
        String methodSignature = methodPointCut.getSignature().getMemberName();
        if (methodSignature.equals("new")){
            methodSignature = "<init>";
        }
        return String.format("%s.%s.%s()" , getTestedClassPackage(),
                    methodPointCut.getSignature().getOwner().getOp().replaceAll("[^A-Za-z0-9]", ""),
                    methodSignature);
    }
    
    private List<MethodPointCut> flattenCombinedPointCut(CombinedPointCut pointcut, List<MethodPointCut> flattendSoFar){
        for (PointCut pc : pointcut.getPointcuts()){
            if (pc instanceof CombinedPointCut){
                flattenCombinedPointCut((CombinedPointCut)pc, flattendSoFar);
            }
            else {
                if (pc instanceof MethodPointCut){
                    flattendSoFar.add((MethodPointCut)pc);
                }
            }
        }
        
        return flattendSoFar;
    }
    
    private List<String> getCorrespondingMethodSignatureSequences(EventDefinitionExt event){
        PointCut pc;
        pc = event.getPointCut();
        
        List<String> methodSigs;
        methodSigs = new LinkedList<>();
        
        if (pc instanceof CombinedPointCut){
            CombinedPointCut cpc = (CombinedPointCut)pc;
            
            Optional<TargetPointCut> tpc;
            tpc = cpc.getPointcuts().stream().filter(ppc -> ppc instanceof TargetPointCut)
                    .map(ppc -> (TargetPointCut)ppc).findFirst();
            
            List<MethodPointCut> methodPointcuts = flattenCombinedPointCut(cpc, new LinkedList<>());
            
            
            methodPointcuts.stream()
                    .map(mpc -> getFormattedMethodSignature(mpc))
                    .forEach(fullSig -> methodSigs.add(fullSig));
        }
        else{
            if (pc instanceof MethodPointCut){
                methodSigs.add(getFormattedMethodSignature((MethodPointCut)pc));
            }
        }
        
        return methodSigs;
    }
    
    private List<String> makeListsCartesianProduct(List<String> list1, List<String> list2){
        List<String> output = new LinkedList<>();
        
        list1.stream().forEach((s) -> {
            list2.stream().forEach((t) -> {
                output.add(s + " " + t);
            });
        });
        return output;
    }
    private List<String> convertEventsToMethodSignatures(List<String> forbidden) throws ParseException {
        if (this.correspondingMethodSignaturesOfEvents == null) {
            this.correspondingMethodSignaturesOfEvents = new HashMap<>();
            
            Map<String, EventDefinitionExt> evs;
            evs = getJavaMopSpec().getEvents().stream().collect(Collectors.toMap(ev -> ev.getId(), ev -> ev));
            
            for(String ev : evs.keySet()){
                if (!this.correspondingMethodSignaturesOfEvents.containsKey(ev)){
                    this.correspondingMethodSignaturesOfEvents.put(ev, new LinkedList<>());
                }
                
                this.correspondingMethodSignaturesOfEvents.get(ev).addAll(getCorrespondingMethodSignatureSequences(evs.get(ev)));
            }
        }
        
        List<String> expandedForbiddenSequence;
        expandedForbiddenSequence = new LinkedList<>();
        
        for(String seq : forbidden){
            String[] forbEvents = seq.split("\\s");
            
            List<String> joinedList = this.correspondingMethodSignaturesOfEvents.get(forbEvents[0]);
            
            for (int i=1;i<forbEvents.length;i++){
                List<String> currentList = this.correspondingMethodSignaturesOfEvents.get(forbEvents[i]);
                joinedList = makeListsCartesianProduct(joinedList, currentList);
            }
            
            expandedForbiddenSequence.addAll(joinedList);
        }
        
        return expandedForbiddenSequence;
                
    }
}
