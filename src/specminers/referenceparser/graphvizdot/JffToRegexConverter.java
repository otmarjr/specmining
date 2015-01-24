/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.referenceparser.graphvizdot;

import automata.Automaton;
import automata.fsa.FSAToREPreparationWorkflow;
import automata.fsa.FSAToRegularExpressionConverter;
import automata.fsa.FiniteStateAutomaton;
import file.Codec;
import file.XMLCodec;
import gui.action.OpenAction;
import gui.regular.FSAToREController;
import gui.viewer.SelectionDrawer;
import java.io.File;
import java.io.Serializable;

/**
 *
 * @author Otmar
 */
public class JffToRegexConverter {

    File jffFile;
    FiniteStateAutomaton automaton;

    public JffToRegexConverter(String jffPath) {
        this.jffFile = new File(jffPath);
    }
    
    public JffToRegexConverter(File jff) {
        this.jffFile = jff;
    }

    public String getRegularExpression() {
        prepareAutomaton();

        FiniteStateAutomaton clone = (FiniteStateAutomaton)this.automaton.clone();
        
        FSAToREPreparationWorkflow workflow = new FSAToREPreparationWorkflow(clone);
        workflow.perform();
        this.automaton = workflow.getPreaparedFSA();
        
        String regex = FSAToRegularExpressionConverter.convertToRegularExpression(this.automaton);

        return regex;
    }

    private void parseAutomaton() {
        XMLCodec jffCodec = new XMLCodec();
        this.automaton = (FiniteStateAutomaton) jffCodec.decode(this.jffFile, null);
    }

    private void validateParsedAutomatonBeforeRegexConversion() {
        if (automaton == null) {
            throw new RuntimeException("No automaton could be read from the file " + this.jffFile.getAbsolutePath() + ". Check if the file is a valid JFlap 4 file by trying to open it on JFlap software.");
        }
        if (this.automaton.getInitialState() == null) {
            throw new RuntimeException("The automaton does not contain an initial state defined. This must be set on the automaton file before converting it to a regular expression.");
        }
        
        if (this.automaton.getFinalStates() == null || this.automaton.getFinalStates().length == 0) {
            throw new RuntimeException("The automaton does not contain any final states. This must be set on the automaton file before converting it to a regular expression.");
        }
    }

    private void prepareAutomaton() {
        parseAutomaton();
        this.validateParsedAutomatonBeforeRegexConversion();
    }
}
