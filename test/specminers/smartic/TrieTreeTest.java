/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.smartic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import jp.ac.titech.cs.se.sparesort.SequenceDatabase;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author otmar
 */
public class TrieTreeTest {

    @Test
    public void supportsSpareSortSample() throws Exception {
        
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
        
        List<List<String>> sequences = null;
        sequences = traces.stream().map(Trace::getEvents).collect(Collectors.toList());
        
        SequenceDatabase<String> seqDB = new SequenceDatabase<>();

        for (Trace t : traces) {
            seqDB.addSequence(t.getEvents());
        }
        
        Map<List<String>, Integer> closedSeqs = seqDB.mineFrequentClosedSequences(1);
        
        TrieTree tree = new TrieTree(sequences, closedSeqs);
        
        
        assertTrue(tree.containsSequence(t1.getEvents(), true));
        assertTrue(tree.containsSequence(t2.getEvents(), true));
        assertTrue(tree.containsSequence(t3.getEvents(), true));
        assertTrue(tree.containsSequence(t4.getEvents(), true));
        assertTrue(tree.containsSequence(t5.getEvents(), true));
        assertTrue(tree.containsSequence(t6.getEvents(), true));
        
        
    }

}
