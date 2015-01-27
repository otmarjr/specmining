/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.referenceparser.graphvizdot;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.w3c.dom.Document;
import specminers.StringHelper;

/**
 *
 * @author Otmar
 */
public class PradelsDotFilesToJffConverter {
    File dotFile;
    Set<String> stateNames;
    Set<Triple<String,String,String>> transitions;
    Set<String> initialStates;
    Set<String> finalStates;
    static final String FINAL_STATE_SHAPE_NAME = "doublecircle";
    
    
    public PradelsDotFilesToJffConverter(String dotFilePath){
        dotFile = new File(dotFilePath);
    }
    
    public PradelsDotFilesToJffConverter(File file){
        dotFile = file;
    }
    
    public void convert() throws IOException{
        this.stateNames = new HashSet<>();
        this.transitions = new HashSet<>();
        this.initialStates = new HashSet<>();
        this.finalStates = new HashSet<>();
        
        parseStates();
        parseTranstions();
    }
    
    private Document getJffXMLDocument() throws ParserConfigurationException, IOException{
        this.convert();
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        
        Document doc = docBuilder.newDocument();
        
        Element structure = doc.createElement("structure");
        doc.appendChild(structure);
        
        Element type = doc.createElement("type");
        type.setTextContent("fa");
        structure.appendChild(type);
        
        Element automaton = doc.createElement("automaton");
        
        for (String stateName : this.stateNames){
            Element state = doc.createElement("state");
            state.setAttribute("id", stateName);
            state.setAttribute("name", "q" + stateName);
            
            if (initialStates.contains(stateName)){
                Element initial = doc.createElement("initial");
                state.appendChild(initial);
            }
            
            if (finalStates.contains(stateName)){
                Element accepting = doc.createElement("final");
                state.appendChild(accepting);
            }
            
            automaton.appendChild(state);
        }
        
        
        
        
        for (Triple<String,String,String> transition : this.transitions){
            Element t = doc.createElement("transition");
            
            Element from = doc.createElement("from");
            from.setTextContent(transition.getLeft());
            t.appendChild(from);
            
            Element to = doc.createElement("to");
            to.setTextContent(transition.getMiddle());
            t.appendChild(to);
            
            Element read = doc.createElement("read");
            read.setTextContent(transition.getRight());
            t.appendChild(read);
            
            automaton.appendChild(t);
        }
        
        structure.appendChild(automaton);
        return doc;
    }
    public String getAsJffFormat() throws ParserConfigurationException, IOException, TransformerException{
        Document jff = getJffXMLDocument();
        
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(jff);
        transformer.transform(source, result);

        String xmlString = result.getWriter().toString();

        return xmlString;
    }
    
    public void saveToFile(String outputPath) throws TransformerException, ParserConfigurationException, IOException{
        Document jff = getJffXMLDocument();
        
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(jff);
        StreamResult result = new StreamResult(new File(outputPath));
        transformer.transform(source, result);
    }
  
    private void parseStates() throws IOException {
        
        // Since Pradel's dot files seem to contain one element definition per line,
        // each line is parsed.
        String stateDeclarationPattern = "(\\d{1,3})(\\s|\\t)+\\[shape=(\\w+)\\,.+$";
        List<String> stateDeclarationLines  = FileUtils.readLines(dotFile).stream()
                .map(l -> l.trim())
                .filter(l -> 
                        l.matches(stateDeclarationPattern))
                .collect(Collectors.toList());
        
        
        for (String line : stateDeclarationLines){
            String stateName = StringHelper.extractSingleValueWithRegex(line, stateDeclarationPattern, 1);
            String shape =  StringHelper.extractSingleValueWithRegex(line, stateDeclarationPattern, 3);
         
            if (StringUtils.equalsIgnoreCase(shape, FINAL_STATE_SHAPE_NAME)){
                finalStates.add(stateName);
            }
            
            stateNames.add(stateName);
        }
        
        String initialStatePattern = "initial(\\s|\\t)+->(\\s|\\t)+(\\d{1,3})";
        
        List<String> initialStateDeclarationLines = FileUtils.readLines(dotFile).stream()
                .map(l -> l.trim())
                .filter(l -> l.matches(initialStatePattern))
                .collect(Collectors.toList());
        
        for (String line : initialStateDeclarationLines){
            String initialStateName = StringHelper.extractSingleValueWithRegex(line, initialStatePattern, 3);
            this.initialStates.add(initialStateName);
        }
    }

    private void parseTranstions() throws IOException {
        String transitionsPattern = "(\\d{1,3})(\\s|\\t)+->(\\s|\\t)+(\\d{1,3})(\\s|\\t)+\\[label=\"#(\\d{1,5})\\.([\\w|\\.|<|>|\\(|\\)|\\,]+)\\s+.+";
        List<String> transitionsDeclarationLines  = FileUtils.readLines(dotFile).stream()
                .map(l -> l.trim())
                .filter(l -> 
                        l.matches(transitionsPattern))
                .collect(Collectors.toList());
        
        
        for (String line : transitionsDeclarationLines){
            String sourceName = StringHelper.extractSingleValueWithRegex(line, transitionsPattern, 1);
            String destinationName = StringHelper.extractSingleValueWithRegex(line, transitionsPattern, 4);
            String signature = StringHelper.extractSingleValueWithRegex(line, transitionsPattern, 7);
         
            transitions.add(Triple.of(sourceName, destinationName, signature));
        }
    }
            
}
