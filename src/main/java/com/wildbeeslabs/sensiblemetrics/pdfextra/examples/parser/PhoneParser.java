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
package com.wildbeeslabs.sensiblemetrics.pdfextra.examples.parser;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

/**
 * Phone parser implementation {@link Parser}
 */
@Slf4j
@Data
@EqualsAndHashCode
@ToString
public class PhoneParser implements Parser {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    public static final long serialVersionUID = -5091769487801619635L;

    /**
     * Default phone media type
     */
    public static final String DEFAULT_PHONE_MEDIA_TYPE = "x-phone+xml";

    @Override
    public void parse(final InputStream stream, final ContentHandler handler, final Metadata metadata, final ParseContext context) throws IOException, SAXException, TikaException {
        parse(stream, handler, metadata, new ParseContext());
    }

    @Override
    public Set<MediaType> getSupportedTypes(final ParseContext context) {
        return Collections.singleton(MediaType.application(DEFAULT_PHONE_MEDIA_TYPE));
    }
}
