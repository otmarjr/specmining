/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.smartic;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author otmar
 */
public class Sequence {
    List<String> events;
    Integer count;
    
    public Sequence(List<String> events, int count)
    {
      this.events = events;
      this.count = count;
    }
    
    public Integer getCount(){
        return this.count;
    }
    
    public List<String> getEvents()
    {
        return this.events;
    }
    
    public boolean containsPrefix(List<String> prefix){
        if (prefix == null)
            return this.events == null;
        
        if (this.events == null)
            return false;
        
        for (int i=0;i<prefix.size();i++){
            if (!prefix.get(i).equals(this.events.get(i)))
                return false;
        }
        
        return true;
    }
    
    public List<String> getPostFix(List<String> prefix){
        if (this.containsPrefix(prefix)){
            return this.events.subList(prefix.size(), this.events.size());
        }
        return null;
    }
}
