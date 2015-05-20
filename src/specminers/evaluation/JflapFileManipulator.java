/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.evaluation;

import automata.State;
import automata.Transition;
import automata.fsa.FSATransition;
import automata.fsa.FiniteStateAutomaton;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;
import dk.brics.automaton.RegExp;
import file.XMLCodec;
import java.awt.Point;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.ClassUtils;
import org.reflections.ReflectionUtils;
import static org.reflections.ReflectionUtils.withModifier;
import org.reflections.Reflections;

/**
 *
 * @author otmarpereira
 */
public class JflapFileManipulator {

    File jffFile;
    FiniteStateAutomaton automaton;
    private String correspondingClass;
    private State stateForGettersAndSettersOnly;

    public JflapFileManipulator(String jffPath) {
        this(new File(jffPath));
    }

    public JflapFileManipulator(File jff) {
        this.jffFile = jff;
        this.stateForGettersAndSettersOnly = null;
    }

    private void parseAutomaton() {
        XMLCodec jffCodec = new XMLCodec();
        this.automaton = (FiniteStateAutomaton) jffCodec.decode(this.jffFile, null);
        // Pradel's reference specs follow a pattern, where there is only one
        // transition leaving the initial stating, and this transition correspond
        // to the class' constructor invocation.
        this.correspondingClass = ((FSATransition) this.automaton.getTransitionsFromState(this.automaton.getInitialState())[0]).getLabel().replace(".<init>()", "");

    }

    private String getPackageName(String classFullName) {
        if (classFullName != null && classFullName.contains(".")) {
            int lastDotPosition = classFullName.lastIndexOf(".");
            return classFullName.substring(0, lastDotPosition);
        }
        return "";
    }

    public Set<String> getAllTransitionLabels() {
        this.parseAutomaton();
        return Arrays.stream(this.automaton.getFSATransitions())
                .map(fsat -> fsat.getLabel())
                .collect(Collectors.toSet());
    }

    public void includeTransitions(Map<String, Set<String>> publicAPI, boolean restrictOnlyToClassMethods) {
        this.parseAutomaton();
        if (restrictOnlyToClassMethods) {
            this.addTransitionsFromClass(publicAPI.getOrDefault(correspondingClass, null));
        } else {
            String currentClassPackage = getPackageName(correspondingClass);

            List<String> samePackageClasses = publicAPI.keySet().stream()
                    .filter(k -> getPackageName(k).equals(currentClassPackage))
                    .collect(Collectors.toList());

            samePackageClasses.forEach(c -> {
                addGettersAndSettersAfterInstantiation(publicAPI.getOrDefault(c, null));
            });
            samePackageClasses.forEach(c -> addTransitionsFromClass(publicAPI.getOrDefault(c, null)));
        }

    }

