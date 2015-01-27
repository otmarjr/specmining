/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.tools;

import specminers.StringHelper;

/**
 *
 * @author otmarpereira
 */
public class Sequitur {
    public static void main(String[] args){
        if (args.length > 0){
            String input = args[0];
            
            String regex = StringHelper.generateRegexToMatchInput(input);
            
            System.out.println(regex);
        }
    }
}
