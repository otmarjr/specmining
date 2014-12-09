/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.smartic;

import cz.cuni.mff.ksi.jinfer.base.automaton.Automaton;
import cz.cuni.mff.ksi.jinfer.base.automaton.State;
import cz.cuni.mff.ksi.jinfer.base.automaton.Step;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Otmar
 */
public class MergingBlockTest {
    

    private Automaton<String> getRamansTestStringsPostSkStringsAutomaton(){
        Automaton<String> automaton;
        automaton = new Automaton<>(0, true);
        
        State s0 = automaton.getInitialState();
        State s1 = automaton.createNewState();
        State s2 = automaton.createNewState();
        State s3 = automaton.createNewState();
        State s4 = automaton.createNewState();
        State s5 = automaton.createNewState();
        State s6 = automaton.createNewState();
        State s7 = automaton.createNewState();
        State s8 = automaton.createNewState();
        State s9 = automaton.createNewState();
        State s10 = automaton.createNewState();
        State s11 = automaton.createNewState();
        State s12 = automaton.createNewState();
        State s13 = automaton.createNewState();
        State s14 = automaton.createNewState();
        State s15 = automaton.createNewState();
        State s16 = automaton.createNewState();
        State s17 = automaton.createNewState();
        State s18 = automaton.createNewState();
        
        
        automaton.createNewStep("i", s0, s1, 45);
        automaton.createNewStep("m", s0, s2, 21);
        automaton.createNewStep("r", s0, s3, 9);
        automaton.createNewStep("h", s0, s3, 13);
        automaton.createNewStep("p", s0, s4, 12);
        
        automaton.createNewStep("j", s1, s5, 45);
        
        automaton.createNewStep("n", s2, s6, 21);
        
        automaton.createNewStep("s", s3, s7, 22);
        
        automaton.createNewStep("q", s4, s6, 12);
        
        automaton.createNewStep("k", s5, s5, 36);
        automaton.createNewStep("l", s5, s8, 38);
        automaton.createNewStep("k", s5, s11, 7);
        
        automaton.createNewStep("o", s6, s9, 33);
        
        automaton.createNewStep("t", s7, s10, 22);
        
        automaton.createNewStep("x", s8, s12, 5);
        automaton.createNewStep("e", s8, s13, 33);
        
        automaton.createNewStep("e", s9, s13, 39);
        
        automaton.createNewStep("a", s10, s14, 22);
        
        automaton.createNewStep("l", s11, s9, 6);
        automaton.createNewStep("k", s11, s15, 1);
        
        automaton.createNewStep("y", s12, s16, 6);
        
        automaton.createNewStep("f", s13, s16, 72);
        
        automaton.createNewStep("b", s14, s17, 22);
        
        automaton.createNewStep("l", s15, s18, 1);
        
        automaton.createNewStep("z", s16, s16, 6);
        automaton.createNewStep("g", s16, s16, 72);
        automaton.createNewStep("d", s16, s16, 22);
        
        automaton.createNewStep("c", s17, s16, 2);
        
        automaton.createNewStep("x", s18, s12, 1);
        
        
        s16.setFinalCount(100);
        return automaton;
    }
    @Test
    public void testGetAllStringsAcceptedByAutomaton() {
        System.out.println("getAllStringsAcceptedByAutomaton");
        List<Trace> traces = LearningBlockTest.testDotStringsFileTraces();
        Automaton<String> automaton = getRamansTestStringsPostSkStringsAutomaton();
        MergingBlock instance = new MergingBlock();
        List<List<String>> result = instance.getAllStringsAcceptedByAutomaton(traces, automaton);
        
        assertEquals(traces.size(), result.size());
    }
    
