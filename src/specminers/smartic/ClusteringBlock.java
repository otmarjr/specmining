/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.smartic;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author otmarpereira
 */
public class ClusteringBlock {
    List<Trace> traces;
    
    public ClusteringBlock(List<Trace> filteredTraces){
        this.traces = filteredTraces;
    }
    
    public Set<Trace> getUniqueInputTraces(){
        Set<Trace> uniqueTraces = new HashSet<>();
        
        return uniqueTraces;
    }
    
    public List<List<Trace>> executeParameterlessClusteringController(){
        List<Float> featureScoreList = new LinkedList<>();
        List<List<Trace>> clusters = new LinkedList<>();
        boolean isLocalMaxima = false;
        
        Set<Trace> ipTraces = this.getUniqueInputTraces();
        
        for (int k=1;k<ipTraces.size() && !isLocalMaxima;k++){
            clusters = kMedoid(ipTraces, k);
            Float featureScore = calculateScore(clusters);
            featureScoreList.add(featureScore);
            isLocalMaxima = hasGradientChange(featureScoreList);
        }
        
        return clusters;
    }
    
    public boolean hasGradientChange(List<Float> values){
        boolean hasGradientChange = false;
        
        return hasGradientChange;
    }
    
    public List<List<Trace>> kMedoid(Set<Trace> uniqueTraces, int k){
        List<List<Trace>> kclusters = null;
        
        return kclusters;
    }
    
    public Float calculateScore(List<List<Trace>> clusters){
        Float score = 0f;
        
        return score;
    }
}
