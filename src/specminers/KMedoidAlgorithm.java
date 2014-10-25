/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers;

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
 * @author otmar
 */
public class KMedoidAlgorithm {

    List<String> dataSet;

    public KMedoidAlgorithm(List<String> items) {
        this.dataSet = new LinkedList<>(items);
    }

    private int getSimilarity(String x, String y){
        int similarity = 0;
        
        return similarity;
    }
    
    private int getIndexOfNearestRepresentative(Map<String, Integer> clusters,
            String item){
    
        int index = -1;
        
        
        return index;
    }
    public List<List<String>> getClusters(int k) {
        List<List<String>> clusters = new LinkedList<>();

        int n = this.dataSet.size();

        if (n >= k) {

            List<Integer> seeds = new LinkedList<>();

            for (int i = 0; i < n; i++) {
                seeds.add(i);
            }

            Collections.shuffle(seeds);

            List<Integer> kObjectsIndexes = seeds.subList(0, k + 1);
            List<String> objects;
            objects = new LinkedList<>();

            Map<String, Integer> objectClusters = new HashMap<>();

            kObjectsIndexes
                    .forEach(index -> {
                        objects.add(this.dataSet.get(index));
                        objectClusters.put(this.dataSet.get(index), index);
                    });

            boolean hadChange = true;

            Map<String, Integer> remainingObjectClusters = new HashMap<>();
            
            while (hadChange) {
                List<String> remainingObjects = 
                        this.dataSet.stream()
                                .filter(o -> !objectClusters.containsKey(o))
                                .collect(Collectors.toList());
                
                remainingObjects.forEach(ro ->{
                    int nearestClusterIndex = this.getIndexOfNearestRepresentative(objectClusters, ro);
                    
                    remainingObjectClusters.put(ro, nearestClusterIndex);
                });
            }
            
            return clusters;
        } else {
            for (int i = 0; i < n; i++) {
                List<String> clusteri = new LinkedList<>();
                clusteri.add(this.dataSet.get(i));
                clusters.add(clusteri);
            }
        }

        return clusters;
    }
}
