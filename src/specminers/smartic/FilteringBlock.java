/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.smartic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jp.ac.titech.cs.se.sparesort.SequenceDatabase;

/**
 *
 * @author otmar
 */
public class FilteringBlock {

    private Iterable<Trace> traces;

    public FilteringBlock(Iterable<Trace> executionTraces) {
        this.traces = executionTraces;
    }

    private List<AssociationRule> getTemporalRules() {
        List<AssociationRule> rules = null;

        return rules;
    }

    // According to routine by David Lo's Thesis, Figure 5.4
    private List<AssociationRule> generateRules(List<String> closedSequentialPatterns, double conf) {
        List<AssociationRule> rules = null;

        return rules;
    }

    public List<Trace> getFilteredTraces(int minSupport) throws Exception {

        SequenceDatabase<String> seqDB = new SequenceDatabase<String>();

        for (Trace t : this.traces) {
            seqDB.addSequence(t.getEvents());
        }

        Map<List<String>, Integer> closedSeqs = seqDB.mineFrequentClosedSequences(minSupport);

        List<Sequence> Closed = new ArrayList<Sequence>();
        Map<Sequence, Integer> ClosedCount = new HashMap<>();

        for (List<String> events : closedSeqs.keySet()) {
            Sequence seq = new Sequence(events);
            Closed.add(seq);
            ClosedCount.put(seq, closedSeqs.get(events));
        }
        
        

        List<Trace> filteredTraces = new ArrayList<Trace>();
        List<Trace> erroneousTraces = new ArrayList<Trace>();

        List<AssociationRule> outlierDetectionRules;
        outlierDetectionRules = this.getTemporalRules();

        for (Trace t : this.traces) {
            for (AssociationRule ar : outlierDetectionRules) {

            }
        }

        return filteredTraces;
    }
}
