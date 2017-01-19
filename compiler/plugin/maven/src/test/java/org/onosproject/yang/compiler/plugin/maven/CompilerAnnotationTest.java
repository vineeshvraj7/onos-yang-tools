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


package org.onosproject.yang.compiler.plugin.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Test;
import org.onosproject.yang.compiler.utils.io.YangPluginConfig;
import org.onosproject.yang.compiler.utils.io.impl.YangFileScanner;
import org.onosproject.yang.compiler.utils.io.impl.YangIoUtils;
import org.onosproject.yang.compiler.parser.exceptions.ParserException;

import java.io.File;
import java.io.IOException;

/**
 * Unit test case for compiler annotation.
 */
public class CompilerAnnotationTest {
    private final YangUtilManager utilManager = new YangUtilManager();
    private static final String DIR = "target/compiler/";
    private static final String COMP = System.getProperty("user.dir") + File
            .separator + DIR;


    /**
     * Checks compiler annotation translation should not result in any exception.
     *
     * @throws MojoExecutionException
     */
    @Test
    public void processTranslator() throws IOException,
            ParserException, MojoExecutionException {
        YangIoUtils.deleteDirectory(DIR);
        String searchDir = "src/test/resources/compilerAnnotation";
        utilManager.createYangFileInfoSet(YangFileScanner.getYangFiles(searchDir));
        utilManager.parseYangFileInfoSet();
        utilManager.createYangNodeSet();
        utilManager.resolveDependenciesUsingLinker();

        YangPluginConfig yangPluginConfig = new YangPluginConfig();
        yangPluginConfig.setCodeGenDir(DIR);
        utilManager.translateToJava(yangPluginConfig);
        YangPluginConfig.compileCode(COMP);
        YangIoUtils.deleteDirectory(DIR);
    }
}