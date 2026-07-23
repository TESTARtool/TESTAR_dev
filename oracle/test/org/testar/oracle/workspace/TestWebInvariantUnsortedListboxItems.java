package org.testar.oracle.workspace;

import java.util.List;

import org.junit.Test;
import org.testar.core.Assert;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;
import org.testar.oracle.Oracle;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;
import org.testar.webdriver.alayer.WdRoles;
import org.testar.webdriver.tag.WdTags;

public class TestWebInvariantUnsortedListboxItems extends WorkspaceOracleTestSupport {

    @Test
    public void test_detection_web_invariant_unsorted_list_items() {
        Oracle oracle = loadWorkspaceOracle("WebInvariantUnsortedListboxItems");
        StateStub state = new StateStub();
        createList(state, "menuid", "Transfer", "Accounts", "Bill Pay");

        List<Verdict> verdicts = oracle.getVerdicts(state);

        Assert.isEquals(1, verdicts.size());
        Verdict verdict = verdicts.get(0);
        Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.WARNING_WEB_INVARIANT_FAULT.getTitle()));
        Assert.isTrue(verdict.info().contains("Detected Unordered List (UL) widget 'menuid'"));
        Assert.isTrue(verdict.info().contains("[Transfer, Accounts, Bill Pay]"));
    }

    @Test
    public void test_undetection_web_invariant_sorted_list_items() {
        Oracle oracle = loadWorkspaceOracle("WebInvariantUnsortedListboxItems");
        StateStub state = new StateStub();
        createList(state, "menuid", "Accounts", "Bill Pay", "Transfer");

        List<Verdict> verdicts = oracle.getVerdicts(state);

        Assert.isEquals(1, verdicts.size());
        Verdict verdict = verdicts.get(0);
        Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.OK.getTitle()));
        Assert.isTrue(verdict.info().equals("No problem detected."));
    }

    @Test
    public void test_detection_web_invariant_unsorted_nested_span_list_items() {
        Oracle oracle = loadWorkspaceOracle("WebInvariantUnsortedListboxItems");
        StateStub state = new StateStub();
        createDownshiftList(
            state,
            "downshift-1-menu",
            "Brazil",
            "Serbia",
            "Poland",
            "Oman"
        );

        List<Verdict> verdicts = oracle.getVerdicts(state);

        Assert.isEquals(1, verdicts.size());
        Verdict verdict = verdicts.get(0);
        Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.WARNING_WEB_INVARIANT_FAULT.getTitle()));
        Assert.isTrue(verdict.info().contains("[Brazil, Serbia, Poland, Oman]"));
    }

    private WidgetStub createList(StateStub state, String id, String... values) {
        WidgetStub list = new WidgetStub();
        list.set(Tags.Role, WdRoles.WdUL);
        list.set(WdTags.WebId, id);
        list.set(WdTags.WebAriaRole, "listbox");
        state.addChild(list);
        list.setParent(state);

        for (String value : values) {
            WidgetStub listItem = new WidgetStub();
            listItem.set(Tags.Role, WdRoles.WdLI);
            listItem.set(WdTags.WebTextContent, value);
            list.addChild(listItem);
            listItem.setParent(list);
        }

        return list;
    }

    private WidgetStub createDownshiftList(StateStub state, String id, String... values) {
        WidgetStub list = new WidgetStub();
        list.set(Tags.Role, WdRoles.WdUL);
        list.set(WdTags.WebId, id);
        list.set(WdTags.WebCssClasses, "widget-combobox-menu-list");
        list.set(WdTags.WebAriaRole, "listbox");
        state.addChild(list);
        list.setParent(state);

        for (int index = 0; index < values.length; index++) {
            WidgetStub listItem = new WidgetStub();
            listItem.set(Tags.Role, WdRoles.WdLI);
            listItem.set(WdTags.WebId, "downshift-1-item-" + index);
            listItem.set(WdTags.WebCssClasses, "widget-combobox-item");
            listItem.set(WdTags.WebAriaRole, "option");
            list.addChild(listItem);
            listItem.setParent(list);

            WidgetStub caption = new WidgetStub();
            caption.set(Tags.Role, WdRoles.WdSPAN);
            caption.set(WdTags.WebCssClasses, "widget-combobox-caption-text");
            caption.set(WdTags.WebTextContent, values[index]);
            listItem.addChild(caption);
            caption.setParent(listItem);
        }

        return list;
    }

    @Test
    public void test_ignores_unsorted_footer_link_lists_without_listbox_role() {
        Oracle oracle = loadWorkspaceOracle("WebInvariantUnsortedListboxItems");
        StateStub state = new StateStub();
        WidgetStub footerList = new WidgetStub();
        footerList.set(Tags.Role, WdRoles.WdUL);
        footerList.set(WdTags.WebCssClasses, "widget-html-element footer-links-list");
        state.addChild(footerList);
        footerList.setParent(state);

        addListItemWithLink(footerList, "page.com");
        addListItemWithLink(footerList, "Terms of Use");
        addListItemWithLink(footerList, "Privacy Policy");
        addListItemWithLink(footerList, "Accessibility");
        addListItemWithLink(footerList, "EU Digital Services Act Notice");

        List<Verdict> verdicts = oracle.getVerdicts(state);

        Assert.isEquals(1, verdicts.size());
        Verdict verdict = verdicts.get(0);
        Assert.isTrue(verdict.verdictSeverityTitle().equals(Verdict.Severity.OK.getTitle()));
    }

    private void addListItemWithLink(WidgetStub list, String value) {
        WidgetStub listItem = new WidgetStub();
        listItem.set(Tags.Role, WdRoles.WdLI);
        list.addChild(listItem);
        listItem.setParent(list);

        WidgetStub link = new WidgetStub();
        link.set(Tags.Role, WdRoles.WdA);
        link.set(WdTags.WebTextContent, value);
        link.set(WdTags.WebCssClasses, "footer-link");
        listItem.addChild(link);
        link.setParent(listItem);
    }
}
