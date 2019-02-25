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
package com.wildbeeslabs.sensiblemetrics.pdfextra.parser;

import org.apache.commons.io.IOUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.XHTMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Document area parser implementation {@link Parser}
 */
public class DocumentAreaParser implements Parser {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = -2356647405087933468L;

    /*
     * (non-Javadoc)
     *
     * @see org.apache.tika.parser.Parser#getSupportedTypes(
     * org.apache.tika.parser.ParseContext)
     */
    public Set<MediaType> getSupportedTypes(final ParseContext context) {
        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(MediaType.TEXT_PLAIN)));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.tika.parser.Parser#parse(java.io.InputStream,
     * org.xml.sax.ContentHandler, org.apache.tika.metadata.Metadata)
     */
    public void parse(final InputStream is, final ContentHandler handler, final Metadata metadata) throws IOException, SAXException, TikaException {
        parse(is, handler, metadata, new ParseContext());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.tika.parser.Parser#parse(java.io.InputStream,
     * org.xml.sax.ContentHandler, org.apache.tika.metadata.Metadata,
     * org.apache.tika.parser.ParseContext)
     */
    public void parse(final InputStream is, final ContentHandler handler, final Metadata metadata, final ParseContext context) throws IOException, SAXException, TikaException {
        final File deployArea = new File(IOUtils.toString(is, StandardCharsets.UTF_8));
        final File[] versions = deployArea.listFiles(pathname -> !pathname.getName().startsWith("current"));

        final XHTMLContentHandler xhtml = new XHTMLContentHandler(handler, metadata);
        xhtml.startDocument();
        for (final File v : versions) {
            if (isSymlink(v)) continue;
            xhtml.startElement("a", "href", v.toURI().toURL().toExternalForm());
            xhtml.characters(v.getName());
            xhtml.endElement("a");
        }
    }

    /**
     * Returns binary flag based on input file instance {@link File}
     *
     * @param file - initial input file instance {@link File}
     * @return true - if file is a symlink, false - otherwise
     * @throws IOException
     */
    private boolean isSymlink(final File file) throws IOException {
        return !file.getAbsolutePath().equals(file.getCanonicalPath());
    }
}
