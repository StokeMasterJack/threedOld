package com.tms.threed.featureModel.server;

import com.tms.threed.featureModel.shared.Cardinality;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.featureModel.shared.Var;
import com.tms.threed.featureModel.shared.boolExpr.And;
import com.tms.threed.featureModel.shared.boolExpr.BoolExpr;
import com.tms.threed.featureModel.shared.boolExpr.Junction;
import com.tms.threed.featureModel.shared.boolExpr.Not;
import com.tms.threed.featureModel.shared.boolExpr.Pair;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static com.tms.threed.util.lang.shared.Strings.isEmpty;

public class JsonMarshaller {

    private JsonNodeFactory f = JsonNodeFactory.instance;

    ObjectNode mapConstraints(And constraints) {
        return mapConstraintJunction(constraints);
    }

    ObjectNode mapConstraintBoolExpr(BoolExpr boolExpr) {
        if (boolExpr instanceof Junction) {
            return mapConstraintJunction((Junction) boolExpr);
        } else if (boolExpr instanceof Pair) {
            return mapConstraintPair((Pair) boolExpr);
        } else if (boolExpr instanceof Not) {
            return mapConstraintNot((Not) boolExpr);
        } else if (boolExpr instanceof Var) {
            return mapConstraintVar((Var) boolExpr);
        } else {
            //todo need to add support for: True and False
            throw new UnsupportedOperationException("TODO");
        }
    }

    private ObjectNode mapConstraintVar(Var var) {
        ObjectNode jsJunction = f.objectNode();
        jsJunction.put("type", Var.class.getSimpleName());
        jsJunction.put("code", var.getCode());
        return jsJunction;
    }

    private ObjectNode mapConstraintJunction(Junction junction) {
        ObjectNode jsJunction = f.objectNode();
        jsJunction.put("type", Junction.class.getSimpleName());
        jsJunction.put("subtype", junction.getClass().getSimpleName());

        ArrayNode jsExprList = f.arrayNode();
        List<BoolExpr> exprList = junction.getExprList();
        for (BoolExpr expr : exprList) {
            jsExprList.add(mapConstraintBoolExpr(expr));
        }
        jsJunction.put("exprList", jsExprList);

        return jsJunction;
    }

    private ObjectNode mapConstraintNot(Not not) {
        ObjectNode jsPair = f.objectNode();
        jsPair.put("type", Not.class.getSimpleName());
        jsPair.put("expr", mapConstraintBoolExpr(not.getExpr()));
        return jsPair;
    }

    private ObjectNode mapConstraintPair(Pair pair) {
        ObjectNode jsPair = f.objectNode();
        jsPair.put("type", Pair.class.getSimpleName());
        jsPair.put("subtype", pair.getClass().getSimpleName());

        jsPair.put("expr1", mapConstraintBoolExpr(pair.getExpr1()));
        jsPair.put("expr2", mapConstraintBoolExpr(pair.getExpr2()));

        return jsPair;
    }


    public ObjectNode mapFeatureModel(FeatureModel fm) {
        ObjectNode jsRootVar = mapVar(fm.getRootVar());
        ObjectNode jsExtraConstraints = mapConstraints(fm.getExtraConstraint());

        ObjectNode jsFeatureModel = f.objectNode();
        jsFeatureModel.put("displayName", fm.getDisplayName());
        jsFeatureModel.put("rootVar", jsRootVar);
        jsFeatureModel.put("constraints", jsExtraConstraints);

        return jsFeatureModel;
    }


    ObjectNode mapVar(Var var) {
        ObjectNode jsVar = f.objectNode();

        String varCode = var.getCode();
        jsVar.put("code", varCode);

        String varName = var.getName();
        if (!isEmpty(varName)) {
            jsVar.put("name", varName);
        }

        Cardinality cardinality = var.getCardinality();
        if (cardinality != null) {
            jsVar.put("cardinality", var.getCardinality().name());
        }

        Boolean derived = var.getDerived();
        if (derived != null) {
            jsVar.put("derived", derived);
        }

        Boolean defaultValue = var.getDefaultValue();
        if (defaultValue != null) {
            jsVar.put("defaultValue", defaultValue);
        }

        Boolean manditory = var.getManditory();
        if (manditory != null) {
            jsVar.put("manditory", manditory);
        }

        List<Var> childVars = var.getChildVars();
        if (childVars != null) {
            ArrayNode jsChildVars = f.arrayNode();
            for (Var childVar : childVars) {
                ObjectNode jsChildVar = mapVar(childVar);
                jsChildVars.add(jsChildVar);
            }
            jsVar.put("childNodes", jsChildVars);
        }
        return jsVar;
    }

    public static void prettyPrint(JsonNode jsonNode) throws IOException {
        PrintWriter out = new PrintWriter(System.out);
        JsonFactory f = new MappingJsonFactory();
        JsonGenerator g = f.createJsonGenerator(out);
        g.useDefaultPrettyPrinter();
        g.writeObject(jsonNode);
    }

}