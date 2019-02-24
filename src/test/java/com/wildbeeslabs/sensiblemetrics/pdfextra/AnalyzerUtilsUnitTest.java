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
import org.apache.tika.metadata.Metadata;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

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
    @DisplayName("Test docx media type by default detector")
    public void whenUsingDetector_thenDocumentTypeIsReturned() throws IOException {
        // given
        final String fileName = "content/tika.docx";
        final InputStream stream = this.getClass().getClassLoader().getResourceAsStream(fileName);

        // when
        final String mediaType = AnalyzerUtils.detectDocTypeUsingDetector(stream);

        // then
        assertEquals("application/pdf", mediaType);
        stream.close();
    }

    @Test
    @DisplayName("Test docx media type by default facade")
    public void whenUsingFacade_thenDocumentTypeIsReturned() throws IOException {
        // given
        final String fileName = "content/tika.docx";
        final InputStream stream = this.getClass().getClassLoader().getResourceAsStream(fileName);

        // when
        String mediaType = AnalyzerUtils.detectDocTypeUsingFacade(stream);

        // then
        assertEquals("application/pdf", mediaType);
        stream.close();
    }

    @Test
    @DisplayName("Test docx content by default parser")
    public void whenUsingParser_thenContentIsReturned() throws IOException, TikaException, SAXException {
        // given
        final String fileName = "content/tika.docx";
        final InputStream stream = this.getClass().getClassLoader().getResourceAsStream(fileName);

        // when
        final String content = AnalyzerUtils.extractContentUsingParser(stream);

        // then
        assertThat(content, containsString("Apache Tika - a content analysis toolkit"));
        assertThat(content, containsString("detects and extracts metadata and text"));
        stream.close();
    }

    @Test
    @DisplayName("Test docx content by default facade")
    public void whenUsingFacade_thenContentIsReturned() throws IOException, TikaException {
        // given
        final String fileName = "content/ttika.docx";
        final InputStream stream = this.getClass().getClassLoader().getResourceAsStream(fileName);

        // when
        final String content = AnalyzerUtils.extractContentUsingFacade(stream);

        // then
        assertThat(content, containsString("Apache Tika - a content analysis toolkit"));
        assertThat(content, containsString("detects and extracts metadata and text"));
        stream.close();
    }

    @Test
    @DisplayName("Test xlsx meta data by default parser")
    public void whenUsingParser_thenMetadataIsReturned() throws IOException, TikaException, SAXException {
        // given
        final String fileName = "content/tika.xlsx";
        final InputStream stream = this.getClass().getClassLoader().getResourceAsStream(fileName);

        // when
        final Metadata metadata = AnalyzerUtils.extractMetadatatUsingParser(stream);

        // then
        assertEquals("org.apache.tika.parser.DefaultParser", metadata.get("X-Parsed-By"));
        assertEquals("Microsoft Office User", metadata.get("Author"));
        stream.close();
    }

    @Test
    @DisplayName("Test xlsx meta data by default facade")
    public void whenUsingFacade_thenMetadataIsReturned() throws IOException, TikaException {
        // given
        final String fileName = "content/tika.xlsx";
        final InputStream stream = this.getClass().getClassLoader().getResourceAsStream(fileName);

        // when
        final Metadata metadata = AnalyzerUtils.extractMetadatatUsingFacade(stream);

        // then
        assertEquals("org.apache.tika.parser.DefaultParser", metadata.get("X-Parsed-By"));
        assertEquals("Microsoft Office User", metadata.get("Author"));
        stream.close();
    }
}
