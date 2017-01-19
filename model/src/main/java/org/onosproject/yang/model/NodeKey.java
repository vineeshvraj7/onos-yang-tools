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

import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.hash;
import static org.onosproject.yang.model.ModelConstants.INCOMPLETE_SCHEMA_INFO;

/**
 * Abstraction of an entity which identifies a node uniquely among its
 * siblings.
 */
public class NodeKey<E extends NodeKey> implements Comparable<E> {

    protected SchemaId schemaId;

    /**
     * Create object from builder.
     *
     * @param builder initialized builder
     */
    protected NodeKey(NodeKeyBuilder builder) {
        schemaId = builder.schemaId;
    }

    /**
     * Returns the schema identifier as minimal key required to identify a
     * branching node.
     *
     * @return schema identifier of a key
     */
    public SchemaId schemaId() {
        return schemaId;
    }

    @Override
    public int compareTo(NodeKey o) {
        checkNotNull(o);
        return schemaId.compareTo(o.schemaId());
    }

    /**
     * Returns node key builder.
     *
     * @return node key builder
     */
    public static NodeKeyBuilder builder() {
        return new NodeKeyBuilder();
    }

    @Override
    public int hashCode() {
        return hash(schemaId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!getClass().equals(obj.getClass())) {
            return false;
        }

        NodeKey that = (NodeKey) obj;
        return Objects.equals(schemaId, that.schemaId);
    }

    @Override
    public String toString() {
        return toStringHelper(getClass())
                .add("schemaId", schemaId)
                .toString();
    }

    /**
     * Builder for node key.
     *
     * @param <B> node key type
     */
    public static class NodeKeyBuilder<B extends NodeKeyBuilder<B>> {
        private SchemaId schemaId;

        /**
         * Create the node key from scratch.
         */
        public NodeKeyBuilder() {
        }

        /**
         * Support the derived object to inherit from existing node key builder.
         *
         * @param base existing node key builder
         */
        protected NodeKeyBuilder(NodeKeyBuilder base) {
            checkNotNull(base.schemaId, INCOMPLETE_SCHEMA_INFO);
            schemaId = base.schemaId;
        }

        /**
         * set the schema identifier.
         *
         * @param schema schema identifier
         * @return current builder
         */
        public B schemaId(SchemaId schema) {
            schemaId = schema;
            return (B) this;
        }

        /**
         * set the schema identifier.
         *
         * @param name      name of the node
         * @param nameSpace name space of the node
         * @return current builder
         */
        public B schemaId(String name, String nameSpace) {
            schemaId = new SchemaId(name, nameSpace);
            return (B) this;
        }

        /**
         * construct the node key.
         *
         * @return node key
         */
        public NodeKey build() {
            checkNotNull(schemaId.name(), INCOMPLETE_SCHEMA_INFO);
            checkNotNull(schemaId.namespace(), INCOMPLETE_SCHEMA_INFO);
            return new NodeKey(this);
        }
    }
}
