/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.smartic;

import cz.cuni.mff.ksi.jinfer.base.automaton.Automaton;
import cz.cuni.mff.ksi.jinfer.base.automaton.State;
import cz.cuni.mff.ksi.jinfer.base.automaton.Step;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author otmarpereira
 */
public class MergingBlock {

    public static final String EPSILON_TRANSITION_SYMBOL = "λ";

    class TransitionString {

        TransitionString(Step<String> step, Automaton<String> automaton) {
            loadTokens();
            loadProbabilities();
            this.initialStep = step;
        }

        List<String> tokens;
        List<Double> probabilities;
        Step<String> initialStep;
        Automaton<String> automaton;

        private void loadTokens() {
            this.tokens = new LinkedList<>();
        }

        private void loadProbabilities() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    Automaton<String> X;
    Automaton<String> Y;

    public MergingBlock() {
    }

    public boolean sourceNodesShareSuffixProbabilities(Step<String> s1, Step<String> s2) {
        return false;
    }

    public boolean transitionsAreEquivalent(Step<String> s1, Step<String> s2) {
        return false;
    }

    public Set<List<Step<String>>> getPrefixes(Automaton<String> aut, State<String> n) {
        Set<List<Step<String>>> prefixes = new HashSet<>();

        boolean containsSelfLoop = false;

        for (Step<String> t : aut.getReverseDelta().get(n)) {
            if (!t.getSource().equals(t.getDestination())) {
                Set<List<Step<String>>> preprefixes;
                preprefixes = getPrefixes(aut, t.getSource());

                if (!preprefixes.isEmpty()) {
                    for (List<Step<String>> pp : preprefixes) {
                        List<Step<String>> prefix;
                        prefix = new LinkedList<>(pp);
                        prefix.add(t);
                        prefixes.add(prefix);
                    }
                } else {

                    prefixes.add(new LinkedList<Step<String>>() {
                        {
                            add(t);
                        }
                    });
                }
            } else {
                containsSelfLoop = true;
            }
        }

        if (containsSelfLoop) { // Create one path using the loop once and another path not using the self loop.
            Step<String> selfLoop = aut.getFirstStep(n, n);

            Set<List<Step<String>>> prefixesWithSelfLoop = new HashSet<>();

            for (List<Step<String>> prefix : prefixes) {
                List<Step<String>> prefixWithSelfLoop = new LinkedList<>(prefix);
                prefixWithSelfLoop.add(selfLoop);
                prefixesWithSelfLoop.add(prefixWithSelfLoop);
            }

            prefixes.addAll(prefixesWithSelfLoop);

        }
        return prefixes;
    }

    public boolean pairIsUnifiable(Automaton<String> x, State<String> nodeX, Automaton<String> y, State<String> nodeY) {
        Set<List<Step<String>>> prefixesX = this.getPrefixes(x, nodeX);
        Set<List<Step<String>>> prefixesY = this.getPrefixes(y, nodeY);

        return prefixesX.containsAll(prefixesY) && prefixesY.containsAll(prefixesX);
    }

    public boolean pairIsMergeable(Automaton<String> x, State<String> nodeX, Automaton<String> y, State<String> nodeY) {
        Set<List<Step<String>>> suffixesX = this.getSuffixes(x, nodeX);
        Set<List<Step<String>>> suffixesY = this.getSuffixes(y, nodeY);

        return suffixesX.containsAll(suffixesY) && suffixesY.containsAll(suffixesX);
    }

    public Set<Pair<State<String>, State<String>>> getUnifiablePairs(Automaton<String> x, Automaton<String> y) {
        Set<Pair<State<String>, State<String>>> pairs = new HashSet<>();

        Set<List<Step<String>>> allPathsForX = getSuffixes(x, x.getInitialState());
        Set<List<Step<String>>> allPathsForY = getSuffixes(y, y.getInitialState());

        for (List<Step<String>> pathX : allPathsForX) {
            for (List<Step<String>> pathY : allPathsForY) {
                for (Step<String> tx : pathX) {
                    for (Step<String> ty : pathY) {
                        if (pairIsUnifiable(x, tx.getSource(), y, ty.getSource())) {
                            pairs.add(Pair.of(tx.getSource(), ty.getSource()));
                        }

                        if (pairIsUnifiable(x, tx.getSource(), y, ty.getDestination())) {
                            pairs.add(Pair.of(tx.getSource(), ty.getDestination()));
                        }

                        if (pairIsUnifiable(x, tx.getDestination(), y, ty.getSource())) {
                            pairs.add(Pair.of(tx.getDestination(), ty.getSource()));
                        }

                        if (pairIsUnifiable(x, tx.getDestination(), y, ty.getDestination())) {
                            pairs.add(Pair.of(tx.getDestination(), ty.getDestination()));
                        }
                    }
                }
            }
        }
        return pairs;
    }

