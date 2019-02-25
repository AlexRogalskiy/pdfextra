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
package com.wildbeeslabs.sensiblemetrics.pdfextra.examples.processor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.PhoneExtractingContentHandler;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Phone processor implementation
 */
@Slf4j
@Data
@EqualsAndHashCode
@ToString
public class PhoneProcessor {
    /**
     * Default phone collection {@link Set}
     */
    private final Set<String> phoneNumbers = new HashSet<>();
    /**
     * Default numeric statistics
     */
    private int failedFiles = 0;
    private int successfulFiles = 0;

    /**
     * Processes input path folder {@link Path} using file-based approach and collecting statistics on items found
     *
     * @param folder - initial input path folder {@link Path}
     */
    public void processFolder(final Path folder) {
        try {
            Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) {
                    try {
                        process(file);
                        successfulFiles++;
                    } catch (Exception e) {
                        failedFiles++;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(final Path file, final IOException exc) {
                    failedFiles++;
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            log.error(String.format("ERROR: cannot parse input folder={%s}", folder.toAbsolutePath()));
        }
    }

    /**
     * Processes input path file {@link Path}
     *
     * @param path - initial input path file {@link Path}
     * @throws Exception
     */
    public void process(final Path path) throws Exception {
        final Parser parser = new AutoDetectParser();
        final Metadata metadata = new Metadata();
        final PhoneExtractingContentHandler handler = new PhoneExtractingContentHandler(new BodyContentHandler(), metadata);
        try (InputStream stream = new BufferedInputStream(Files.newInputStream(path))) {
            parser.parse(stream, handler, metadata, new ParseContext());
        }
        final String[] numbers = metadata.getValues("phonenumbers");
        Collections.addAll(getPhoneNumbers(), numbers);
    }
}
