package org.fruit.alayer.windows;

/**
 * Used to support design mode, debugging, and testing scenarios of UWP apps.
 */
public enum ActivateOptions {

    /**
     * Possible Operating Systems.
     */
    AO_NONE(0), AO_DESIGNMODE(1), AO_NOERRORUI(2), AO_NOSPLASHSCREEN(3), AO_PRELAUNCH(33554432);


    // Internal value of the enum.
    private int value;

    public int getValue() {
        return value;
    }


    /**
     * Enum constructor.
     * @param value value of the enum.
     */
    ActivateOptions(int value) {
        this.value = value;
    }


    /**
     * String representation of the enumeration type.
     * @return userfriendly name.
     */
    @Override
    public String toString() {
        if (value == 0) {
            return "None";
        } else if (value == 1) {
            return "Design Mode";
        } else if (value == 2) {
            return "No Error UI";
        } else if (value == 3) {
            return "No Splash Screen";
        } else if (value == 4) {
            return "Pre-launch";
        } else {
            return super.toString();
        }
    }


}