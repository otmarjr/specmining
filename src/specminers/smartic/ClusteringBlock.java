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

/**
 *
 * @author otmarpereira
 */
public class ClusteringBlock {

    Set<Trace> uniqueInputTraces;

    public ClusteringBlock(List<Trace> allTraces) {
        this.uniqueInputTraces = new HashSet<>(allTraces);
    }

    public List<Set<Trace>> executeParameterlessClusteringController() {
        List<Float> featureScoreList = new LinkedList<>();
        List<Set<Trace>> clusters = new LinkedList<>();
        boolean isLocalMaxima = false;

        Set<Trace> ipTraces = this.uniqueInputTraces;

        for (int k = 1; k < ipTraces.size() && !isLocalMaxima; k++) {
            clusters = kMedoid(k);
            Float featureScore = calculateScore(clusters);
            featureScoreList.add(featureScore);
            isLocalMaxima = hasGradientChange(featureScoreList);
        }

        return clusters;
    }

    public boolean hasGradientChange(List<Float> values) {
        boolean hasGradientChange = false;

        return hasGradientChange;
    }

    public List<Set<Trace>> kMedoid(int k) {
        List<Set<Trace>> kclusters = new LinkedList<>();

        List<Trace> l = new LinkedList(this.uniqueInputTraces);

        Collections.shuffle(l);

        Map<Trace, Set<Trace>> medoidsSets;
        medoidsSets = new HashMap<>();

        while (medoidsSets.size() < k) {
            Set<Trace> s = new HashSet<>();

            medoidsSets.put(l.get(medoidsSets.size()), s);
        }

        boolean medoidChanged = true;

        while (medoidChanged) {
            this.uniqueInputTraces.stream()
                    .filter((t) -> (!medoidsSets.containsKey(t)))
                    .forEach((t) -> {
                        Trace closestMedoid;
                        closestMedoid = medoidsSets.keySet()
                        .stream()
                        .min((k1, k2)
                                -> Integer
                                .compare(k1.getSequenceAlignmentPenalty(t),
                                        k2.getSequenceAlignmentPenalty(t))
                        ).get();

                        medoidsSets.get(closestMedoid).add(t);
                    });
            
            
        }

        return kclusters;
    }

    public Float calculateScore(List<Set<Trace>> clusters) {
        Float score = 0f;

        return score;
    }
}
