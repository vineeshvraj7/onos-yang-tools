/*
 * Copyright 2017-present Open Networking Laboratory
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

package org.onosproject.yang.runtime.impl;

import org.onosproject.yang.compiler.datamodel.TraversalType;
import org.onosproject.yang.compiler.datamodel.YangNode;

/**
 * Represents  model converter Traversal info which is needed every time the traversal of
 * a YANG node happens. This contains YANG node and its corresponding traversal
 * type information.
 */
class ModelConverterTraversalInfo {

    /**
     * YANG node of the current traversal.
     */
    private final YangNode yangNode;

    /**
     * Traverse type of the current traversal.
     */
    private final TraversalType traverseType;

    /**
     * Creates  model converter traversal info by taking the traversal type and the YANG
     * node.
     *
     * @param yangNode     YANG node
     * @param traverseType traversal type
     */
    ModelConverterTraversalInfo(YangNode yangNode, TraversalType traverseType) {
        this.yangNode = yangNode;
        this.traverseType = traverseType;
    }

    /**
     * Returns the YANG node of the current traversal.
     *
     * @return YANG node
     */
    YangNode getYangNode() {
        return yangNode;
    }

    /**
     * Returns the traversal type of the current traversal.
     *
     * @return traversal type
     */
    TraversalType getTraverseType() {
        return traverseType;
    }
}
