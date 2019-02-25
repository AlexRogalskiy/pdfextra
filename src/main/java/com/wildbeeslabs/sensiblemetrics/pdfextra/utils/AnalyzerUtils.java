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
package com.wildbeeslabs.sensiblemetrics.pdfextra.utils;

import jdk.internal.joptsimple.internal.Strings;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.ContentHandlerDecorator;
import org.apache.tika.sax.ToXMLContentHandler;
import org.apache.tika.sax.XHTMLContentHandler;
import org.apache.tika.sax.xpath.Matcher;
import org.apache.tika.sax.xpath.MatchingContentHandler;
import org.apache.tika.sax.xpath.XPathParser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Analyzer utils implementation
 */
@Slf4j
@UtilityClass
public class AnalyzerUtils {

    public static MediaType detectDocTypeByDetector(final InputStream stream) throws IOException {
        final Detector detector = new DefaultDetector();
        final Metadata metadata = new Metadata();
        return detector.detect(stream, metadata);
    }

    public static String detectDocTypeByFacade(final InputStream stream) throws IOException {
        final Tika tika = new Tika();
        return tika.detect(stream);
    }

    public static String getContentByParser(final InputStream stream) throws IOException, TikaException, SAXException {
        final Parser parser = new AutoDetectParser();
        final ContentHandler handler = new BodyContentHandler();
        final Metadata metadata = new Metadata();
        final ParseContext context = new ParseContext();
        parser.parse(stream, handler, metadata, context);
        return handler.toString();
    }

    public static String getContentByFacade(final InputStream stream) throws IOException, TikaException {
        final Tika tika = new Tika();
        return tika.parseToString(stream);
    }

    public static Metadata getMetadataByParser(final InputStream stream) throws IOException, SAXException, TikaException {
        final Parser parser = new AutoDetectParser();
        final ContentHandler handler = new BodyContentHandler();
        final Metadata metadata = new Metadata();
        final ParseContext context = new ParseContext();
        parser.parse(stream, handler, metadata, context);
        return metadata;
    }

    public static Metadata getMetadataByFacade(final InputStream stream) throws IOException {
        final Tika tika = new Tika();
        final Metadata metadata = new Metadata();
        tika.parse(stream, metadata);
        return metadata;
    }

    public static String parseToHTML(final InputStream stream) throws IOException, SAXException, TikaException {
        final ContentHandler handler = new ToXMLContentHandler();
        final AutoDetectParser parser = new AutoDetectParser();
        final Metadata metadata = new Metadata();
        parser.parse(stream, handler, metadata);
        return handler.toString();
    }

    public static String parseBodyToHTML(final InputStream stream) throws IOException, SAXException, TikaException {
        final ContentHandler handler = new BodyContentHandler(new ToXMLContentHandler());
        final AutoDetectParser parser = new AutoDetectParser();
        final Metadata metadata = new Metadata();
        parser.parse(stream, handler, metadata);
        return handler.toString();
    }

    // "/xhtml:html/xhtml:body/xhtml:div/descendant::node()"
    public static String parseHTMLByXPath(final InputStream stream, final String xpath) throws IOException, SAXException, TikaException {
        // Only get things under html -> body -> div (class=header)
        final XPathParser xhtmlParser = new XPathParser("xhtml", XHTMLContentHandler.XHTML);
        final Matcher divContentMatcher = xhtmlParser.parse(xpath);
        final ContentHandler handler = new MatchingContentHandler(new ToXMLContentHandler(), divContentMatcher);
        final AutoDetectParser parser = new AutoDetectParser();
        final Metadata metadata = new Metadata();
        parser.parse(stream, handler, metadata);
        return handler.toString();
    }

    public static List<String> parseToPlainTextChunks(final InputStream stream, int maxChunkSize) throws IOException, SAXException, TikaException {
        final List<String> chunks = new ArrayList<>();
        chunks.add(Strings.EMPTY);
        final ContentHandlerDecorator handler = new ContentHandlerDecorator() {
            @Override
            public void characters(char[] ch, int start, int length) {
                final String lastChunk = chunks.get(chunks.size() - 1);
                final String thisStr = new String(ch, start, length);
                if (lastChunk.length() + length > maxChunkSize) {
                    chunks.add(thisStr);
                } else {
                    chunks.set(chunks.size() - 1, lastChunk + thisStr);
                }
            }
        };
        final AutoDetectParser parser = new AutoDetectParser();
        final Metadata metadata = new Metadata();
        parser.parse(stream, handler, metadata);
        return chunks;
    }
}
