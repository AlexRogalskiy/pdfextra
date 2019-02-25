/*
 * The MIT License
 *
 * Copyright 2019 WildBees Labs, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.wildbeeslabs.sensiblemetrics.pdfextra.helpers;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Assert utility helpers
 *
 * @author Alexander Rogalskiy
 * @version 1.1
 * @since 1.0
 */
public class AssertHelpers {

    /**
     * Checks whether initial content contains input value
     *
     * @param value   - initial input value to be checked
     * @param content - initial input content to check by
     */
    public static void assertContains(final String value, final String content) {
        assertTrue(String.format("Should have found {%s} in {%s}", content.contains(value), value, content), content.contains(value));
    }

    /**
     * Checks whether initial content not contains input value
     *
     * @param value   - initial input value to be checked
     * @param content - initial input content to check by
     */
    public static void assertNotContains(final String value, final String content) {
        assertFalse(String.format("Should not have found {%s} in {%s}", content.contains(value), value, content), content.contains(value));
    }
}
