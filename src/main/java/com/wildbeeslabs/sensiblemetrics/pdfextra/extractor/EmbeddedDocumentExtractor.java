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
package com.wildbeeslabs.sensiblemetrics.pdfextra.extractor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.extractor.ParsingEmbeddedDocumentExtractor;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.xml.sax.ContentHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Embedded document extractor {@link ParsingEmbeddedDocumentExtractor}
 *
 * @author Alexander Rogalskiy
 * @version 1.1
 * @since 1.0
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EmbeddedDocumentExtractor extends ParsingEmbeddedDocumentExtractor {
    /**
     * Default parse instance {@link Parser}
     */
    private final Parser parser = new AutoDetectParser();
    /**
     * Default detector instance {@link Detector}
     */
    private final Detector detector = ((AutoDetectParser) parser).getDetector();
    /**
     * Default tika configuration {@link TikaConfig}
     */
    private final TikaConfig config = TikaConfig.getDefaultConfig();

    private final Path outputDir;
    private int fileCount = 0;

    private EmbeddedDocumentExtractor(final Path outputDir, final ParseContext context) {
        super(context);
        this.outputDir = outputDir;
    }

    @Override
    public boolean shouldParseEmbedded(final Metadata metadata) {
        return true;
    }

    @Override
    public void parseEmbedded(final InputStream stream, final ContentHandler handler, final Metadata metadata, boolean outputHtml) throws IOException {
        String name = metadata.get(Metadata.RESOURCE_NAME_KEY);
        if (Objects.isNull(name)) {
            name = "file_" + fileCount++;
        } else {
            name = FilenameUtils.normalize(FilenameUtils.getName(name));
        }

        final MediaType contentType = getDetector().detect(stream, metadata);
        if (name.indexOf('.') == -1 && Objects.nonNull(contentType)) {
            try {
                name += getConfig().getMimeRepository().forName(contentType.toString()).getExtension();
            } catch (MimeTypeException e) {
                log.error(String.format("ERROR: cannot extract mime type extension by input media type={%s}", contentType));
            }
        }
        final Path outputFile = getOutputDir().resolve(name);
        Files.createDirectories(outputFile.getParent());
        Files.copy(stream, outputFile);
    }
}
