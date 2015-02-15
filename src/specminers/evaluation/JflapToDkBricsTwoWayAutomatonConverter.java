/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.evaluation;

import automata.State;
import automata.fsa.FSATransition;
import automata.fsa.FiniteStateAutomaton;
import com.sun.javafx.scene.paint.GradientUtils;
import dk.brics.automaton.Automaton;
import dk.brics.automaton.Transition;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Otmar
 */
public class JflapToDkBricsTwoWayAutomatonConverter {

    public JflapToDkBricsTwoWayAutomatonConverter(automata.fsa.FiniteStateAutomaton jflapFSA) {
        this.jflapFSA = jflapFSA;
    }

    private final automata.fsa.FiniteStateAutomaton jflapFSA;

    public dk.brics.automaton.Automaton convertToDkBricsAutomaton(Map<String, Character> labelsMappingJffToDK) {
        dk.brics.automaton.Automaton automaton = new Automaton();

        Map<automata.State, dk.brics.automaton.State> statesMapping = new HashMap<>();

        // Add all the states!
        for (automata.State s : jflapFSA.getStates()) {
            if (jflapFSA.getInitialState() == s) {
                statesMapping.put(s, automaton.getInitialState());
            } else {
                statesMapping.put(s, new dk.brics.automaton.State());
            }
        }

        List<FSATransition> jffTransitions = Arrays.asList(jflapFSA.getFSATransitions());
        Set<String> labels = jffTransitions.stream().map(jt -> jt.getLabel())
                .collect(Collectors.toSet());

        /*
         labelsMappingJffToDK = new HashMap();
         labelsSourceJffToDK = new HashMap();
         Character currentChar = 'a';

         for (String l : labels) {
         labelsMappingJffToDK.put(l, currentChar);
         labelsSourceJffToDK.put(currentChar, l);
         currentChar++;
         }
         */
        Set<automata.State> jffFinalStates = Arrays.stream(jflapFSA.getFinalStates())
                .collect(Collectors.toSet());

        // Now, connect them up!
        for (automata.State jffState : statesMapping.keySet()) {
            dk.brics.automaton.State dkState = statesMapping.get(jffState);

            if (jffFinalStates.contains(jffState)) {
                dkState.setAccept(true);
            } else {
                dkState.setAccept(false);
            }

            List<FSATransition> outwardTransitions = jffTransitions.stream()
                    .filter(t -> t.getFromState().equals(jffState))
                    .collect(Collectors.toList());

            for (FSATransition fst : outwardTransitions) {
                dk.brics.automaton.State target = statesMapping.get(fst.getToState());
                dk.brics.automaton.Transition t = new Transition(labelsMappingJffToDK.get(fst.getLabel()), target);
                statesMapping.get(fst.getFromState()).addTransition(t);
            }
        }

        return automaton;
    }

    public automata.fsa.FiniteStateAutomaton convertToJFlapFSA(dk.brics.automaton.Automaton automaton, Map<Character, String> labelsMapping) {
        automata.fsa.FiniteStateAutomaton fsa = new FiniteStateAutomaton();

        Map<dk.brics.automaton.State, automata.State> statesMapping = new HashMap<>();

        // Add all the states!
        int id = 0;
        for (dk.brics.automaton.State s : automaton.getStates()) {
            statesMapping.put(s, fsa.createStateWithId(new java.awt.Point(id, id), id));
            id++;

            if (automaton.getInitialState() == s) {
                fsa.setInitialState(statesMapping.get(s));
            }
        }

        Set<dk.brics.automaton.State> dkFinalStates = automaton.getAcceptStates();

        for (dk.brics.automaton.State dkState : statesMapping.keySet()) {
            automata.State jffState = statesMapping.get(dkState);

            if (dkFinalStates.contains(dkState)) {
                fsa.addFinalState(jffState);
            }

            Set<dk.brics.automaton.Transition> outwardTransitions = dkState.getTransitions();

            for (Transition dkt : outwardTransitions) {
                automata.State target = statesMapping.get(dkt.getDest());
                for (char c = dkt.getMin(); c <= dkt.getMax(); c++) {
                    String label = labelsMapping.get(c);
                    automata.fsa.FSATransition t = new FSATransition(jffState, target, label);
                    fsa.addTransition(t);
                }
            }
        }

        return fsa;
    }
}
