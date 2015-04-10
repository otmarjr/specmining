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
    
    @Test
    public void testAutomatonDiffExample() {
        // (0) -- i -- > (1) -- a --> (2) becomes: ib*ab*
        // (0) -- i -- > (1) -- a --> (2) --> c (3) ib*ab*cb*
        
        String originalAutomatonRegex = "ib*ab*";
        RegExp r1 = new RegExp(originalAutomatonRegex);
        Automaton a1 = r1.toAutomaton();
        
        String originalAutomatonRegex2 = "ib*ab*cb*";
        RegExp r2 = new RegExp(originalAutomatonRegex);
        Automaton a2 = r1.toAutomaton();
        
        String forbiddenSubstring = "[a-z]*bb+[a-z]*";
        RegExp r3;
        r3 = new RegExp(forbiddenSubstring);
        Automaton a3 = r3.toAutomaton();
        
        
        Automaton diff1 = a1.minus(a3);
        Automaton diff2 = a2.minus(a3);
        
        Set<String> examples1 = diff1.getStrings(6);
        Set<String> examples2 = diff2.getStrings(6);
        
        assertFalse(examples1.stream().anyMatch(str -> str.contains("bb")));
        assertFalse(examples2.stream().anyMatch(str -> str.contains("bb")));
    }
    
    @Test
    public void testUrlAutomatonDiff() {
        // ss+ cannot happen on url class. 
        String urlSpec = "a[b-k]*((l[b-k]*|l[b-k]*m[b-k]*)|((m[b-k]*|m[b-k]*l[b-k]*)))";
        String urlSpecExtended = urlSpec.replace("[b-k]*", "([b-k]|s)*");
        RegExp urlRegex = new RegExp(urlSpecExtended);
        Automaton urlAutomaton = urlRegex.toAutomaton();
        
        String forbiddenSequence = "[a-z]*ss+[a-z]*";
        RegExp forbRE = new RegExp(forbiddenSequence);
        Automaton sPlusAut = forbRE.toAutomaton();
        
        Automaton urlPrime = urlAutomaton.minus(sPlusAut);
        
        String lol = urlPrime.getShortestExample(true);
        Set<String> examplesPrime = urlPrime.getStrings(4);
        Set<String> examplesOriginal = urlAutomaton.getStrings(4);
        
        assertTrue(examplesOriginal.stream().anyMatch(str -> str.contains("ss")));
        assertFalse(examplesPrime.stream().anyMatch(str -> str.contains("ss")));
    }
}
