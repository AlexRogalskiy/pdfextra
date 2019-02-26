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
package com.wildbeeslabs.sensiblemetrics.pdfextra.templater;

import com.wildbeeslabs.sensiblemetrics.pdfextra.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Wrapper class around Velocity engine templater.
 */
public class Templater {

    private VelocityEngine engine;

    public String transform(final InputStream template, final Map<String, Object> arguments) {
        Utils.notNull(template, () -> new IllegalArgumentException("template"));
        final BufferedReader reader = new BufferedReader(new InputStreamReader(template, StandardCharsets.UTF_8));
        return transform(reader, arguments);
    }

    public String transform(final String input, final Map<String, Object> arguments) {
        Utils.notNull(input, () -> new IllegalArgumentException("input"));
        return transform(new StringReader(input), arguments);
    }

    protected String transform(final Reader reader, final Map<String, Object> arguments) {
        Utils.notNull(reader, () -> new IllegalArgumentException("reader"));
        final StringWriter result = new StringWriter();
        final VelocityContext velocityContext = new VelocityContext(arguments);
        this.engine.evaluate(velocityContext, result, StringUtils.EMPTY, reader);
        return result.toString();
    }

    public void setEngine(final VelocityEngine engine) {
        this.engine = engine;
    }
}
