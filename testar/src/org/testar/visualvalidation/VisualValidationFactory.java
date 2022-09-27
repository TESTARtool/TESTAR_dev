package org.testar.visualvalidation;

import org.testar.extendedsettings.ExtendedSettingsFactory;

public class VisualValidationFactory {

    public static VisualValidationManager createVisualValidator(String protocol) {
        VisualValidationManager visualValidator;
        VisualValidationSettings visualValidation = ExtendedSettingsFactory.createVisualValidationSettings();
        visualValidation.protocol = protocol;

        if (visualValidation.enabled) {
            visualValidator = new VisualValidator(visualValidation);
        } else {
            visualValidator = new DummyVisualValidator();
        }

        return visualValidator;
    }
}
