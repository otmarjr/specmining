/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.smartic;

import com.tecacet.math.fsm.Alphabet;
import com.tecacet.math.fsm.BasicWord;
import com.tecacet.math.fsm.DFA;
import com.tecacet.math.fsm.DFABuilder;
import com.tecacet.math.fsm.DeterministicFiniteAutomaton;
import com.tecacet.math.fsm.FABuilderException;
import com.tecacet.math.fsm.FAException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author otmar
 */
public class TrieTree {

    private class Transition {

        public String fromState;
        public String targetState;
        public String transitionLabel;

        public Transition(String from, String to, String label) {
            this.fromState = from;
            this.targetState = to;
            this.transitionLabel = label;
        }
    }

    private class NodeAnnotations {

        public List<String> prefix;
        public Sequence owner;
        public List<String> postfix;
        public Integer count;
        public String event;
        public List<String> children;
    }

    private final String PREDEFINED_ROOT_NAME = "root";

    private List<List<String>> sequences;
    Map<String, Map<String, String>> stateTransitions;
    Set<String> finalStates;
    DeterministicFiniteAutomaton<String, String> automaton;
    List<String> allStates;
    List<Transition> allTransitions;
    Map<String, NodeAnnotations> stateAnnotations;
    Map<List<String>, Integer> sequenceCounts;
    List<Sequence> originalSequences;

    public TrieTree(List<List<String>> sequences,
            Map<List<String>, Integer> sequenceCounts) {
        this.sequences = sequences;
        this.sequenceCounts = sequenceCounts;
        this.originalSequences = sequenceCounts.keySet().stream()
                .map(k -> new Sequence(k, sequenceCounts.get(k)))
                .collect(Collectors.toList());
        this.buildTree();
    }

    private Set<String> getAlphabet() {
        Set<String> alphabet = new HashSet<>();

        this.sequences.stream().forEach((t) -> {
            t.stream().forEach((w) -> {
                alphabet.add(w);
            });
        });

        return alphabet;
    }

    private void buildTree() {

        this.convertTracesToTransitionsAndStates();

        this.buildAutomaton();

    }

