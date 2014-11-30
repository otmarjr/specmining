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
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Otmar
 */
public class MergingBlockTest {
    

    @Test
    public void testGetAllStringsAcceptedByAutomaton() {
        System.out.println("getAllStringsAcceptedByAutomaton");
        List<Trace> traces = null;
        Automaton<String> automaton = null;
        MergingBlock instance = new MergingBlock();
        Set<List<String>> expResult = null;
        Set<List<String>> result = instance.getAllStringsAcceptedByAutomaton(traces, automaton);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