    public Set<Pair<State<String>, State<String>>> getMergeablePairs(Automaton<String> x, Automaton<String> y) {
        Set<Pair<State<String>, State<String>>> pairs = new HashSet<>();

        State<String> acceptingX = x.getDelta().keySet().stream().filter(s -> s.getFinalCount() > 0).findFirst().get();
        State<String> acceptingY = y.getDelta().keySet().stream().filter(s -> s.getFinalCount() > 0).findFirst().get();

        Set<List<Step<String>>> allPathsForX = getPrefixes(x, acceptingX);
        Set<List<Step<String>>> allPathsForY = getPrefixes(y, acceptingY);

        for (List<Step<String>> pathX : allPathsForX) {
            for (List<Step<String>> pathY : allPathsForY) {
                for (Step<String> tx : pathX) {
                    for (Step<String> ty : pathY) {
                        if (pairIsMergeable(x, tx.getSource(), y, ty.getSource())) {
                            pairs.add(Pair.of(tx.getSource(), ty.getSource()));
                        }

                        if (pairIsMergeable(x, tx.getSource(), y, ty.getDestination())) {
                            pairs.add(Pair.of(tx.getSource(), ty.getDestination()));
                        }

                        if (pairIsMergeable(x, tx.getDestination(), y, ty.getSource())) {
                            pairs.add(Pair.of(tx.getDestination(), ty.getSource()));
                        }

                        if (pairIsMergeable(x, tx.getDestination(), y, ty.getDestination())) {
                            pairs.add(Pair.of(tx.getDestination(), ty.getDestination()));
                        }
                    }
                }
            }
        }

        return pairs;
    }

    public Automaton<String> getTopPartiallyMergedAutomaton(Set<Pair<State<String>, State<String>>> unifiableList, Automaton<String> x, Automaton<String> y) {
        Automaton<String> aut = new Automaton<>(0, false);

        Map<Pair<State<String>, State<String>>, State<String>> pairNewStates;
        pairNewStates = new HashMap<>();

        // create states
        for (Pair<State<String>, State<String>> pair : unifiableList) {
            State<String> s = aut.createNewState();
            pairNewStates.put(pair, s);
        }

        Map<State<String>, Set<Pair<State<String>, State<String>>>> xStatesInUnifiableList = new HashMap<>();
        Map<State<String>, Set<Pair<State<String>, State<String>>>> yStatesInUnifiableList = new HashMap<>();
        
        for (Pair<State<String>,State<String>> pair : unifiableList){
            State<String> xState = pair.getLeft();
            State<String> yState = pair.getRight();
            
            if (xStatesInUnifiableList.containsKey(xState)){
                xStatesInUnifiableList.get(xState).add(pair);
            }
            else{
                xStatesInUnifiableList.put(xState, new HashSet<>());
                xStatesInUnifiableList.get(xState).add(pair);
            }
            
            if (yStatesInUnifiableList.containsKey(yState)){
                yStatesInUnifiableList.get(yState).add(pair);
            }
            else{
                yStatesInUnifiableList.put(yState, new HashSet<>());
                yStatesInUnifiableList.get(yState).add(pair);
            }
        }
        
        for (Pair<State<String>, State<String>> pair : pairNewStates.keySet()) {
            // Create transitions to states from X present in unifiable list.
            State<String> xState = pair.getLeft();
            State<String> newState = pairNewStates.get(pair);
            
            for (Step<String> tx : x.getDelta().get(xState)){
                if (xStatesInUnifiableList.containsKey(tx.getDestination())){
                    for (Pair<State<String>, State<String>> p2 : xStatesInUnifiableList.get(xState)){
                        State<String> newStateP2 = pairNewStates.get(p2);
                        
                        aut.createNewStep(tx.getAcceptSymbol(), newState, newStateP2);
                    }
                }
            }
            
            State<String> yState = pair.getRight();
            
            for (Step<String> ty : y.getDelta().get(yState)){
                if (yStatesInUnifiableList.containsKey(ty.getDestination())){
                    for (Pair<State<String>, State<String>> p2 : yStatesInUnifiableList.get(yState)){
                        State<String> newStateP2 = pairNewStates.get(p2);
                        
                        aut.createNewStep(ty.getAcceptSymbol(), newState, newStateP2);
                    }
                }
            }
        }

        return aut;
    }
    
