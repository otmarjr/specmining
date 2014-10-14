/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.smartic;

/**
 *
 * @author otmar
 */
public class FilteringBlock {
    private Iterable<String> traces;
    
    public FilteringBlock(Iterable<String> executionTraces)
    {
        this.traces = executionTraces;
    }
    
    public Iterable<String> getFilteredTraces()
    {
        Iterable<String> filteredTraces =  null;
        
        return filteredTraces;
    }
}
