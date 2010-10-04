package com.tms.threed.featureModel.server;

import com.tms.threed.featureModel.shared.Cardinality;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.featureModel.shared.VarCodeFixer;
import com.tms.threed.featureModel.shared.boolExpr.Conflict;
import com.tms.threed.featureModel.shared.boolExpr.Iff;
import com.tms.threed.featureModel.shared.boolExpr.Implication;
import com.tms.threed.featureModel.shared.boolExpr.Xor;
import com.tms.threed.threedCore.shared.SeriesId;
import com.tms.threed.util.lang.shared.Path;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tms.threed.util.lang.shared.Strings.isEmpty;
import static com.tms.threed.util.lang.shared.Strings.notEmpty;

/**
 * Not thread safe. Use and throw away
 */
public class FeatureModelBuilderXml {

    public static final String DEFAULT_VALUE = "defaultValue";  //  true/[false]
    public static final String MANDITORY = "manditory";         //  true/[false]
    public static final String PICK = "pick";                   //  1,0-1,all
    public static final String CODE = "code";
    public static final String NAME = "name";
    public static final String INCLUDES = "includes";
    public static final String CONFLICTS = "conflicts";
    public static final String IFFS = "iffs";
    public static final String DERIVED = "derived";
    private static final String VERSION = "version";
    private static final String MODEL_TAG_NAME = "model";
    private static final String FEATURE_TAG_NAME = "feature";
    private static final String FEATURES_TAG_NAME = FEATURE_TAG_NAME + "s";
    private static final String DISPLAY_NAME_TAG_NAME =  "name";


    private FeatureModel fm;
//    private SFeatureModel sfm;
    private Var root;

//    private File modelPngRoot;
//    private File xmlFile;

    private Path modelPngRoot;


    private Document doc;
    private Element modelElement;
    private Element featuresElement;


    private Map<String, String> includeMap = new HashMap<String, String>();
    private Map<String, String> iffMap = new HashMap<String, String>();
    private Map<String, String> conflictMap = new HashMap<String, String>();

    private final SeriesId seriesId;
    private final File modelXmlFile;

    public FeatureModelBuilderXml(@Nonnull SeriesId seriesId, @Nonnull File modelXmlFile) {
        assert modelXmlFile != null;
        assert seriesId != null;
        this.modelXmlFile = modelXmlFile;
        this.seriesId = seriesId;
    }


    public FeatureModel buildModel() {
        log.debug("creating model from PNG folder structure and model.xml file");

        checkModelXmlFile();





        parseDom();


        processModelElement();

        return fm;
    }

    public void checkModelXmlFile() {
        log.debug("Checking model.xml file: [" + modelXmlFile + "]");
        if (!modelXmlFile.canRead() || !modelXmlFile.isFile()) {
            throw new IllegalStateException("Unable to read model.xml: [" + modelXmlFile + "]");
        }
    }

