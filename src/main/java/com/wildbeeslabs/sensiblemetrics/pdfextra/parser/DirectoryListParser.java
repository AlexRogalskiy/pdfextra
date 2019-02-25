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

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Directory list parser implementation {@link Parser}
 */
public class DirectoryListParser implements Parser {

    private static final long serialVersionUID = 2717930544410610735L;

    /**
     * DEfault supported file types
     */
    private static Set<MediaType> DEFAULT_SUPPORTED_TYPES = new HashSet<>(Collections.singletonList(MediaType.TEXT_PLAIN));

    /*
     * (non-Javadoc)
     *
     * @see org.apache.tika.parser.Parser#getSupportedTypes(
     * org.apache.tika.parser.ParseContext)
     */
    public Set<MediaType> getSupportedTypes(final ParseContext context) {
        return DEFAULT_SUPPORTED_TYPES;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.tika.parser.Parser#parse(java.io.InputStream,
     * org.xml.sax.ContentHandler, org.apache.tika.metadata.Metadata)
     */
    public void parse(final InputStream is, final ContentHandler handler, final Metadata metadata) throws IOException, SAXException, TikaException {
        this.parse(is, handler, metadata, new ParseContext());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.tika.parser.Parser#parse(java.io.InputStream,
     * org.xml.sax.ContentHandler, org.apache.tika.metadata.Metadata,
     * org.apache.tika.parser.ParseContext)
     */
    public void parse(final InputStream is, final ContentHandler handler, final Metadata metadata, final ParseContext context) throws IOException {
        final List<String> lines = FileUtils.readLines(TikaInputStream.get(is).getFile(), UTF_8);
        for (final String line : lines) {
            final String[] fileToks = line.split("\\s+");
            if (fileToks.length < 8) continue;
            String filePermissions = fileToks[0];
            String numHardLinks = fileToks[1];
            String fileOwner = fileToks[2];
            String fileOwnerGroup = fileToks[3];
            String fileSize = fileToks[4];

            final StringBuilder lastModDate = new StringBuilder();
            lastModDate.append(fileToks[5]);
            lastModDate.append(StringUtils.SPACE);
            lastModDate.append(fileToks[6]);
            lastModDate.append(StringUtils.SPACE);
            lastModDate.append(fileToks[7]);

            final StringBuilder fileName = new StringBuilder();
            for (int i = 8; i < fileToks.length; i++) {
                fileName.append(fileToks[i]);
                fileName.append(StringUtils.SPACE);
            }
            fileName.deleteCharAt(fileName.length() - 1);
            this.addMetadata(metadata, filePermissions, numHardLinks,
                fileOwner, fileOwnerGroup, fileSize,
                lastModDate.toString(), fileName.toString());
        }
    }

    private void addMetadata(final Metadata metadata, final String filePerms,
                             final String numHardLinks, final String fileOwner, final String fileOwnerGroup,
                             final String fileSize, final String lastModDate, final String fileName) {
        metadata.add("FilePermissions", filePerms);
        metadata.add("NumHardLinks", numHardLinks);
        metadata.add("FileOwner", fileOwner);
        metadata.add("FileOwnerGroup", fileOwnerGroup);
        metadata.add("FileSize", fileSize);
        metadata.add("LastModifiedDate", lastModDate);
        metadata.add("Filename", fileName);

        if (filePerms.indexOf("x") != -1 && filePerms.indexOf("d") == -1) {
            if (Objects.nonNull(metadata.get("NumExecutables"))) {
                int numExecs = Integer.valueOf(metadata.get("NumExecutables"));
                numExecs++;
                metadata.set("NumExecutables", String.valueOf(numExecs));
            } else {
                metadata.set("NumExecutables", "1");
            }
        }
    }
}
