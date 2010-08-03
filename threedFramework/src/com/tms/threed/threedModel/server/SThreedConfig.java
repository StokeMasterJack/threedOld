package com.tms.threed.threedModel.server;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import com.tms.threed.threedCore.shared.ThreedConfig;

public class SThreedConfig extends ThreedConfig {

    public SThreedConfig(ThreedConfig threedConfig) {
        super(threedConfig);
    }

    public JsonNode toJsonNode() {
        JsonNodeFactory f = JsonNodeFactory.instance;
        ObjectNode jsNode = f.objectNode();
        jsNode.put(ROOT_HTTP, getThreedRootHttp().toString());
        jsNode.put(ROOT_FILE_SYSTEM, getThreedRootFileSystem().toString());
        jsNode.put(PNG_ROOT_HTTP, getPngRootHttp().toString());
        jsNode.put(JPG_ROOT_HTTP, getJpgRootHttp().toString());
        return jsNode;
    }

    public String toJsonString() {
        return toJsonNode().toString();
    }

}