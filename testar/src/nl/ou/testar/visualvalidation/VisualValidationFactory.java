package nl.ou.testar.visualvalidation;

import org.testar.settings.ExtendedSettingsFactory;

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