    public Boolean containsSequence(List<String> sequence, Boolean verbose) {
        try {
            BasicWord<String> input = new BasicWord<>(sequence);

            if (verbose) {
                System.out.println();
                List<String> path = this.automaton.getPath(input);
                path.stream().forEach(p -> System.out.print("->" + p));
            }

            return this.automaton.accepts(input);

        } catch (FAException ex) {
            Logger.getLogger(TrieTree.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private void convertTracesToTransitionsAndStates() {
        this.stateTransitions = new LinkedHashMap<>();
        this.allStates = new LinkedList<>();

        String previousState = PREDEFINED_ROOT_NAME;

        stateTransitions.put(previousState, new HashMap<>());
        int counter = 1;

        this.finalStates = new HashSet<>();

        for (List<String> sequence : sequences) {
            previousState = PREDEFINED_ROOT_NAME;

            int eventIndex = 0;

            for (String event : sequence) {
                Map<String, String> trans = stateTransitions.get(previousState);

                if (!trans.containsKey(event)) {
                    counter++;
                    String newState = "s" + counter + "_" + event;

                    trans.put(event, newState);
                    previousState = newState;
                    stateTransitions.put(previousState, new HashMap<>());
                } else {
                    previousState = trans.get(event);
                }

                if (eventIndex == sequence.size() - 1) {
                    finalStates.add(previousState);
                }

                eventIndex++;
            }
        }

        this.stateTransitions.keySet().stream().forEach(s -> this.allStates.add(s));
        this.allTransitions = this.stateTransitions.keySet().stream()
                .flatMap(k -> this.stateTransitions.get(k).keySet().stream().map(l -> new Transition(k, this.stateTransitions.get(k).get(l), l)))
                .collect(Collectors.toList());

    }

    Map<String, List<String>> statePrefixes;

    private List<String> getStatePrefix(String state) {
        /* According to Lo: 
         For each node q 2 trie, q.event,and q.prefix denotes their corresponding event
         and sequence of events from trie’s root to q
         */

        if (statePrefixes == null) {
            this.statePrefixes = new LinkedHashMap<>();
        }

        if (statePrefixes.containsKey(state)) {
            return statePrefixes.get(state);
        }

        List<String> prefix = new LinkedList<>();

        String currentState = state;

        while (!currentState.equals(PREDEFINED_ROOT_NAME)) {
            final String currStateWorkAround = currentState;
            Transition incomingTransition = this.allTransitions.stream()
                    .filter(t -> t.targetState.equals(currStateWorkAround))
                    .collect(Collectors.toList()).get(0);

            prefix.add(incomingTransition.transitionLabel);

            currentState = incomingTransition.fromState;
        }

        Collections.reverse(prefix);
        this.statePrefixes.put(state, prefix);
        return prefix;
    }

    private Map<String, Sequence> stateOwners;

    private Sequence getOwner(String state) {
        /* According to Lo: 
         A node’s owner, q.owner, is the closed pattern sharing prefix q.prefix 
         that has the maximum count.
         */

        if (stateOwners == null) {
            stateOwners = new LinkedHashMap<>();
        }

        if (stateOwners.containsKey(state)) {
            return stateOwners.get(state);
        }

        final List<String> prefix = this.getStatePrefix(state);

        Sequence owner = this.originalSequences.stream()
                .filter(os -> os.containsPrefix(prefix))
                .max(Comparator.comparing(s -> s.getCount())).get();

        stateOwners.put(state, owner);
        return owner;
    }

    Map<String, List<String>> statePostfixes;

    private List<String> getStatePostfix(String state) {
        if (this.statePostfixes == null) {
            this.statePostfixes = new LinkedHashMap<>();
        }

        if (this.statePostfixes.containsKey(state)) {
            return this.statePostfixes.get(state);
        }

        Sequence owner = this.getOwner(state);

        List<String> postfix = owner.getPostFix(this.getStatePrefix(state));

        this.statePostfixes.put(state, postfix);

        return postfix;
    }

    private Integer getStateCount(String state) {
        Sequence owner = this.getOwner(state);

        return owner.getCount();
    }

    private List<String> getChildren(String state) {
        List<String> children = new LinkedList<>();

        List<Transition> outgoingTransitions = this.allTransitions.stream()
                .filter(t -> t.fromState.equals(state))
                .collect(Collectors.toList());

        outgoingTransitions.forEach(t -> {
            children.addAll(getChildren(t.targetState));
            children.add(t.targetState);
        });

        return children;
    }

    private void annotateNodes() {

        if (stateAnnotations == null) {
            this.stateAnnotations = new HashMap<>();

            this.allStates.stream().filter(s -> !s.equals(PREDEFINED_ROOT_NAME))
            .forEach(s -> {
                NodeAnnotations na = new NodeAnnotations();

                na.event = s.indexOf('_') > 0 ? s.substring(s.indexOf('_') + 1) : "";
                na.prefix = getStatePrefix(s);
                na.owner = getOwner(s);
                na.postfix = getStatePostfix(s);
                na.count = getStateCount(s);
                na.children = getChildren(s);

                this.stateAnnotations.put(s, na);
            });
        }
    }

    private List<String> getInterestingNodes(double conf) {
        this.annotateNodes();
        
        return this.stateAnnotations.keySet().stream()
                .filter(k -> {
                    NodeAnnotations q;
                    q = this.stateAnnotations.get(k);
                    
                    return q.children.stream().anyMatch(c -> {
                        NodeAnnotations qd = this.stateAnnotations.get(c);
                        
                        return conf <= qd.count/(1.0*q.count)
                                &&
                                qd.count/(1.0*q.count) < 1;
                    });
                            
                    
                }).collect(Collectors.toList());
    }
    
    public List<AssociationRule> generateRules(double conf)
    {
        List<String> interesting = this.getInterestingNodes(conf);
        
        List<AssociationRule> rules;
        
        rules = interesting.stream().map(s -> {
            NodeAnnotations qi = this.stateAnnotations.get(s);
            
            List<String> qdesc = qi.children.stream()
                    .filter(c -> {
                        NodeAnnotations qd = this.stateAnnotations.get(c);
                        
                        return conf <= qd.count/(1.0*qi.count)
                                &&
                                qd.count/(1.0*qi.count) < 1;
                    }).collect(Collectors.toList());
                    
            
            return qdesc.stream().map(qid -> new AssociationRule(qi.prefix, 
                    this.stateAnnotations.get(qid).postfix, this.stateAnnotations.get(qid).count/(1.0*qi.count))).collect(Collectors.toList());
        }).flatMap(rc -> rc.stream()).collect(Collectors.toList());
        
        return AssociationRule.pruneRedundant(rules);
    }

    private void buildAutomaton() {

        Set<String> alphabet = this.getAlphabet();
        Alphabet<String> events = new Alphabet<>(alphabet);

        DFABuilder<String, String> builder = DFA.newDFA(events);

        try {
            builder.setInitialState(PREDEFINED_ROOT_NAME);
        } catch (FABuilderException ex) {
            Logger.getLogger(TrieTree.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.stateTransitions.keySet().stream().forEach((state) -> {
            this.stateTransitions.get(state).keySet().stream().forEach((transitionToken) -> {
                try {
                    builder.addTransition(state, this.stateTransitions.get(state).get(transitionToken), transitionToken);
                } catch (FABuilderException ex) {
                    Logger.getLogger(TrieTree.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        });

        finalStates.stream().forEach((fs) -> {
            try {
                builder.addFinalState(fs);
            } catch (FABuilderException ex) {
                Logger.getLogger(TrieTree.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        try {
            this.automaton = builder.build();
        } catch (FABuilderException ex) {
            Logger.getLogger(TrieTree.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
