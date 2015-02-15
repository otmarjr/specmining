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
import dk.brics.automaton.RegExp;
import file.XMLCodec;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.reflections.ReflectionUtils;
import static org.reflections.ReflectionUtils.withModifier;
import static org.reflections.ReflectionUtils.withParametersCount;
import static org.reflections.ReflectionUtils.withPrefix;
import org.reflections.Reflections;

/**
 *
 * @author otmarpereira
 */
public class JflapFileManipulator {

    File jffFile;
    FiniteStateAutomaton automaton;
    private String correspondingClass;

    public JflapFileManipulator(String jffPath) {
        this(new File(jffPath));
    }

    public JflapFileManipulator(File jff) {
        this.jffFile = jff;
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
            this.addTransitionsFromClass(correspondingClass, publicAPI.getOrDefault(correspondingClass, null));
        } else {
            String currentClassPackage = getPackageName(correspondingClass);

            List<String> samePackageClasses = publicAPI.keySet().stream()
                    .filter(k -> getPackageName(k).equals(currentClassPackage))
                    .collect(Collectors.toList());

            samePackageClasses.forEach(c -> addTransitionsFromClass(c, publicAPI.getOrDefault(c, null)));
        }
    }

    private Set<FSATransition> getFSATransitionsFromState(State st) {
        return Stream.of(automaton.getFSATransitions()).filter(t -> t.getFromState().equals(st))
                .collect(Collectors.toSet());
    }

    private void addTransitionsFromClass(String className, Set<String> transitions) {
        if (transitions != null) {
            for (State st : this.automaton.getStates()) {
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

    private Set<String> getAllMethodsFromClass(String fullClassName) {
        Set<String> classMethods = this.getAllTransitionLabels()
                .stream().filter(l -> l.startsWith(fullClassName))
                .collect(Collectors.toSet());

        return classMethods;
    }

    private Set<String> getAllExpansionsForSequenceWithStarWildcard(String sequence) {
        Set<String> expansions = new HashSet<>();

        int wildcardIndex = sequence.indexOf("*");
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

    private List<String> expandForbiddenSequencesWithWildCards(List<String> forbiddenSequences) {
        List<String> expanded = new LinkedList<>();

        final String allSubtypesWildCard = "+";

        List<String> forbiddenWithExpandedSubtypes = new LinkedList<>();

        for (String seq : forbiddenSequences) {
            if (!seq.contains(allSubtypesWildCard)) {
                forbiddenWithExpandedSubtypes.add(seq);
            } else {
                forbiddenWithExpandedSubtypes.addAll(getAllExpansionsForSequenceWithSubtypesWildcard(seq));
            }
        }

        final String starWildCard = "*";

        for (String seq : forbiddenWithExpandedSubtypes) {
            if (!seq.contains(starWildCard)) {
                expanded.add(seq);
            } else {
                Set<String> allExps = getAllExpansionsForSequenceWithStarWildcard(seq);
                expanded.addAll(allExps);
            }
        }

        return expanded;
    }

    public void removeInvalidSequences(List<String> forbiddenSequences) {
        this.loadJffLabelsMapToChars();
        JflapToDkBricsTwoWayAutomatonConverter converter = new JflapToDkBricsTwoWayAutomatonConverter(automaton);
        dk.brics.automaton.Automaton dkAut = converter.convertToDkBricsAutomaton(labelsMappingJffToDK);

        List<String> expandedForbiddenSeqs = expandForbiddenSequencesWithWildCards(forbiddenSequences);

        String currentPackageName = this.getPackageName(this.correspondingClass);
        expandedForbiddenSeqs = expandedForbiddenSeqs.stream()
                .filter(seq
                        -> Arrays.asList(seq.split("\\)"))
                        .stream().allMatch(signature
                                -> signature.startsWith(currentPackageName)
                        ))
                .collect(Collectors.toList());

        String testSeq = "";
        List<String> testInput = null;
        automata.State sbefore =  null;
        automata.State safter =  null;

        if (jffFile.getName().contains("DatagramSocket")) {
            Set<String> expandedWithGetLocal = expandedForbiddenSeqs
                    .stream().filter(s -> s.contains("getLocalAddress"))
                    .collect(Collectors.toSet());

            testInput = new LinkedList<>();
            testInput.add("java.net.DatagramSocket.<init>()");
            testInput.add("java.net.DatagramSocket.connect()");
            testInput.add("java.net.DatagramSocket.disconnect()");
            testInput.add("java.net.DatagramSocket.getLocalAddress()");
            testInput.add("java.net.DatagramSocket.getLocalPort()");
            testInput.add("java.net.DatagramPacket.<init>()");
            testInput.add("java.net.DatagramSocket.setSoTimeout()");
            testInput.add("java.net.DatagramSocket.send()");
            testInput.add("java.net.DatagramSocket.receive()");
            testInput.add("java.net.DatagramSocket.close()");
            testInput.add("java.net.DatagramSocket.<init>()");
            testInput.add("java.net.DatagramSocket.connect()");
            testInput.add("java.net.DatagramSocket.disconnect()");
            testInput.add("java.net.DatagramSocket.getLocalAddress()");
            testInput.add("java.net.DatagramSocket.getLocalPort()");
            testInput.add("java.net.DatagramPacket.<init>()");
            testInput.add("java.net.DatagramSocket.setSoTimeout()");
            testInput.add("java.net.DatagramSocket.send()");
            testInput.add("java.net.DatagramSocket.receive()");
            testInput.add("java.net.DatagramSocket.close()");
            
            testSeq = testInput.stream().map(sig -> Character.toString(this.labelsMappingJffToDK.get(sig)))
                    .collect(Collectors.joining(""));

            
            sbefore = runInputOnAutomaton(testInput);
        }
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
            String forbiddenAsRegex = String.format("%s %s %s", "[" +  firstChar +"-" + lastChar + "]*", encForb, "[" +  firstChar +"-" + lastChar + "]*");
            RegExp forbRegex = new RegExp(forbiddenAsRegex);
            dk.brics.automaton.Automaton forbAut = forbRegex.toAutomaton();
            dkAut = dkAut.minus(forbAut);
        }

        FiniteStateAutomaton prunedFSA = converter.convertToJFlapFSA(dkAut, labelsDkLabelToJffLabel);

        this.automaton = prunedFSA;
        
        if (jffFile.getName().contains("DatagramSocket")) {
            safter = runInputOnAutomaton(testInput);
            
            assert this.automaton.isFinalState(safter) == this.automaton.isFinalState(sbefore);
        }
    }

    private automata.State runInputOnAutomaton(List<String> input) {
        automata.State s = this.automaton.getInitialState();

        for (String label : input){
            Optional<FSATransition> ft = Stream.of(automaton.getTransitionsFromState(s))
            .map(t -> (FSATransition)t)
                    .filter(t -> t.getLabel().equals(label))
                    .findFirst();
            
            if (ft.isPresent()){
                s = ft.get().getToState();
            }
            else {
                break;
            }
        }
        
        return s;
    }
}
