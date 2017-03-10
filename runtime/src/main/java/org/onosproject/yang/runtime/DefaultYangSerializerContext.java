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

package org.onosproject.yang.runtime;

import org.onosproject.yang.model.SchemaContext;

import java.util.List;

/**
 * Represents YANG serializer context implementation.
 */
public class DefaultYangSerializerContext implements YangSerializerContext {

    private SchemaContext rootContext;
    private List<Annotation> annotations;

    /**
     * Creates an instance of YANG serializer context.
     *
     * @param c root's schema context
     * @param a dependent annotations
     */
    public DefaultYangSerializerContext(SchemaContext c, List<Annotation> a) {
        rootContext = c;
        annotations = a;
    }

    @Override
    public SchemaContext getContext() {
        return rootContext;
    }

    @Override
    public List<Annotation> getProtocolAnnotations() {
        return annotations;
    }
}
