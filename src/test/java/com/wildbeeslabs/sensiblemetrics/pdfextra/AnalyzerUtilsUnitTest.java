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
package com.wildbeeslabs.sensiblemetrics.pdfextra;

import com.wildbeeslabs.sensiblemetrics.pdfextra.utils.AnalyzerUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.language.detect.LanguageResult;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

/**
 * Analyzer utils unit test
 *
 * @author Alexander Rogalskiy
 * @version 1.1
 * @since 1.0
 */
@Slf4j
@Data
@EqualsAndHashCode
@ToString
public class AnalyzerUtilsUnitTest {

    @Test
    @DisplayName("Test document media type by default detector")
    public void whenUsingDetector_thenDocumentTypeIsReturned() throws IOException {
        // given
        final String fileName = "src/test/java/resources/content/tika.pdf";
        final File file = new File(fileName);
        assertTrue("File should exist", file.exists());

        try (final InputStream stream = new BufferedInputStream(new FileInputStream(file))) {
            // when
            final MediaType mediaType = AnalyzerUtils.detectDocTypeByDetector(stream);

            // then
            assertEquals("application/pdf", String.valueOf(mediaType));
            assertNotEquals("application/octet-stream", String.valueOf(mediaType.toString()));
        }
    }

    @Test
    @DisplayName("Test document media type by default facade")
    public void whenUsingFacade_thenDocumentTypeIsReturned() throws IOException {
        // given
        final String fileName = "src/test/java/resources/content/tika.docx";
        final File file = new File(fileName);
        assertTrue("File should exist", file.exists());

        try (final InputStream stream = new FileInputStream(file)) {
            // when
            String mediaType = AnalyzerUtils.detectDocTypeByFacade(stream);

            // then
            assertEquals("application/x-tika-ooxml", mediaType);
            assertNotEquals("application/pdf", mediaType);
        }
    }

    @Test
    @DisplayName("Test document content by default parser")
    public void whenUsingParser_thenContentIsReturned() throws IOException, TikaException, SAXException {
        // given
        final String fileName = "src/test/java/resources/content/tika.docx";
        final File file = new File(fileName);
        assertTrue("File should exist", file.exists());

        try (final InputStream stream = new FileInputStream(file)) {
            // when
            final String content = AnalyzerUtils.getContentByParser(stream);

            // then
            assertThat(content, containsString("Apache Tika - a content analysis toolkit"));
            assertThat(content, containsString("detects and extracts metadata and text"));
        }
    }

    @Test
    @DisplayName("Test document content by default facade")
    public void whenUsingFacade_thenContentIsReturned() throws IOException, TikaException {
        // given
        final String fileName = "src/test/java/resources/content/tika.docx";
        final File file = new File(fileName);
        assertTrue("File should exist", file.exists());

        try (final InputStream stream = new FileInputStream(file)) {
            // when
            final String content = AnalyzerUtils.getContentByFacade(stream);

            // then
            assertThat(content, containsString("Apache Tika - a content analysis toolkit"));
            assertThat(content, containsString("detects and extracts metadata and text"));
        }
    }

    @Test
    @DisplayName("Test document meta data by default parser")
    public void whenUsingParser_thenMetadataIsReturned() throws IOException, TikaException, SAXException {
        // given
        final String fileName = "src/test/java/resources/content/tika.xlsx";
        final File file = new File(fileName);
        assertTrue("File should exist", file.exists());

        try (final InputStream stream = new FileInputStream(file)) {
            // when
            final Metadata metadata = AnalyzerUtils.getMetadataByParser(stream);

            // then
            assertEquals("org.apache.tika.parser.DefaultParser", metadata.get("X-Parsed-By"));
            assertEquals("Microsoft Office User", metadata.get("Author"));
        }
    }

    @Test
    @DisplayName("Test document meta data by default facade")
    public void whenUsingFacade_thenMetadataIsReturned() throws IOException {
        // given
        final String fileName = "src/test/java/resources/content/tika.xlsx";
        final File file = new File(fileName);
        assertTrue("File should exist", file.exists());

        try (final InputStream stream = new FileInputStream(file)) {
            // when
            final Metadata metadata = AnalyzerUtils.getMetadataByFacade(stream);

            // then
            assertEquals("org.apache.tika.parser.DefaultParser", metadata.get("X-Parsed-By"));
            assertEquals("Microsoft Office User", metadata.get("Author"));
        }
    }

    @Test(expected = IllegalStateException.class)
    @DisplayName("Test document language by default facade")
    public void whenUsingFacade_thenLanguageIsReturned() throws IOException, TikaException {
        // given
        final String fileName = "src/test/java/resources/content/tika.xlsx";
        final File file = new File(fileName);
        assertTrue("File should exist", file.exists());

        try (final InputStream stream = new FileInputStream(file)) {
            // when
            final String content = AnalyzerUtils.getContentByFacade(stream);

            final LanguageDetector languageDetector = LanguageDetector.getDefaultLanguageDetector();
            languageDetector.loadModels();
            final List<LanguageResult> languageResultList = languageDetector.detectAll(content);

            assertThat(languageResultList, hasSize(10));
            assertEquals("en", languageResultList.get(0).getLanguage());
        }
    }
}
