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

import static org.onosproject.yangutils.datamodel.utils.YangConstructType.CHOICE_DATA;
import static org.onosproject.yangutils.datamodel.utils.YangErrMsgConstants.DATA_MISSING_ERROR_TAG;
import static org.onosproject.yangutils.datamodel.utils.YangErrMsgConstants.ERROR_PATH_MISSING_CHOICE;
import static org.onosproject.yangutils.datamodel.utils.YangErrMsgConstants.MISSING_CHOICE_ERROR_APP_TAG;

/*-
 * Reference RFC 6020.
 *
 * The "choice" statement defines a set of alternatives, only one of
 *  which may exist at any one time.  The argument is an identifier,
 *  followed by a block of sub-statements that holds detailed choice
 *  information.  The identifier is used to identify the choice node in
 *  the schema tree.  A choice node does not exist in the data tree.
 *
 *  A choice consists of a number of branches, defined with the "case"
 *  sub-statement.  Each branch contains a number of child nodes.  The
 *  nodes from at most one of the choice's branches exist at the same
 *  time.
 *
 *  The choice's sub-statements
 *
 *                +--------------+---------+-------------+------------------+
 *                | substatement | section | cardinality |data model mapping|
 *                +--------------+---------+-------------+------------------+
 *                | anyxml       | 7.10    | 0..n        |-not supported    |
 *                | case         | 7.9.2   | 0..n        |-YangChoice       |
 *                | config       | 7.19.1  | 0..1        |-boolean          |
 *                | container    | 7.5     | 0..n        |-child case nodes |
 *                | default      | 7.9.3   | 0..1        |-string           |
 *                | description  | 7.19.3  | 0..1        |-string           |
 *                | if-feature   | 7.18.2  | 0..n        |-YangIfFeature    |
 *                | leaf         | 7.6     | 0..n        |-child case nodes |
 *                | leaf-list    | 7.7     | 0..n        |-child case nodes |
 *                | list         | 7.8     | 0..n        |-child case nodes |
 *                | mandatory    | 7.9.4   | 0..1        |-string           |
 *                | reference    | 7.19.4  | 0..1        |-string           |
 *                | status       | 7.19.2  | 0..1        |-string           |
 *                | when         | 7.19.5  | 0..1        |-YangWhen         |
 *                +--------------+---------+-------------+------------------+
 */

/**
 * Represents data model node to maintain information defined in YANG choice.
 */
