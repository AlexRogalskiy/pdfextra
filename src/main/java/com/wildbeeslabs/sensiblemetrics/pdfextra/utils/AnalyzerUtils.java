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

import com.google.common.collect.ImmutableMap;
import com.wildbeeslabs.sensiblemetrics.pdfextra.model.DocumentInfo;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.tika.Tika;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.*;
import org.apache.tika.parser.html.HtmlMapper;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.parser.html.IdentityHtmlMapper;
import org.apache.tika.parser.txt.TXTParser;
import org.apache.tika.parser.xml.XMLParser;
import org.apache.tika.sax.*;
import org.apache.tika.sax.xpath.Matcher;
import org.apache.tika.sax.xpath.MatchingContentHandler;
import org.apache.tika.sax.xpath.XPathParser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.*;
import java.net.URL;
import java.nio.CharBuffer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Analyzer utilities implementation
 */
@Slf4j
@UtilityClass
public class AnalyzerUtils {

    /**
     * Default maximum text chunk size
     */
    public static final int DEFAULT_MAX_TEXT_CHUNK_SIZE = 40;

    /**
     * Returns document media type {@link MediaType} by input stream {@link InputStream}
     *
     * @param stream - initial input stream {@link InputStream}
     * @return document media type {@link MediaType}
     * @throws IOException
     */
    public static MediaType detectDocTypeByDetector(final InputStream stream) throws IOException {
        final Detector detector = new DefaultDetector();
        final Metadata metadata = new Metadata();
        return detector.detect(stream, metadata);
    }

    /**
     * Returns document type {@link String} by input stream {@link InputStream}
     *
     * @param stream - initial input stream {@link InputStream}
     * @return document type {@link String}
     * @throws IOException
     */
    public static String detectDocTypeByFacade(final InputStream stream) throws IOException {
        final Tika tika = new Tika();
        return tika.detect(stream);
    }

    /**
     * Returns text content by input stream {@link InputStream} and {@link AutoDetectParser} parser
     *
     * @param stream - initial input stream {@link InputStream}
     * @return text content {@link String}
     * @throws IOException
     * @throws TikaException
     * @throws SAXException
     */
    public static String getContentByParser(final InputStream stream, final ContentHandler handler) throws IOException, TikaException, SAXException {
        final Parser autoDetectParser = new AutoDetectParser();
        final Metadata metadata = new Metadata();
        final ParseContext context = new ParseContext();
        autoDetectParser.parse(stream, handler, metadata, context);
        return handler.toString();
    }

    /**
     * Returns text content by input stream {@link InputStream}
     *
     * @param stream - initial input stream {@link InputStream}
     * @return text content {@link String}
     * @throws IOException
     * @throws TikaException
     */
    public static String getContentByFacade(final InputStream stream) throws IOException, TikaException {
        final Tika tika = new Tika();
        return tika.parseToString(stream);
    }

    /**
     * Returns text content by input stream {@link InputStream}
     *
     * @param stream    - initial input stream {@link InputStream}
     * @param chunkSize - initial chunk buffer size
     * @return text content {@link String}
     * @throws IOException
     */
    public static String getContentByChunks(final InputStream stream, int chunkSize) throws IOException {
        final StringBuffer stringBuffer = new StringBuffer();
        final Tika tika = new Tika();
        try (final Reader reader = tika.parse(stream)) {
            char[] buffer = new char[chunkSize];
            int n = reader.read(buffer);
            while (n != -1) {
                stringBuffer.append(CharBuffer.wrap(buffer, 0, n));
                n = reader.read(buffer);
            }
        }
        return stringBuffer.toString();
    }