    private void addGettersAndSettersAfterInstantiation(Set<String> transitions) {
        if (transitions != null && !transitions.isEmpty()) {
            if (stateForGettersAndSettersOnly == null) {
                State initialSt = this.automaton.getInitialState();
                stateForGettersAndSettersOnly = this.automaton.createState(initialSt.getPoint());
                this.automaton.addFinalState(stateForGettersAndSettersOnly);
            }

            FSATransition instatiationTransition = Stream.of(this.automaton.getFSATransitions())
                    .filter(t -> t.getFromState().equals(this.automaton.getInitialState()) && t.getLabel().contains("<init>"))
                    .collect(Collectors.toList())
                    .get(0);

            State firstStateAfterInstantiation = instatiationTransition.getToState();

            Set<String> upperCases = new HashSet<>();

            for (int i = 0; i < 26; i++) {
                char upper = (char) ('A' + i);
                upperCases.add(Character.toString(upper));
            }

            Set<String> getterMethodWildCards = upperCases.stream()
                    .map(ul -> ".get" + ul).collect(Collectors.toSet());

            Set<String> setterMethodWildCards = upperCases.stream()
                    .map(ul -> ".set" + ul).collect(Collectors.toSet());

            Set<String> specialGetterIs = upperCases.stream()
                    .map(ul -> ".is" + ul).collect(Collectors.toSet());

            Set<String> specialGetterTo = upperCases.stream()
                    .map(ul -> ".to" + ul).collect(Collectors.toSet());

            Set<String> getterAndSettersTransitions = transitions.stream()
                    .filter(t -> getterMethodWildCards.stream().anyMatch(m -> t.contains(m))
                            || setterMethodWildCards.stream().anyMatch(m -> t.contains(m))
                            || t.contains(".hashCode") || t.contains(".equals")
                            || specialGetterIs.stream().anyMatch(m -> t.contains(m))
                            || specialGetterTo.stream().anyMatch(m -> t.contains(m)))
                    .collect(Collectors.toSet());

            for (String methodSig : getterAndSettersTransitions) {
                Set<FSATransition> acceptedMethodSigs = getFSATransitionsFromState(firstStateAfterInstantiation);

                if (!acceptedMethodSigs.stream().anyMatch(t -> t.getLabel().equalsIgnoreCase(methodSig) && t.getFromState().equals(firstStateAfterInstantiation))) {
                    FSATransition fsaT = new FSATransition(firstStateAfterInstantiation, stateForGettersAndSettersOnly, methodSig);
                    FSATransition fsaTransFromInit = new FSATransition(this.automaton.getInitialState(), stateForGettersAndSettersOnly, methodSig);
                    this.automaton.addTransition(fsaT);
                    this.automaton.addTransition(fsaTransFromInit);
                }

                acceptedMethodSigs = getFSATransitionsFromState(stateForGettersAndSettersOnly);

                if (!acceptedMethodSigs.stream().anyMatch(t -> t.getLabel().equalsIgnoreCase(methodSig) && t.getToState().equals(stateForGettersAndSettersOnly))) {
                    FSATransition fsaT = new FSATransition(stateForGettersAndSettersOnly, stateForGettersAndSettersOnly, methodSig);
                    this.automaton.addTransition(fsaT);
                }
            }
        }
    }

    private Set<FSATransition> getFSATransitionsFromState(State st) {
        return Stream.of(automaton.getFSATransitions()).filter(t -> t.getFromState().equals(st))
                .collect(Collectors.toSet());
    }

    private void addTransitionsFromClass(Set<String> transitions) {
        if (transitions != null) {
            for (State st : this.automaton.getStates()) {
                if (!st.equals(this.stateForGettersAndSettersOnly)) {
                    for (String methodSig : transitions) {
                        Set<FSATransition> acceptedMethodSigs = getFSATransitionsFromState(st);

                        if (!acceptedMethodSigs.stream().anyMatch(t -> t.getLabel().equalsIgnoreCase(methodSig))) {
                            FSATransition fsaT = new FSATransition(st, st, methodSig);
                            this.automaton.addTransition(fsaT);
                        }
                    }
                }
            }
        }
    }

    public void saveToFile(String targetPath) {
        XMLCodec jffCodec = new XMLCodec();
        jffCodec.encode(this.automaton, new File(targetPath), null);
    }

    Map<String, Character> labelsMappingJffToDK;
    Map<Character, String> labelsDkLabelToJffLabel;

    public void loadJffLabelsMapToChars() {
        Character currentChar = 'a';
        this.labelsDkLabelToJffLabel = new HashMap<>();
        this.labelsMappingJffToDK = new HashMap<>();

        for (String l : this.getAllTransitionLabels()) {
            labelsMappingJffToDK.put(l, currentChar);
            labelsDkLabelToJffLabel.put(currentChar, l);
            currentChar++;
        }
    }

    private Set<String> getAllExpansionsForSequenceWithStarWildcard(String sequence) {
        Set<String> expansions = new HashSet<>();

        int wildcardIndex = sequence.indexOf("*");
        if (wildcardIndex == -1) {
            expansions.add(sequence);
            return expansions;
        }

        int endIndexPreviousMethod = sequence.substring(0, wildcardIndex - 1).lastIndexOf(")");
        String beforeWildcardMethod = sequence.substring(0, endIndexPreviousMethod + 1);
        String afterWildcard = "";

        if (wildcardIndex < sequence.length() - 1) {
            afterWildcard = sequence.substring(wildcardIndex + 1);
        }

        String wildcardExpresion = sequence.substring(endIndexPreviousMethod + 1, wildcardIndex + 1).trim();
        String classWildcard = wildcardExpresion.substring(wildcardExpresion.lastIndexOf("."));
        String className = wildcardExpresion.replace(classWildcard, "");

        String classWildCardRegex = "^" + classWildcard.replace(".", "").replace("*", ".+") + "$";
        try {
            Class<?> cls = Class.forName(className);
            //List<Method> classMethods = Arrays.asList(cls.getMethods());
            Set<Method> classMethods = ReflectionUtils.getAllMethods(cls, Predicates.not(withModifier(Modifier.PRIVATE)));

            for (Method m : classMethods) {
                if (m.getName().matches(classWildCardRegex)) {
                    String expansion = String.format("%s%s%s", beforeWildcardMethod, className + "." + m.getName(), afterWildcard);
                    expansions.add(expansion);
                }
            }

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(JflapFileManipulator.class.getName()).log(Level.SEVERE, null, ex);
            // throw new RuntimeException(ex);
        }

        return expansions;
    }

