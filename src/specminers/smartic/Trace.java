/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.smartic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import specminers.StringHelper;

/**
 *
 * @author otmar
 */
public class Trace {

    @Override
    public boolean equals(Object other){
        if (!(other instanceof Trace))
            return false;
        
        Trace t2 = (Trace)other;
        
        if (t2.events == null) return this.events == null;
        
        if (t2.events.size() != this.events.size()) return false;
        
        for (int i=0;i<this.events.size();i++){
            if (!this.events.get(i).equals(t2.events.get(i)))
                return false;
        }
        
        return true;
    }
    @Override
    public int hashCode(){
        HashCodeBuilder hcb;
        
        hcb = new HashCodeBuilder(17, 43);
        
        if (this.events != null){
            this.events.forEach(e -> hcb.append(e));
        }
        
        return hcb.toHashCode();
    }
    
    List<String> events;

    public Trace() {
        this.events = new ArrayList<>();
    }

    public void addEvent(String event) {
        this.events.add(event);
    }

    public List<String> getEvents() {
        return this.events;
    }

    public boolean containsSubTraceBeforePosition(int position, SubTrace tk) {
        List<String> subtrace1Tok = this.events.subList(0, position);

        return tk.getEvents().stream().allMatch(ti -> subtrace1Tok.contains(ti));
    }
    
    public String getEventsString(){
        return getEventsString("");
    }
    public String getEventsString(String delimiter){
        return StringUtils.join(events, delimiter);
    }
    
    public int getSequenceAlignmentPenalty(Trace other){
        Trace x = this;
        Trace y = other;
        
        String ex = x.getEventsString();
        String ey = y.getEventsString();
        
        String reX = StringHelper.generateRegexToMatchInput(ex);
        String rey = StringHelper.generateRegexToMatchInput(ey);
        
        return StringHelper.getSequenceAlignmentPenalty(ex, ey, 1, 1);
    }
    
    public class SubTrace {

        private final int fromIndex;
        private final int toIndex;

        public SubTrace(int from, int to) {
            this.fromIndex = from;
            this.toIndex = to;
        }

        public List<String> getEvents() {
            return events.subList(fromIndex, toIndex + 1);
        }

        public List<String> getAjPlus1ToAEnd() {
            return events.subList(toIndex + 1, events.size());
        }
    }
}
