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

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.language.translate.MicrosoftTranslator;
import org.apache.tika.language.translate.Translator;

import java.io.IOException;

/**
 * Translator utilities implementation
 */
@Slf4j
@UtilityClass
public class TranslatorUtils {

    /**
     * Returns translated text content by input text, source and target languages and translator instance {@link Translator}
     *
     * @param text           - initial input text to be translated
     * @param sourceLanguage - initial source language
     * @param targetLanguage - initial target language
     * @param translator     - initial translator instance {@link Translator}
     * @return translated text content
     */
    public static String translate(final String text, final String sourceLanguage, final String targetLanguage, final Translator translator) {
        try {
            return translator.translate(text, sourceLanguage, targetLanguage);
        } catch (Exception e) {
            log.error("ERROR: cannot translate text={%s} from source language={%s} to target language={%s}", text, sourceLanguage, targetLanguage);
        }
        return null;
    }

    /**
     * Returns translated text content by input text, source and target languages and {@link MicrosoftTranslator} translator with translator ID and secret credentials
     *
     * @param text             - initial input text to be translated
     * @param sourceLanguage   - initial source language
     * @param targetLanguage   - initial target language
     * @param translatorId     - initial {@link MicrosoftTranslator} translator identifier
     * @param translatorSecret - initial {@link MicrosoftTranslator} translator secret
     * @return translated text content
     */
    public static String translateByMicrosoft(final String text, final String sourceLanguage, final String targetLanguage, final String translatorId, final String translatorSecret) {
        final MicrosoftTranslator translator = new MicrosoftTranslator();
        translator.setId(translatorId);
        translator.setSecret(translatorSecret);
        return translate(text, sourceLanguage, targetLanguage, translator);
    }

    /**
     * Returns language description in ISO format by input text
     *
     * @param text - initial input text to be translated
     * @return language description in ISO format
     * @throws IOException
     */
    public static String detectLanguage(final String text) throws IOException {
        final LanguageDetector languageDetector = LanguageDetector.getDefaultLanguageDetector();
        languageDetector.loadModels();
        return languageDetector.detect(text).getLanguage();
    }
}
