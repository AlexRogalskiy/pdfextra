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
package com.wildbeeslabs.sensiblemetrics.pdfextra.examples.detector;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.detect.Detector;
import org.apache.tika.detect.XmlRootExtractor;
import org.apache.tika.io.LookaheadInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Objects;

import static com.wildbeeslabs.sensiblemetrics.pdfextra.examples.parser.EncryptedPrescriptionParser.DEFAULT_PRESCRIPTION_MEDIA_TYPE;

/**
 * Encrypted prescription detector implementation {@link Detector}
 */
@Slf4j
@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class EncryptedPrescriptionDetector implements Detector {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = -3121249891046922883L;

    /**
     * Default key value {@link Key}
     */
    private final Key key;

    public MediaType detect(final InputStream stream, final Metadata metadata) throws IOException {
        MediaType type = MediaType.OCTET_STREAM;
        try (final  InputStream lookahead = new LookaheadInputStream(stream, 1024)) {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);

            final InputStream decrypted = new CipherInputStream(lookahead, cipher);
            final QName name = new XmlRootExtractor().extractRootElement(decrypted);
            if (Objects.nonNull(name) && "http://example.com/xpd".equals(name.getNamespaceURI()) && "prescription".equals(name.getLocalPart())) {
                type = MediaType.application(DEFAULT_PRESCRIPTION_MEDIA_TYPE);
            }
        } catch (GeneralSecurityException e) {
            // unable to decrypt, fall through
        }
        return type;
    }
}
