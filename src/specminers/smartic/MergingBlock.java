/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.smartic;

import cz.cuni.mff.ksi.jinfer.base.automaton.Automaton;
import cz.cuni.mff.ksi.jinfer.base.automaton.State;
import cz.cuni.mff.ksi.jinfer.base.automaton.Step;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    public Set<Pair<State<String>, State<String>>> getUnifiablePairs(Automaton<String> x, Automaton<String> y) {
        Set<Pair<State<String>, State<String>>> pairs = new HashSet<>();

        for (State<String> nodeX : x.getDelta().keySet()) {
            Set<List<Step<String>>> prefixesX = this.getPrefixes(x, nodeX);
            for (State<String> nodeY : y.getDelta().keySet()) {
                Set<List<Step<String>>> prefixesY = this.getPrefixes(y, nodeY);
                
                if (prefixesX.containsAll(prefixesY) && prefixesY.containsAll(prefixesX)){
                    pairs.add(Pair.of(nodeX, nodeY));
                }
            }
        }

        return pairs;
    }
    
    public Set<Pair<State<String>, State<String>>> getMergeablePairs(Automaton<String> x, Automaton<String> y) {
        Set<Pair<State<String>, State<String>>> pairs = new HashSet<>();

        for (State<String> nodeX : x.getDelta().keySet()) {
            Set<List<Step<String>>> suffixesX = this.getSuffixes(x, nodeX);
            for (State<String> nodeY : y.getDelta().keySet()) {
                Set<List<Step<String>>> suffixesY = this.getSuffixes(y, nodeY);
                
                if (suffixesX.containsAll(suffixesY) && suffixesY.containsAll(suffixesX)){
                    pairs.add(Pair.of(nodeX, nodeY));
                }
            }
        }

        return pairs;
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
}
