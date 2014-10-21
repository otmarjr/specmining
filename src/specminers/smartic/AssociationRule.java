/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.smartic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import specminers.smartic.Trace.SubTrace;

/**
 *
 * @author otmar
 */
public class AssociationRule {

    private List<String> pre;
    private List<String> post;
    private double confidence;

    public static List<AssociationRule> pruneRedundant(List<AssociationRule> rules) {
        List<AssociationRule> nonRedundantSet = rules.stream()
                .filter(r -> !rules.stream().filter(r2 -> r != r2)
                            .anyMatch(r2 -> r.canBeInferedFromOtherRule(r2)))
                .collect(Collectors.toList());
        
        return nonRedundantSet;
    }

    public AssociationRule(List<String> presequence, List<String> postsequence, double confidence) {
        this.pre = presequence;
        this.post = postsequence;
        this.confidence = confidence;
    }

    public double getConfidence() {
        return this.confidence;
    }
    
    private Boolean hasSamePrefix(AssociationRule other){
        if (other.pre.size() != this.pre.size())
            return false;
        
        for (int i=0;i<this.pre.size();++i){
            if (this.pre.get(i) == null ? other.pre.get(i) != null : !this.pre.get(i).equals(other.pre.get(i)))
                return false;
        }
        
        return true;
    }
    
    private Boolean postFixContainedByOtherRule(AssociationRule other){
        if (other.post.size() < this.post.size())
            return false;
        
        for (int i=0;i<this.post.size();++i){
            if (!other.post.contains(this.post.get(i)))
                return false;
        }
        
        return true;
    }
    
    private Boolean canBeInferedFromOtherRule(AssociationRule other){
        boolean samePrefix = this.hasSamePrefix(other);
        boolean postFixInferrableFromOther = this.postFixContainedByOtherRule(other);
        return samePrefix &&  postFixInferrableFromOther;
    }
    
    
    public boolean sequenceSatisfiesPost(List<String> sequence){
        return this.post.stream().allMatch(a -> sequence.contains(a));
    }
    
    public List<SubTrace> substringsSatisfyingPre(Trace t){
        
        Map<String, List<Integer>> preMembersMatches = new LinkedHashMap<>();

        this.pre.stream().forEach(ai -> preMembersMatches.putIfAbsent(ai, new LinkedList<>()));
        
        for (int i=0;i< t.getEvents().size();++i){
            String ai = t.getEvents().get(i);
            if (this.pre.contains(ai)){
                preMembersMatches.get(ai).add(i);
            }
        }
        
        List<SubTrace> substrings = new LinkedList<>();
        
        
        OptionalInt minimumSize = preMembersMatches.values()
                .stream().mapToInt(l -> l.size()).min();
        
        if (minimumSize.isPresent() && minimumSize.getAsInt() > 0){
            String aj = this.pre.get(this.pre.size()-1);
            String ai = this.pre.get(0);
            int totalSubStrings = 0;
            
            int subStringIndex = 0;
            
            while (totalSubStrings < minimumSize.getAsInt()){
                int i = preMembersMatches.get(ai).get(subStringIndex);
                int j = preMembersMatches.get(aj).get(subStringIndex);
                
                List<String> traceij = t.getEvents().subList(i, j+1);
                
                boolean containsAllPreMembers = 
                        this.pre.stream().allMatch(m -> traceij.contains(m));
                
                if (containsAllPreMembers){
                    int firstI = preMembersMatches.get(ai).get(0);
                    substrings.add(t.new SubTrace(firstI, j));
                    totalSubStrings++;
                }
                subStringIndex++;
            }
        }
        
        return substrings;
    }

    @Override
    public String toString() {
        return String.join("", this.pre) + "->" + String.join("", this.post);
    }
}
