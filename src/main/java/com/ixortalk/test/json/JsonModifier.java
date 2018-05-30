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
package com.ixortalk.test.json;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonModifier {

    public static String modifiedJsonString(
            Object objectToModifyJson,
            ObjectMapper objectMapper,
            String fieldToModify,
            String value) throws IOException {
        return modifiedJsonString(objectToModifyJson, objectMapper, "/", fieldToModify, value);
    }

    public static String modifiedJsonString(
            Object objectToModifyJson,
            ObjectMapper objectMapper,
            String parentJsonPointer,
            String fieldToModify,
            String value) throws IOException {
        JsonNode rootNode = objectMapper.valueToTree(objectToModifyJson);
        JsonNode jsonNode = rootNode.at(parentJsonPointer);
        ((ObjectNode) jsonNode).put(fieldToModify, value);
        return rootNode.toString();
    }
}