    public Automaton<String> getBottomPartiallyMergedAutomaton(Set<Pair<State<String>, State<String>>> mergeable, Automaton<String> x, Automaton<String> y) {
        Automaton<String> aut = new Automaton<>(0, false);

        Map<Pair<State<String>, State<String>>, State<String>> pairNewStates;
        pairNewStates = new HashMap<>();

        // create states
        for (Pair<State<String>, State<String>> pair : mergeable) {
            State<String> s = aut.createNewState();
            pairNewStates.put(pair, s);
        }

        Map<State<String>, Set<Pair<State<String>, State<String>>>> xStatesInMergeableList = new HashMap<>();
        Map<State<String>, Set<Pair<State<String>, State<String>>>> yStatesInMergeableList = new HashMap<>();
        
        for (Pair<State<String>,State<String>> pair : mergeable){
            State<String> xState = pair.getLeft();
            State<String> yState = pair.getRight();
            
            if (xStatesInMergeableList.containsKey(xState)){
                xStatesInMergeableList.get(xState).add(pair);
            }
            else{
                xStatesInMergeableList.put(xState, new HashSet<>());
                xStatesInMergeableList.get(xState).add(pair);
            }
            
            if (yStatesInMergeableList.containsKey(yState)){
                yStatesInMergeableList.get(yState).add(pair);
            }
            else{
                yStatesInMergeableList.put(yState, new HashSet<>());
                yStatesInMergeableList.get(yState).add(pair);
            }
        }
        
        for (Pair<State<String>, State<String>> pair : pairNewStates.keySet()) {
            // Create transitions to states from X present in unifiable list.
            State<String> xState = pair.getLeft();
            State<String> newState = pairNewStates.get(pair);
            
            for (Step<String> tx : x.getDelta().get(xState)){
                if (xStatesInMergeableList.containsKey(tx.getDestination())){
                    for (Pair<State<String>, State<String>> p2 : xStatesInMergeableList.get(xState)){
                        State<String> newStateP2 = pairNewStates.get(p2);
                        
                        aut.createNewStep(tx.getAcceptSymbol(), newState, newStateP2);
                    }
                }
            }
            
            State<String> yState = pair.getRight();
            
            for (Step<String> ty : y.getDelta().get(yState)){
                if (yStatesInMergeableList.containsKey(ty.getDestination())){
                    for (Pair<State<String>, State<String>> p2 : yStatesInMergeableList.get(yState)){
                        State<String> newStateP2 = pairNewStates.get(p2);
                        
                        aut.createNewStep(ty.getAcceptSymbol(), newState, newStateP2);
                    }
                }
            }
        }

        return aut;
    }

