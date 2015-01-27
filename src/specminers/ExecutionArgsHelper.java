/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Otmar
 */
public class ExecutionArgsHelper {
    public static Map<String, String> convertArgsToMap(String[] args) {
        Map<String, String> options = new HashMap();

        String lastOption = null;

        for (String arg : args) {
            if (arg.startsWith("-")) {
                options.put(arg, "");
                lastOption = arg;
            } else {
                if (lastOption != null && options.containsKey(lastOption)) {
                    options.put(lastOption, arg);
                }
            }
        }

        return options;
    }
    
    public static void displayHelp(List<String> lines){
        if (lines != null){
            lines.stream().forEach(l -> System.out.println(l));
        }
    }
    
}
