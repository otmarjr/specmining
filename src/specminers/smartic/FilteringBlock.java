/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.smartic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import jp.ac.titech.cs.se.sparesort.SequenceDatabase;

/**
 *
 * @author otmar
 */
public class FilteringBlock {

    private List<Trace> traces;
    private List<AssociationRule> outlierDetectionRules;

    private final float CONFIDENCE_EXPERIMENT_CHAPTER_5_DAVID_LO = 0.5f;
    private final float SUPPORT_EXPERIMENT_CHAPTER_5_DAVID_LO = 0.1f;

    public FilteringBlock(List<Trace> executionTraces) {
        this.traces = executionTraces;
    }

    // According to routine by David Lo's Thesis, Figure 5.4
    public List<AssociationRule> generateRules(List<Sequence> closed,
            double conf) {
        List<List<String>> closedSequences = closed.stream().map(Sequence::getEvents).collect(Collectors.toList());
        Map<List<String>, Integer> counts = closed.stream().collect(Collectors.toMap(s -> s.getEvents(), s -> s.getCount()));
        TrieTree trie = new TrieTree(closedSequences, counts);

        return trie.generateRules(conf);
    }

    private void prepareInputsForFiltering() throws Exception {
        SequenceDatabase<String> seqDB = new SequenceDatabase<>();

        this.traces.forEach(t -> seqDB.addSequence(t.getEvents()));

        int support = Math.round(this.traces.size()
                * SUPPORT_EXPERIMENT_CHAPTER_5_DAVID_LO);

        Map<List<String>, Integer> closedSeqs
                = seqDB.mineFrequentClosedSequences(support);

        List<Sequence> Closed = new ArrayList<>();

        Map<Sequence, Integer> ClosedCount = new HashMap<>();

        closedSeqs.keySet().stream().forEach((events) -> {
            Sequence seq = new Sequence(events, closedSeqs.get(events));
            Closed.add(seq);
            ClosedCount.put(seq, closedSeqs.get(events));
        });

        this.outlierDetectionRules = this.generateRules(Closed,
                CONFIDENCE_EXPERIMENT_CHAPTER_5_DAVID_LO);
    }

    public List<Trace> getFilteredTraces() throws Exception {

        this.prepareInputsForFiltering();

        List<Trace> Filtered;
        Filtered = new ArrayList<>();
        
        List<Trace> Err;
        Err = new ArrayList<>();

        for (Trace t : this.traces) {
            boolean traceAddedToErr = false;

            for (AssociationRule r : this.outlierDetectionRules) {
                if (!traceAddedToErr) {
                    List<Trace.SubTrace> substringsPre
                            = r.substringsSatisfyingPre(t);

                    if (substringsPre.stream().anyMatch(sub
                            -> !r.sequenceSatisfiesPost(sub.getAjPlus1ToAEnd()))) {
                        Err.add(t);
                        traceAddedToErr = true;
                    }
                }
            }
            
            if (!traceAddedToErr){
                Filtered.add(t);
            }
        }
        

        return Filtered;
    }
}
