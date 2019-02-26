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
package com.wildbeeslabs.sensiblemetrics.pdfextra.exporter;

import com.openhtmltopdf.css.constants.IdentValue;
import com.openhtmltopdf.pdfboxout.PdfBoxFontResolver;
import com.openhtmltopdf.pdfboxout.PdfBoxRenderer;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.slf4j.Slf4jLogger;
import com.openhtmltopdf.util.XRLog;
import com.wildbeeslabs.sensiblemetrics.pdfextra.exception.GeneralException;
import com.wildbeeslabs.sensiblemetrics.pdfextra.templater.Templater;
import com.wildbeeslabs.sensiblemetrics.pdfextra.utils.Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * PDF exporter implementation
 */
@Slf4j
public class PdfExporter {

    private Templater templater;

    private String serverPort;

    public byte[] export(final InputStream template, final Map<String, Object> arguments) {
        XRLog.setLoggerImpl(new Slf4jLogger());

        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();

            String resultHtml = this.templater.transform(template, arguments);
            byte[] colorProfile = Utils.resourceBytes("srgb_profile.icm");

            final PdfBoxRenderer renderer = new PdfRendererBuilder()
                .usePdfVersion(1.4f)
                .usePdfAConformance(null)
                .useColorProfile(colorProfile)
                .defaultTextDirection(PdfRendererBuilder.TextDirection.LTR)
                .withHtmlContent(resultHtml, "http://localhost:" + serverPort + "/")
                .toStream(out)
                .buildPdfRenderer();

            PdfBoxFontResolver fontResolver = renderer.getFontResolver();

            byte[] fontBytesArial = Utils.resourceBytes("arial.ttf");
            byte[] fontBytesTahoma = Utils.resourceBytes("tahomar.ttf");
            byte[] fontBytesTahomaBold = Utils.resourceBytes("tahomabd.ttf");
            byte[] fontBytesTahomaItalic = Utils.resourceBytes("verdanait.ttf");

            fontResolver.addFont(() -> new ByteArrayInputStream(fontBytesArial), "Ariel", null, null, false);
            fontResolver.addFont(() -> new ByteArrayInputStream(fontBytesTahoma), "Tahoma", null, null, false);
            fontResolver.addFont(() -> new ByteArrayInputStream(fontBytesTahomaBold), "Tahoma", 700, null, false);
            fontResolver.addFont(() -> new ByteArrayInputStream(fontBytesTahomaItalic), "Tahoma", null, IdentValue.ITALIC, false);

            renderer.layout();
            renderer.createPDF();

            return out.toByteArray();
        } catch (IOException ex) {
            throw new GeneralException(ex);
        }
    }

    public void setTemplater(Templater templater) {
        this.templater = templater;
    }
}