    private Set<String> getAllExpansionsForSequenceWithSubtypesWildcard(String sequence) {
        Set<String> expansions = new HashSet<>();

        int wildcardIndex = sequence.indexOf("+");
        if (wildcardIndex == -1) {
            expansions.add(sequence);
            return expansions;
        }
        int endIndexPreviousMethod = sequence.substring(0, wildcardIndex - 1).lastIndexOf(")");
        String beforeWildcardMethod = sequence.substring(0, endIndexPreviousMethod + 1);
        String afterWildcard = "";

        if (wildcardIndex < sequence.length() - 1) {
            afterWildcard = sequence.substring(wildcardIndex + 1);
        }

        String wildcardExpresion = sequence.substring(endIndexPreviousMethod + 1, wildcardIndex + 1).trim();
        try {
            String baseClassName = wildcardExpresion.replace("+", "");
            Class<?> cls = Class.forName(baseClassName);
            Reflections reflections = new Reflections(baseClassName);
            Set subTypes = reflections.getSubTypesOf(cls.getClass());

            for (Object t : subTypes) {
                Class<?> type = (Class<?>) t;
                String className = t.getClass().getName();

                if (className.startsWith("java.util") || className.startsWith("java.net")) {
                    String expansion = String.format("%s%s%s", beforeWildcardMethod, type.getName(), afterWildcard);
                    expansions.add(expansion);
                }
            }

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(JflapFileManipulator.class.getName()).log(Level.SEVERE, null, ex);
        }

        return expansions;
    }

    private Set<String> expandForbiddenSequencesWithWildCards(List<String> forbiddenSequences) {
        Set<String> expanded = new HashSet<>();

        final String allSubtypesWildCard = "+";

        List<String> forbiddenWithExpandedSubtypes = new LinkedList<>();

        for (String seq : forbiddenSequences) {
            if (seq.contains(allSubtypesWildCard)) {
                List<String> components = Arrays.asList(seq.split("\\s"));
                Set<String> currentExpansions = getAllExpansionsForSequenceWithSubtypesWildcard(components.get(0));

                for (int i = 1; i < components.size(); i++) {
                    Set<String> componentExpansions = getAllExpansionsForSequenceWithSubtypesWildcard(components.get(i));
                    Set<List<String>> joinedSets = Sets.cartesianProduct(currentExpansions, componentExpansions);
                    currentExpansions = joinedSets.stream().map(l -> l.stream().collect(Collectors.joining(" "))).collect(Collectors.toSet());
                }
                forbiddenWithExpandedSubtypes.addAll(currentExpansions);
            } else {
                forbiddenWithExpandedSubtypes.add(seq);
            }
        }

        final String starWildCard = "*";

        for (String seq : forbiddenWithExpandedSubtypes) {
            if (seq.contains(starWildCard)) {
                List<String> components = Arrays.asList(seq.split("\\s"));
                Set<String> currentExpansions = getAllExpansionsForSequenceWithStarWildcard(components.get(0));

                for (int i = 1; i < components.size(); i++) {
                    Set<String> componentExpansions = getAllExpansionsForSequenceWithStarWildcard(components.get(i));
                    Set<List<String>> joinedSets = Sets.cartesianProduct(currentExpansions, componentExpansions);
                    currentExpansions = joinedSets.stream().map(l -> l.stream().collect(Collectors.joining(" "))).collect(Collectors.toSet());
                }
                expanded.addAll(currentExpansions);
            } else {
                expanded.add(seq);
            }
        }

        return expanded;
    }

    private dk.brics.automaton.Automaton getDkBricsAutomatonWithChars() {
        this.loadJffLabelsMapToChars();
        JflapToDkBricsTwoWayAutomatonConverter converter = new JflapToDkBricsTwoWayAutomatonConverter(automaton);
        dk.brics.automaton.Automaton dkAut = converter.convertToDkBricsAutomaton(labelsMappingJffToDK);
        

        return dkAut;
    }