    @Test
    public void testGetAllStringsAcceptedByAutomatonWithNewlyAcceptableStrings() {
        System.out.println("getAllStringsAcceptedByAutomaton");
        List<Trace> traces = LearningBlockTest.testDotStringsFileTraces();
        
        Trace newlyAcceptableTraceOnState5Loop = new Trace();
        
        newlyAcceptableTraceOnState5Loop.addEvent("i");
        newlyAcceptableTraceOnState5Loop.addEvent("j");
        IntStream.range(0, 1000).forEach(i -> newlyAcceptableTraceOnState5Loop.addEvent("k"));
        newlyAcceptableTraceOnState5Loop.addEvent("l");
        newlyAcceptableTraceOnState5Loop.addEvent("x");
        newlyAcceptableTraceOnState5Loop.addEvent("y");
        
        traces.add(newlyAcceptableTraceOnState5Loop);
        Automaton<String> automaton = getRamansTestStringsPostSkStringsAutomaton();
        MergingBlock instance = new MergingBlock();
        List<List<String>> result = instance.getAllStringsAcceptedByAutomaton(traces, automaton);
        
        assertEquals(traces.size(), result.size());
    }
    
    @Test
    public void testGetPrefixes() {
        Automaton<String> automaton = this.getRamansTestStringsPostSkStringsAutomaton();
        
        MergingBlock mb = new MergingBlock();
        
        State s0 = automaton.getInitialState();
        State s1 = automaton.getDelta().keySet().stream().filter(k -> k.getName() ==1).findFirst().get();
        State s2 = automaton.getDelta().keySet().stream().filter(k -> k.getName() ==2).findFirst().get();
        State s3 = automaton.getDelta().keySet().stream().filter(k -> k.getName() ==3).findFirst().get();
        State s4 = automaton.getDelta().keySet().stream().filter(k -> k.getName() ==4).findFirst().get();
        State s5 = automaton.getDelta().keySet().stream().filter(k -> k.getName() ==5).findFirst().get();
        State s6 = automaton.getDelta().keySet().stream().filter(k -> k.getName() ==6).findFirst().get();
        State s7 = automaton.getDelta().keySet().stream().filter(k -> k.getName() ==7).findFirst().get();
        State s8 = automaton.getDelta().keySet().stream().filter(k -> k.getName() ==8).findFirst().get();
        State s9 = automaton.getDelta().keySet().stream().filter(k -> k.getName() ==9).findFirst().get();
        State s10 = automaton.getDelta().keySet().stream().filter(k -> k.getName() ==10).findFirst().get();
        State s11 = automaton.getDelta().keySet().stream().filter(k -> k.getName() ==11).findFirst().get();
        State s12 = automaton.getDelta().keySet().stream().filter(k -> k.getName() ==12).findFirst().get();
        State s13 = automaton.getDelta().keySet().stream().filter(k -> k.getName() ==13).findFirst().get();
        State s14 = automaton.getDelta().keySet().stream().filter(k -> k.getName() ==14).findFirst().get();
        State s15 = automaton.getDelta().keySet().stream().filter(k -> k.getName() ==15).findFirst().get();
        State s16 = automaton.getDelta().keySet().stream().filter(k -> k.getName() ==16).findFirst().get();
        State s17 = automaton.getDelta().keySet().stream().filter(k -> k.getName() ==17).findFirst().get();
        State s18 = automaton.getDelta().keySet().stream().filter(k -> k.getName() ==18).findFirst().get();
        
        
        Set<List<Step<String>>> prefix0 = mb.getPrefixes(automaton, automaton.getInitialState());
        Set<List<Step<String>>> prefix1 = mb.getPrefixes(automaton, s1);
        Set<List<Step<String>>> prefix2 = mb.getPrefixes(automaton, s2);
        Set<List<Step<String>>> prefix3 = mb.getPrefixes(automaton, s3);
        Set<List<Step<String>>> prefix4 = mb.getPrefixes(automaton, s4);
        Set<List<Step<String>>> prefix5 = mb.getPrefixes(automaton, s5);
        Set<List<Step<String>>> prefix6 = mb.getPrefixes(automaton, s6);
        Set<List<Step<String>>> prefix7 = mb.getPrefixes(automaton, s7);
        Set<List<Step<String>>> prefix8 = mb.getPrefixes(automaton, s8);
        Set<List<Step<String>>> prefix9 = mb.getPrefixes(automaton, s9);
        Set<List<Step<String>>> prefix10 = mb.getPrefixes(automaton, s10);
        Set<List<Step<String>>> prefix11 = mb.getPrefixes(automaton, s11);
        Set<List<Step<String>>> prefix12 = mb.getPrefixes(automaton, s12);
        Set<List<Step<String>>> prefix13 = mb.getPrefixes(automaton, s13);
        Set<List<Step<String>>> prefix14 = mb.getPrefixes(automaton, s14);
        Set<List<Step<String>>> prefix15 = mb.getPrefixes(automaton, s15);
        Set<List<Step<String>>> prefix16 = mb.getPrefixes(automaton, s16);
        Set<List<Step<String>>> prefix17 = mb.getPrefixes(automaton, s17);
        Set<List<Step<String>>> prefix18 = mb.getPrefixes(automaton, s18);
        
        Step<String> t01 = automaton.getFirstStep(0, 1);
        Step<String> t02 = automaton.getFirstStep(0, 2);
        Set<Step<String>> t03 = automaton.getAllSteps(0, 3);
        Step<String> t04 = automaton.getFirstStep(0, 4);
        
        Step<String> t15 = automaton.getFirstStep(1, 5);
        
        Step<String> t26 = automaton.getFirstStep(2, 6);
        
        Step<String> t37 = automaton.getFirstStep(3, 7);
        
        Step<String> t46 = automaton.getFirstStep(4, 6);
        
        Step<String> t55 = automaton.getFirstStep(5, 5);
        Step<String> t58 = automaton.getFirstStep(5, 8);
        Step<String> t511 = automaton.getFirstStep(5, 11);
        
        Step<String> t69 = automaton.getFirstStep(6, 9);
        
        Step<String> t710 = automaton.getFirstStep(7, 10);
        
        Step<String> t812 = automaton.getFirstStep(8, 12);
        Step<String> t813 = automaton.getFirstStep(8, 13);
        
        Step<String> t913 = automaton.getFirstStep(9, 13);
        
        Step<String> t1014 = automaton.getFirstStep(10, 14);
        
        Step<String> t119 = automaton.getFirstStep(11, 9);
        Step<String> t1115 = automaton.getFirstStep(11, 15);
        
        Step<String> t1216 = automaton.getFirstStep(12, 16);
        
        Step<String> t1316 = automaton.getFirstStep(13, 16);
        
        Step<String> t1417 = automaton.getFirstStep(14, 17);
        
        Step<String> t1518 = automaton.getFirstStep(15, 18);
        
        Set<Step<String>> t1616 = automaton.getAllSteps(16, 16);
        
        Step<String> t1716 = automaton.getFirstStep(17, 16);
        
        
        assertEquals(0, prefix0.size());
        
        assertEquals(1, prefix1.size());
        assertTrue(prefix1.contains(Arrays.asList(t01)));
        
        assertEquals(1, prefix2.size());
        assertTrue(prefix2.contains(Arrays.asList(t02)));
        
        assertEquals(2, prefix3.size());
        assertTrue(prefix3.contains(Arrays.asList(t03.stream().findFirst().get())));
        assertTrue(prefix3.contains(Arrays.asList(t03.stream().skip(1).findFirst().get())));
        
        assertEquals(1, prefix4.size());
        assertTrue(prefix4.contains(Arrays.asList(t04)));
        
        assertEquals(1, prefix5.size());
        assertTrue(prefix5.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01,t15,t55))));
        
        assertEquals(2, prefix6.size());
        assertTrue(prefix6.stream().anyMatch(pref -> pref.equals(Arrays.asList(t04,t46))));
        assertTrue(prefix6.stream().anyMatch(pref -> pref.equals(Arrays.asList(t02,t26))));
        
        assertEquals(2, prefix7.size());
        assertTrue(prefix7.stream().anyMatch(pref -> pref.equals(Arrays.asList(automaton.getOutStepOnSymbol(s3, "r"),t37))));
        assertTrue(prefix7.stream().anyMatch(pref -> pref.equals(Arrays.asList(automaton.getOutStepOnSymbol(s3, "h"),t37))));
        
    }
}
