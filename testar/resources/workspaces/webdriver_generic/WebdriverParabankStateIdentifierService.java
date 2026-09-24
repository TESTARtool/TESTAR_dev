/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */
import org.testar.core.Assert;
import org.testar.core.service.StateIdentifierService;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.webdriver.tag.WdTags;

public final class WebdriverParabankStateIdentifierService implements StateIdentifierService {

    private static final String PAYEE_PHONE_NUMBER_NAME = "payee.phoneNumber";

    private final StateIdentifierService delegate;

    public WebdriverParabankStateIdentifierService(StateIdentifierService delegate) {
        this.delegate = Assert.notNull(delegate);
    }

    @Override
    public State identifyState(State state) {
        Assert.notNull(state);

        for (Widget widget : state) {
            // For the bill pay phone number widget, the WebId property is dynamic, so always use WebName instead.
            normalizePayeePhoneNumberId(widget);
        }

        return delegate.identifyState(state);
    }

    private void normalizePayeePhoneNumberId(Widget widget) {
        if (PAYEE_PHONE_NUMBER_NAME.equals(widget.get(WdTags.WebName, ""))) {
            widget.set(WdTags.WebId, PAYEE_PHONE_NUMBER_NAME);
        }
    }
}
