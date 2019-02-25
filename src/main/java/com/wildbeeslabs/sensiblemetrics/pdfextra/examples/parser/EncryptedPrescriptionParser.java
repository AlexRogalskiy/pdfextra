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
package com.wildbeeslabs.sensiblemetrics.pdfextra.examples.parser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Collections;
import java.util.Set;

/**
 * Encrypted parser implementation {@link AbstractParser}
 */
@Slf4j
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EncryptedPrescriptionParser extends AbstractParser {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    public static final long serialVersionUID = -7816987249611278541L;
    /**
     * Default prescription media type
     */
    public static final String DEFAULT_PRESCRIPTION_MEDIA_TYPE = "x-prescription+xml";

    /**
     * Default key value {@link Key}
     */
    private final Key key;

    @Override
    public void parse(final InputStream stream, final ContentHandler handler, final Metadata metadata, final ParseContext context) throws IOException, SAXException, TikaException {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, getKey());
            final InputStream decrypted = new CipherInputStream(stream, cipher);
            new PrescriptionParser().parse(decrypted, handler, metadata, context);
        } catch (GeneralSecurityException e) {
            throw new TikaException("Unable to decrypt a digital prescription", e);
        }
    }

    @Override
    public Set<MediaType> getSupportedTypes(ParseContext context) {
        return Collections.singleton(MediaType.application(DEFAULT_PRESCRIPTION_MEDIA_TYPE));
    }
}
