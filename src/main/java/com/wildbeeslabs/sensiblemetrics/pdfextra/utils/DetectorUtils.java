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

import com.wildbeeslabs.sensiblemetrics.pdfextra.examples.detector.EncryptedPrescriptionDetector;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.detect.CompositeDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.mime.MimeTypesFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

/**
 * Detector utilities implementation
 */
@Slf4j
@UtilityClass
public class DetectorUtils {

    /**
     * Default tika mimitypes configuration
     */
    public static final String DEFAULT_MIME_TYPES_CONFIG = "/org/apache/tika/mime/tika-mimetypes.xml";

    /**
     * Returns mime info of input content file by default collection of mime types
     *
     * @param name - initial input content file name
     * @return mime info of input content file
     * @throws Exception
     */
    public static String getMimeInfo(final String name) throws Exception {
        final Tika tika = new Tika(MimeTypesFactory.create(DEFAULT_MIME_TYPES_CONFIG));
        return tika.detect(name);
    }

    /**
     * Returns mime info of input content file by collection of prescription types
     *
     * @param name     - initial input content file name
     * @param metaName - initial input meta data name
     * @return mime info of input content file
     * @throws Exception
     */
    public static String getMimeInfoByCustomDetector(final String name, final String metaName) throws Exception {
        final Detector detector = MimeTypesFactory.create(DEFAULT_MIME_TYPES_CONFIG);
        final Detector customDetector = new Detector() {

            /**
             * Default explicit serialVersionUID for interoperability
             */
            private static final long serialVersionUID = -5420638839201540749L;

            public MediaType detect(final InputStream input, final Metadata metadata) {
                final String type = metadata.get(metaName);
                if (Objects.nonNull(type)) {
                    return MediaType.parse(type);
                }
                return MediaType.OCTET_STREAM;
            }
        };
        final Tika tika = new Tika(new CompositeDetector(customDetector, detector));
        return tika.detect(name);
    }

    /**
     * Returns mime info of input content file by collection of prescription types
     *
     * @param sourceType - initial input prescription file name (e.g. "file:///path/to/prescription-type.xml")
     * @param fileName   - initial input content file name (e.g. "/path/to/prescription.xpd")
     * @return mime info of input content file
     * @throws Exception
     */
    public static String getMimeInfo(final String sourceType, final String fileName) throws Exception {
        final MimeTypes typeDatabase = MimeTypesFactory.create(new URL(sourceType));
        final Tika tika = new Tika(typeDatabase);
        return tika.detect(fileName);
    }

    /**
     * Returns mime info of input content file by collection of prescription types
     *
     * @param sourceType - initial input prescription file name (e.g. "file:///path/to/prescription-type.xml")
     * @param fileName   - initial input content file name (e.g. "/path/to/prescription.xpd")
     * @return mime info of input content file
     * @throws Exception
     */
    public static String getMimeInfoByEncryptedDetector(final String sourceType, final String fileName) throws Exception {
        final MimeTypes typeDatabase = MimeTypesFactory.create(new URL(sourceType));
        final Tika tika = new Tika(new CompositeDetector(typeDatabase, new EncryptedPrescriptionDetector(null)));
        return tika.detect(fileName);
    }
}
