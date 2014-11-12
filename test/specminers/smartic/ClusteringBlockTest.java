/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.smartic;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author otmarpereira
 */
public class ClusteringBlockTest {

    public ClusteringBlockTest() {
    }
    
    Trace t1; //ABCD
    Trace t1Prime; // (A(BC)+D)+
    Trace t2; // D(E)+
    Trace t2Prime; // (DE)+
    Trace t3; // (A(BC)+D)+
    
    private void loadBaseDataItems() {
        t1 = new Trace();
        t1Prime = new Trace();
        t2 = new Trace();
        t2Prime = new Trace();
        t3 = new Trace();

        t1.addEvent("A");
        t1.addEvent("B");
        t1.addEvent("C");
        t1.addEvent("D");

        t1Prime.addEvent("A");
        t1Prime.addEvent("B");
        t1Prime.addEvent("C");
        t1Prime.addEvent("B");
        t1Prime.addEvent("C");
        t1Prime.addEvent("D");
        t1Prime.addEvent("A");
        t1Prime.addEvent("B");
        t1Prime.addEvent("C");
        t1Prime.addEvent("B");
        t1Prime.addEvent("C");
        t1Prime.addEvent("B");
        t1Prime.addEvent("C");
        t1Prime.addEvent("D");

        t2.addEvent("D");
        t2.addEvent("E");
        t2.addEvent("E");
        t2.addEvent("E");

        t2Prime.addEvent("D");
        t2Prime.addEvent("E");
        t2Prime.addEvent("D");
        t2Prime.addEvent("E");

        t3.addEvent("A");
        t3.addEvent("B");
        t3.addEvent("C");
        t3.addEvent("B");
        t3.addEvent("C");
        t3.addEvent("D");
        t3.addEvent("A");
        t3.addEvent("B");
        t3.addEvent("C");
        t3.addEvent("B");
        t3.addEvent("C");
        t3.addEvent("B");
        t3.addEvent("C");
        t3.addEvent("D");
    }
    

    private List<Trace> getTestDataSet() {
        this.loadBaseDataItems();
        
        List<Trace> traces = new LinkedList<>();

        traces.add(t1);
        traces.add(t1Prime);
        traces.add(t2);
        traces.add(t2Prime);
        traces.add(t3);
        return traces;
    }

    @Test
    public void testUniqueTracesDistiction() {

        int uniqueTraces = 4;
        int expected = uniqueTraces;
        List<Trace> traces = this.getTestDataSet();

        System.out.println("ClusteringBlock constructor");
        ClusteringBlock instance = new ClusteringBlock(traces);

        assertEquals(expected, instance.uniqueInputTraces.size());
    }

    @Test
    public void testClustering() {
        int k = 2;

        List<Trace> traces = this.getTestDataSet();

        ClusteringBlock instance = new ClusteringBlock(traces);

        System.out.println("k medoid cluster");
        Map<Trace, Set<Trace>> clusters = instance.kMedoid(k);

        int expected = k;
        int actual = clusters.size();

        int totalSize;
        totalSize = clusters.keySet().stream()
                .mapToInt(m -> clusters.get(m).size()).sum();

        
        Trace clusterT1 = clusters.keySet().stream().filter(c -> clusters.get(c).contains(t1)).findFirst().get();
        Trace clusterT1Prime = clusters.keySet().stream().filter(c -> clusters.get(c).contains(t1Prime)).findFirst().get();
        Trace clusterT2 = clusters.keySet().stream().filter(c -> clusters.get(c).contains(t2)).findFirst().get();
        Trace clusterT2Prime = clusters.keySet().stream().filter(c -> clusters.get(c).contains(t2Prime)).findFirst().get();
        Trace clusterT3 = clusters.keySet().stream().filter(c -> clusters.get(c).contains(t3)).findFirst().get();
        
        
        assertEquals(expected, actual);
        assertEquals(instance.getUniqueInputTraces().size(), totalSize);
        assertEquals(clusterT1, clusterT1Prime);
        assertEquals(clusterT1, clusterT3);
        assertEquals(clusterT2, clusterT2Prime);
        assertNotSame(clusterT1, clusterT2);
        
        
    }

}
