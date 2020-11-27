package nl.ou.testar.visualvalidation;

import org.testar.settings.ExtendedSettingsFactory;

public class VisualValidationFactory {

    public static VisualValidationManager createVisualValidator() {
        VisualValidationManager visualValidator;
        VisualValidationSettings visualValidation = ExtendedSettingsFactory.createVisualValidationSettings();

        if (visualValidation.enabled) {
            visualValidator = new VisualValidator(visualValidation);
        } else {
            visualValidator = new DummyVisualValidator();
        }

        return visualValidator;
    }
}
