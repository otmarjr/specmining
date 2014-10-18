/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.smartic;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author otmar
 */
public class Trace {
    List<String> events;
    
    public Trace()
    {
        this.events = new ArrayList<>();
    }
    
    public void addEvent(String event)
    {
        this.events.add(event);
    }
    
    public List<String> getEvents()
    {
        return this.events;
    }
}
