/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.referenceparser.graphvizdot;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import static org.junit.Assert.*;
import specminers.TestsHelper;

/**
 *
 * @author Otmar
 */
public class PradelsDotFilesToJffConverterTest {
    
    public PradelsDotFilesToJffConverterTest() {
    }

    private PradelsDotFilesToJffConverter getDatagramSocketDotFileInstance(){
        return new PradelsDotFilesToJffConverter(new File(TestsHelper.getTestFilesFolder(),"/pradels dot files/java.net.DatagramSocket.dot"));
    }
    /**
     * Test of convert method, of class PradelsDotFilesToJffConverter.
     */
    @Test
    public void testConvert() throws IOException {
        PradelsDotFilesToJffConverter instance;
        instance = getDatagramSocketDotFileInstance();
        instance.convert();
        assertTrue(true);
    }

    /**
     * Test of getAsJffFormat method, of class PradelsDotFilesToJffConverter.
     */
    @Test
    public void testGetAsJffFormat() throws ParserConfigurationException, IOException, TransformerException {
        System.out.println("getAsJffFormat");
        PradelsDotFilesToJffConverter instance = getDatagramSocketDotFileInstance();
        String result = instance.getAsJffFormat();
        assertNotNull(result);
        assertTrue(StringUtils.isNotBlank(result));
    }

    /**
     * Test of saveToFile method, of class PradelsDotFilesToJffConverter.
     */
    @Test
    public void testSaveToFile() throws TransformerException, ParserConfigurationException, IOException {
        System.out.println("saveToFile");
        PradelsDotFilesToJffConverter instance = getDatagramSocketDotFileInstance();
        File jffFile = new File(TestsHelper.getTestFilesFolder(),"/intermediate jff/datagramsocket.jff");
        if (jffFile.exists()) {
            jffFile.delete();
        }
        
        instance.saveToFile(jffFile.getAbsolutePath());
        
        assertTrue(jffFile.exists());
    }
    
}
