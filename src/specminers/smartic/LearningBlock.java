/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.smartic;

import java.util.List;
import cz.cuni.mff.ksi.jinfer.base.automaton.*;
import cz.cuni.mff.ksi.jinfer.twostep.processing.automatonmergingstate.conditiontesting.skstrings.SKStrings;
import java.util.stream.Collectors;
/**
 *
 * @author otmarpereira
 */
public class LearningBlock {

    public static List<List<String>> learnAutomaton(List<Trace> traces) throws InterruptedException {
        List<List<String>> transitions = null;

        Automaton<String> aut = new Automaton<>(true);
        
        traces.forEach(t -> aut.buildPTAOnSymbol(t.getEvents()));
        
        SKStrings<String> alg;
        alg = new SKStrings<>(2, 1f,"AND");
        
        List<List<List<State<String>>>> merge = alg.getMergableStates(aut);
        
        String s = aut.toString();
        return transitions;
    }
}
