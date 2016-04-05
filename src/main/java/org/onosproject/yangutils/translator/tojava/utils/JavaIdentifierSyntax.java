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

package org.onosproject.yangutils.translator.tojava.utils;

import java.util.ArrayList;

import org.onosproject.yangutils.datamodel.YangNode;
import org.onosproject.yangutils.translator.exception.TranslatorException;
import org.onosproject.yangutils.translator.tojava.HasJavaFileInfo;
import org.onosproject.yangutils.translator.tojava.JavaFileInfo;

import static org.onosproject.yangutils.utils.UtilConstants.COLAN;
import static org.onosproject.yangutils.utils.UtilConstants.DEFAULT_BASE_PKG;
import static org.onosproject.yangutils.utils.UtilConstants.EMPTY_STRING;
import static org.onosproject.yangutils.utils.UtilConstants.HYPHEN;
import static org.onosproject.yangutils.utils.UtilConstants.JAVA_KEY_WORDS;
import static org.onosproject.yangutils.utils.UtilConstants.PERIOD;
import static org.onosproject.yangutils.utils.UtilConstants.QUOTES;
import static org.onosproject.yangutils.utils.UtilConstants.REGEX_FOR_FIRST_DIGIT;
import static org.onosproject.yangutils.utils.UtilConstants.REGEX_WITH_SPECIAL_CHAR;
import static org.onosproject.yangutils.utils.UtilConstants.REVISION_PREFIX;
import static org.onosproject.yangutils.utils.UtilConstants.SLASH;
import static org.onosproject.yangutils.utils.UtilConstants.UNDER_SCORE;
import static org.onosproject.yangutils.utils.UtilConstants.VERSION_PREFIX;

/**
 * Represents an utility Class for translating the name from YANG to java convention.
 */
public final class JavaIdentifierSyntax {

    private static final int MAX_MONTHS = 12;
    private static final int MAX_DAYS = 31;
    private static final int INDEX_ZERO = 0;
    private static final int INDEX_ONE = 1;
    private static final int INDEX_TWO = 2;
    private static final int VALUE_CHECK = 10;
    private static final String ZERO = "0";

    /**
     * Create instance of java identifier syntax.
     */
    private JavaIdentifierSyntax() {
    }

    /**
     * Returns the root package string.
     *
     * @param version YANG version
     * @param nameSpace name space of the module
     * @param revision revision of the module defined
     * @return returns the root package string
     */
    public static String getRootPackage(byte version, String nameSpace, String revision) {

        String pkg;
        pkg = DEFAULT_BASE_PKG;
        pkg = pkg + PERIOD;
        pkg = pkg + getYangVersion(version);
        pkg = pkg + PERIOD;
        pkg = pkg + getPkgFromNameSpace(nameSpace);
        pkg = pkg + PERIOD;
        pkg = pkg + getYangRevisionStr(revision);

        return pkg.toLowerCase();
    }

    /**
     * Returns the contained data model parent node.
     *
     * @param currentNode current node which parent contained node is required
     * @return parent node in which the current node is an attribute
     */
    public static YangNode getParentNodeInGenCode(YangNode currentNode) {

        /*
         * TODO: recursive parent lookup to support choice/augment/uses. TODO:
         * need to check if this needs to be updated for
         * choice/case/augment/grouping
         */
        return currentNode.getParent();
    }

    /**
     * Returns the node package string.
     *
     * @param curNode current java node whose package string needs to be set
     * @return returns the root package string
     */
    public static String getCurNodePackage(YangNode curNode) {

        String pkg;
        if (!(curNode instanceof HasJavaFileInfo)
                || curNode.getParent() == null) {
            throw new TranslatorException("missing parent node to get current node's package");
        }

        YangNode parentNode = getParentNodeInGenCode(curNode);
        if (!(parentNode instanceof HasJavaFileInfo)) {
            throw new TranslatorException("missing parent java node to get current node's package");
        }
        JavaFileInfo parentJavaFileHandle = ((HasJavaFileInfo) parentNode).getJavaFileInfo();
        pkg = parentJavaFileHandle.getPackage() + PERIOD + parentJavaFileHandle.getJavaName();
        return pkg.toLowerCase();
    }

    /**
     * Returns version.
     *
     * @param ver YANG version
     * @return version
     */
    private static String getYangVersion(byte ver) {
        return VERSION_PREFIX + ver;
    }

