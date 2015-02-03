/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.evaluation;

import com.mifmif.common.regex.Generex;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javamop.parser.astex.MOPSpecFileExt;
import javamop.parser.astex.mopspec.FormulaExt;
import javamop.parser.astex.mopspec.JavaMOPSpecExt;
import javamop.parser.astex.mopspec.PropertyAndHandlersExt;
import javamop.parser.main_parser.JavaMOPParser;
import javamop.parser.main_parser.ParseException;

/**
 *
 * @author Otmar
 */
public class MopExtractor {
    File mopSpecFile;
    private String extendedRegularExpression;
    private static final String ERE_PATTERN = "^ere[\\s\\t]+\\:[\\s\\t]+(.+)$";
    MOPSpecFileExt parsedMopSpecFile;
    MOPSpecFile translatedMopFile;
    
    public MopExtractor(String mopFilePath){
        this(new File(mopFilePath));
    }
    }
}
