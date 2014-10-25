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
        String input = "A B C B C D A B C B C B C D";
        String expResult = "(A(BC)+D)+";
        String result = StringUtils.generateRegexToMatchInput(input);
        assertEquals(expResult, result);
    }
    
}
