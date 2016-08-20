/*
 * Copyright 2016-present Open Networking Laboratory
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

package org.onosproject.yangutils.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.onosproject.yangutils.datamodel.exceptions.DataModelException;
import org.onosproject.yangutils.datamodel.utils.Parsable;
import org.onosproject.yangutils.datamodel.utils.YangConstructType;
import org.onosproject.yangutils.datamodel.utils.builtindatatype.YangDataTypes;

import static org.onosproject.yangutils.datamodel.utils.DataModelUtils.detectCollidingChildUtil;

/*
 *  The "list" statement is used to define an interior data node in the
 *  schema tree.  A list node may exist in multiple instances in the data
 *  tree.  Each such instance is known as a list entry.  The "list"
 *  statement takes one argument, which is an identifier, followed by a
 *  block of sub-statements that holds detailed list information.
 *
 *  A list entry is uniquely identified by the values of the list's keys,
 *  if defined.
 *
 *  The list's sub-statements
 *
 *                +--------------+---------+-------------+------------------+
 *                | substatement | section | cardinality |data model mapping|
 *                +--------------+---------+-------------+------------------+
 *                | anyxml       | 7.10    | 0..n        |-not supported    |
 *                | choice       | 7.9     | 0..n        |-child nodes      |
 *                | config       | 7.19.1  | 0..1        |-boolean          |
 *                | container    | 7.5     | 0..n        |-child nodes      |
 *                | description  | 7.19.3  | 0..1        |-string           |
 *                | grouping     | 7.11    | 0..n        |-child nodes      |
 *                | if-feature   | 7.18.2  | 0..n        |-YangIfFeature    |
 *                | key          | 7.8.2   | 0..1        |-String list      |
 *                | leaf         | 7.6     | 0..n        |-YangLeaf         |
 *                | leaf-list    | 7.7     | 0..n        |-YangLeafList     |
 *                | list         | 7.8     | 0..n        |-child nodes      |
 *                | max-elements | 7.7.4   | 0..1        |-int              |
 *                | min-elements | 7.7.3   | 0..1        |-int              |
 *                | must         | 7.5.3   | 0..n        |-YangMust         |
 *                | ordered-by   | 7.7.5   | 0..1        |-TODO             |
 *                | reference    | 7.19.4  | 0..1        |-string           |
 *                | status       | 7.19.2  | 0..1        |-YangStatus       |
 *                | typedef      | 7.3     | 0..n        |-child nodes      |
 *                | unique       | 7.8.3   | 0..n        |-TODO             |
 *                | uses         | 7.12    | 0..n        |-child nodes      |
 *                | when         | 7.19.5  | 0..1        |-YangWhen         |
 *                +--------------+---------+-------------+------------------+
 */

/**
 * Represents list data represented in YANG.
 */
