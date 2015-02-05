/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.evaluation;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author otmarpereira
 */
public class MopExtractorTest {
    
    @Test
    public void testAutomatonDiff() {
        String originalAutomatonRegex = "i+[abc]*";
        RegExp r1 = new RegExp(originalAutomatonRegex);
        Automaton a1 = r1.toAutomaton();
        
        String forbiddenSubstring = "[a-z]*ba[a-z]*";
        RegExp r2;
        r2 = new RegExp(forbiddenSubstring);
        Automaton a2 = r2.toAutomaton();
        
        
        
        Automaton diff = a1.minus(a2);
        
        String sequenceWithForbiddenSubstring = "ibaac";
        
        assertTrue(a1.run(sequenceWithForbiddenSubstring));
        assertFalse(diff.run(sequenceWithForbiddenSubstring));
    }
    
}
