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
    

    private Automaton<String> getRamansTestStringsPostSkStringsAutomaton(){
        Automaton<String> automaton;
        automaton = new Automaton<>(0, true);
        
        State s0 = automaton.getInitialState();
        State s1 = automaton.createNewState();
        State s2 = automaton.createNewState();
        State s3 = automaton.createNewState();
        State s4 = automaton.createNewState();
        State s5 = automaton.createNewState();
        State s6 = automaton.createNewState();
        State s7 = automaton.createNewState();
        State s8 = automaton.createNewState();
        State s9 = automaton.createNewState();
        State s10 = automaton.createNewState();
        State s11 = automaton.createNewState();
        State s12 = automaton.createNewState();
        State s13 = automaton.createNewState();
        State s14 = automaton.createNewState();
        State s15 = automaton.createNewState();
        State s16 = automaton.createNewState();
        State s17 = automaton.createNewState();
        State s18 = automaton.createNewState();
        
        
        automaton.createNewStep("i", s0, s1, 45);
        automaton.createNewStep("m", s0, s2, 21);
        automaton.createNewStep("r", s0, s3, 9);
        automaton.createNewStep("h", s0, s3, 13);
        automaton.createNewStep("p", s0, s4, 12);
        
        automaton.createNewStep("j", s1, s5, 45);
        
        automaton.createNewStep("j", s2, s6, 21);
        
        automaton.createNewStep("s", s3, s7, 22);
        
        automaton.createNewStep("q", s4, s6, 12);
        
        automaton.createNewStep("k", s5, s5, 36);
        automaton.createNewStep("l", s5, s8, 38);
        automaton.createNewStep("k", s5, s11, 7);
        
        automaton.createNewStep("o", s6, s9, 33);
        
        automaton.createNewStep("t", s7, s10, 22);
        
        automaton.createNewStep("x", s8, s12, 5);
        automaton.createNewStep("e", s8, s13, 33);
        
        automaton.createNewStep("e", s9, s13, 39);
        
        automaton.createNewStep("a", s10, s14, 22);
        
        automaton.createNewStep("l", s11, s9, 6);
        automaton.createNewStep("k", s11, s15, 1);
        
        automaton.createNewStep("y", s12, s16, 6);
        
        automaton.createNewStep("f", s13, s16, 72);
        
        automaton.createNewStep("b", s14, s17, 22);
        
        automaton.createNewStep("l", s15, s18, 1);
        
        automaton.createNewStep("z", s16, s16, 6);
        automaton.createNewStep("g", s16, s16, 72);
        automaton.createNewStep("d", s16, s16, 22);
        
        automaton.createNewStep("c", s17, s16, 2);
        
        automaton.createNewStep("x", s18, s12, 1);
        
        return automaton;
    }
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