public abstract class YangList
        extends YangNode
        implements YangLeavesHolder, YangCommonInfo, Parsable, CollisionDetector,
        YangAugmentableNode, YangMustHolder, YangWhenHolder, YangIfFeatureHolder, YangSchemaNode,
        YangIsFilterContentNodes, YangConfig {

    private static final long serialVersionUID = 806201609L;

    /**
     * If list maintains config data.
     */
    private boolean isConfig;

    /**
     * Description of list.
     */
    private String description;

    /**
     * Reference RFC 6020.
     *
     * The "key" statement, which MUST be present if the list represents
     * configuration, and MAY be present otherwise, takes as an argument a
     * string that specifies a space-separated list of leaf identifiers of this
     * list. A leaf identifier MUST NOT appear more than once in the key. Each
     * such leaf identifier MUST refer to a child leaf of the list. The leafs
     * can be defined directly in sub-statements to the list, or in groupings
     * used in the list.
     *
     * The combined values of all the leafs specified in the key are used to
     * uniquely identify a list entry. All key leafs MUST be given values when a
     * list entry is created. Thus, any default values in the key leafs or their
     * types are ignored. It also implies that any mandatory statement in the
     * key leafs are ignored.
     *
     * A leaf that is part of the key can be of any built-in or derived type,
     * except it MUST NOT be the built-in type "empty".
     *
     * All key leafs in a list MUST have the same value for their "config" as
     * the list itself.
     *
     * List of key leaf names.
     */
    private List<String> keyList;

    /**
     * Reference RFC 6020.
     *
     * The "unique" statement is used to put constraints on valid list
     * entries.  It takes as an argument a string that contains a space-
     * separated list of schema node identifiers, which MUST be given in the
     * descendant form.  Each such schema node identifier MUST refer to a leaf.
     *
     * If one of the referenced leafs represents configuration data, then
     * all of the referenced leafs MUST represent configuration data.
     *
     * The "unique" constraint specifies that the combined values of all the
     * leaf instances specified in the argument string, including leafs with
     * default values, MUST be unique within all list entry instances in
     * which all referenced leafs exist.
     *
     * List of unique leaf/leaf-list names
     */
    private List<String> uniqueList;

    /**
     * List of leaves.
     */
    private List<YangLeaf> listOfLeaf;

    /**
     * List of leaf-lists.
     */
    private List<YangLeafList> listOfLeafList;

    private List<YangAugmentedInfo> yangAugmentedInfo = new ArrayList<>();

    /**
     * Reference RFC 6020.
     *
     * The "max-elements" statement, which is optional, takes as an argument a
     * positive integer or the string "unbounded", which puts a constraint on
     * valid list entries. A valid leaf-list or list always has at most
     * max-elements entries.
     *
     * If no "max-elements" statement is present, it defaults to "unbounded".
     */
    private YangMaxElement maxElements;

    /**
     * Reference RFC 6020.
     *
     * The "min-elements" statement, which is optional, takes as an argument a
     * non-negative integer that puts a constraint on valid list entries. A
     * valid leaf-list or list MUST have at least min-elements entries.
     *
     * If no "min-elements" statement is present, it defaults to zero.
     *
     * The behavior of the constraint depends on the type of the leaf-list's or
     * list's closest ancestor node in the schema tree that is not a non-
     * presence container:
     *
     * o If this ancestor is a case node, the constraint is enforced if any
     * other node from the case exists.
     *
     * o Otherwise, it is enforced if the ancestor node exists.
     */
    private YangMinElement minElements;

    /**
     * reference.
     */
    private String reference;

    /**
     * Status of the node.
     */
    private YangStatusType status = YangStatusType.CURRENT;

    /**
     * List of must statement constraints.
     */
    private List<YangMust> mustConstraintList;

    /**
     * When data of the node.
     */
    private YangWhen when;

    /**
     * List of if-feature.
     */
    private List<YangIfFeature> ifFeatureList;

    /**
     * Compiler Annotation.
     */
    private transient YangCompilerAnnotation compilerAnnotation;

    private boolean isAugmented;

    /**
     * Creates a YANG list object.
     */
    public YangList() {
        super(YangNodeType.LIST_NODE, new HashMap<YangSchemaNodeIdentifier, YangSchemaNodeContextInfo>());
        listOfLeaf = new LinkedList<>();
        listOfLeafList = new LinkedList<>();
    }

    @Override
    public void addToChildSchemaMap(YangSchemaNodeIdentifier schemaNodeIdentifier,
            YangSchemaNodeContextInfo yangSchemaNodeContextInfo) {
        getYsnContextInfoMap().put(schemaNodeIdentifier, yangSchemaNodeContextInfo);
    }

    @Override
    public void incrementMandatoryChildCount() {
        // TODO
    }

    @Override
    public void addToDefaultChildMap(YangSchemaNodeIdentifier yangSchemaNodeIdentifier, YangSchemaNode yangSchemaNode) {
        // TODO
    }

    @Override
    public YangSchemaNodeType getYangSchemaNodeType() {
        return YangSchemaNodeType.YANG_MULTI_INSTANCE_NODE;
    }

    /**
     * Returns the compiler annotation.
     *
     * @return the compiler annotation
     */
    public YangCompilerAnnotation getCompilerAnnotation() {
        return compilerAnnotation;
    }

    /**
     * Sets the compiler annotation.
     *
     * @param compilerAnnotation the compiler annotation to set
     */
    public void setCompilerAnnotation(YangCompilerAnnotation compilerAnnotation) {
        this.compilerAnnotation = compilerAnnotation;
    }

    /**
     * Returns the when.
     *
     * @return the when
     */
    @Override
    public YangWhen getWhen() {
        return when;
    }

    /**
     * Sets the when.
     *
     * @param when the when to set
     */
    @Override
    public void setWhen(YangWhen when) {
        this.when = when;
    }

    /**
     * Returns the config flag.
     *
     * @return the isConfig
     */
    @Override
    public boolean isConfig() {
        return isConfig;
    }

    /**
     * Sets the config flag.
     *
     * @param isConfig the config flag
     */
    @Override
    public void setConfig(boolean isConfig) {
        this.isConfig = isConfig;
    }

    /**
     * Returns the description.
     *
     * @return the description
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description set the description
     */
    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the list of unique field names.
     *
     * @return the list of unique field names
     */
    public List<String> getUniqueList() {
        return uniqueList;
    }

    /**
     * Sets the list of unique field names.
     *
     * @param uniqueList the list of unique field names
     */
    private void setUniqueList(List<String> uniqueList) {
        this.uniqueList = uniqueList;
    }

    /**
     * Returns the list of key field names.
     *
     * @return the list of key field names
     */
    public List<String> getKeyList() {
        return keyList;
    }

    /**
     * Sets the list of key field names.
     *
     * @param keyList the list of key field names
     */
    private void setKeyList(List<String> keyList) {
        this.keyList = keyList;
    }

    /**
     * Adds a key field name.
     *
     * @param key key field name.
     * @throws DataModelException a violation of data model rules
     */
    public void addKey(String key)
            throws DataModelException {
        if (getKeyList() == null) {
            setKeyList(new LinkedList<String>());
        }

        if (getKeyList().contains(key)) {
            throw new DataModelException("A leaf identifier must not appear more than once in the\n" +
                    "   key");
        }

        getKeyList().add(key);
    }

    /**
     * Adds a unique field name.
     *
     * @param unique unique field name.
     * @throws DataModelException a violation of data model rules
     */
    public void addUnique(String unique)
            throws DataModelException {
        if (getUniqueList() == null) {
            setUniqueList(new LinkedList<>());
        }
        if (getUniqueList().contains(unique)) {
            throw new DataModelException("A leaf identifier must not appear more than once in the\n" +
                    "   unique");
        }
        getUniqueList().add(unique);
    }

    /**
     * Returns the list of leaves.
     *
     * @return the list of leaves
     */
    @Override
    public List<YangLeaf> getListOfLeaf() {
        return listOfLeaf;
    }

    /**
     * Sets the list of leaves.
     *
     * @param leafsList the list of leaf to set
     */
    @Override
    public void setListOfLeaf(List<YangLeaf> leafsList) {
        listOfLeaf = leafsList;
    }

    /**
     * Adds a leaf.
     *
     * @param leaf the leaf to be added
     */
    @Override
    public void addLeaf(YangLeaf leaf) {
        getListOfLeaf().add(leaf);
    }

    /**
     * Returns the list of leaf-list.
     *
     * @return the list of leaf-list
     */
    @Override
    public List<YangLeafList> getListOfLeafList() {
        return listOfLeafList;
    }

    /**
     * Sets the list of leaf-list.
     *
     * @param listOfLeafList the list of leaf-list to set
     */
    @Override
    public void setListOfLeafList(List<YangLeafList> listOfLeafList) {
        this.listOfLeafList = listOfLeafList;
    }

    /**
     * Adds a leaf-list.
     *
     * @param leafList the leaf-list to be added
     */
    @Override
    public void addLeafList(YangLeafList leafList) {
        getListOfLeafList().add(leafList);
    }

    /**
     * Returns the max elements.
     *
     * @return the max elements
     */
    public YangMaxElement getMaxElements() {
        return maxElements;
    }

    /**
     * Sets the max elements.
     *
     * @param max the max elements
     */
    public void setMaxElements(YangMaxElement max) {
        this.maxElements = max;
    }

    /**
     * Returns the minimum elements.
     *
     * @return the minimum elements
     */
    public YangMinElement getMinElements() {
        return minElements;
    }

    /**
     * Sets the minimum elements.
     *
     * @param minElements the minimum elements
     */
    public void setMinElements(YangMinElement minElements) {
        this.minElements = minElements;
    }

    /**
     * Returns the textual reference.
     *
     * @return the reference
     */
    @Override
    public String getReference() {
        return reference;
    }

    /**
     * Sets the textual reference.
     *
     * @param reference the reference to set
     */
    @Override
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * Returns the status.
     *
     * @return the status
     */
    @Override
    public YangStatusType getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the status to set
     */
    @Override
    public void setStatus(YangStatusType status) {
        this.status = status;
    }

    /**
     * Returns the type of the parsed data.
     *
     * @return returns LIST_DATA
     */
    @Override
    public YangConstructType getYangConstructType() {
        return YangConstructType.LIST_DATA;
    }

    /**
     * Validates the data on entering the corresponding parse tree node.
     *
     * @throws DataModelException a violation of data model rules
     */
    @Override
    public void validateDataOnEntry()
            throws DataModelException {
        // TODO auto-generated method stub, to be implemented by parser
    }

    /**
     * Validates the data on exiting the corresponding parse tree node.
     *
     * @throws DataModelException a violation of data model rules
     */
    @Override
    public void validateDataOnExit()
            throws DataModelException {
        List<String> keys = getKeyList();
        List<YangLeaf> leaves = getListOfLeaf();
        List<YangLeafList> leafLists = getListOfLeafList();

        validateConfig(leaves, leafLists);

        //A list must have atleast one key leaf if config is true
        if (isConfig && (keys == null || leaves == null) && !isUsesPresentInList()
                && !isListPresentInGrouping()) {
            throw new DataModelException("A list must have atleast one key leaf if config is true;");
        } else if (keys != null) {
            validateKey(leaves, keys);
        }
    }

    /**
     * Validates config statement of YANG list.
     *
     * @param leaves    list of leaf attributes of YANG list
     * @param leafLists list of leaf-list attributes of YANG list
     * @throws DataModelException a violation of data model rules
     */
    private void validateConfig(List<YangLeaf> leaves, List<YangLeafList> leafLists)
            throws DataModelException {

        /*
         * If a node has "config" set to "false", no node underneath it can have
         * "config" set to "true".
         */
        if (!isConfig && leaves != null) {
            for (YangLeaf leaf : leaves) {
                if (leaf.isConfig()) {
                    throw new DataModelException("If a list has \"config\" set to \"false\", no node underneath " +
                            "it can have \"config\" set to \"true\".");
                }
            }
        }

        if (!isConfig && leafLists != null) {
            for (YangLeafList leafList : leafLists) {
                if (leafList.isConfig()) {
                    throw new DataModelException("If a list has \"config\" set to \"false\", no node underneath " +
                            "it can have \"config\" set to \"true\".");
                }
            }
        }
    }

    /**
     * Validates key statement of list.
     *
     * @param leaves list of leaf attributes of list
     * @param keys   list of key attributes of list
     * @throws DataModelException a violation of data model rules
     */
    private void validateKey(List<YangLeaf> leaves, List<String> keys)
            throws DataModelException {
        boolean leafFound = false;
        List<YangLeaf> keyLeaves = new LinkedList<>();

        /*
         * 1. Leaf identifier must refer to a child leaf of the list 2. A leaf
         * that is part of the key must not be the built-in type "empty".
         */
        for (String key : keys) {
            if (leaves != null && !leaves.isEmpty()) {
                for (YangLeaf leaf : leaves) {
                    if (key.equals(leaf.getName())) {
                        if (leaf.getDataType().getDataType() == YangDataTypes.EMPTY) {
                            throw new DataModelException(" A leaf that is part of the key must not be the built-in " +
                                    "type \"empty\".");
                        }
                        leafFound = true;
                        keyLeaves.add(leaf);
                        break;
                    }
                }
            }

            if (!leafFound && !isUsesPresentInList() && !isListPresentInGrouping()) {
                throw new DataModelException("An identifier, in key, must refer to a child leaf of the list");
            }
            leafFound = false;
        }

        /*
         * All key leafs in a list MUST have the same value for their "config"
         * as the list itself.
         */
        for (YangLeaf keyLeaf : keyLeaves) {
            if (isConfig != keyLeaf.isConfig()) {
                throw new DataModelException("All key leafs in a list must have the same value for their" +
                        " \"config\" as the list itself.");
            }
        }
    }

    @Override
    public void detectCollidingChild(String identifierName, YangConstructType dataType)
            throws DataModelException {
        // Asks helper to detect colliding child.
        detectCollidingChildUtil(identifierName, dataType, this);
    }

    @Override
    public void detectSelfCollision(String identifierName, YangConstructType dataType)
            throws DataModelException {
        if (getName().equals(identifierName)) {
            throw new DataModelException("YANG file error: Duplicate input identifier detected, same as list \"" +
                    getName() + "\"");
        }
    }

    private boolean isUsesPresentInList() {
        YangNode node = getChild();
        while (node != null) {
            if (node instanceof YangUses) {
                return true;
            }
            node = node.getNextSibling();
        }
        return false;
        // TODO When grouping linking is done this method has to be modified.
    }

    private boolean isListPresentInGrouping() {
        YangNode node = getParent();
        while (node != null) {
            if (node instanceof YangGrouping) {
                return true;
            }
            node = node.getParent();
        }
        return false;
        // TODO When grouping linking is done this method has to be modified.
    }

    @Override
    public List<YangIfFeature> getIfFeatureList() {
        return ifFeatureList;
    }

    @Override
    public void addIfFeatureList(YangIfFeature ifFeature) {
        if (getIfFeatureList() == null) {
            setIfFeatureList(new LinkedList<>());
        }
        getIfFeatureList().add(ifFeature);
    }

    @Override
    public void setIfFeatureList(List<YangIfFeature> ifFeatureList) {
        this.ifFeatureList = ifFeatureList;
    }

    @Override
    public List<YangMust> getListOfMust() {
        return mustConstraintList;
    }

    @Override
    public void setListOfMust(List<YangMust> mustConstraintList) {
        this.mustConstraintList = mustConstraintList;
    }

    @Override
    public void addMust(YangMust must) {
        if (getListOfMust() == null) {
            setListOfMust(new LinkedList<>());
        }
        getListOfMust().add(must);
    }

    @Override
    public void addAugmentation(YangAugmentedInfo augmentInfo) {
        yangAugmentedInfo.add(augmentInfo);
    }

    @Override
    public void removeAugmentation(YangAugmentedInfo augmentInfo) {
        yangAugmentedInfo.remove(augmentInfo);
    }

    @Override
    public List<YangAugmentedInfo> getAugmentedInfoList() {
        return yangAugmentedInfo;
    }

    @Override
    public void setIsAugmented(boolean isAugmented) {
        this.isAugmented = isAugmented;
    }

    @Override
    public boolean isAugmented() {
        return isAugmented;
    }

    @Override
    public void setLeafNameSpaceAndAddToParentSchemaMap() {
        // Add namespace for all leafs.
        for (YangLeaf yangLeaf : getListOfLeaf()) {
            yangLeaf.setLeafNameSpaceAndAddToParentSchemaMap(getNameSpace());
        }
        // Add namespace for all leaf list.
        for (YangLeafList yangLeafList : getListOfLeafList()) {
            yangLeafList.setLeafNameSpaceAndAddToParentSchemaMap(getNameSpace());
        }
    }
}
