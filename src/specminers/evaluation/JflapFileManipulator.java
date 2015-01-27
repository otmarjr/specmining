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
import file.XMLCodec;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        this.correspondingClass = ((FSATransition)this.automaton.getTransitionsFromState(this.automaton.getInitialState())[0]).getLabel().replace(".<init>()", "");
                
    }
    
    private String getPackageName(String classFullName){
        if (classFullName != null && classFullName.contains(".")){
            int lastDotPosition = classFullName.lastIndexOf(".");
            return classFullName.substring(0,lastDotPosition);
        }
        return "";
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
            
            samePackageClasses.forEach(c -> addTransitionsFromClass(c, publicAPI.getOrDefault(c,null)));
        }
    }

    private List<FSATransition> getFSATransitionsFromState(State st){
        return Stream.of(automaton.getFSATransitions()).filter(t -> t.getFromState().equals(st))
                .collect(Collectors.toList());
    }
    
    private void addTransitionsFromClass(String className, Set<String> transitions) {
        if (transitions != null){
            for (State st : this.automaton.getStates()){
                if (!automaton.getInitialState().equals(st)){
                    for (String methodSig : transitions){
                        List<FSATransition> acceptedMethodSigs = getFSATransitionsFromState(st);
                        
                        if (!acceptedMethodSigs.stream().anyMatch(t -> t.getLabel().equalsIgnoreCase(methodSig))){
                            FSATransition fsaT = new FSATransition(st, st, methodSig);
                            this.automaton.addTransition(fsaT);
                        }
                    }
                }
            }
        }
    }
    
    public void saveToFile(String targetPath){
        XMLCodec jffCodec = new XMLCodec();
        jffCodec.encode(this.automaton, new File(targetPath), null);
    }
}