    private void parseDom() {
        SAXReader r = new SAXReader();
        try {
            doc = r.read(modelXmlFile);

        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        log.debug("Loaded and parsed XML file");
    }

    private void processModelElement() {
        modelElement = doc.getRootElement();
        fm = new FeatureModel(seriesId,modelElement.attributeValue(DISPLAY_NAME_TAG_NAME));
        root = fm.getRootVar();

        featuresElement = modelElement.element(FEATURES_TAG_NAME);

        Var rootVar = fm.getRootVar();
        processFeatureChildren(rootVar, featuresElement);
        processIncludes();
        processIffs();
        processConflicts();

        processXors();

    }

    private String parseVersion() {
        String version = modelElement.attributeValue(VERSION);
        if (notEmpty(version)) return version.trim();
        return "";
    }

    private void processXors() {
        final List<Var> vars = fm.getVars();
        for (Var var : vars) {
            final Cardinality c = var.getCardinality();
            if (c != null && c.equals(Cardinality.PickOneGroup)) {
                final List<Var> childNodes = var.getChildVars();
                assert childNodes != null;
                assert childNodes.size() > 0 : var.getCode() + " has no children";

                if (childNodes.size() == 1) {
                    Var onlyChild = childNodes.get(0);
                    fm.addConstraint(onlyChild);
                } else if (childNodes.size() > 1) {
                    final Xor xor = fm.xor();
                    for (Var child : childNodes) {
                        xor.add(child);
                    }
                    fm.addConstraint(xor);
                } else {
                    throw new IllegalStateException();
                }


            }
        }
    }

    private void processFeature(Var parentVar, Element childElement) {
        final boolean isSol = isSol(childElement);
//        System.out.println("processFeature: ");
//        System.out.println("\t parentVar: " + parentVar);
//        System.out.println("\t childElement: " + childElement.getName() + "." + childElement.attributeValue("code") + " sol: " + isSol);

        if (isSol) {
            processSolitaryFeature(parentVar, childElement);
        } else {
            processFeatureGroup(parentVar, childElement);
        }
    }

    private boolean isSol(Element childElement) {return childElement.getName().equals(FEATURE_TAG_NAME);}

    private void processIncludes() {
        for (String varCode : includeMap.keySet()) {
            String includes = includeMap.get(varCode);
            processInclude(varCode, includes);
        }
    }

    private void processIffs() {
        for (String varCode : iffMap.keySet()) {
            String iffs = iffMap.get(varCode);
            processIff(varCode, iffs);
        }
    }

    private void processConflicts() {
        for (String varCode : conflictMap.keySet()) {
            String conflicts = conflictMap.get(varCode);
            processConflict(varCode, conflicts);
        }
    }

    private void processInclude(String varCode, String includes) {
        assert notEmpty(includes);
        assert varCode != null;
        final Implication implication = fm.createImplication(varCode, includes);
        fm.addConstraint(implication);
    }

    private void processIff(String varCode, String iffs) {
        assert notEmpty(iffs);
        assert varCode != null;
        final Iff iff = fm.createIff(varCode, iffs);
        fm.addConstraint(iff);
    }

    private void processConflict(String varCode, String conflicts) {
        assert notEmpty(conflicts);
        assert varCode != null;
        final Conflict conflict = fm.createConflict(varCode, conflicts);
        fm.addConstraint(conflict);
    }

    private void processFeatureGroup(Var parentVar, Element childElement) {
        String varCode = childElement.getName();
        varCode = VarCodeFixer.fixupVarCode(varCode, parentVar.getCode());
        final Var childVar = parentVar.addVar(varCode);
        common(childVar, childElement);
        processFeatureChildren(childVar, childElement);
    }

    private void processFeatureChildren(Var parentVar, Element parentElement) {
        List<Element> childElements = parentElement.elements();
        for (Element childElement : childElements) {
            processFeature(parentVar, childElement);
        }
    }

    private Boolean isDerived(Element e) {
        String derived = e.attributeValue(DERIVED);
        if (isEmpty(derived)) return null;
        return derived.trim().equalsIgnoreCase("true");
    }

    private Cardinality getCardinality(Element e) {
        String pick = e.attributeValue(PICK);
        if (isEmpty(pick)) return null;
        pick = pick.trim();
        if (pick.equals("1")) return Cardinality.PickOneGroup;
        else if (pick.equals("0-1")) return Cardinality.ZeroOrOneGroup;
        else if (pick.equals("all")) return Cardinality.AllGroup;
        throw new IllegalStateException("Unsupported pick value (cardinality): [" + pick + "] ");
    }

    private Boolean getManditory(Element e) {
        String manditory = e.attributeValue(MANDITORY);
        if (isEmpty(manditory)) return null;
        return manditory.trim().equalsIgnoreCase("true");
    }

    private Boolean getDefaultValue(Element e) {
        String defaultValue = e.attributeValue(DEFAULT_VALUE);
        if (isEmpty(defaultValue)) {
            return null;
        }
        return defaultValue.trim().equalsIgnoreCase("true");
    }

    private void common(Var var, Element e) {
        assert var != null;
        assert e != null;
        var.setDefaultValue(getDefaultValue(e));
        var.setDerived(isDerived(e));
        var.setCardinality(getCardinality(e));
        var.setManditory(getManditory(e));
    }

    private void processSolitaryFeature(Var parentVar, Element childElement) {
        String varCode = childElement.attributeValue(CODE);
        String varName = childElement.attributeValue(NAME);
        String iffs = childElement.attributeValue(IFFS);
        String includes = childElement.attributeValue(INCLUDES);
        String conflicts = childElement.attributeValue(CONFLICTS);


        if (isEmpty(varCode)) {
            String msg = "FeatureElement [" + childElement.getPath() + "] is missing required attribute 'code'";
            log.error(msg);
            throw new IllegalStateException(msg);
        }

        varCode = VarCodeFixer.fixupVarCode(varCode, parentVar.getCode());

        if (isEmpty(varName)) {
            varName = varCode;
        }


        try {
            Var newChildVar = parentVar.addVar(varCode, varName);
            common(newChildVar, childElement);
        } catch (Exception e1) {
            throw new RuntimeException("Problem adding element [" + childElement.asXML() + "] to parent [" + parentVar + "]", e1);
        }

        //process constraints
        if (notEmpty(includes)) {
            includeMap.put(varCode, includes);
        }

        if (notEmpty(iffs)) {
            iffMap.put(varCode, iffs);
        }

        if (notEmpty(conflicts)) {
            conflictMap.put(varCode, conflicts);
        }

    }

    private static final Log log = LogFactory.getLog(FeatureModelBuilderXml.class);

}