/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.smartic;

import cz.cuni.mff.ksi.jinfer.base.automaton.Automaton;
import cz.cuni.mff.ksi.jinfer.base.automaton.State;
import cz.cuni.mff.ksi.jinfer.base.automaton.Step;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author otmarpereira
 */
public class MergingBlock {
    
    class TransitionString{

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
    Automaton<String> mergedAutomaton;
    
    public MergingBlock(Automaton<String> x, Automaton<String> y){
        this.X = x;
        this.Y = y;
        this.mergedAutomaton = null;
    }
    
    public boolean sourceNodesShareSuffixProbabilities(Step<String> s1, Step<String> s2){
        return false;
    }
    
    public boolean transitionsAreEquivalent(Step<String> s1, Step<String> s2){
        return false;
    }
    
    public List<Step<String>> getPrefix(Automaton<String> aut, State<String> n){
        return null;
    }
    
    public List<Step<String>> getSuffixes(Automaton<String> aut, State<String> n){
        return null;
    }
    
    public Automaton<String> getMergedAutomaton() {
        if (this.mergedAutomaton == null){
            
        }
        
        return this.mergedAutomaton;
    }
}
