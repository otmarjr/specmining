/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.smartic;

import java.util.List;
import cz.cuni.mff.ksi.jinfer.base.automaton.*;
import java.util.stream.Collectors;
/**
 *
 * @author otmarpereira
 */
public class LearningBlock {

    public static List<List<String>> learnAutomaton(List<Trace> traces) {
        List<List<String>> transitions = null;

        Automaton<String> aut = new Automaton<>(false);
        
        traces.forEach(t -> aut.buildPTAOnSymbol(t.getEvents()));
        
        String s = aut.toString();
        return transitions;
    }
}
