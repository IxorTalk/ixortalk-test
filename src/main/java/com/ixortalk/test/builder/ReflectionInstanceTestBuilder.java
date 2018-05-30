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
package com.ixortalk.test.builder;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;

public abstract class ReflectionInstanceTestBuilder<T> {

    public final T build() {
        boolean wasAccessible = false;
        Constructor<T> constructor = null;
        try {
            constructor = getInstanceClass().getDeclaredConstructor();
            wasAccessible = constructor.isAccessible();
            constructor.setAccessible(true);
            T instance = constructor.newInstance();
            setFields(instance);
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Error creating test instance via reflection: " + e.getMessage(), e);
        } finally {
            if (constructor != null) {
                constructor.setAccessible(wasAccessible);
            }
        }
    }

    private Class<T> getInstanceClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public abstract void setFields(T instance);
}
