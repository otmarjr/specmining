/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package specminers.referenceparser.javamop;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javamop.parser.astex.MOPSpecFileExt;
import javamop.parser.astex.mopspec.FormulaExt;
import javamop.parser.main_parser.JavaMOPParser;
import javamop.parser.main_parser.ParseException;
import javax.sql.CommonDataSource;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Otmar
 */
public class Extractor {

    static final String MOP_SPEC_FILES_EXTENSION = "mop";
    static final String ERE_PATTERN_REGEX = "^\t?ere\\s*\\:(.+)$";
    static Pattern ERE_PATTERN = Pattern.compile(ERE_PATTERN_REGEX);

    File specsFolder;
    List<String> specificationStatements;
    List<File> extendedRegularExpressionFiles;

    Extractor(String specifcationsFolderPath) {
        specsFolder = new File(specifcationsFolderPath);
    }

    private void loadExtendedRegularExpressionFiles() {
        String[] extensions = new String[]{MOP_SPEC_FILES_EXTENSION};

        List<File> candidates = FileUtils
                .listFiles(this.specsFolder, extensions, true)
                .stream().collect(Collectors.toList());

        this.extendedRegularExpressionFiles = candidates
                .stream()
                .filter(f -> {
                    try {

                        return FileUtils.readLines(f).stream().anyMatch(l
                                -> ERE_PATTERN.matcher(l).matches());
                    } catch (IOException ex) {
                        Logger.getLogger(Extractor.class.getName()).log(Level.SEVERE, null, ex);
                        return false;
                    }
                })
                .collect(Collectors.toList());

    }

    public List<String> getSpecification() throws IOException, ParseException {
        if (specificationStatements == null) {
            loadSpecification();
        }
        return specificationStatements;
    }

    private String expandExtendedRegularExpression(String ere, File specificationFile) throws IOException, ParseException {
        String contents = FileUtils.readFileToString(specificationFile);

        MOPSpecFileExt spec = getJavaMOPSpec(specificationFile);

        String strEvents = spec.getSpecs().get(0).getEventStr();
        
        if (spec.getSpecs().get(0).getPropertiesAndHandlers().get(0).getProperty() instanceof FormulaExt){
            FormulaExt fext = (FormulaExt)spec.getSpecs().get(0).getPropertiesAndHandlers().get(0).getProperty();
            String strFormula = fext.toString();
            System.out.println(strFormula);
        }
        
        if (1 > 0) {
            return "";
        }
        ere = ere.trim();
        String componentsPattern = "\\(\\w+\\)|\\w+";
        Matcher m = Pattern.compile(componentsPattern).matcher(ere);

        String aspectJExpression = "((execution)|(call))";
        String aspectjAdvicePattern = aspectJExpression;

        while (m.lookingAt()) {

            String component = ere.substring(m.start(), m.end());
            String componentDefintionRegex = "\\t*(creation)?[\\s\\t]+(event)[\\s|\\t]+" + component + aspectjAdvicePattern + "\\{(.+)\\}"; // According to JavaMOP 4 syntax
            Pattern p = Pattern.compile(componentDefintionRegex, Pattern.DOTALL);
            Matcher definitionMatcher = p.matcher(contents);

            if (definitionMatcher.find()) {
                String definition = definitionMatcher.group(5) + definitionMatcher.group(6);
                return definition;
            }
        }

        return ere;
    }

    private List<String> extractSpecStatementsFromFile(File specificationFile) throws IOException, ParseException {
        List<String> statements = new LinkedList<>();
        List<String> matchingLines = FileUtils.readLines(specificationFile).stream().filter(l -> ERE_PATTERN.matcher(l).matches()).collect(Collectors.toList());

        for (String matchingLine : matchingLines) {
            Matcher m = ERE_PATTERN.matcher(matchingLine);
            if (m.matches()) {
                statements.add(expandExtendedRegularExpression(m.group(1), specificationFile));
            } else {
                System.out.println(matchingLine + " does not really match the ERE pattern");
            }
        }

        return statements;
    }

    private MOPSpecFileExt getJavaMOPSpec(String filePath) throws ParseException {
        return getJavaMOPSpec(new File(filePath));
    }

    private MOPSpecFileExt getJavaMOPSpec(File mopFile) throws ParseException {
        return JavaMOPParser.parse(mopFile);
    }

    private void loadSpecification() throws IOException, ParseException {
        this.loadExtendedRegularExpressionFiles();
        this.specificationStatements = new LinkedList<>();
        for (File specFile : this.extendedRegularExpressionFiles) {
            this.specificationStatements.addAll(extractSpecStatementsFromFile(specFile));
        }
    }
}
