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
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.xml.ElementMetadataHandler;
import org.apache.tika.parser.xml.XMLParser;
import org.apache.tika.sax.TeeContentHandler;
import org.xml.sax.ContentHandler;

import java.util.Collections;
import java.util.Set;

/**
 * Prescription parser implementation {@link XMLParser}
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PrescriptionParser extends XMLParser {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = -6729646995818754304L;

    /**
     * Default prescription media type
     */
    public static final String DEFAULT_PRESCRIPTION_MEDIA_TYPE = "x-prescription+xml";

    @Override
    protected ContentHandler getContentHandler(final ContentHandler handler, final Metadata metadata, final ParseContext context) {
        String xpd = "http://example.com/2011/xpd";
        final ContentHandler doctor = new ElementMetadataHandler(xpd, "doctor", metadata, "xpd:doctor");
        final ContentHandler patient = new ElementMetadataHandler(xpd, "patient", metadata, "xpd:patient");
        return new TeeContentHandler(super.getContentHandler(handler, metadata, context), doctor, patient);
    }

    @Override
    public Set<MediaType> getSupportedTypes(final ParseContext context) {
        return Collections.singleton(MediaType.application(DEFAULT_PRESCRIPTION_MEDIA_TYPE));
    }
}
