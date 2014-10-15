/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.smartic;

import java.util.List;

/**
 *
 * @author otmar
 */
public class AssociationRule {
    private List<String> pre;
    private List<String> post;
    
    
    public AssociationRule(List<String> presequence, List<String> postsequence)
    {
        this.pre = presequence;
        this.post = postsequence;
    }
}