    /**
     * Returns meta data {@link Metadata} by input stream {@link InputStream} and {@link AutoDetectParser} parser
     *
     * @param stream - initial input stream {@link InputStream}
     * @return meta data {@link Metadata}
     * @throws IOException
     * @throws SAXException
     * @throws TikaException
     */
    public static Metadata getMetadataByParser(final InputStream stream) throws IOException, SAXException, TikaException {
        final Parser autoDetectParser = new AutoDetectParser();
        final ContentHandler handler = new BodyContentHandler();
        final Metadata metadata = new Metadata();
        final ParseContext context = new ParseContext();
        autoDetectParser.parse(stream, handler, metadata, context);
        return metadata;
    }

    /**
     * Returns meta data {@link Metadata} by input stream {@link InputStream}
     *
     * @param stream - initial input stream {@link InputStream}
     * @return meta data {@link Metadata}
     * @throws IOException
     */
    public static Metadata getMetadataByFacade(final InputStream stream) throws IOException {
        final Tika tika = new Tika();
        final Metadata metadata = new Metadata();
        tika.parse(stream, metadata);
        return metadata;
    }

    /**
     * Returns html text by input stream {@link InputStream}
     *
     * @param stream - initial input stream {@link InputStream}
     * @return parsed html text by input stream {@link InputStream}
     * @throws IOException
     * @throws SAXException
     * @throws TikaException
     */
    public static String parseToHTML(final InputStream stream) throws IOException, SAXException, TikaException {
        final ContentHandler handler = new ToXMLContentHandler();
        final AutoDetectParser autoDetectParser = new AutoDetectParser();
        final Metadata metadata = new Metadata();
        autoDetectParser.parse(stream, handler, metadata);
        return handler.toString();
    }

    /**
     * Returns html body text by input stream {@link InputStream}
     *
     * @param stream - initial input stream {@link InputStream}
     * @return parsed html text by input stream {@link InputStream}
     * @throws IOException
     * @throws SAXException
     * @throws TikaException
     */
    public static String parseBodyToHTML(final InputStream stream) throws IOException, SAXException, TikaException {
        final ContentHandler handler = new BodyContentHandler(new ToXMLContentHandler());
        final AutoDetectParser autoDetectParser = new AutoDetectParser();
        final Metadata metadata = new Metadata();
        autoDetectParser.parse(stream, handler, metadata);
        return handler.toString();
    }

    /**
     * Returns html text by input stream {@link InputStream} and xpath identifier {@link String}
     *
     * @param stream - initial input stream {@link InputStream}
     * @param xpath  - initial xpath identifier {@link String} ("/xhtml:html/xhtml:body/xhtml:div/descendant::node()")
     * @return parsed html text by input stream {@link InputStream}
     * @throws IOException
     * @throws SAXException
     * @throws TikaException
     */
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

    /**
     * Returns collection of file strings {@link List} by input stream {@link InputStream} with default chunk size
     *
     * @param stream - initial input stream {@link InputStream}
     * @return collection of file strings {@link List}
     * @throws IOException
     * @throws SAXException
     * @throws TikaException
     */
    public static List<String> parseToPlainTextChunks(final InputStream stream) throws IOException, SAXException, TikaException {
        return parseToPlainTextChunks(stream, DEFAULT_MAX_TEXT_CHUNK_SIZE);
    }

