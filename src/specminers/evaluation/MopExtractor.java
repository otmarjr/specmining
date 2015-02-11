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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import org.apache.commons.lang3.tuple.Pair;
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

    private boolean isUsingFailHandler() throws ParseException {
        List<HandlerExt> handlers = getMOPPropertiesAndHandlers()
                .stream().flatMap(ph -> ph.getHandlerList().stream())
                .collect(Collectors.toList());

        return handlers.stream().anyMatch(h -> h.getState().equals("fail"));
    }

    private List<String> getRegexFormulaExpansions(String formulaRegex) throws ParseException {
        String simplifiedRegex = convertWordRegexToSingleCharRegex(formulaRegex);

                try{
        Generex g2 = new Generex(simplifiedRegex);
        }
        catch (IllegalArgumentException iex){
            System.out.println("Regex " + formulaRegex + " is problematic!");
        }

                
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

    private String getRegularExpressionWithoutTransformation(String originalRegex) {
        return originalRegex;
    }

    private String getRegexComplementWithParentherizedRegions(String originalRegex) {
        String splitter = "\\([\\w\\s\\|]+\\)[\\*\\+]*";

        Pattern p = Pattern.compile(splitter);
        Matcher m = p.matcher(originalRegex);

        List<String> groupedElements = new LinkedList<>();
        List<Pair<Integer, Integer>> groupingPositions = new LinkedList();

        while (m.find()) {
            Pair<Integer, Integer> startEndPositions = Pair.of(m.start(), m.end());
            String matching = originalRegex.substring(startEndPositions.getLeft(), startEndPositions.getRight());
            groupedElements.add(matching);
            groupingPositions.add(startEndPositions);
        }

        List<String> complementedElements = new LinkedList<>();
        int currentGroup = 0;

        for (int i = 0; i < originalRegex.length(); i++) {
            if (currentGroup < groupingPositions.size() && i == groupingPositions.get(currentGroup).getLeft()) {
                complementedElements.add(groupedElements.get(currentGroup));
                i = groupingPositions.get(currentGroup).getRight();
                currentGroup++;
            } else {
                int endOfCurrentToken = originalRegex.indexOf(" ", i);
                if (endOfCurrentToken == -1) {
                    endOfCurrentToken = originalRegex.length();
                }

                complementedElements.add(originalRegex.substring(i, endOfCurrentToken));
                i = endOfCurrentToken;
            }
        }

        Map<String, String> groupInversions = new HashMap<>();

        for (String group : groupedElements) {

            int groupIndex = complementedElements.indexOf(group);
            boolean thereAreElementsBeforeGroup = groupIndex > 0;
            boolean thereAreElementsAfterGroup = groupIndex < complementedElements.size() - 1;

            String mixElement;

            if (thereAreElementsBeforeGroup) {
                mixElement = complementedElements.get(groupIndex - 1).replace("*", "");
            } else {
                assert thereAreElementsAfterGroup;
                mixElement = complementedElements.get(groupIndex + 1).replace("*", "");
            }

            List<String> options = Arrays.asList(group.split("\\|"));
            List<String> invertedOptions = new LinkedList<>();

            for (String option : options) {
                List<String> optionComponents = Arrays.asList(option.split(" "))
                        .stream().filter(s -> s.trim().length() > 0)
                        .collect(Collectors.toList());
                List<String> invertedOptionComponents = new LinkedList<>();

                if (thereAreElementsBeforeGroup) {
                    String suffix = "";
                    if (optionComponents.get(0).contains(")")){
                        suffix = optionComponents.get(0).substring(optionComponents.get(0).indexOf(")"));
                    }
                    invertedOptionComponents.add(optionComponents.get(0).replace(")", "").replace("*", "").replace("+", ""));
                    invertedOptionComponents.add(mixElement + suffix);
                }
                else{
                    String preamble = optionComponents.get(0).contains("(") ? "(" : "";
                    
                    invertedOptionComponents.add(preamble + mixElement);
                    invertedOptionComponents.add(optionComponents.get(0).replace("(", ""));
                }
                
                invertedOptionComponents.addAll(optionComponents.subList(1, optionComponents.size()));
                String invertedOption = invertedOptionComponents.stream()
                        .collect(Collectors.joining(" "));

                invertedOptions.add(invertedOption);
            }

            String invertedGroup = invertedOptions.stream()
                    .collect(Collectors.joining("|"));

            groupInversions.put(group, invertedGroup);
        }

        List<String> complementedInvertedElements = new LinkedList<>();
        currentGroup = 0;

        for (String complement : complementedElements) {
            if (groupedElements.contains(complement)) {
                complementedInvertedElements.add(groupInversions.get(complement));
            } else {
                complementedInvertedElements.add(complement);
            }
        }

        String result = complementedInvertedElements.stream().collect(Collectors.joining(" "));

        return result;
    }

    private String getRegexComplement(String originalRegex) {
        List<String> complementedElements = Arrays.asList(originalRegex.split(" "));

        int pivotIndex = complementedElements.size() / 2;
        List<String> secondHalf = complementedElements.subList(pivotIndex, complementedElements.size());
        List<String> firstHalf = complementedElements.subList(0, pivotIndex);

        List<String> invertedList = new LinkedList<>();

        // Invert the positions, but first, switching from * to + operator
        // in order to assure the presence of a violation in the generated
        // regular expression.
        for (String s : secondHalf) {
            invertedList.add(s.replace("*", ""));
        }

        invertedList.addAll(firstHalf.stream().map(s -> s.replace("*", "")).collect(Collectors.toList()));
        this.components = invertedList;

        String result = invertedList.stream().collect(Collectors.joining(" "));

        return result;
    }

    private String getRegularExpressionWithComplement(String originalRegex) {
        boolean containsParenthesis = originalRegex.contains("(");

        RegexExtractionStrategy strategy = r -> getRegexComplement(r);

        if (containsParenthesis) {
            strategy = r -> getRegexComplementWithParentherizedRegions(r);
        }

        return strategy.getRegexComplement(originalRegex).replace(originalRegex, originalRegex);
    }

    private String getRegex(FormulaExt fext) throws ParseException {
        boolean shouldGetRegexComplement = isUsingFailHandler();

        String originalRegex = fext.getFormula();

        this.components = Arrays.asList(originalRegex.split(" "));

        RegexExtractionStrategy strategy = r -> getRegularExpressionWithoutTransformation(r);

        if (shouldGetRegexComplement) {
            strategy = r -> getRegularExpressionWithComplement(r);
        }

        return strategy.getRegexComplement(originalRegex);
    }

    public List<String> getForbiddenSequences() throws ParseException {
        List<String> forbidden = new LinkedList<>();
        if (containsParseableSpec()) {
            JavaMOPSpecExt spec = getMOPSpec().getSpecs().get(0);
            FormulaExt fext = (FormulaExt) spec.getPropertiesAndHandlers().get(0).getProperty();
            String formula = getRegex(fext);
            String parserRegex = convertWordRegexToSingleCharRegex(formula);
            forbidden.addAll(getRegexFormulaExpansions(parserRegex));
            forbidden = convertEventsToMethodSignatures(forbidden);
            // forbidden = removeRedundantSequences(forbidden);
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

    private String getFormattedMethodSignature(MethodPointCut methodPointCut) {
        String methodSignature = methodPointCut.getSignature().getMemberName();
        if (methodSignature.equals("new")) {
            methodSignature = "<init>";
        }
        return String.format("%s.%s.%s()", getTestedClassPackage(),
                methodPointCut.getSignature().getOwner().getOp().replaceAll("[^A-Za-z0-9]", ""),
                methodSignature);
    }

    private List<MethodPointCut> flattenCombinedPointCut(CombinedPointCut pointcut, List<MethodPointCut> flattendSoFar) {
        for (PointCut pc : pointcut.getPointcuts()) {
            if (pc instanceof CombinedPointCut) {
                flattenCombinedPointCut((CombinedPointCut) pc, flattendSoFar);
            } else {
                if (pc instanceof MethodPointCut) {
                    flattendSoFar.add((MethodPointCut) pc);
                }
            }
        }

        return flattendSoFar;
    }

    private List<String> getCorrespondingMethodSignatureSequences(EventDefinitionExt event) {
        PointCut pc;
        pc = event.getPointCut();

        List<String> methodSigs;
        methodSigs = new LinkedList<>();

        if (pc instanceof CombinedPointCut) {
            CombinedPointCut cpc = (CombinedPointCut) pc;

            Optional<TargetPointCut> tpc;
            tpc = cpc.getPointcuts().stream().filter(ppc -> ppc instanceof TargetPointCut)
                    .map(ppc -> (TargetPointCut) ppc).findFirst();

            List<MethodPointCut> methodPointcuts = flattenCombinedPointCut(cpc, new LinkedList<>());

            methodPointcuts.stream()
                    .map(mpc -> getFormattedMethodSignature(mpc))
                    .forEach(fullSig -> methodSigs.add(fullSig));
        } else {
            if (pc instanceof MethodPointCut) {
                methodSigs.add(getFormattedMethodSignature((MethodPointCut) pc));
            }
        }

        return methodSigs;
    }

    private List<String> makeListsCartesianProduct(List<String> list1, List<String> list2) {
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

            for (String ev : evs.keySet()) {
                if (!this.correspondingMethodSignaturesOfEvents.containsKey(ev)) {
                    this.correspondingMethodSignaturesOfEvents.put(ev, new LinkedList<>());
                }

                this.correspondingMethodSignaturesOfEvents.get(ev).addAll(getCorrespondingMethodSignatureSequences(evs.get(ev)));
            }
        }

        List<String> expandedForbiddenSequence;
        expandedForbiddenSequence = new LinkedList<>();

        for (String seq : forbidden) {
            String[] forbEvents = seq.split("\\s");

            List<String> joinedList = this.correspondingMethodSignaturesOfEvents.get(forbEvents[0]);

            for (int i = 1; i < forbEvents.length; i++) {
                List<String> currentList = this.correspondingMethodSignaturesOfEvents.get(forbEvents[i]);
                joinedList = makeListsCartesianProduct(joinedList, currentList);
            }

            expandedForbiddenSequence.addAll(joinedList);
        }

        return expandedForbiddenSequence;

    }
}
