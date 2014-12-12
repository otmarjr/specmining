/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.smartic;

import cz.cuni.mff.ksi.jinfer.base.automaton.Automaton;
import cz.cuni.mff.ksi.jinfer.base.automaton.Step;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 *
 * @author otmarpereira
 */
public class TransitionString {

    String symbol;
    double probability;

    TransitionString(Step<String> step, Automaton<String> automaton) {
        this.symbol = step.getAcceptSymbol();
        int totalOutputCount = automaton.getDelta().get(step.getSource())
                .stream()
                .mapToInt(transition -> transition.getUseCount())
                .sum();

        this.probability = (double) ((double) step.getUseCount()) / totalOutputCount;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Trace)) {
            return false;
        }
        TransitionString t2;
        t2 = (TransitionString) other;

        if (t2.symbol == null) {
            return this.symbol == null;
        }

        return t2.probability == this.probability
                && t2.symbol.equals(this.symbol);
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hcb;

        hcb = new HashCodeBuilder(73, 653);

        if (this.symbol != null) {
            hcb.append(this.symbol);
        }

        hcb.append(this.probability);
        
        return hcb.toHashCode();
    }

}
