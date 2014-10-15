/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.smartic;

import com.tecacet.math.fsm.Alphabet;
import com.tecacet.math.fsm.BasicWord;
import com.tecacet.math.fsm.DFA;
import com.tecacet.math.fsm.DFABuilder;
import com.tecacet.math.fsm.DeterministicFiniteAutomaton;
import com.tecacet.math.fsm.FABuilderException;
import com.tecacet.math.fsm.FAException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author otmar
 */
public class TrieTree {

    private final String PREDEFINED_ROOT_NAME = "root";
    
    private List<Trace> traces;
    Map<String, Map<String, String>> stateTransitions;
    Set<String> finalStates;
    DeterministicFiniteAutomaton<String, String> automaton;

    public TrieTree(List<Trace> traces) {
        this.traces = traces;
        this.buildTree();
    }

    private Set<String> getAlphabet() {
        Set<String> alphabet = new HashSet<>();

        for (Trace t : this.traces) {
            for (String w : t.getEvents()) {
                alphabet.add(w);
            }
        }

        return alphabet;
    }

    private void buildTree() {
        
        this.convertTracesToTransitionsAndStates();
        
        this.buildAutomaton();
        
    }

    public Boolean containsTrace(Trace trace){
        try {
            return this.automaton.accepts(new BasicWord<String>(trace.getEvents()));
        } catch (FAException ex) {
            Logger.getLogger(TrieTree.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    private void convertTracesToTransitionsAndStates() {
        this.stateTransitions = new LinkedHashMap<>();

        String previousState = PREDEFINED_ROOT_NAME;

        stateTransitions.put(previousState, new HashMap<>());
        int counter = 1;

        this.finalStates = new HashSet<>();

        for (Trace trace : traces) {
            previousState = PREDEFINED_ROOT_NAME;

            int eventIndex = 0;

            for (String event : trace.getEvents()) {
                Map<String, String> trans = stateTransitions.get(previousState);

                if (!trans.containsKey(event)) {
                    counter++;
                    String newState = "s" + counter + "_" + event;

                    trans.put(event, newState);
                    previousState = newState;
                    stateTransitions.put(previousState, new HashMap<>());
                } else {
                    previousState = trans.get(event);
                }

                if (eventIndex == trace.getEvents().size() - 1) {
                    finalStates.add(previousState);
                }

                eventIndex++;
            }
        }
    }

    private void buildAutomaton() {
        
        Set<String> alphabet = this.getAlphabet();
        Alphabet<String> events = new Alphabet<>(alphabet);
        
        DFABuilder<String, String> builder = DFA.newDFA(events);

        try {
            builder.setInitialState(PREDEFINED_ROOT_NAME);
        } catch (FABuilderException ex) {
            Logger.getLogger(TrieTree.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.stateTransitions.keySet().stream().forEach((state) -> {
            this.stateTransitions.get(state).keySet().stream().forEach((transitionToken) -> {
                try {
                    builder.addTransition(state, this.stateTransitions.get(state).get(transitionToken), transitionToken);
                } catch (FABuilderException ex) {
                    Logger.getLogger(TrieTree.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        });
        
        finalStates.stream().forEach((fs) -> {
            try {
                builder.addFinalState(fs);
            } catch (FABuilderException ex) {
                Logger.getLogger(TrieTree.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        try {
            this.automaton = builder.build();
        } catch (FABuilderException ex) {
            Logger.getLogger(TrieTree.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
