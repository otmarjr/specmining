/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.smartic;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author otmar
 */
public class TrieTreeTest {

    @Test
    public void supportsSpareSortSample() {
        
        List<Trace> traces = new ArrayList<>();

        Trace t1 = new Trace();
        t1.addEvent("metA");
        t1.addEvent("metB");
        t1.addEvent("metC");
        traces.add(t1);

        Trace t2 = new Trace();
        t2.addEvent("metA");
        t2.addEvent("metC");
        traces.add(t2);

        Trace t3 = new Trace();
        t3.addEvent("metC");
        t3.addEvent("metA");
        t3.addEvent("metB");
        traces.add(t3);

        Trace t4 = new Trace();
        t4.addEvent("metB");
        traces.add(t4);

        Trace  t5 = new Trace();
        t5.addEvent("metB");
        t5.addEvent("metC");
        traces.add(t5);

        Trace t6 = new Trace();
        t6.addEvent("metB");
        t6.addEvent("metB");
        t6.addEvent("metA");
        traces.add(t6);
        
        TrieTree tree = new TrieTree(traces);
        
        assertTrue(tree.containsTrace(t1));
        assertTrue(tree.containsTrace(t2));
        assertTrue(tree.containsTrace(t3));
        assertTrue(tree.containsTrace(t4));
        assertTrue(tree.containsTrace(t5));
        assertTrue(tree.containsTrace(t6));
    }

}
