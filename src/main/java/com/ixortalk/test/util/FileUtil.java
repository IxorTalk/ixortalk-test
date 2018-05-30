/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-present IxorTalk CVBA
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.ixortalk.test.util;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileUtil {

    public static String jsonFile(final String fileName) {
        return fileContent("json", fileName);
    }

    public static String fileContent(final String dirPrefix, final String fileName) {
        String name = dirPrefix + "/" + fileName;
        return fileContent(name);
    }

    public static String fileContent(final String fileName) {
        try {
            return IOUtils.toString(FileUtil.class.getClassLoader().getResourceAsStream(fileName), UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error reading file " + fileName + ": " + e.getMessage(), e);
        }
    }

    public static File file(final String name) {
        try {
            return Paths.get(FileUtil.class.getClassLoader().getResource(name).toURI()).toFile();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error reading file " + name + ": " + e.getMessage(), e);
        }
    }
}
