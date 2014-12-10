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
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.internal.matchers.IsCollectionContaining;

/**
 *
 * @author Otmar
 */
public class MergingBlockTest {

    private Automaton<String> getRamansTestStringsPostSkStringsAutomaton() {
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
        State s1 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 1).findFirst().get();
        State s2 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 2).findFirst().get();
        State s3 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 3).findFirst().get();
        State s4 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 4).findFirst().get();
        State s5 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 5).findFirst().get();
        State s6 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 6).findFirst().get();
        State s7 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 7).findFirst().get();
        State s8 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 8).findFirst().get();
        State s9 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 9).findFirst().get();
        State s10 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 10).findFirst().get();
        State s11 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 11).findFirst().get();
        State s12 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 12).findFirst().get();
        State s13 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 13).findFirst().get();
        State s14 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 14).findFirst().get();
        State s15 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 15).findFirst().get();
        State s16 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 16).findFirst().get();
        State s17 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 17).findFirst().get();
        State s18 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 18).findFirst().get();

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

        Set<Step<String>> t1616All = automaton.getAllSteps(16, 16);
        Step<String> t1616f = automaton.getFirstStep(16, 16);

        Step<String> t1716 = automaton.getFirstStep(17, 16);

        Step<String> t1812 = automaton.getFirstStep(18, 12);

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

        assertEquals(2, prefix5.size());
        assertTrue(prefix5.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t55))));
        assertTrue(prefix5.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15))));

        assertEquals(2, prefix6.size());
        assertTrue(prefix6.stream().anyMatch(pref -> pref.equals(Arrays.asList(t04, t46))));
        assertTrue(prefix6.stream().anyMatch(pref -> pref.equals(Arrays.asList(t02, t26))));

        assertEquals(2, prefix7.size());
        assertTrue(prefix7.stream().anyMatch(pref -> pref.equals(Arrays.asList(automaton.getOutStepOnSymbol(s0, "r"), t37))));
        assertTrue(prefix7.stream().anyMatch(pref -> pref.equals(Arrays.asList(automaton.getOutStepOnSymbol(s0, "h"), t37))));

        assertEquals(2, prefix8.size());
        assertTrue(prefix8.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t55, t58))));
        assertTrue(prefix8.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t58))));

        assertEquals(4, prefix9.size());
        assertTrue(prefix9.stream().anyMatch(pref -> pref.equals(Arrays.asList(t04, t46, t69))));
        assertTrue(prefix9.stream().anyMatch(pref -> pref.equals(Arrays.asList(t02, t26, t69))));
        assertTrue(prefix9.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t55, t511, t119))));
        assertTrue(prefix9.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t511, t119))));

        assertEquals(2, prefix10.size());
        assertTrue(prefix10.stream().anyMatch(pref -> pref.equals(Arrays.asList(automaton.getOutStepOnSymbol(s0, "r"), t37, t710))));
        assertTrue(prefix10.stream().anyMatch(pref -> pref.equals(Arrays.asList(automaton.getOutStepOnSymbol(s0, "h"), t37, t710))));

        assertEquals(2, prefix11.size());
        assertTrue(prefix11.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t55, t511))));
        assertTrue(prefix11.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t511))));

        assertEquals(4, prefix12.size());
        assertTrue(prefix12.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t55, t58, t812))));
        assertTrue(prefix12.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t58, t812))));
        assertTrue(prefix12.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t55, t511, t1115, t1518, t1812))));
        assertTrue(prefix12.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t511, t1115, t1518, t1812))));

        assertEquals(6, prefix13.size());
        assertTrue(prefix13.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t55, t58, t813))));
        assertTrue(prefix13.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t58, t813))));
        assertTrue(prefix13.stream().anyMatch(pref -> pref.equals(Arrays.asList(t04, t46, t69, t913))));
        assertTrue(prefix13.stream().anyMatch(pref -> pref.equals(Arrays.asList(t02, t26, t69, t913))));
        assertTrue(prefix13.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t55, t511, t119, t913))));
        assertTrue(prefix13.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t511, t119, t913))));

        assertEquals(2, prefix14.size());
        assertTrue(prefix14.stream().anyMatch(pref -> pref.equals(Arrays.asList(automaton.getOutStepOnSymbol(s0, "r"), t37, t710, t1014))));
        assertTrue(prefix14.stream().anyMatch(pref -> pref.equals(Arrays.asList(automaton.getOutStepOnSymbol(s0, "h"), t37, t710, t1014))));

        assertEquals(2, prefix15.size());
        assertTrue(prefix15.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t55, t511, t1115))));
        assertTrue(prefix15.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t511, t1115))));

        assertEquals(24, prefix16.size());
        assertTrue(prefix16.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t55, t58, t812, t1216))));
        assertTrue(prefix16.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t58, t812, t1216))));
        assertTrue(prefix16.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t55, t511, t1115, t1518, t1812, t1216))));
        assertTrue(prefix16.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t511, t1115, t1518, t1812, t1216))));
        assertTrue(prefix16.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t55, t58, t812, t1216, t1616f))));
        assertTrue(prefix16.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t58, t812, t1216, t1616f))));
        assertTrue(prefix16.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t55, t511, t1115, t1518, t1812, t1216, t1616f))));
        assertTrue(prefix16.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t511, t1115, t1518, t1812, t1216, t1616f))));
        assertTrue(prefix16.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t55, t58, t813, t1316))));
        assertTrue(prefix16.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t58, t813, t1316))));
        assertTrue(prefix16.stream().anyMatch(pref -> pref.equals(Arrays.asList(t04, t46, t69, t913, t1316))));
        assertTrue(prefix16.stream().anyMatch(pref -> pref.equals(Arrays.asList(t02, t26, t69, t913, t1316))));
        assertTrue(prefix16.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t55, t511, t119, t913, t1316))));
        assertTrue(prefix16.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t511, t119, t913, t1316))));
        assertTrue(prefix16.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t55, t58, t813, t1316, t1616f))));
        assertTrue(prefix16.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t58, t813, t1316, t1616f))));
        assertTrue(prefix16.stream().anyMatch(pref -> pref.equals(Arrays.asList(t04, t46, t69, t913, t1316, t1616f))));
        assertTrue(prefix16.stream().anyMatch(pref -> pref.equals(Arrays.asList(t02, t26, t69, t913, t1316, t1616f))));
        assertTrue(prefix16.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t55, t511, t119, t913, t1316, t1616f))));
        assertTrue(prefix16.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t511, t119, t913, t1316, t1616f))));
        assertTrue(prefix16.stream().anyMatch(pref -> pref.equals(Arrays.asList(automaton.getOutStepOnSymbol(s0, "r"), t37, t710, t1014, t1417, t1716))));
        assertTrue(prefix16.stream().anyMatch(pref -> pref.equals(Arrays.asList(automaton.getOutStepOnSymbol(s0, "h"), t37, t710, t1014, t1417, t1716))));
        assertTrue(prefix16.stream().anyMatch(pref -> pref.equals(Arrays.asList(automaton.getOutStepOnSymbol(s0, "r"), t37, t710, t1014, t1417, t1716, t1616f))));
        assertTrue(prefix16.stream().anyMatch(pref -> pref.equals(Arrays.asList(automaton.getOutStepOnSymbol(s0, "h"), t37, t710, t1014, t1417, t1716, t1616f))));
        
        assertEquals(2, prefix17.size());
        assertTrue(prefix17.stream().anyMatch(pref -> pref.equals(Arrays.asList(automaton.getOutStepOnSymbol(s0, "r"), t37, t710, t1014,t1417))));
        assertTrue(prefix17.stream().anyMatch(pref -> pref.equals(Arrays.asList(automaton.getOutStepOnSymbol(s0, "h"), t37, t710, t1014,t1417))));

        assertEquals(2, prefix18.size());
        assertTrue(prefix18.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t55, t511, t1115,t1518))));
        assertTrue(prefix18.stream().anyMatch(pref -> pref.equals(Arrays.asList(t01, t15, t511, t1115,t1518))));
    }
    
    @Test
    public void testGetSufixes() {
        Automaton<String> automaton = this.getRamansTestStringsPostSkStringsAutomaton();

        MergingBlock mb = new MergingBlock();

        State s0 = automaton.getInitialState();
        State s1 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 1).findFirst().get();
        State s2 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 2).findFirst().get();
        State s3 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 3).findFirst().get();
        State s4 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 4).findFirst().get();
        State s5 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 5).findFirst().get();
        State s6 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 6).findFirst().get();
        State s7 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 7).findFirst().get();
        State s8 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 8).findFirst().get();
        State s9 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 9).findFirst().get();
        State s10 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 10).findFirst().get();
        State s11 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 11).findFirst().get();
        State s12 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 12).findFirst().get();
        State s13 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 13).findFirst().get();
        State s14 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 14).findFirst().get();
        State s15 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 15).findFirst().get();
        State s16 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 16).findFirst().get();
        State s17 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 17).findFirst().get();
        State s18 = automaton.getDelta().keySet().stream().filter(k -> k.getName() == 18).findFirst().get();

        Set<List<Step<String>>> suffix18 = mb.getSuffixes(automaton, s18);
        Set<List<Step<String>>> suffix17 = mb.getSuffixes(automaton, s17);
        Set<List<Step<String>>> suffix16 = mb.getSuffixes(automaton, s16);
        Set<List<Step<String>>> suffix15 = mb.getSuffixes(automaton, s15);
        Set<List<Step<String>>> suffix14 = mb.getSuffixes(automaton, s14);
        Set<List<Step<String>>> suffix13 = mb.getSuffixes(automaton, s13);
        Set<List<Step<String>>> suffix12 = mb.getSuffixes(automaton, s12);
        Set<List<Step<String>>> suffix11 = mb.getSuffixes(automaton, s11);
        Set<List<Step<String>>> suffix10 = mb.getSuffixes(automaton, s10);
        Set<List<Step<String>>> suffix9 = mb.getSuffixes(automaton, s9);
        Set<List<Step<String>>> suffix8 = mb.getSuffixes(automaton, s8);
        Set<List<Step<String>>> suffix7 = mb.getSuffixes(automaton, s7);
        Set<List<Step<String>>> suffix6 = mb.getSuffixes(automaton, s6);
        Set<List<Step<String>>> suffix5 = mb.getSuffixes(automaton, s5);
        Set<List<Step<String>>> suffix4 = mb.getSuffixes(automaton, s4);
        Set<List<Step<String>>> suffix3 = mb.getSuffixes(automaton, s3);
        Set<List<Step<String>>> suffix2 = mb.getSuffixes(automaton, s2);
        Set<List<Step<String>>> suffix1 = mb.getSuffixes(automaton, s1);
        Set<List<Step<String>>> suffix0 = mb.getSuffixes(automaton, automaton.getInitialState());

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

        Set<Step<String>> t1616All = automaton.getAllSteps(16, 16);
        Step<String> t1616f = automaton.getFirstStep(16, 16);

        Step<String> t1716 = automaton.getFirstStep(17, 16);

        Step<String> t1812 = automaton.getFirstStep(18, 12);
        
        assertEquals(2, suffix18.size());
        assertThat(suffix18, IsCollectionContaining.hasItem(Arrays.asList(t1812,t1216,t1616f)));
        assertThat(suffix18, IsCollectionContaining.hasItem(Arrays.asList(t1812,t1216)));
        
        assertEquals(2, suffix17.size());
        assertThat(suffix17, IsCollectionContaining.hasItem(Arrays.asList(t1716,t1616f)));
        assertThat(suffix17, IsCollectionContaining.hasItem(Arrays.asList(t1716)));
        
        assertEquals(1, suffix16.size());
        assertThat(suffix16, IsCollectionContaining.hasItem(Arrays.asList(t1616f)));
        
        assertEquals(2, suffix15.size());
        assertThat(suffix15, IsCollectionContaining.hasItem(Arrays.asList(t1518,t1812,t1216,t1616f)));
        assertThat(suffix15, IsCollectionContaining.hasItem(Arrays.asList(t1518,t1812,t1216)));
        
        assertEquals(2, suffix14.size());
        assertThat(suffix14, IsCollectionContaining.hasItem(Arrays.asList(t1417,t1716,t1616f)));
        assertThat(suffix14, IsCollectionContaining.hasItem(Arrays.asList(t1417,t1716)));
        
        assertEquals(2, suffix13.size());
        assertThat(suffix13, IsCollectionContaining.hasItem(Arrays.asList(t1316,t1616f)));
        assertThat(suffix13, IsCollectionContaining.hasItem(Arrays.asList(t1316)));
        
        assertEquals(2, suffix12.size());
        assertThat(suffix12, IsCollectionContaining.hasItem(Arrays.asList(t1216,t1616f)));
        assertThat(suffix12, IsCollectionContaining.hasItem(Arrays.asList(t1216)));
        
        assertEquals(4, suffix11.size());
        assertThat(suffix11, IsCollectionContaining.hasItem(Arrays.asList(t1115,t1518,t1812,t1216,t1616f)));
        assertThat(suffix11, IsCollectionContaining.hasItem(Arrays.asList(t1115,t1518,t1812,t1216)));
        assertThat(suffix11, IsCollectionContaining.hasItem(Arrays.asList(t119,t913,t1316,t1616f)));
        assertThat(suffix11, IsCollectionContaining.hasItem(Arrays.asList(t119,t913,t1316)));
        
        assertEquals(2, suffix10.size());
        assertThat(suffix10, IsCollectionContaining.hasItem(Arrays.asList(t1014,t1417,t1716,t1616f)));
        assertThat(suffix10, IsCollectionContaining.hasItem(Arrays.asList(t1014,t1417,t1716)));
        
        assertEquals(2, suffix9.size());
        assertThat(suffix9, IsCollectionContaining.hasItem(Arrays.asList(t913,t1316,t1616f)));
        assertThat(suffix9, IsCollectionContaining.hasItem(Arrays.asList(t913,t1316)));
        
        assertEquals(4, suffix8.size());
        assertThat(suffix8, IsCollectionContaining.hasItem(Arrays.asList(t813,t1316,t1616f)));
        assertThat(suffix8, IsCollectionContaining.hasItem(Arrays.asList(t813,t1316)));
        assertThat(suffix8, IsCollectionContaining.hasItem(Arrays.asList(t812,t1216,t1616f)));
        assertThat(suffix8, IsCollectionContaining.hasItem(Arrays.asList(t812,t1216)));
        
        assertEquals(2, suffix7.size());
        assertThat(suffix7, IsCollectionContaining.hasItem(Arrays.asList(t710,t1014,t1417,t1716,t1616f)));
        assertThat(suffix7, IsCollectionContaining.hasItem(Arrays.asList(t710,t1014,t1417,t1716)));
        
        assertEquals(2, suffix6.size());
        assertThat(suffix6, IsCollectionContaining.hasItem(Arrays.asList(t69,t913,t1316,t1616f)));
        assertThat(suffix6, IsCollectionContaining.hasItem(Arrays.asList(t69,t913,t1316)));
        
        assertEquals(16, suffix5.size());
        assertThat(suffix5, IsCollectionContaining.hasItem(Arrays.asList(t58, t813,t1316,t1616f)));
        assertThat(suffix5, IsCollectionContaining.hasItem(Arrays.asList(t58, t813,t1316)));
        assertThat(suffix5, IsCollectionContaining.hasItem(Arrays.asList(t58, t812,t1216,t1616f)));
        assertThat(suffix5, IsCollectionContaining.hasItem(Arrays.asList(t58, t812,t1216)));
        assertThat(suffix5, IsCollectionContaining.hasItem(Arrays.asList(t55, t58, t813,t1316,t1616f)));
        assertThat(suffix5, IsCollectionContaining.hasItem(Arrays.asList(t55, t58, t813,t1316)));
        assertThat(suffix5, IsCollectionContaining.hasItem(Arrays.asList(t55, t58, t812,t1216,t1616f)));
        assertThat(suffix5, IsCollectionContaining.hasItem(Arrays.asList(t55, t58, t812,t1216)));
        assertThat(suffix5, IsCollectionContaining.hasItem(Arrays.asList(t511,t1115,t1518,t1812,t1216,t1616f)));
        assertThat(suffix5, IsCollectionContaining.hasItem(Arrays.asList(t511,t1115,t1518,t1812,t1216)));
        assertThat(suffix5, IsCollectionContaining.hasItem(Arrays.asList(t511,t119,t913,t1316,t1616f)));
        assertThat(suffix5, IsCollectionContaining.hasItem(Arrays.asList(t511,t119,t913,t1316)));
        assertThat(suffix5, IsCollectionContaining.hasItem(Arrays.asList(t55, t511,t1115,t1518,t1812,t1216,t1616f)));
        assertThat(suffix5, IsCollectionContaining.hasItem(Arrays.asList(t55, t511,t1115,t1518,t1812,t1216)));
        assertThat(suffix5, IsCollectionContaining.hasItem(Arrays.asList(t55, t511,t119,t913,t1316,t1616f)));
        assertThat(suffix5, IsCollectionContaining.hasItem(Arrays.asList(t55, t511,t119,t913,t1316)));
        
        assertEquals(2, suffix4.size());
        assertThat(suffix4, IsCollectionContaining.hasItem(Arrays.asList(t46, t69,t913,t1316,t1616f)));
        assertThat(suffix4, IsCollectionContaining.hasItem(Arrays.asList(t46, t69,t913,t1316)));
        
        assertEquals(2, suffix3.size());
        assertThat(suffix3, IsCollectionContaining.hasItem(Arrays.asList(t37,t710,t1014,t1417,t1716,t1616f)));
        assertThat(suffix3, IsCollectionContaining.hasItem(Arrays.asList(t37,t710,t1014,t1417,t1716)));
        
        assertEquals(2, suffix2.size());
        assertThat(suffix2, IsCollectionContaining.hasItem(Arrays.asList(t26, t69,t913,t1316,t1616f)));
        assertThat(suffix2, IsCollectionContaining.hasItem(Arrays.asList(t26, t69,t913,t1316)));
        
        assertEquals(16, suffix1.size());
        assertThat(suffix1, IsCollectionContaining.hasItem(Arrays.asList(t15,t58, t813,t1316,t1616f)));
        assertThat(suffix1, IsCollectionContaining.hasItem(Arrays.asList(t15,t58, t813,t1316)));
        assertThat(suffix1, IsCollectionContaining.hasItem(Arrays.asList(t15,t58, t812,t1216,t1616f)));
        assertThat(suffix1, IsCollectionContaining.hasItem(Arrays.asList(t15,t58, t812,t1216)));
        assertThat(suffix1, IsCollectionContaining.hasItem(Arrays.asList(t15,t55, t58, t813,t1316,t1616f)));
        assertThat(suffix1, IsCollectionContaining.hasItem(Arrays.asList(t15,t55, t58, t813,t1316)));
        assertThat(suffix1, IsCollectionContaining.hasItem(Arrays.asList(t15,t55, t58, t812,t1216,t1616f)));
        assertThat(suffix1, IsCollectionContaining.hasItem(Arrays.asList(t15,t55, t58, t812,t1216)));
        assertThat(suffix1, IsCollectionContaining.hasItem(Arrays.asList(t15,t511,t1115,t1518,t1812,t1216,t1616f)));
        assertThat(suffix1, IsCollectionContaining.hasItem(Arrays.asList(t15,t511,t1115,t1518,t1812,t1216)));
        assertThat(suffix1, IsCollectionContaining.hasItem(Arrays.asList(t15,t511,t119,t913,t1316,t1616f)));
        assertThat(suffix1, IsCollectionContaining.hasItem(Arrays.asList(t15,t511,t119,t913,t1316)));
        assertThat(suffix1, IsCollectionContaining.hasItem(Arrays.asList(t15,t55, t511,t1115,t1518,t1812,t1216,t1616f)));
        assertThat(suffix1, IsCollectionContaining.hasItem(Arrays.asList(t15,t55, t511,t1115,t1518,t1812,t1216)));
        assertThat(suffix1, IsCollectionContaining.hasItem(Arrays.asList(t15,t55, t511,t119,t913,t1316,t1616f)));
        assertThat(suffix1, IsCollectionContaining.hasItem(Arrays.asList(t15,t55, t511,t119,t913,t1316)));
        
        
        assertEquals(24, suffix0.size());
        assertThat(suffix0, IsCollectionContaining.hasItem(Arrays.asList(t01, t15,t58, t813,t1316,t1616f)));
        assertThat(suffix0, IsCollectionContaining.hasItem(Arrays.asList(t01, t15,t58, t813,t1316)));
        assertThat(suffix0, IsCollectionContaining.hasItem(Arrays.asList(t01, t15,t58, t812,t1216,t1616f)));
        assertThat(suffix0, IsCollectionContaining.hasItem(Arrays.asList(t01, t15,t58, t812,t1216)));
        assertThat(suffix0, IsCollectionContaining.hasItem(Arrays.asList(t01, t15,t55, t58, t813,t1316,t1616f)));
        assertThat(suffix0, IsCollectionContaining.hasItem(Arrays.asList(t01, t15,t55, t58, t813,t1316)));
        assertThat(suffix0, IsCollectionContaining.hasItem(Arrays.asList(t01, t15,t55, t58, t812,t1216,t1616f)));
        assertThat(suffix0, IsCollectionContaining.hasItem(Arrays.asList(t01, t15,t55, t58, t812,t1216)));
        assertThat(suffix0, IsCollectionContaining.hasItem(Arrays.asList(t01, t15,t511,t1115,t1518,t1812,t1216,t1616f)));
        assertThat(suffix0, IsCollectionContaining.hasItem(Arrays.asList(t01, t15,t511,t1115,t1518,t1812,t1216)));
        assertThat(suffix0, IsCollectionContaining.hasItem(Arrays.asList(t01, t15,t511,t119,t913,t1316,t1616f)));
        assertThat(suffix0, IsCollectionContaining.hasItem(Arrays.asList(t01, t15,t511,t119,t913,t1316)));
        assertThat(suffix0, IsCollectionContaining.hasItem(Arrays.asList(t01, t15,t55, t511,t1115,t1518,t1812,t1216,t1616f)));
        assertThat(suffix0, IsCollectionContaining.hasItem(Arrays.asList(t01, t15,t55, t511,t1115,t1518,t1812,t1216)));
        assertThat(suffix0, IsCollectionContaining.hasItem(Arrays.asList(t01, t15,t55, t511,t119,t913,t1316,t1616f)));
        assertThat(suffix0, IsCollectionContaining.hasItem(Arrays.asList(t01, t15,t55, t511,t119,t913,t1316)));
        assertThat(suffix0, IsCollectionContaining.hasItem(Arrays.asList(automaton.getOutStepOnSymbol(s0, "r"), t37,t710,t1014,t1417,t1716,t1616f)));
        assertThat(suffix0, IsCollectionContaining.hasItem(Arrays.asList(automaton.getOutStepOnSymbol(s0, "r"), t37,t710,t1014,t1417,t1716)));
        assertThat(suffix0, IsCollectionContaining.hasItem(Arrays.asList(automaton.getOutStepOnSymbol(s0, "h"), t37,t710,t1014,t1417,t1716,t1616f)));
        assertThat(suffix0, IsCollectionContaining.hasItem(Arrays.asList(automaton.getOutStepOnSymbol(s0, "h"), t37,t710,t1014,t1417,t1716)));
        assertThat(suffix0, IsCollectionContaining.hasItem(Arrays.asList(t04, t46, t69,t913,t1316,t1616f)));
        assertThat(suffix0, IsCollectionContaining.hasItem(Arrays.asList(t04, t46, t69,t913,t1316)));
        assertThat(suffix0, IsCollectionContaining.hasItem(Arrays.asList(t02, t26, t69,t913,t1316,t1616f)));
        assertThat(suffix0, IsCollectionContaining.hasItem(Arrays.asList(t02, t26, t69,t913,t1316)));
    }

    private String prefixesToString(Set<List<Step<String>>> prefixes) {
        StringBuilder sb = new StringBuilder();

        prefixes.stream().forEach(pref -> sb.append(getPrefixAsLine(pref)));

        return sb.toString();
    }
    
    private String prefixesToString(List<List<Step<String>>> prefixes) {
        StringBuilder sb = new StringBuilder();

        prefixes.stream().forEach(pref -> sb.append(getPrefixAsLine(pref)));

        return sb.toString();
    }
    
    private String getPrefixAsLine(List<Step<String>> prefix){
        return prefix.stream()
                    .map(t -> 
                            "(" + t.getSource().getName() + 
                                "," + 
                                  t.getDestination().getName() + 
                             ")").collect(Collectors.joining(", ")) + "\n";
    }
}
