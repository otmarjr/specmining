/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.smartic;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
    
    public List<List<String>> substringsSatisfyingPre(Trace t){
        // using criteria of page 53 of Lo's Thesis
        
        int elementsLeftToMatch = this.pre.size();
        int indexOfLastMatch = -1;
        int indexOfLastInspection = 0;
        
        List<String> text = t.getEvents();
        
        while (elementsLeftToMatch > 0 
                && indexOfLastInspection < text.size()){
            String candidate = this.pre.get(indexOfLastMatch+1);
            
            if (text.get(indexOfLastInspection).equals(candidate)){
                elementsLeftToMatch--;
                indexOfLastMatch = indexOfLastInspection;
            }
            
            indexOfLastInspection++;
        }
        
        if (elementsLeftToMatch == 0){
            int diff = text.size()-(indexOfLastMatch+1);
            
            List<List<String>> substrings = new LinkedList<>();
            
            for (int i=1;i<=diff;i++){
                
            }
            
            return substrings;
        }
        else{
            return new LinkedList<>();
        }
    }

    @Override
    public String toString() {
        return String.join("", this.pre) + "->" + String.join("", this.post);
    }
}
