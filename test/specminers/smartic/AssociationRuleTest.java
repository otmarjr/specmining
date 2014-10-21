/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.smartic;

import java.util.Arrays;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author otmar
 */
public class AssociationRuleTest {
    
    /**
     * Test of substringsSatisfyingPre method, of class AssociationRule.
     */
    @Test
    public void testSubstringsSatisfyingPre() {
        System.out.println("substringsSatisfyingPre");
        Trace t = new Trace();
        
        t.addEvent("a");
        t.addEvent("b");
        t.addEvent("c");
        t.addEvent("a");
        t.addEvent("b");
        t.addEvent("e");
        
        List<String> pre = Arrays.asList("a", "b");
        List<String> post = Arrays.asList("e");
        
        AssociationRule instance = new AssociationRule(pre, post, 0.8);
        List<Trace.SubTrace> result = instance.substringsSatisfyingPre(t);
        
        List<String> subtrace1 = Arrays.asList("a", "b");
        List<String> subtrace2 = Arrays.asList("a", "b", "c", "a", "b");
        
        assertEquals(2, result.size());
        assertThat(result.get(0).getEvents(), is(subtrace1));
        assertThat(result.get(1).getEvents(), is(subtrace2));
        
        
    }

    
}
