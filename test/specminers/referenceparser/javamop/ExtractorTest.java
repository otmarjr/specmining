/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.referenceparser.javamop;

import com.runtimeverification.rvmonitor.logicrepository.LogicException;
import com.runtimeverification.rvmonitor.logicrepository.LogicRepositoryData;
import com.runtimeverification.rvmonitor.logicrepository.parser.logicrepositorysyntax.LogicRepositoryType;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ere.ERE;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ere.FSM;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ere.Symbol;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ere.parser.EREParser;
import com.runtimeverification.rvmonitor.logicrepository.plugins.ere.parser.Token;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Otmar
 */
public class ExtractorTest {

    public ExtractorTest() {
    }

    /**
     * Test of getSpecification method, of class Extractor.
     */
    @Test
    public void testGetSpecification() throws Exception {
        System.out.println("getSpecification");
        Extractor instance = new Extractor("C:\\Users\\Otmar\\Google Drive\\Mestrado\\SpecMining\\annotated-java-api\\properties\\java\\util");
        List<String> result = instance.getSpecification();
        assertNotNull(result);
    }

    @Test
    public void testEREParser() throws LogicException, FileNotFoundException {

        String logicStr;
        logicStr = "create add* ((next | previous)+ set* (remove | add+ | epsilon))*";
        String eventsStr = "add create next previous set remove epsilon";
        String replaceAll = eventsStr.replaceAll("\\s+", " ");

        EREParser ereParser = EREParser.parse(logicStr);
        ERE ere = ereParser.getERE();

        String[] eventStrings = eventsStr.split(" ");
        Symbol[] events = new Symbol[eventStrings.length];
        for (int i = 0; i < eventStrings.length; i++) {
            events[i] = Symbol.get(eventStrings[i]);
        }

        FSM fsm = FSM.get(ere,events);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        fsm.print(ps);

        String output = os.toString();

        String logic = "fsm";
        String formula = output;

        assertNotNull("");

    }

}
