/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.referenceparser.javamop;

import java.util.List;
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
        Extractor instance = new Extractor("C:\\Users\\Otmar\\Google Drive\\Mestrado\\SpecMining\\annotated-java-api\\properties\\java\\net");
        List<String> result = instance.getSpecification();
        assertNotNull(result);
    }
    
}