    /**
     * Returns package name from name space.
     *
     * @param nameSpace name space of YANG module
     * @return java package name as per java rules
     */
    private static String getPkgFromNameSpace(String nameSpace) {

        ArrayList<String> pkgArr = new ArrayList<String>();
        nameSpace = nameSpace.replace(QUOTES, EMPTY_STRING);
        String properNameSpace = nameSpace.replaceAll(REGEX_WITH_SPECIAL_CHAR, COLAN);
        String[] nameSpaceArr = properNameSpace.split(COLAN);

        for (String nameSpaceString : nameSpaceArr) {
            pkgArr.add(nameSpaceString);
        }
        return getPkgFrmArr(pkgArr);
    }

    /**
     * Returns revision string array.
     *
     * @param date YANG module revision
     * @return revision string
     * @throws TranslatorException when date is invalid.
     */
    private static String getYangRevisionStr(String date) throws TranslatorException {

        String[] revisionArr = date.split(HYPHEN);

        String rev = REVISION_PREFIX;
        rev = rev + revisionArr[INDEX_ZERO];

        if (Integer.parseInt(revisionArr[INDEX_ONE]) <= MAX_MONTHS
                && Integer.parseInt(revisionArr[INDEX_TWO]) <= MAX_DAYS) {
            for (int i = INDEX_ONE; i < revisionArr.length; i++) {

                Integer val = Integer.parseInt(revisionArr[i]);
                if (val < VALUE_CHECK) {
                    rev = rev + ZERO;
                }
                rev = rev + val;
            }

            return rev;
        } else {
            throw new TranslatorException("Date in revision is not proper: " + date);
        }
    }

    /**
     * Returns the package string.
     *
     * @param pkgArr package array
     * @return package string
     */
    private static String getPkgFrmArr(ArrayList<String> pkgArr) {

        String pkg = EMPTY_STRING;
        int size = pkgArr.size();
        int i = 0;
        for (String member : pkgArr) {
            boolean presenceOfKeyword = JAVA_KEY_WORDS.contains(member);
            if (presenceOfKeyword || member.matches(REGEX_FOR_FIRST_DIGIT)) {
                member = UNDER_SCORE + member;
            }
            pkg = pkg + member;
            if (i != size - 1) {
                pkg = pkg + PERIOD;
            }
            i++;
        }
        return pkg;
    }

    /**
     * Returns package sub name from YANG identifier name.
     *
     * @param name YANG identifier name
     * @return java package sub name as per java rules
     */
    public static String getSubPkgFromName(String name) {

        ArrayList<String> pkgArr = new ArrayList<String>();
        String[] nameArr = name.split(COLAN);

        for (String nameString : nameArr) {
            pkgArr.add(nameString);
        }
        return getPkgFrmArr(pkgArr);
    }

    /**
     * Returns the YANG identifier name as java identifier.
     *
     * @param yangIdentifier identifier in YANG file
     * @return corresponding java identifier
     */
    public static String getCamelCase(String yangIdentifier) {

        String[] strArray = yangIdentifier.split(HYPHEN);
        String camelCase = strArray[0];
        for (int i = 1; i < strArray.length; i++) {
            camelCase = camelCase + strArray[i].substring(0, 1).toUpperCase() + strArray[i].substring(1);
        }
        return camelCase;
    }

    /**
     * Returns the YANG identifier name as java identifier with first letter
     * in caps.
     *
     * @param yangIdentifier identifier in YANG file
     * @return corresponding java identifier
     */
    public static String getCaptialCase(String yangIdentifier) {
        return yangIdentifier.substring(0, 1).toUpperCase() + yangIdentifier.substring(1);
    }

    /**
     * Returns the YANG identifier name as java identifier with first letter
     * in small.
     *
     * @param yangIdentifier identifier in YANG file.
     * @return corresponding java identifier
     */
    public static String getSmallCase(String yangIdentifier) {
        return yangIdentifier.substring(0, 1).toLowerCase() + yangIdentifier.substring(1);
    }

    /**
     * Returns the java Package from package path.
     *
     * @param packagePath package path
     * @return java package
     */
    public static String getJavaPackageFromPackagePath(String packagePath) {
        return packagePath.replace(SLASH, PERIOD);
    }

    /**
     * Returns the directory path corresponding to java package.
     *
     * @param packagePath package path
     * @return java package
     */
    public static String getPackageDirPathFromJavaJPackage(String packagePath) {
        return packagePath.replace(PERIOD, SLASH);
    }
}
