package org.fruit.alayer.windows;


import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test to demonstrate operarion from UIRootElement object code.
 * Created by florendegier on 26/05/2017.
 */
public class UIARootElementTest {

   private UIARootElement sut = new UIARootElement();

   @Test
   public void at() throws Exception {

      try {
         UIAElement element = sut.at(0.0, 0.0);
         fail("Expected an exception, got " + element);
      } catch (UnsupportedOperationException uoe) {
         assertNotNull("A exception is expected",uoe);
      }

   }

   @Test
   public void visibleAt() throws Exception {
   }

   @Test
   public void visibleAt1() throws Exception {
   }

   @Test
   public void obscuredByChildren() throws Exception {
   }



}
