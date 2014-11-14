/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.smartic;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author otmarpereira
 */
public class ClusteringBlock {

    Set<Trace> uniqueInputTraces;

    Set<Trace> currentClusteringDataItems;
    
    public ClusteringBlock(List<Trace> allTraces) {
        this.uniqueInputTraces = new HashSet<>(allTraces);
    }

    public Set<Trace> getUniqueInputTraces(){
        return this.uniqueInputTraces;
    }
    
    public Map<Trace, Set<Trace>> executeParameterlessClusteringController() {
        List<Float> featureScoreList = new LinkedList<>();
        Map<Trace, Set<Trace>> clusters = null;
        boolean isLocalMaxima = false;

        Set<Trace> ipTraces = this.uniqueInputTraces;

        for (int k = 1; k < ipTraces.size() && !isLocalMaxima; k++) {
            clusters = kMedoid(k);
            Float featureScore = calculateScore(clusters);
            featureScoreList.add(featureScore);
            isLocalMaxima = hasGradientChange(featureScoreList);
        }

        clusters = kMedoid(clusters.size()-1);
        return clusters;
    }

    public boolean hasGradientChange(List<Float> values) {
        boolean hasGradientChange = false;

        if (values.size() > 2){
            float last = values.get(values.size()-1);
            float beforeLast = values.get(values.size()-2);
            float beforeBeforeLast = values.get(values.size()-3);
            
            boolean beforeLastIsLocalMaxima = beforeLast < last
                    && beforeLast < beforeBeforeLast;
            
            hasGradientChange = beforeLastIsLocalMaxima;
        }
        
        return hasGradientChange;
    }

    private List<Trace> getNonMedoids(Map<Trace, Set<Trace>> medoids){
        return this.currentClusteringDataItems.stream()
                .filter((t) -> (!medoids.containsKey(t)))
                .collect(Collectors.toList());
    }
        
    private void associateWithClosestMedoid(Map<Trace, Set<Trace>> medoids) {
        List<Trace> nonMedoids =  this.getNonMedoids(medoids);

        medoids.keySet().stream().forEach((m) -> {
            medoids.put(m, new HashSet<Trace>(){{ add(m);}});
        });
        
        nonMedoids.forEach((t) -> {
            Trace closestMedoid;
            closestMedoid = medoids.keySet()
                    .stream()
                    .min((k1, k2)
                            -> Integer
                            .compare(k1.getSequenceAlignmentPenalty(t),
                                    k2.getSequenceAlignmentPenalty(t))
                    ).get();

            medoids.get(closestMedoid).add(t);
        });
    }

    private int getMedoidConfigCost(Map<Trace, Set<Trace>> medoids) {
        return medoids.keySet().stream()
                .mapToInt(m -> medoids.get(m).stream()
                        .mapToInt(o -> o.getSequenceAlignmentPenalty(m))
                        .sum()).sum();
    }

    public Map<Trace,Set<Trace>> kMedoid(int k) {
        return this.kMedoid(this.uniqueInputTraces, k);
    }
    
    
    public Map<Trace,Set<Trace>> kMedoid(Set<Trace> traces, int k) {

        this.currentClusteringDataItems = new HashSet<>(traces);
        
        List<Trace> l = new LinkedList(traces);

        Collections.shuffle(l);

        Map<Trace, Set<Trace>> medoidsSets;
        medoidsSets = new HashMap<>();

        while (medoidsSets.size() < k) {
            Set<Trace> s = new HashSet<>();

            medoidsSets.put(l.get(medoidsSets.size()), s);
        }

        boolean medoidChanged = true;

        while (medoidChanged) {
            medoidChanged = false;

            associateWithClosestMedoid(medoidsSets);
            int currentCost = getMedoidConfigCost(medoidsSets);
            int minCost = currentCost;
            
            Map<Trace, Set<Trace>> cheapestConfig = medoidsSets;
            
            Map<Trace, Set<Trace>> medoidsPrime;
            medoidsPrime = new HashMap<>();
            medoidsPrime.putAll(medoidsSets);
            
            List<Trace> nonMedoids = getNonMedoids(medoidsSets);
            
            for (Trace m : medoidsSets.keySet()){
                for (Trace o : nonMedoids){
                    medoidsPrime.remove(m);
                    medoidsPrime.put(o, new HashSet<>());
                    
                    associateWithClosestMedoid(medoidsPrime);
                    int newCost = getMedoidConfigCost(medoidsPrime);
                    
                    if (newCost < minCost){
                        minCost = newCost;
                        cheapestConfig = new HashMap<>();
                        cheapestConfig.putAll(medoidsPrime);
                        medoidChanged = true;
                    }
                    
                    medoidsPrime.remove(o);
                    medoidsPrime.put(m, new HashSet<>());
                }
            }
            
            if (medoidChanged){
                medoidsSets = cheapestConfig;
            }

        }

        return medoidsSets;
    }

    public Float calculateScore(Map<Trace,Set<Trace>> IPClusters) {
        Float score = 0f;

        Set<Trace> medoidCenters =  IPClusters.keySet();
        Map<Trace, Set<Trace>> medoidCentersCluster = kMedoid(medoidCenters,1);
        Trace repMedoid = medoidCentersCluster.keySet()
                .stream().findFirst().get();
        
        Float simMedoids = 0f;
        Float simWithinClusters = 0f;
        
        for (Trace medoid : IPClusters.keySet()){
            if (!medoid.equals(repMedoid)){
                simMedoids += medoid.getSequenceAlignmentPenalty(repMedoid);
            }
        }
        
        float averageSimMedoids = simMedoids/(medoidCenters.size()*1f);
        
        float numNonemptyClusters = 0;
        
        for (Trace medoid : IPClusters.keySet()){
            Set<Trace> cluster = IPClusters.get(medoid);
       
            List<Trace> dataItems = cluster.stream()
                    .filter(d -> !d.equals(medoid))
                    .collect(Collectors.toList());
            if (dataItems.size() > 0) {
                int AClusterSim = dataItems.stream()
                        .mapToInt(d -> d.getSequenceAlignmentPenalty(medoid))
                        .sum();

                float averageAClusterSim = AClusterSim/(1f*dataItems.size());

                simWithinClusters += averageAClusterSim;
                numNonemptyClusters +=1f;
            }
        }
       
        float averageSimWithinClusters = simWithinClusters / (numNonemptyClusters);
        
        score = averageSimWithinClusters - averageSimMedoids;
        return score;
    }
}
