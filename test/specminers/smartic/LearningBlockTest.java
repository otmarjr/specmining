/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.smartic;

import cz.cuni.mff.ksi.jinfer.base.automaton.Automaton;
import cz.cuni.mff.ksi.jinfer.base.automaton.State;
import cz.cuni.mff.ksi.jinfer.base.automaton.Step;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author otmar
 */
public class LearningBlockTest {

    public LearningBlockTest() {
    }

    @Test
    public void testLearnAutomaton() throws InterruptedException {
        System.out.println("learnAutomaton");
        List<Trace> traces = new LinkedList<>();

        Trace t1 = new Trace();
        t1.addEvent("A");
        t1.addEvent("B");
        t1.addEvent("C");
        t1.addEvent("D");
        t1.addEvent("E");

        Trace t2 = new Trace();
        t2.addEvent("A");
        t2.addEvent("B");
        t2.addEvent("C");
        t2.addEvent("X");
        t2.addEvent("Y");

        Trace t3 = new Trace();
        t3.addEvent("A");
        t3.addEvent("E");
        t3.addEvent("B");
        t3.addEvent("D");
        t3.addEvent("E");

        traces.add(t1);
        traces.add(t2);
        traces.add(t3);

        List<List<String>> expResult = null;
        Automaton<String> result;
        result = LearningBlock.learnAutomaton(traces, 2, 1);

        int numberOfDistinctKStrings = 9;
        assertEquals(numberOfDistinctKStrings, result.getDelta().size());
    }

