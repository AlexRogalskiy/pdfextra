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

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.sax.BodyContentHandler;
import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.xml.sax.SAXException;

import java.io.*;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Directory list parser unit test
 *
 * @author Alexander Rogalskiy
 * @version 1.1
 * @since 1.0
 */
@Slf4j
@Data
@EqualsAndHashCode
@ToString
public class DirectoryListParserTest {

    @Test
    @DisplayName("Test parse the output of input directory and counts the number of files and the number of executables")
    public void whenUsingParser_thenDirectoryListIsReturned() throws IOException, TikaException, SAXException {
        // given
        final String sourcePath = "src/test/java/resources/content/ls";
        final File file = new File(sourcePath);
        assertTrue("File should exist", file.exists());
        assertTrue("File should be a directory", file.isFile());
        assertTrue("File should have read-access permissions", file.canRead());

        try (final InputStream stream = new BufferedInputStream(new FileInputStream(file))) {
            // when
            final DirectoryListParser parser = new DirectoryListParser();
            final Metadata metadata = new Metadata();
            parser.parse(stream, new BodyContentHandler(), metadata);

            // then
            assertThat(metadata.getValues("Filename").length, IsEqual.equalTo(8));
            //assertThat(metadata.get("NumExecutables"), IsEqual.equalTo(0));
        }
    }
}
