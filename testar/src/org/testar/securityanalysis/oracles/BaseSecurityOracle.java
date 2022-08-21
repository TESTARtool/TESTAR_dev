package org.testar.securityanalysis.oracles;

import org.openqa.selenium.devtools.DevTools;
import org.testar.monkey.alayer.*;
import org.testar.securityanalysis.SecurityResultWriter;

public class BaseSecurityOracle {
    protected SecurityResultWriter securityResultWriter;

    public BaseSecurityOracle(SecurityResultWriter securityResultWriter)
    {
        this.securityResultWriter = securityResultWriter;
    }

    public void addListener(DevTools devTools)
    {
        /** Add listeners **/
    }

    public Verdict getVerdict()
    {
        /** ORACLE **/
        return Verdict.OK;
    }
}