    public static List<Trace> testDotStringsFileTraces() {
        // Traces corresponding to test.strings file in Raman's material.
        List<Trace> traces = new LinkedList<>();
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("l");
                addEvent("x");
                addEvent("y");
                addEvent("z");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("m");
                addEvent("n");
                addEvent("o");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("r");
                addEvent("s");
                addEvent("t");
                addEvent("a");
                addEvent("b");
                addEvent("c");
                addEvent("d");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("l");
                addEvent("x");
                addEvent("y");
                addEvent("z");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("m");
                addEvent("n");
                addEvent("o");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("m");
                addEvent("n");
                addEvent("o");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("k");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("h");
                addEvent("s");
                addEvent("t");
                addEvent("a");
                addEvent("b");
                addEvent("c");
                addEvent("d");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("p");
                addEvent("q");
                addEvent("o");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("p");
                addEvent("q");
                addEvent("o");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("r");
                addEvent("s");
                addEvent("t");
                addEvent("a");
                addEvent("b");
                addEvent("c");
                addEvent("d");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("h");
                addEvent("s");
                addEvent("t");
                addEvent("a");
                addEvent("b");
                addEvent("c");
                addEvent("d");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("r");
                addEvent("s");
                addEvent("t");
                addEvent("a");
                addEvent("b");
                addEvent("c");
                addEvent("d");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("k");
                addEvent("k");
                addEvent("l");
                addEvent("x");
                addEvent("y");
                addEvent("z");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("h");
                addEvent("s");
                addEvent("t");
                addEvent("a");
                addEvent("b");
                addEvent("c");
                addEvent("d");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("p");
                addEvent("q");
                addEvent("o");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("m");
                addEvent("n");
                addEvent("o");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("h");
                addEvent("s");
                addEvent("t");
                addEvent("a");
                addEvent("b");
                addEvent("c");
                addEvent("d");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("p");
                addEvent("q");
                addEvent("o");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("p");
                addEvent("q");
                addEvent("o");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("m");
                addEvent("n");
                addEvent("o");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("k");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("k");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("p");
                addEvent("q");
                addEvent("o");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("r");
                addEvent("s");
                addEvent("t");
                addEvent("a");
                addEvent("b");
                addEvent("c");
                addEvent("d");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("m");
                addEvent("n");
                addEvent("o");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("r");
                addEvent("s");
                addEvent("t");
                addEvent("a");
                addEvent("b");
                addEvent("c");
                addEvent("d");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("m");
                addEvent("n");
                addEvent("o");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("m");
                addEvent("n");
                addEvent("o");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("r");
                addEvent("s");
                addEvent("t");
                addEvent("a");
                addEvent("b");
                addEvent("c");
                addEvent("d");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("k");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("p");
                addEvent("q");
                addEvent("o");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("m");
                addEvent("n");
                addEvent("o");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("h");
                addEvent("s");
                addEvent("t");
                addEvent("a");
                addEvent("b");
                addEvent("c");
                addEvent("d");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("p");
                addEvent("q");
                addEvent("o");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("k");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("m");
                addEvent("n");
                addEvent("o");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("m");
                addEvent("n");
                addEvent("o");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("m");
                addEvent("n");
                addEvent("o");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("r");
                addEvent("s");
                addEvent("t");
                addEvent("a");
                addEvent("b");
                addEvent("c");
                addEvent("d");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("h");
                addEvent("s");
                addEvent("t");
                addEvent("a");
                addEvent("b");
                addEvent("c");
                addEvent("d");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("h");
                addEvent("s");
                addEvent("t");
                addEvent("a");
                addEvent("b");
                addEvent("c");
                addEvent("d");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("p");
                addEvent("q");
                addEvent("o");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("h");
                addEvent("s");
                addEvent("t");
                addEvent("a");
                addEvent("b");
                addEvent("c");
                addEvent("d");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("h");
                addEvent("s");
                addEvent("t");
                addEvent("a");
                addEvent("b");
                addEvent("c");
                addEvent("d");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("m");
                addEvent("n");
                addEvent("o");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("k");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("p");
                addEvent("q");
                addEvent("o");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("l");
                addEvent("x");
                addEvent("y");
                addEvent("z");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("m");
                addEvent("n");
                addEvent("o");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("h");
                addEvent("s");
                addEvent("t");
                addEvent("a");
                addEvent("b");
                addEvent("c");
                addEvent("d");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("p");
                addEvent("q");
                addEvent("o");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("l");
                addEvent("x");
                addEvent("y");
                addEvent("z");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("h");
                addEvent("s");
                addEvent("t");
                addEvent("a");
                addEvent("b");
                addEvent("c");
                addEvent("d");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("r");
                addEvent("s");
                addEvent("t");
                addEvent("a");
                addEvent("b");
                addEvent("c");
                addEvent("d");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("m");
                addEvent("n");
                addEvent("o");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("m");
                addEvent("n");
                addEvent("o");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("m");
                addEvent("n");
                addEvent("o");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("l");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("m");
                addEvent("n");
                addEvent("o");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("r");
                addEvent("s");
                addEvent("t");
                addEvent("a");
                addEvent("b");
                addEvent("c");
                addEvent("d");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("m");
                addEvent("n");
                addEvent("o");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("h");
                addEvent("s");
                addEvent("t");
                addEvent("a");
                addEvent("b");
                addEvent("c");
                addEvent("d");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("p");
                addEvent("q");
                addEvent("o");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("h");
                addEvent("s");
                addEvent("t");
                addEvent("a");
                addEvent("b");
                addEvent("c");
                addEvent("d");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("i");
                addEvent("j");
                addEvent("k");
                addEvent("l");
                addEvent("x");
                addEvent("y");
                addEvent("z");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("m");
                addEvent("n");
                addEvent("o");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });
        traces.add(new Trace() {
            {
                addEvent("m");
                addEvent("n");
                addEvent("o");
                addEvent("e");
                addEvent("f");
                addEvent("g");
            }
        });

        return traces;
    }
    
    private String getAutomatonTransitionsInRamansPFSAFileFormat(Automaton<String> aut) {
        StringBuilder sb = new StringBuilder();
         
        for (State<String> s : aut.getDelta().keySet()){
            int source = s.getName()-1; // Ramans file is indexed beginning from 0
            for (Step<String> t : aut.getDelta().get(s)){
                int sink = t.getDestination().getName()-1;
                String line = source + "	" + sink + "	                   " + t.getAcceptSymbol() + "	" + t.getUseCount() + System.getProperty("line.separator");
                sb.append(line);
            }
        }
        
        return sb.toString();
    }
    @Test
    public void testLearnAutomatonRamansPFSAPackageExample() throws InterruptedException{
        List<Trace> traces = testDotStringsFileTraces();
        
        assertEquals(100, traces.size());
        
        int statesAfterSkStrings = 19;
        
        Automaton<String> automaton = LearningBlock.learnAutomaton(traces, 2, 1);
        
        assertEquals(19, automaton.getDelta().size());
    }
    
    
}
