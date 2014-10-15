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
public class Sequence {
    List<String> events;
    
    public Sequence(List<String> events)
    {
      this.events = events;
    }
    
    public List<String> getEvents()
    {
        return this.events;
    }
}
