/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers;

import edu.hawaii.jmotif.gi.GrammarRuleRecord;
import java.util.List;
import edu.hawaii.jmotif.gi.sequitur.SAXRule;
import edu.hawaii.jmotif.gi.sequitur.SequiturFactory;
import edu.hawaii.jmotif.timeseries.TSException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author otmar
 */
public class StringUtils {
    public static String generateRegexToMatchInput(String input) 
            throws TSException{
        String regex = null;
        
        SAXRule r = SequiturFactory.runSequitur(input);

        ArrayList<GrammarRuleRecord> recs = r.getRuleRecords();
        
        String result = "";
        
         Map<String, String> symbolsResolution;
        symbolsResolution = new HashMap<>();
        
        
        for (int i=recs.size()-1;i>=0;i--){
            symbolsResolution.put(recs.get(i).getRuleName(), recs.get(i).getRuleString());
        }
        
        for (GrammarRuleRecord gr : recs){
            result += "-" + gr.getRuleName() + " : " + gr.getRuleString() +" - exp: " + gr.getExpandedRuleString();
        }
        
        return result;
        
        // return regex;
    }
}