    public void removeInvalidSequences(List<String> forbiddenSequences) {

        dk.brics.automaton.Automaton dkAut = this.getDkBricsAutomatonWithChars();

        Set<String> expandedForbiddenSeqs = expandForbiddenSequencesWithWildCards(forbiddenSequences);

        String currentPackageName = this.getPackageName(this.correspondingClass);
        expandedForbiddenSeqs = expandedForbiddenSeqs.stream()
                .filter(seq
                        -> Arrays.asList(seq.split("\\)"))
                        .stream().allMatch(signature
                                -> signature.startsWith(currentPackageName)
                        ))
                .collect(Collectors.toSet());

        Set<String> encodedForbiddenSeqs = new HashSet<>();

        Map<String, String> forbiddenEncodings = new HashMap<>();

        for (String seq : expandedForbiddenSeqs) {
            String sequenceEncodeAsCharsPerMethodSignature = "";
            int lastStartIndex = 0;
            for (int i = 0; i < seq.length(); i++) {
                if (seq.charAt(i) == ')') {
                    String lastSig = seq.substring(lastStartIndex, i + 1).trim();
                    Character c = this.labelsMappingJffToDK.get(lastSig);
                    sequenceEncodeAsCharsPerMethodSignature += Character.toString(c);
                    lastStartIndex = i + 1;
                }
            }
            encodedForbiddenSeqs.add(sequenceEncodeAsCharsPerMethodSignature);
            forbiddenEncodings.put(sequenceEncodeAsCharsPerMethodSignature, seq);
        }

        char lastChar = this.labelsDkLabelToJffLabel.keySet().stream()
                .sorted()
                .collect(Collectors.toList())
                .get(this.labelsDkLabelToJffLabel.size() - 1);

        char firstChar = this.labelsDkLabelToJffLabel.keySet().stream()
                .sorted()
                .collect(Collectors.toList())
                .get(0);

        for (String encForb : encodedForbiddenSeqs) {
            String forbiddenAsRegex = String.format("%s %s %s", "[" + firstChar + "-" + lastChar + "]*", encForb, "[" + firstChar + "-" + lastChar + "]*");
            RegExp forbRegex = new RegExp(forbiddenAsRegex);
            dk.brics.automaton.Automaton forbAut = forbRegex.toAutomaton();
            dkAut = dkAut.minus(forbAut);
        }
        JflapToDkBricsTwoWayAutomatonConverter converter = new JflapToDkBricsTwoWayAutomatonConverter(automaton);
        FiniteStateAutomaton prunedFSA = converter.convertToJFlapFSA(dkAut, labelsDkLabelToJffLabel);

        this.automaton = prunedFSA;
    }

    public boolean acceptsSequence(List<String> sequence) {
        State s = runInputOnAutomaton(sequence);

        return this.automaton.isFinalState(s);
    }

    private String convertJffSequenceToDkBricsAutomatonSequence(List<String> sequence) {
        String convertedSeq;

        convertedSeq = sequence
                .stream()
                .filter(call -> this.labelsMappingJffToDK.containsKey(call))
                .map(signature -> Character.toString(this.labelsMappingJffToDK.get(signature)))
                .collect(Collectors.joining(""));
        return convertedSeq;
    }

    public double calculateSequencesRecall(Set<List<String>> minedSequences) {
        double recall;
        dk.brics.automaton.Automaton dkAut = this.getDkBricsAutomatonWithChars();

        // Remove self loops
        for (dk.brics.automaton.State q : dkAut.getStates()) {
            Set<dk.brics.automaton.Transition> toRemoveTransitions = new HashSet<>();
            for (dk.brics.automaton.Transition t : q.getTransitions()) {
                if (t.getDest().equals(q)) {
                    //if (!q.isAccept()) {
                    toRemoveTransitions.add(t);
                    //}
                }
            }

            q.getTransitions().removeAll(toRemoveTransitions);
        }

        int maximumSize = dkAut.getNumberOfStates();
        if (maximumSize > 1) {
            maximumSize--;
        }
        Set<String> universe = dkAut.getStrings(maximumSize);

        Set<String> minedSequencesConvertedToDkAlphabet = minedSequences.stream()
                .map(seq -> convertJffSequenceToDkBricsAutomatonSequence(seq))
                .collect(Collectors.toSet());

        double numberOfValidMinedSequences = minedSequencesConvertedToDkAlphabet.stream().filter(seq
                -> universe.stream().anyMatch(us -> us.contains(seq)))
                .count() * 1D;

        recall = numberOfValidMinedSequences / universe.size();

        return recall;
    }