    /**
     * Returns collection of file strings {@link List} by input stream {@link InputStream} and max chunk size
     *
     * @param stream       - initial input stream {@link InputStream}
     * @param maxChunkSize - initial input maximum chunk size
     * @return collection of file strings {@link List}
     * @throws IOException
     * @throws SAXException
     * @throws TikaException
     */
    public static List<String> parseToPlainTextChunks(final InputStream stream, int maxChunkSize) throws IOException, SAXException, TikaException {
        final List<String> chunks = new ArrayList<>();
        chunks.add(StringUtils.EMPTY);
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

    /**
     * Returns document info {@link DocumentInfo} by input file name {@link String}
     *
     * @param fileName - initial input file name {@link String}
     * @return document info {@link DocumentInfo}
     * @throws IOException
     * @throws TikaException
     */
    public static DocumentInfo getDocumentInfo(final String fileName) throws IOException, TikaException {
        final Tika tika = new Tika();
        final Metadata metadata = new Metadata();
        final File file = new File(fileName);
        try (final InputStream stream = new FileInputStream(file)) {
            final String content = tika.parseToString(stream, metadata);
            return DocumentInfo
                .builder()
                .name(metadata.get(Metadata.RESOURCE_NAME_KEY))
                .content(content)
                .created(metadata.getDate(TikaCoreProperties.CREATED))
                .build();
        }
    }

    /**
     * Returns file content by input file name {@link String} and content handler {@link ContentHandler}
     *
     * @param filename - initial input file name {@link String}
     * @param handler  - initial input content handler {@link ContentHandler}
     * @return file content
     * @throws Exception
     */
    public static String parseFileStream(final String filename, final ContentHandler handler) throws Exception {
        try (final InputStream stream = new FileInputStream(new File(filename))) {
            //final ContentHandler handler = new BodyContentHandler();//new DefaultHandler();
            return getContentByParser(stream, handler);
        }
    }

    /**
     * Returns url content by input address name {@link String} and content handler {@link ContentHandler}
     *
     * @param address - initial input address name {@link String}
     * @return address content
     * @throws Exception
     */
    public static String parseURLStream(final String address, final ContentHandler handler) throws Exception {
        try (final InputStream stream = new GZIPInputStream(new URL(address).openStream())) {
            return getContentByParser(stream, handler);
        }
    }

    /**
     * Returns file content by input file name {@link String} and content handler {@link ContentHandler}
     *
     * @param filename - initial input file name {@link String}
     * @return file content
     * @throws Exception
     */
    public static String parseTikaInputStream(final String filename, final ContentHandler handler) throws Exception {
        try (final InputStream stream = TikaInputStream.get(Paths.get(filename))) {
            return getContentByParser(stream, handler);
        }
    }

    /**
     * Returns file instance {@link File} by input file name {@link String}
     *
     * @param filename - initial input file name {@link String}
     * @return file instance {@link File}
     * @throws Exception
     */
    public static File getFileByName(final String filename) throws Exception {
        try (final InputStream stream = TikaInputStream.get(Paths.get(filename))) {
            final TikaInputStream tikaInputStream = TikaInputStream.get(stream);
            return tikaInputStream.getFile();
        }
    }

    /**
     * Returns html content by input stream {@link InputStream} and {@link ContentHandler} handler
     *
     * @param stream  - initial input stream {@link InputStream}
     * @param handler - initial input content handler {@link ContentHandler}
     * @return html content
     * @throws Exception
     */
    public static String parseByHtml(final InputStream stream, final ContentHandler handler) throws Exception {
        final Metadata metadata = new Metadata();
        final ParseContext context = new ParseContext();
        final Parser parser = new HtmlParser();
        parser.parse(stream, handler, metadata, context);
        return handler.toString();
    }

    /**
     * Returns html content by input stream {@link InputStream} and {@link ContentHandler} handler
     *
     * @param stream  - initial input stream {@link InputStream}
     * @param handler - initial input content handler {@link ContentHandler}
     * @return html content
     * @throws Exception
     */
    public static String parseByCompositeHtml(final InputStream stream, final ContentHandler handler) throws Exception {
        final ParseContext context = new ParseContext();
        final Map<MediaType, Parser> parsersByType = new ImmutableMap.Builder<MediaType, Parser>().
            put(MediaType.parse("text/html"), new HtmlParser()).
            put(MediaType.parse("application/xml"), new XMLParser())
            .build();

        final CompositeParser parser = new CompositeParser();
        parser.setParsers(parsersByType);
        parser.setFallback(new TXTParser());

        final Metadata metadata = new Metadata();
        metadata.set(Metadata.CONTENT_TYPE, "text/html");
        parser.parse(stream, handler, metadata, context);
        return handler.toString();
    }

    /**
     * Stores content of input stream {@link InputStream} to output file
     *
     * @param stream   - initial input stream {@link InputStream}
     * @param filename - initial output file name {@link String}
     * @throws Exception
     */
    public static void storeByParser(final InputStream stream, final String filename) throws Exception {
        final Metadata metadata = new Metadata();
        final ParseContext context = new ParseContext();
        final Parser autoDetectParser = new AutoDetectParser();
        final LinkContentHandler linkCollector = new LinkContentHandler();
        try (final OutputStream output = new FileOutputStream(new File(filename))) {
            final ContentHandler handler = new TeeContentHandler(new BodyContentHandler(output), linkCollector);
            autoDetectParser.parse(stream, handler, metadata, context);
        }
    }

    /**
     * Returns content of input stream {@link InputStream} by {@link ContentHandler} handler and locale {@link Locale}
     *
     * @param stream  - initial input stream {@link InputStream}
     * @param handler - initial input content handler {@link ContentHandler}
     * @param locale  - initial locale instance {@link Locale}
     * @throws Exception
     */
    public static String parseByLocale(final InputStream stream, final ContentHandler handler, final Locale locale) throws Exception {
        final Metadata metadata = new Metadata();
        final Parser autoDetectParser = new AutoDetectParser();
        final ParseContext context = new ParseContext();
        context.set(Locale.class, locale);
        autoDetectParser.parse(stream, handler, metadata, context);
        return handler.toString();
    }

    /**
     * Returns html content of input stream {@link InputStream} by {@link ContentHandler} handler
     *
     * @param stream  - initial input stream {@link InputStream}
     * @param handler - initial input content handler {@link ContentHandler}
     * @throws Exception
     */
    public static String testHtmlMapper(final InputStream stream, final ContentHandler handler) throws Exception {
        final Metadata metadata = new Metadata();
        final Parser autoDetectParser = new AutoDetectParser();
        final ParseContext context = new ParseContext();
        context.set(HtmlMapper.class, new IdentityHtmlMapper());
        autoDetectParser.parse(stream, handler, metadata, context);
        return handler.toString();
    }


    /**
     * Returns html content of input stream {@link InputStream} by {@link ContentHandler} handler
     *
     * @param stream  - initial input stream {@link InputStream}
     * @param handler - initial input content handler {@link ContentHandler}
     * @throws Exception
     */
    public static void parseByCustomDecorator(final InputStream stream, final ContentHandler handler, final Parser parser) throws Exception {
        final Metadata metadata = new Metadata();
        final Parser autoDetectParser = new AutoDetectParser();
        final ParseContext context = new ParseContext();
        context.set(Parser.class, new ParserDecorator(parser));
        autoDetectParser.parse(stream, handler, metadata, context);
    }

    /**
     * Returns collection of supported meta data {@link Metadata} by input file name {@link String}
     *
     * @param filename - initial input file name {@link String}
     * @return collection of supported meta data {@link Metadata}
     * @throws IOException
     * @throws SAXException
     * @throws TikaException
     */
    public List<Metadata> parseByRecursiveParser(final String filename) throws IOException, SAXException, TikaException {
        final Parser autoDetectParser = new AutoDetectParser();
        final ContentHandlerFactory factory = new BasicContentHandlerFactory(BasicContentHandlerFactory.HANDLER_TYPE.HTML, -1);
        final RecursiveParserWrapperHandler handler = new RecursiveParserWrapperHandler(factory);
        final Metadata metadata = new Metadata();
        metadata.set(Metadata.RESOURCE_NAME_KEY, filename);
        final ParseContext context = new ParseContext();
        try (final InputStream stream = new FileInputStream(new File(filename))) {
            autoDetectParser.parse(stream, handler, metadata, context);
            return handler.getMetadataList();
        }
    }
}
