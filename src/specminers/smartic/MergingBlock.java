/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.smartic;

import cz.cuni.mff.ksi.jinfer.base.automaton.Automaton;
import cz.cuni.mff.ksi.jinfer.base.automaton.State;
import cz.cuni.mff.ksi.jinfer.base.automaton.Step;
import java.util.List;

/**
 *
 * @author otmarpereira
 */
public class MergingBlock {
    Automaton<String> X;
    Automaton<String> Y;
    Automaton<String> mergedAutomaton;
    
    public MergingBlock(Automaton<String> x, Automaton<String> y){
        this.X = x;
        this.Y = y;
        this.mergedAutomaton = null;
    }
    
    public List<Step<String>> getPrefix(Automaton<String> aut, State<String> n){
        return null;
    }
    
    public Automaton<String> getMergedAutomaton() {
        if (this.mergedAutomaton == null){
            
        }
        
        return this.mergedAutomaton;
    }
}
