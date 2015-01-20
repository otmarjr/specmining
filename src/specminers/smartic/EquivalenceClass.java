/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.smartic;

import cz.cuni.mff.ksi.jinfer.base.automaton.State;
import java.util.Collection;

/**
 *
 * @author otmar
 */
public class EquivalenceClass {
    private State<String> repObj;
    private Collection<State<String>> equiv;
    
    public EquivalenceClass(final State<String> representativeObject, 
            final Collection<State<String>> equivalentNodes){
        this.repObj = representativeObject;
        this.equiv = equivalentNodes;
    }
    
    public State<String> getRepresentativeObject(){
        return this.repObj;
    }
    
    public Collection<State<String>> getEquivalentNodes() {
        return this.equiv;
    }
}
