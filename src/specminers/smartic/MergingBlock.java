/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.smartic;

import cz.cuni.mff.ksi.jinfer.base.automaton.Automaton;
import cz.cuni.mff.ksi.jinfer.base.automaton.State;
import cz.cuni.mff.ksi.jinfer.base.automaton.Step;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author otmarpereira
 */
public class MergingBlock {

    public static final String EPSILON_TRANSITION_SYMBOL = "λ";
    
    class TransitionString {

        TransitionString(Step<String> step, Automaton<String> automaton) {
            loadTokens();
            loadProbabilities();
            this.initialStep = step;
        }

        List<String> tokens;
        List<Double> probabilities;
        Step<String> initialStep;
        Automaton<String> automaton;

        private void loadTokens() {
            this.tokens = new LinkedList<>();
        }

        private void loadProbabilities() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    Automaton<String> X;
    Automaton<String> Y;

    public MergingBlock() {
    }

    public boolean sourceNodesShareSuffixProbabilities(Step<String> s1, Step<String> s2) {
        return false;
    }

    public boolean transitionsAreEquivalent(Step<String> s1, Step<String> s2) {
        return false;
    }

    public List<Step<String>> getPrefix(Automaton<String> aut, State<String> n) {
        return null;
    }

    public List<Step<String>> getSuffixes(Automaton<String> aut, State<String> n) {
        return null;
    }

    public void handleExceptionalCases(Automaton<String> automaton){
        State startNode = automaton.getInitialState();
        
        Map<State<String>, Set<Step<String>>> d = automaton.getDelta();
        
        boolean startNodeIsSinkOfATransition =  d.keySet().stream()
                .anyMatch(s -> d.get(s).stream()
                        .anyMatch(t -> t.getDestination().equals(startNode)));
        
        if (startNodeIsSinkOfATransition){
            automaton.createNewDummyInitialState(EPSILON_TRANSITION_SYMBOL);
        }
    }
    
    public Set<List<String>> getAllStringsAcceptedByAutomaton(List<Trace> traces, Automaton<String> automaton){
        Set<List<String>> accepted = new HashSet<>();
    
        for (Trace t : traces){
            List<String> events = t.getEvents();
            if (events.size() > 0) continue;
            State<String> currentState = automaton.getInitialState();
            boolean recognized = true;
            
            for (String e : events){
                Step<String> next = automaton.getOutStepOnSymbol(currentState, e);
                
                if (next != null){
                    currentState = next.getDestination();
                }
                else {
                    recognized = false;
                    break;
                }
            }
            
            if (recognized && currentState.getFinalCount() > 0){
                accepted.add(events);
            }
        }
        
        return accepted;
    }
    
    public Automaton<String> getMergedAutomaton(Automaton<String> x, Automaton<String> y, List<Trace> trainSetX, List<Trace> trainSetY) {
        Automaton<String> merged = null;
        
        double weightX = 1.0*trainSetX.size()/(trainSetX.size() + trainSetY.size());
        double weightY = 1.0*trainSetY.size()/(trainSetX.size() + trainSetY.size());
        
        handleExceptionalCases(x);
        handleExceptionalCases(y);
        
        return merged;
    }
}