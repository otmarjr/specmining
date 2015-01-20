/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.smartic;

import cz.cuni.mff.ksi.jinfer.base.automaton.Automaton;
import cz.cuni.mff.ksi.jinfer.base.automaton.State;
import cz.cuni.mff.ksi.jinfer.base.automaton.Step;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author otmar
 */
public class ECDataStructure {
    private State<String> repObj;
    private Map<State<String>, Set<State<String>>> descMap;
    private Set<Step<String>> transSet;
    private Automaton<String> automaton;
    
    public ECDataStructure(State<String> representativeObject, Map<State<String>, Set<State<String>>> descendantsMap, Set<Step<String>> transitionsSet, Automaton<String> automaton){
        this.repObj = representativeObject;
        this.descMap = descendantsMap;
        this.transSet = transitionsSet;
    }
    
    
    public State<String> getRepresentativeObject(){
        return this.repObj;
    }
    
    public Map<State<String>, Set<State<String>>> getDescendentsReachabilityMap(){
        return this.descMap;
    }
    
    public Set<Step<String>> getTransitionsSet(){
        return this.transSet;
    }
}
