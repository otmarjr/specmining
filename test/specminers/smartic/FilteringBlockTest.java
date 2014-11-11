/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.smartic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jp.ac.titech.cs.se.sparesort.MiningStrategy;
import jp.ac.titech.cs.se.sparesort.SequenceDatabase;
import jp.ac.titech.cs.se.sparesort.bide.RecursiveBIDE;
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

    @Test
     public void traceWithoutRepetitionEqualsPostFilteringSuperSet() 
             throws Exception{
        
        List<Trace> traces = new ArrayList<>();
        Trace t1 = new Trace();
        t1.addEvent("A");
        t1.addEvent("B");
        t1.addEvent("C");
        traces.add(t1);

        Trace t2 = new Trace();
        t2.addEvent("A");
        traces.add(t2);
        
        FilteringBlock fb = new FilteringBlock(traces);
        
         assertEquals(traces.size(), fb.getTraces().size());
     }

     @Test
      public void multisetTraceIncreasesPostFilteringSuperSet() 
             throws Exception{
        
        List<Trace> traces = new ArrayList<>();
        Trace t1 = new Trace();
        t1.addEvent("A");
        t1.addEvent("B");
        t1.addEvent("C");
        
        traces.add(t1);

        Trace t2 = new Trace();
        t2.addEvent("A");
        t2.addEvent("B");
        t2.addEvent("C");
        t2.addEvent("B");
        t2.addEvent("C");
        t2.addEvent("D");
        t2.addEvent("A");
        t2.addEvent("B");
        t2.addEvent("C");
        t2.addEvent("B");
        t2.addEvent("C");
        t2.addEvent("B");
        t2.addEvent("C");
        t2.addEvent("D");
        
        traces.add(t2);
        
        FilteringBlock fb = new FilteringBlock(traces);
        
         assertTrue(fb.getTraces().size() > traces.size());
     }
     
     
    @Test
    public void testPrefixTree() throws Exception{
        
        List<Trace> traces = new ArrayList<>();
        Trace t1 = new Trace();
        t1.addEvent("A");
        t1.addEvent("B");
        t1.addEvent("C");
        traces.add(t1);

        Trace t2 = new Trace();
        t2.addEvent("A");
        traces.add(t2);
        
        FilteringBlock fb = new FilteringBlock(traces);
        
        SequenceDatabase<String> seqDB = new SequenceDatabase<>();

        MiningStrategy<String> st = new RecursiveBIDE<>();
        seqDB.setMiningStrategy(st);
        
        traces.stream().forEach((t) -> {
            seqDB.addSequence(t.getEvents());
        });
        
        Map<List<String>, Integer> rawSeqs = seqDB.mineFrequentClosedSequences(1);
        
        List<Sequence> sequences = rawSeqs.keySet().stream()
                .map(rw -> new Sequence(rw, rawSeqs.get(rw)))
                .collect(Collectors.toList());
        
        List<AssociationRule> rules = fb.generateRules(sequences, 0.5);
        
        assertNotNull(rules);
        assertEquals(1, rules.size());
    }
    
}

