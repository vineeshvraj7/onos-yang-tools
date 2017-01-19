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

package org.onosproject.yang.model;

import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.hash;
import static org.onosproject.yang.model.ModelConstants.LEAF_IS_TERMINAL;
import static org.onosproject.yang.model.ModelConstants.NON_KEY_LEAF;
import static org.onosproject.yang.model.ModelConstants.NO_KEY_SET;

/**
 * Representation of an entity which identifies a resource in the logical tree
 * data store. It is a list of node keys to identify the branch point
 * hierarchy to reach a resource in the instance tree.
 */

public class ResourceId {

    /**
     * List of node keys.
     */
    private List<NodeKey> nodeKeyList;

    /**
     * Create object from builder.
     *
     * @param builder initialized builder
     */
    private ResourceId(Builder builder) {
        nodeKeyList = builder.nodeKeyList;
    }

    /**
     * Returns the list of node key used to uniquely identify the branch in the
     * logical tree starting from root.
     *
     * @return node key uniquely identifying the branch
     */
    public List<NodeKey> nodeKeys() {
        return nodeKeyList;
    }

    /**
     * Retrieves a new resource builder.
     *
     * @return resource builder
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public int hashCode() {
        return hash(nodeKeyList);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        ResourceId that = (ResourceId) obj;
        List<NodeKey> thatList = that.nodeKeyList;
        return nodeKeyList.size() == thatList.size() &&
                nodeKeyList.containsAll(thatList);
    }

    @Override
    public String toString() {
        return toStringHelper(getClass())
                .add("nodeKeyList", nodeKeyList)
                .toString();
    }

    /**
     * Builder to construct resource identifier.
     */
    public static class Builder {

        private List<NodeKey> nodeKeyList = new LinkedList<>();
        private NodeKey.NodeKeyBuilder curKeyBuilder = null;

        /**
         * Adds the descendent node's schema identity.
         *
         * @param name      name of descendent node
         * @param nameSpace name space pf descendent node
         * @return updated builder pointing to the specified schema location
         */
        public Builder addBranchPointSchema(String name, String nameSpace) {
            if (curKeyBuilder != null) {
                if (curKeyBuilder instanceof LeafListKey.LeafListKeyBuilder) {
                    throw new ModelException(LEAF_IS_TERMINAL);
                }
                nodeKeyList.add(curKeyBuilder.build());
            }

            curKeyBuilder = new NodeKey.NodeKeyBuilder();
            curKeyBuilder.schemaId(name, nameSpace);

            return this;
        }

        /**
         * Adds a multi instance attribute's node identity.
         *
         * @param name      name of the leaf list
         * @param nameSpace name space of leaf list
         * @param val       value of attribute to identify the instance
         * @return updated builder pointing to the specific attribute
         * value instance
         */
        public Builder addLeafListBranchPoint(String name, String nameSpace,
                                              Object val) {
            LeafListKey.LeafListKeyBuilder leafListKeyBuilder;
            if (curKeyBuilder instanceof LeafListKey.LeafListKeyBuilder) {
                throw new ModelException(NON_KEY_LEAF);
            }
            leafListKeyBuilder = new LeafListKey.LeafListKeyBuilder()
                    .schemaId(name, nameSpace).value(val);

            curKeyBuilder = leafListKeyBuilder;
            return this;
        }

        /**
         * Adds a multi instance nodes key attribute value to identify
         * the branch point of instance tree.
         *
         * @param name      name of the key attribute
         * @param nameSpace name space of key attribute
         * @param val       value of the key leaf, to match in the list entry
         * @return updated builder with list branching information
         */
        public Builder addKeyLeaf(String name, String nameSpace, Object val) {
            ListKey.ListKeyBuilder listKeyBuilder;
            if (!(curKeyBuilder instanceof ListKey.ListKeyBuilder)) {
                if (curKeyBuilder instanceof LeafListKey.LeafListKeyBuilder) {
                    throw new ModelException(LEAF_IS_TERMINAL);
                }

                listKeyBuilder = new ListKey.ListKeyBuilder(curKeyBuilder);
            } else {
                listKeyBuilder = (ListKey.ListKeyBuilder) curKeyBuilder;
            }

            listKeyBuilder.addKeyLeaf(name, nameSpace, val);
            curKeyBuilder = listKeyBuilder;
            return this;
        }

        /**
         * Builds a resource identifier to based on set path information of
         * the resource.
         *
         * @return built resource identifier
         */
        public ResourceId build() {
            checkNotNull(curKeyBuilder, NO_KEY_SET);
            nodeKeyList.add(curKeyBuilder.build());
            return new ResourceId(this);
        }
    }
}
