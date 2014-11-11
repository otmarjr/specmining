/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers;

import simpleSequitur.sequitur;
import edu.hawaii.jmotif.gi.GrammarRuleRecord;
import java.util.List;
import edu.hawaii.jmotif.gi.sequitur.SAXRule;
import edu.hawaii.jmotif.gi.sequitur.SequiturFactory;
import edu.hawaii.jmotif.timeseries.TSException;
import java.util.ArrayList;

/**
 *
 * @author otmar
 */
public class StringUtils {
    public static String generateRegexToMatchInput(String input) 
            throws TSException{
        
        return sequitur.getGrammarBasedRegex(input, null);
    }
    
    public static int getSequenceAlignmentPenalty(String s1, String s2, int gapPenalty, int mismatchPenalty){
        int m = s1.length();
        int n = s2.length();
        
        int[][] A = new int[m+1][n+1];
        
        for (int i=0;i<m;i++) A[i][0] = 0;
        for (int i=0;i<n;i++) A[0][i] = 0;
        
        for (int i=1;i<=m;i++){
            for (int j=1;j<=n;j++){
                String s1i = s1.substring(i-1,i);
                String s2j = s2.substring(j-1,j);
                
                int previousPenalty = A[i][j] = A[i-1][j-1];
                
                if (s1i.equals(s2j)){
                    A[i][j] = previousPenalty;
                }
                else{
                    int p1 = previousPenalty + mismatchPenalty;
                    int p2 = gapPenalty + A[i-1][j];
                    int p3 = gapPenalty + A[i][j-1];
                    
                    A[i][j] = Math.min(Math.min(p1, p2), p3);
                }
            }
        }
        
        return A[m][n];
    }
}
