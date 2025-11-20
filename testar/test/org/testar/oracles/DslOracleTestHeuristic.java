package org.testar.oracles;

import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

public class DslOracleTestHeuristic {

    private StateStub pageState;

    private WidgetStub headerAccountServices;
    private WidgetStub headerAccountSiblings;

    private WidgetStub headerUsers;

    @Before
    public void setup() {
        pageState = new StateStub();
        pageState.set(WdTags.WebTitle, "Parabank | Accounts");

        headerAccountServices = new StateStub();
        headerAccountServices.set(Tags.Role, WdRoles.WdH1);
        headerAccountServices.set(WdTags.WebTextContent, "Account Services");

        headerAccountSiblings = new StateStub();
        headerAccountSiblings.set(Tags.Role, WdRoles.WdH1);
        headerAccountSiblings.set(WdTags.WebTextContent, "Account Siblings");

        headerUsers = new StateStub();
        headerUsers.set(Tags.Role, WdRoles.WdH2);
        headerUsers.set(WdTags.WebTextContent, "Users");

        // Tree:
        // pageState (title: "Parabank | Accounts")
        //   |-- headerAccountServices (H1: "Account Services")
        //   |     |-- headerUsers (H2: "Users")
        //   |-- headerAccountSiblings (H1: "Account Siblings")
        pageState.addChild(headerAccountServices);
        headerAccountServices.setParent(pageState);

        pageState.addChild(headerAccountSiblings);
        headerAccountSiblings.setParent(pageState);

        headerAccountServices.addChild(headerUsers);
        headerUsers.setParent(headerAccountServices);
    }

    @Test
    public void appliesWhenConstraintsNull_rootIsWholePage() {        
        DslOracle dslOracle = new TestDslOracle(null);
        assertTrue(dslOracle.getConstraintWidgetOrState(pageState).equals(pageState));
    }

    @Test
    public void appliesWhenConstraintsEmpty_rootIsWholePage() {        
        DslOracle dslOracle = new TestDslOracle(Collections.emptyList());
        assertTrue(dslOracle.getConstraintWidgetOrState(pageState).equals(pageState));
    }

    @Test
    public void matchesStateThenAccountServicesThenUsers_rootIsAccountServicesHeader() {
        DslOracle dslOracle = new TestDslOracle(java.util.Arrays.asList("Parabank", "Account Services", "Users"));
        assertTrue(dslOracle.getConstraintWidgetOrState(pageState).equals(headerAccountServices));
    }

    @Test
    public void matchesStateThenSiblingHeaderThenUsers_rootIsAccountServicesHeader() {
        DslOracle dslOracle = new TestDslOracle(java.util.Arrays.asList("Parabank", "Account Siblings", "Users"));
        assertTrue(dslOracle.getConstraintWidgetOrState(pageState).equals(headerAccountServices));
    }

    @Test
    public void matchesStateThenAccountServices_rootIsWholePage() {
        DslOracle dslOracle = new TestDslOracle(java.util.Arrays.asList("Parabank", "Account Services"));
        assertTrue(dslOracle.getConstraintWidgetOrState(pageState).equals(pageState));
    }

    @Test
    public void matchesAccountServicesOnly_rootIsWholePage() {
        DslOracle dslOracle = new TestDslOracle(java.util.Arrays.asList("Account Services"));
        assertTrue(dslOracle.getConstraintWidgetOrState(pageState).equals(pageState));
    }

    @Test
    public void matchesUsersOnly_rootIsAccountServicesHeader() {
        DslOracle dslOracle = new TestDslOracle(java.util.Arrays.asList("Users"));
        assertTrue(dslOracle.getConstraintWidgetOrState(pageState).equals(headerAccountServices));
    }

    @Test
    public void matchesStateThenUsers_rootIsAccountServicesHeader() {
        DslOracle dslOracle = new TestDslOracle(java.util.Arrays.asList("Parabank", "Users"));
        assertTrue(dslOracle.getConstraintWidgetOrState(pageState).equals(headerAccountServices));
    }

    @Test
    public void matchesRegexOnStateTitle_rootIsAccountServicesHeader() {
        DslOracle dslOracle = new TestDslOracle(java.util.Arrays.asList("Parabank | .*", "Account Services", "Users"));
        assertTrue(dslOracle.getConstraintWidgetOrState(pageState).equals(headerAccountServices));
    }

    @Test
    public void matchesRegexOnHeader_rootIsAccountServicesHeader() {
        DslOracle dslOracle = new TestDslOracle(java.util.Arrays.asList("Parabank", "Account .*", "Users"));
        assertTrue(dslOracle.getConstraintWidgetOrState(pageState).equals(headerAccountServices));
    }

    @Test
    public void doesNotMatchWhenOrderIsWrong_rootIsEmptyStateStub() {
        DslOracle dslOracle = new TestDslOracle(java.util.Arrays.asList("Parabank", "Users", "Account Services"));
        assertTrue(dslOracle.getConstraintWidgetOrState(pageState) instanceof StateStub);
    }

    @Test
    public void doesNotMatchWhenSecondConstraintMissing_rootIsEmptyStateStub() {
        DslOracle dslOracle = new TestDslOracle(java.util.Arrays.asList("Parabank", "Nothing"));
        assertTrue(dslOracle.getConstraintWidgetOrState(pageState) instanceof StateStub);
    }

    public static class TestDslOracle extends DslOracle {

        private List<String> testSectionConstraints;

        public TestDslOracle(List<String> testSectionConstraints) {
            this.testSectionConstraints = testSectionConstraints;
            initialize();
        }

        @Override
        public void initialize() {
            addSectionConstraint(testSectionConstraints);
        }

        @Override
        public String getMessage() {
            return "TestDslOracle";
        }

        @Override
        public Verdict getVerdict(State state) {
            return Verdict.OK;
        }
    }
}
