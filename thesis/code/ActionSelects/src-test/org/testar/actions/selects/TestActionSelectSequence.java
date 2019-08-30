package org.testar.actions.selects;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestActionSelectSequence {

  @Test
  public void testProbabilities() {
    // Init test
    ActionSelectSequence actionSelectSequence = new ActionSelectSequence();
    List<Integer> probabilities = new ArrayList<>();
    probabilities.add(100);
    probabilities.add(90);
    probabilities.add(75);
    probabilities.add(40);
    probabilities.add(25); 
    actionSelectSequence.setProbabilities(probabilities);
    
    // Test and verify
    assertThat(actionSelectSequence.getProbability(), is(100));
    assertThat(actionSelectSequence.getProbability(), is(90));
    assertThat(actionSelectSequence.getProbability(), is(75));
    assertThat(actionSelectSequence.getProbability(), is(40));
    assertThat(actionSelectSequence.getProbability(), is(25));

    assertThat(actionSelectSequence.getProbability(), is(25));
    assertThat(actionSelectSequence.getProbability(), is(25));
    assertThat(actionSelectSequence.getProbability(), is(25));
    assertThat(actionSelectSequence.getProbability(), is(25));
}
}
