/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2018-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.plugin;

/**
 * Enumerates possible operating systems supported by Testar.
 */
public enum OperatingSystems {
    /**
     * Possible Operating Systems.
     */
    UNKNOWN(0), WINDOWS(1), LINUX(2), MAC(3), ANDROID(4),
    WINDOWS_7(5), WINDOWS_10(6), WEBDRIVER(7), IOS(8);

    // Internal value of the enum.
    private int value;

    /**
     * Enum constructor.
     * @param value value of the enum.
     */
    OperatingSystems(int value) {
        this.value = value;
    }

    /**
     * String representation of the enumeration type.
     * @return userfriendly name.
     */
    @Override
    public String toString() {
        if (value == 0) {
            return "Unknown";
        } else if (value == 1) {
            return "Windows";
        } else if (value == 2) {
            return "Linux";
        } else if (value == 3) {
            return "Mac";
        } else if (value == 4) {
            return "Android";
        } else if (value == 5) {
            return "Windows 7";
        } else if (value == 6){
            return "Windows 10";
        } else if (value == 7){
            return "WebDriver";
        } else if (value == 8){
            return "iOS";
        } else {
            return super.toString();
        }
    }
}
