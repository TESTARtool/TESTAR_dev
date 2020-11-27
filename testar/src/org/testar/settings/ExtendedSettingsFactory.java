/***************************************************************************************************
 *
 * Copyright (c) 2020 Open Universiteit - www.ou.nl
 * Copyright (c) 2020 Universitat Politecnica de Valencia - www.upv.es
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package org.testar.settings;

import nl.ou.testar.visualvalidation.VisualValidationSettings;
import nl.ou.testar.visualvalidation.ocr.tesseract.TesseractSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ExtendedSettingsFactory {
    private static final ReentrantReadWriteLock _readWriteLock = new ReentrantReadWriteLock(true);
    // Store the created setting container because they old the loaded value.
    private static final List<ExtendedSettingBase<?>> _settingContainers = new ArrayList<>();
    private static String _absolutePath;

    /**
     * Initialize the extended settings factory.
     *
     * @param absolutePath Absolute path for extended settings file.
     */
    public static void Initialize(String absolutePath) {
        _settingContainers.clear();
        _absolutePath = absolutePath;
    }

    /**
     * Save all created extended settings.
     */
    public static void SaveAll() {
        _settingContainers.forEach(ExtendedSettingBase::Save);
    }

    @SuppressWarnings("unchecked")
    static <S extends ExtendedSettingBase<S>> S createSettings(Class<S> settingClass,
                                                               IExtendedSettingDefaultValue<S> defaultFunctor) {
        S setting = (S) _settingContainers.stream()
                .filter(element -> element.getClass() == settingClass)
                .findFirst()
                .orElse(null);

        // If this is the first time create the new container and settings.
        if (setting == null) {
            ExtendedSettingContainer<S> container = new ExtendedSettingContainer<>(
                    new ExtendedSettingFile(_absolutePath, _readWriteLock), settingClass, defaultFunctor);
            setting = (S) container.GetSettings();
            _settingContainers.add(setting);
        }
        return setting;
    }

    public static VisualValidationSettings createVisualValidationSettings() {
        return createSettings(VisualValidationSettings.class, VisualValidationSettings::CreateDefault);
    }

    public static ExampleSetting createTestSetting() {
        return createSettings(ExampleSetting.class, ExampleSetting::CreateDefault);
    }

    public static TesseractSettings createTesseractSetting() {
        return createSettings(TesseractSettings.class, TesseractSettings::CreateDefault);
    }
}
