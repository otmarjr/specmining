/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.smartic;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author otmar
 */
public class FilteringBlockTest {
    
    /**
     * Test of getFilteredTraces method, of class FilteringBlock.
     */
    @Test
    public void testGetFilteredTraces() {
        System.out.println("getFilteredTraces");
        FilteringBlock instance = null;
        Iterable<String> expResult = null;
        Iterable<String> result = instance.getFilteredTraces();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