    private String charDkInputToOriginalSequence(String dkAutSequence) {
        List<String> events = new LinkedList<>();

        for (int j = 0; j < dkAutSequence.length(); j++) {
            events.add(this.labelsDkLabelToJffLabel.get(dkAutSequence.charAt(j)));
        }

        String seq = events.stream().collect(Collectors.joining(", "));

        return seq;
    }

    private automata.State runInputOnAutomaton(List<String> input) {
        if (this.automaton == null) {
            this.parseAutomaton();
        }
        automata.State s = this.automaton.getInitialState();

        for (String label : input) {
            Optional<FSATransition> ft = Stream.of(automaton.getTransitionsFromState(s))
                    .map(t -> (FSATransition) t)
                    .filter(t -> t.getLabel().equals(label))
                    .findFirst();

            if (ft.isPresent()) {
                s = ft.get().getToState();
            } else {
                break;
            }
        }

        return s;
    }

    BigInteger getNumberOfAcceptedStringsRoughly() {
        dk.brics.automaton.Automaton dkAut = this.getDkBricsAutomatonWithChars();

        BigInteger total = BigInteger.ONE;

        for (dk.brics.automaton.State q : dkAut.getStates()) {
            total = total.multiply(BigInteger.valueOf(q.getTransitions().size()));
        }

        return total;

    }

