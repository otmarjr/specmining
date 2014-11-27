/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.smartic;

import cz.cuni.mff.ksi.jinfer.base.automaton.Automaton;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author otmar
 */
public class LearningBlockTest {
    
    public LearningBlockTest() {
    }

    @Test
    public void testLearnAutomaton() throws InterruptedException {
        System.out.println("learnAutomaton");
        List<Trace> traces = new LinkedList<>();
        
        Trace t1 = new Trace();
        t1.addEvent("A");
        t1.addEvent("B");
        t1.addEvent("C");
        t1.addEvent("D");
        t1.addEvent("E");
        
        Trace t2 = new Trace();
        t2.addEvent("A");
        t2.addEvent("B");
        t2.addEvent("C");
        t2.addEvent("X");
        t2.addEvent("Y");
        
        Trace t3 = new Trace();
        t3.addEvent("A");
        t3.addEvent("E");
        t3.addEvent("B");
        t3.addEvent("D");
        t3.addEvent("E");
        
        traces.add(t1);
        traces.add(t2);
        traces.add(t3);
        
        List<List<String>> expResult = null;
        Automaton<String> result;
        result = LearningBlock.learnAutomaton(traces, 2, 1);
        
        int numberOfDistinctKStrings = 9;
        assertEquals(numberOfDistinctKStrings, result.getDelta().size());
    }
    
}