public class YangChoice extends YangNode
        implements YangCommonInfo, Parsable, CollisionDetector, YangAugmentableNode,
        YangWhenHolder, YangIfFeatureHolder, YangAppErrorHolder, YangIsFilterContentNodes {

    private static final long serialVersionUID = 806201604L;

    /**
     * If the choice represents config data.
     */
    private boolean isConfig;

    /**
     * Description of choice.
     */
    private String description;

    /**
     * Reference RFC 6020.
     * <p>
     * The "mandatory" statement, which is optional, takes as an argument the
     * string "true" or "false", and puts a constraint on valid data. If
     * "mandatory" is "true", at least one node from exactly one of the choice's
     * case branches MUST exist.
     * <p>
     * If not specified, the default is "false".
     * <p>
     * The behavior of the constraint depends on the type of the choice's
     * closest ancestor node in the schema tree which is not a non-presence
     * container:
     * <p>
     * o If this ancestor is a case node, the constraint is enforced if any
     * other node from the case exists.
     * <p>
     * o Otherwise, it is enforced if the ancestor node exists.
     */
    private String mandatory;

    /**
     * Reference of the choice.
     */
    private String reference;

    /**
     * Status of the node.
     */
    private YangStatusType status;

    /**
     * Reference RFC 6020.
     * <p>
     * The "default" statement indicates if a case should be considered as the
     * default if no child nodes from any of the choice's cases exist. The
     * argument is the identifier of the "case" statement. If the "default"
     * statement is missing, there is no default case.
     * <p>
     * The "default" statement MUST NOT be present on choices where "mandatory"
     * is true.
     * <p>
     * The default case is only important when considering the default values of
     * nodes under the cases. The default values for nodes under the default
     * case are used if none of the nodes under any of the cases are present.
     * <p>
     * There MUST NOT be any mandatory nodes directly under the default case.
     * <p>
     * Default values for child nodes under a case are only used if one of the
     * nodes under that case is present, or if that case is the default case. If
     * none of the nodes under a case are present and the case is not the
     * default case, the default values of the cases' child nodes are ignored.
     * <p>
     * the default case to be used if no case members is present.
     */
    private String defaultValueInString;

    /**
     * When data of the node.
     */
    private YangWhen when;

    /**
     * List of if-feature.
     */
    private List<YangIfFeature> ifFeatureList;

    private List<YangAugmentedInfo> yangAugmentedInfo = new ArrayList<>();

    /**
     * YANG application error information.
     */
    private YangAppErrorInfo yangAppErrorInfo;

    /**
     * Create a choice node.
     */
    public YangChoice() {
        super(YangNodeType.CHOICE_NODE, new HashMap<YangSchemaNodeIdentifier, YangSchemaNodeContextInfo>());
        yangAppErrorInfo = new YangAppErrorInfo();
        yangAppErrorInfo.setErrorTag(DATA_MISSING_ERROR_TAG);
        yangAppErrorInfo.setErrorAppTag(MISSING_CHOICE_ERROR_APP_TAG);
        yangAppErrorInfo.setErrorAppPath(ERROR_PATH_MISSING_CHOICE);
    }

    @Override
    public void addToChildSchemaMap(YangSchemaNodeIdentifier schemaNodeIdentifier,
                                    YangSchemaNodeContextInfo yangSchemaNodeContextInfo)
            throws DataModelException {
        getYsnContextInfoMap().put(schemaNodeIdentifier, yangSchemaNodeContextInfo);
        YangSchemaNodeContextInfo yangSchemaNodeContextInfo1 = new YangSchemaNodeContextInfo();
        yangSchemaNodeContextInfo1.setSchemaNode(yangSchemaNodeContextInfo.getSchemaNode());
        yangSchemaNodeContextInfo1.setContextSwitchedNode(this);
        getParent().addToChildSchemaMap(schemaNodeIdentifier, yangSchemaNodeContextInfo1);
    }

    @Override
    public void setNameSpaceAndAddToParentSchemaMap() {
        // Get parent namespace.
        YangNameSpace nameSpace = this.getParent().getNameSpace();
        // Set namespace for self node.
        setNameSpace(nameSpace);
    }

    @Override
    public void incrementMandatoryChildCount() {
        //For non data nodes, mandatory child to be added to parent node.
        // TODO
    }

    @Override
    public void addToDefaultChildMap(YangSchemaNodeIdentifier yangSchemaNodeIdentifier, YangSchemaNode yangSchemaNode) {
        //For non data nodes, default child to be added to parent node.
        // TODO
    }

    @Override
    public YangSchemaNodeType getYangSchemaNodeType() {
        return YangSchemaNodeType.YANG_NON_DATA_NODE;
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
     * Returns config flag.
     *
     * @return the config flag
     */
    public boolean isConfig() {
        return isConfig;
    }

    /**
     * Sets config flag.
     *
     * @param isCfg the config flag
     */
    public void setConfig(boolean isCfg) {
        isConfig = isCfg;
    }

    /**
     * Returns the mandatory status.
     *
     * @return the mandatory status
     */
    public String getMandatory() {
        return mandatory;
    }

    /**
     * Sets the mandatory status.
     *
     * @param mandatory the mandatory status
     */
    public void setMandatory(String mandatory) {
        this.mandatory = mandatory;
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
     * Returns the default value.
     *
     * @return the default value
     */
    public String getDefaultValueInString() {
        return defaultValueInString;
    }

    /**
     * Sets the default value.
     *
     * @param defaultValueInString the default value
     */
    public void setDefaultValueInString(String defaultValueInString) {
        this.defaultValueInString = defaultValueInString;
    }

    /**
     * Returns the type of the data.
     *
     * @return choice data
     */
    @Override
    public YangConstructType getYangConstructType() {
        return CHOICE_DATA;
    }

    /**
     * Validates the data on entering the corresponding parse tree node.
     *
     * @throws DataModelException a violation of data model rules
     */
    @Override
    public void validateDataOnEntry() throws DataModelException {
        // TODO auto-generated method stub, to be implemented by parser
    }

    /**
     * Validates the data on exiting the corresponding parse tree node.
     *
     * @throws DataModelException a violation of data model rules
     */
    @Override
    public void validateDataOnExit() throws DataModelException {
        if (defaultValueInString != null && !defaultValueInString.isEmpty()) {
            YangNode node = getChild();
            boolean matched = false;
            // Check whether default string matches the case
            while (node != null) {
                if (node instanceof YangCase) {
                    if (defaultValueInString.equals(((YangCase) node).getName())) {
                        matched = true;
                        break;
                    }
                }
                node = node.getNextSibling();
            }

            if (!matched) {
                throw new DataModelException("YANG file error: default string \"" + defaultValueInString
                        + "\" not matching choice \"" + getName() + "\" case.");
            }
        }
    }

    @Override
    public void detectCollidingChild(String identifierName, YangConstructType dataType) throws DataModelException {

        if (getParent() instanceof YangCase && dataType != YangConstructType.CASE_DATA) {
            ((CollisionDetector) getParent()).detectCollidingChild(identifierName, dataType);
        }
        YangNode node = getChild();
        while (node != null) {
            if (node instanceof CollisionDetector) {
                ((CollisionDetector) node).detectSelfCollision(identifierName, dataType);
            }
            node = node.getNextSibling();
        }
    }

    @Override
    public void detectSelfCollision(String identifierName, YangConstructType dataType) throws DataModelException {

        if (dataType == CHOICE_DATA) {
            if (getName().equals(identifierName)) {
                throw new DataModelException("YANG file error: Identifier collision detected in choice \"" +
                        getName() + "\"");
            }
            return;
        }

        YangNode node = getChild();
        while (node != null) {
            if (node instanceof CollisionDetector) {
                ((CollisionDetector) node).detectSelfCollision(identifierName, dataType);
            }
            node = node.getNextSibling();
        }
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
    public void setAppErrorInfo(YangAppErrorInfo yangAppErrorInfo) {
        this.yangAppErrorInfo = yangAppErrorInfo;
    }

    @Override
    public YangAppErrorInfo getAppErrorInfo() {
        return yangAppErrorInfo;
    }

}