    AutomataStats getAutomataMethodsStats(String clazzName) {
        AutomataStats stats = new AutomataStats();
        
        this.parseAutomaton();
        
        stats.setNumberOfRelevantMethods(this.getAllTransitionLabels().size());
        
        Set<String> relevantMethods = new HashSet<>(this.getAllTransitionLabels());
        
        try {
            Class<?> cls = Class.forName(clazzName);
            List<Method> methods = Arrays.asList(cls.getMethods());
            stats.setNumberOfPublicMethods(methods.size());
            stats.setNumberOfComplexRelevantMethods(0);
            
            
            
            for (Method m : methods){
                String signature = String.format("%s.%s()", clazzName, m.getName());
                if (relevantMethods.contains(signature)){
                    
                    Type superClass = cls.getGenericSuperclass();
                    
                    ParameterizedType paramType = null;
                    
                    if (superClass instanceof ParameterizedType){
                        paramType = (ParameterizedType)superClass;
                    }
                    else{
                        Logger.getLogger(JflapFileManipulator.class.getName()).log(Level.INFO, "signature of class" + cls.getName() + " has no generic type parameters.");
                    }
                    List<Type> genericTypeArguments = new LinkedList<>();
                    
                   
                    if (paramType != null){
                        genericTypeArguments = Arrays.asList(paramType.getActualTypeArguments());
                    }
                    
                    List<Type> genericParamTypes = Arrays.asList(m.getGenericParameterTypes());
                    
                    for (int i=0;i<m.getParameterTypes().length;i++){
                        Class<?> c = m.getParameterTypes()[i];
                        Type t = m.getGenericParameterTypes()[i];
                        
                        boolean primitiveOrGeneric = ClassUtils.isPrimitiveOrWrapper(c) || genericTypeArguments.contains(t) || c.equals(String.class);
                        //ArrayDeque.remove(Object o)
                        boolean declaredObjectForParametrizedClass = !genericParamTypes.isEmpty()
                                && c.equals(Object.class);
                        
                        boolean collectionOfGenericType = false;
                        
                        if (c.equals(Collection.class)){
                            ParameterizedType pt2 = (ParameterizedType)t;
                            for (Type t2 : pt2.getActualTypeArguments()){
                                if (t2 instanceof WildcardType || genericParamTypes.contains(t2)){
                                    collectionOfGenericType = true;
                                    break;
                                }
                            }
                        }
                        
                        boolean selfType = c.isAssignableFrom(cls);
                        if (!primitiveOrGeneric && !declaredObjectForParametrizedClass && !collectionOfGenericType && !selfType){
                            stats.setNumberOfComplexRelevantMethods(stats.getNumberOfComplexRelevantMethods()+1);
                            stats.getComplexRelevantMethods().add(signature);
                            break;
                        }
                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(JflapFileManipulator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return stats;
    }
    
    AutomataStats getAutomataScenariosStats() {
        BigInteger totalScenarios = BigInteger.ONE;

        AutomataStats stats = new AutomataStats();

        this.parseAutomaton();

        State stGetSet = this.automaton.getStateWithID(this.automaton.getStates().length - 1);

        //this.automaton.removeFinalState(stGetSet);
        dk.brics.automaton.Automaton dkAut = this.getDkBricsAutomatonWithChars();

        Map<dk.brics.automaton.State, Long> selfLoopsPerState = new HashMap();

        for (dk.brics.automaton.State q : dkAut.getStates()) {
            Set<dk.brics.automaton.Transition> toRemoveTransitions = new HashSet<>();
            for (dk.brics.automaton.Transition t : q.getTransitions()) {
                if (t.getDest().equals(q)) {
                    //if (!q.isAccept()) {
                    toRemoveTransitions.add(t);
                    //}
                    if (selfLoopsPerState.containsKey(q)) {
                        selfLoopsPerState.put(q, selfLoopsPerState.get(q) + 1);
                    } else {
                        selfLoopsPerState.put(q, 1l);
                    }
                }
            }

            q.getTransitions().removeAll(toRemoveTransitions);
        }

        Integer shortestScenarioSize = Integer.MAX_VALUE;
        Integer longestSize = 0;

        String shortestExample = "";
        String longestExample = "";

        for (int i = 2; i < dkAut.getNumberOfStates(); i++) {
            try {
                Set<String> acceptedStringsOfSize = dkAut.getStrings(i);
                List<String> acc  = new LinkedList<>();
                acc.addAll(acceptedStringsOfSize);
                if (!acceptedStringsOfSize.isEmpty()) {

                    if (i < shortestScenarioSize) {
                        shortestScenarioSize = i;
                        shortestExample = charDkInputToOriginalSequence(acc.get(0));
                    }

                    if (i > longestSize) {
                        longestSize = i;
                        longestExample = charDkInputToOriginalSequence(acc.get(0));
                    }

                    totalScenarios = totalScenarios.add(BigInteger.valueOf(acc.size()));
                    
                }

            } catch (OutOfMemoryError error) {
                totalScenarios = getNumberOfAcceptedStringsRoughly();
                break;
            }

        }

        // SelfLoop multiplier!
        BigInteger factor = BigInteger.ONE;
        for (dk.brics.automaton.State key : selfLoopsPerState.keySet()) {
            factor = factor.multiply(BigInteger.valueOf(selfLoopsPerState.get(key)));
        }

        stats.setNumberOfScenarios(totalScenarios);
        stats.setLongestScenario(longestSize);
        stats.setLongestScenarioExample(longestExample);
        stats.setShortestScenario(shortestScenarioSize);
        stats.setShortestScenarioExample(shortestExample);

        return stats;

    }

    Long getNumberOfAcceptedStrings() {
        Long total = 0l;

        this.parseAutomaton();

        dk.brics.automaton.Automaton dkAut = this.getDkBricsAutomatonWithChars();

        Map<dk.brics.automaton.State, Long> selfLoopsPerState = new HashMap();

        for (dk.brics.automaton.State q : dkAut.getStates()) {
            Set<dk.brics.automaton.Transition> toRemoveTransitions = new HashSet<>();
            for (dk.brics.automaton.Transition t : q.getTransitions()) {
                if (t.getDest().equals(q)) {
                    //if (!q.isAccept()) {
                    toRemoveTransitions.add(t);
                    //}
                    if (selfLoopsPerState.containsKey(q)) {
                        selfLoopsPerState.put(q, selfLoopsPerState.get(q) + 1);
                    } else {
                        selfLoopsPerState.put(q, 1l);
                    }
                }
            }

            q.getTransitions().removeAll(toRemoveTransitions);
        }

        for (int i = 2; i < dkAut.getNumberOfStates(); i++) {
            total += dkAut.getStrings(i).size();
        }

        // SelfLoop multiplier!
        Long factor = 1l;
        for (dk.brics.automaton.State key : selfLoopsPerState.keySet()) {
            factor *= selfLoopsPerState.get(key);
        }
        return total * factor;
    }
}
