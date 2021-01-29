/*
 * Zed Attack Proxy (ZAP) and its related class files.
 *
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2020 The ZAP Development Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaproxy.zap.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.zaproxy.zap.utils.Enableable;

public class DataDrivenNode extends Enableable implements Cloneable {
    private static final String CONFIG_NAME = "name";
    private static final String CONFIG_PATTERN = "pattern";
    private static final String CONFIG_CHILD_NODES = "childNodes";

    private DataDrivenNode parentNode;
    private List<DataDrivenNode> childNodes;

    private String name;
    private Pattern pattern;

    public DataDrivenNode(String name, Pattern pattern, DataDrivenNode parentNode) {
        super();

        this.name = name;
        this.pattern = pattern;

        this.parentNode = parentNode;
        this.childNodes = new ArrayList<DataDrivenNode>();
    }

    public DataDrivenNode(String config) {
        super();

        JSONObject configData = JSONObject.fromObject(config);

        this.name = configData.getString(CONFIG_NAME);
        this.pattern = Pattern.compile(configData.getString(CONFIG_PATTERN));

        this.childNodes = new ArrayList<DataDrivenNode>();
        JSONArray childrenConfig = configData.getJSONArray(CONFIG_CHILD_NODES);
        for (int childCounter = 0; childCounter < childrenConfig.size(); childCounter++) {
            JSONObject childConfig = childrenConfig.getJSONObject(childCounter);

            DataDrivenNode childNode = new DataDrivenNode(childConfig.toString());
            this.childNodes.add(childNode);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public DataDrivenNode getParentNode() {
        return parentNode;
    }

    public List<DataDrivenNode> getChildNodes() {
        return childNodes;
    }

    public void setChildNodes(List<DataDrivenNode> childNodes) {
        this.childNodes = childNodes;
    }

    @Override
    protected DataDrivenNode clone() {
        DataDrivenNode clone = new DataDrivenNode(this.name, this.pattern, this.parentNode);
        clone.setChildNodes(this.childNodes);
        return clone;
    }

    public String getConfig() {
        JSONObject serialized = new JSONObject();

        serialized.put(CONFIG_NAME, this.name);
        serialized.put(CONFIG_PATTERN, this.pattern.pattern());

        List<String> serializedChildren = new ArrayList<String>();
        for (DataDrivenNode child : this.childNodes) {
            serializedChildren.add(child.getConfig());
        }
        serialized.put(CONFIG_CHILD_NODES, serializedChildren);

        return serialized.toString();
    }
}
