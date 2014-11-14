/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author otmar
 */
public class StringUtilsTest {
    
    public StringUtilsTest() {
    }

    @Test
    public void testGenerateRegexToMatchInput() throws Exception {
        System.out.println("generateRegexToMatchInput");
        String input = "ABCBCDABCBCBCD";
        String expResult = "(A(BC)+D)+";
        String result = StringHelper.generateRegexToMatchInput(input);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testPlusOperatorOnPrimitiveRepetition() {
        System.out.println("testPlusOperatorOnPrimitiveRepetition");
        String input = "BAAB";
        String expected  = "B(A)+B";
        String result = StringHelper.generateRegexToMatchInput(input);
        assertEquals(expected, result);
    }
    
     @Test
    public void testNestedPlusOperator() {
        System.out.println("testPlusOperatorOnPrimitiveRepetition");
        String input = "AAAA";
        String expected  = "(A)+";
        String result = StringHelper.generateRegexToMatchInput(input);
        assertEquals(expected, result);
    }
    
    @Test
    public void testGlobalSequenceAlignmentCoursera1() {
        String input1 = "AGGGCT";
        String input2 = "AGGCA";
        
        int pgap = 1;
        int pmismatch = 1;
        
        
        int penalty = StringHelper.getSequenceAlignmentPenalty(input1, input2, pgap, pmismatch);
        
        assertEquals(pgap + pmismatch, penalty);
    }
    
    
}
