/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.smartic;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;
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

    @Test
    public void kMedoidsWorksWithSingleCluster() {
        System.out.println("kMedoidsWorksWithSingleCluster");

        int k = 1;
        List<Trace> items = this.getTestDataSet();
        ClusteringBlock instance = new ClusteringBlock(items);

        Map<Trace, Set<Trace>> clusters = instance.kMedoid(k);

        assertEquals(k, clusters.size());
    }

    private List<Trace> fabricateDataSet(int numberOfClusters) {
        List<Trace> traces = new LinkedList<>();

        int N = 10;

        char currentClusterLetter = 'A';

        for (int i = 0; i < numberOfClusters; i++) {
            char currentDifferentiatingLetter = currentClusterLetter;
            List<String> commonPrefix = new LinkedList();

            String e = Character.toString(currentClusterLetter);
            IntStream.range(1, N)
                    .forEach(j -> commonPrefix.add(e));

            for (int j = 0; j < N; j++) {
                Trace t = new Trace();
                commonPrefix.forEach(a -> t.addEvent(a));
                t.addEvent(Character.toString(currentDifferentiatingLetter));
                traces.add(t);
                currentDifferentiatingLetter++;
            }

            currentClusterLetter++;
        }

        Collections.shuffle(traces);
        return traces;
    }

    @Test
    public void testParametelessController() {
        List<Trace> tracesk2 = this.fabricateDataSet(2);
        List<Trace> tracesk3 = this.fabricateDataSet(3);
        List<Trace> tracesk4 = this.fabricateDataSet(4);
        List<Trace> tracesk5 = this.fabricateDataSet(5);

        ClusteringBlock instance2 = new ClusteringBlock(tracesk2);
        ClusteringBlock instance3 = new ClusteringBlock(tracesk3);
        ClusteringBlock instance4 = new ClusteringBlock(tracesk4);
        ClusteringBlock instance5 = new ClusteringBlock(tracesk5);

        Map<Trace, Set<Trace>> clusters2
                = instance2.executeParameterlessClusteringController();

        Map<Trace, Set<Trace>> clusters3
                = instance3.executeParameterlessClusteringController();

        Map<Trace, Set<Trace>> clusters4
                = instance4.executeParameterlessClusteringController();

        Map<Trace, Set<Trace>> clusters5
                = instance5.executeParameterlessClusteringController();

        assertEquals(2, clusters2.size());
        assertEquals(3, clusters3.size());
        assertEquals(4, clusters4.size());
        assertEquals(5, clusters5.size());
    }
}