    public Automaton<String> mergeAutomatons(Automaton<String> x, Automaton<String> y, Automaton<String> top,
            Automaton<String> bottom){
        Automaton<String> aut = new Automaton<String>();
        
        Set<State<String>> initialStates = new HashSet<>();
        
        initialStates.add(x.getInitialState());
        initialStates.add(y.getInitialState());
        initialStates.add(top.getInitialState());
        initialStates.add(bottom.getInitialState());
        
        return aut;
    }
    public Set<List<Step<String>>> getSuffixes(Automaton<String> aut, State<String> n) {
        Set<List<Step<String>>> suffixes = new HashSet<>();
        boolean addedSelfLoop = false;
        boolean anyOutwardTransitionAfterSelfLoop = false;

        for (Step<String> t : aut.getDelta().get(n)) {
            if (!t.getSource().equals(t.getDestination())) {
                anyOutwardTransitionAfterSelfLoop = true;
                Set<List<Step<String>>> susufixes;
                susufixes = getSuffixes(aut, t.getDestination());

                Set<List<Step<String>>> susuffixesWithSelfLoop = susufixes.stream().filter(suf -> {
                    Step<String> lastStep = suf.get(suf.size() - 1);
                    return lastStep.getSource().equals(lastStep.getDestination());
                }).collect(Collectors.toSet());

                for (List<Step<String>> susufWithSelfLoop : susuffixesWithSelfLoop) {
                    List<Step<String>> susufWithoutSelfLoop = susufWithSelfLoop.stream().limit(susufWithSelfLoop.size() - 1).collect(Collectors.toList());

                    susufixes.add(susufWithoutSelfLoop);
                }

                if (!susufixes.isEmpty()) {
                    for (List<Step<String>> pp : susufixes) {
                        List<Step<String>> sufix;
                        sufix = new LinkedList<>(pp);
                        sufix.add(0, t);
                        suffixes.add(sufix);
                    }
                } else {

                    suffixes.add(new LinkedList<Step<String>>() {
                        {
                            add(t);
                        }
                    });
                }
            } else {
                if (!addedSelfLoop) {
                    if (t.getDestination().isFinal()) {
                        suffixes.add(new LinkedList<Step<String>>() {
                            {
                                add(t);
                            }
                        });
                    }
                    addedSelfLoop = true;
                }
            }
        }

        if (anyOutwardTransitionAfterSelfLoop && addedSelfLoop) {
            Step<String> selfLoop = aut.getFirstStep(n, n);

            Set<List<Step<String>>> suffixesWithSelfLoop = new HashSet<>();

            for (List<Step<String>> suffix : suffixes) {
                List<Step<String>> suffixWithSelfLoop = new LinkedList<>(suffix);
                suffixWithSelfLoop.add(0, selfLoop);
                suffixesWithSelfLoop.add(suffixWithSelfLoop);
            }

            suffixes.addAll(suffixesWithSelfLoop);
        }

        return suffixes;
    }

    public void handleExceptionalCases(Automaton<String> automaton) {
        State startNode = automaton.getInitialState();

        Map<State<String>, Set<Step<String>>> d = automaton.getDelta();

        boolean startNodeIsSinkOfATransition = d.keySet().stream()
                .anyMatch(s -> d.get(s).stream()
                        .anyMatch(t -> t.getDestination().equals(startNode)));

        if (startNodeIsSinkOfATransition) {
            automaton.createNewDummyInitialState(EPSILON_TRANSITION_SYMBOL);
        }
    }

    public List<List<String>> getAllStringsAcceptedByAutomaton(List<Trace> traces, Automaton<String> automaton) {
        List<List<String>> accepted = new LinkedList<>();
        Set<List<String>> rejected = new HashSet<>();

        for (Trace t : traces) {
            List<String> events = t.getEvents();
            if (events.isEmpty()) {
                continue;
            }
            State<String> currentState = automaton.getInitialState();
            boolean recognized = true;

            for (String e : events) {
                Step<String> next = automaton.getOutStepOnSymbol(currentState, e);

                if (next != null) {
                    currentState = next.getDestination();
                } else {
                    recognized = false;
                    break;
                }
            }

            if (recognized && currentState.getFinalCount() > 0) {
                accepted.add(events);
            } else {
                rejected.add(events);
            }
        }

        return accepted;
    }

    public Automaton<String> getMergedAutomaton(Automaton<String> x, Automaton<String> y, List<Trace> trainSetX, List<Trace> trainSetY) {
        Automaton<String> merged = null;

        double weightX = 1.0 * trainSetX.size() / (trainSetX.size() + trainSetY.size());
        double weightY = 1.0 * trainSetY.size() / (trainSetX.size() + trainSetY.size());

        handleExceptionalCases(x);
        handleExceptionalCases(y);

        return merged;
    }
    
    public List<State<String>> getNodesByBreadthFirstSearch(Automaton<String> automaton){
        LinkedList<State<String>> V  = new LinkedList<>();
        Queue<State<String>> Q = new LinkedBlockingDeque<>();
        
        V.add(automaton.getInitialState());
        Q.add(automaton.getInitialState());
        
        while (!Q.isEmpty()){
            State<String> t = Q.poll();
            
            for (Step<String> delta : automaton.getDelta().get(t)){
                State<String> u = delta.getDestination();
                
                if (!V.contains(u)){
                    V.add(u);
                    Q.add(u);
                }
            }
        }
        
        return V;
    }
    
    public Set<Pair<State<String>, State<String>>> createUnifiableList(Automaton<String> x, Automaton<String> y){
        Set<Pair<State<String>, State<String>>> uni = new HashSet<>();
        
        List<State<String>> nodesX  = new LinkedList<>();
        int currentXDistance = 0;
        
        return uni;
    }
}
