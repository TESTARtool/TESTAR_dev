package org.fruit.alayer;


import org.junit.AfterClass;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

/**
 * Test to validate the operation of the Tag class.
 * Created by florendegier on 28/04/2017.
 */
public class TagTest {

   @AfterClass
   public static void cleanup() {

      File file = new File("dummy.dat");
      if (!file.delete()) {
         System.out.println("File could not be removed");
      }
   }

   @Test
   public void from() throws Exception {

      Tag<Boolean> tag1 = Tag.from("Boolean", Boolean.class);
      assertNotNull("Tag should return a proper value", tag1);

      assertEquals("The name of the tag shall be identical to the provided name",
         "Boolean", tag1.name());


      Tag<Boolean> tag2 = Tag.from("Boolean", Boolean.class);
      assertEquals("There should be one object", tag1, tag2);

      assertEquals("Type should be a boolean", Boolean.class, tag2.type());

   }

   @Test
   public void isOneOf() throws Exception {

      Tag<String> tag1 = Tag.from("firstString", String.class);
      Tag<String> tag2 = Tag.from("secondString", String.class);
      Tag<String> tag3 = Tag.from("thirdString", String.class);

      assertFalse("tag3 should be another tag", tag3.isOneOf(tag1, tag2));
      assertTrue("tag2 is one the tags", tag2.isOneOf(tag1, tag2));

   }

   @Test
   public void testWriteReadData() {
      try (FileOutputStream fos = new FileOutputStream("dummy.dat")) {
         ObjectOutputStream oos = new ObjectOutputStream(fos);
         oos.writeObject(Tag.from("firstString", String.class));
         oos.writeObject(Tag.from("secondString", String.class));
         oos.writeObject(Tag.from("firstString", String.class));
         oos.flush();
         fos.close();
      } catch (IOException e) {
         fail("It should be possible to work with dummy.dat");
      }

      try (FileInputStream fis = new FileInputStream("dummy.dat")) {
         ObjectInputStream ois = new ObjectInputStream(fis);
         Object obj = ois.readObject();
         assertTrue("A Tag shall be read", obj instanceof Tag<?>);
         fis.close();
      } catch (IOException | ClassNotFoundException e) {
         fail("It should be possible to read dummy.dat and it should return a String");
      }
   }

   @Test
   public void testNotEqual() {
      Tag<String> tag1 = Tag.from("firstString", String.class);
      Tag<String> tag2 = Tag.from("secondString", String.class);
      Tag<Boolean> tag3 = Tag.from("firstString", Boolean.class);

      assertTrue("Tag shall be equal to its self", tag1.equals(tag1));
      assertFalse("Tag shall not be equal to null", tag1.equals(null));
      assertFalse("Tag shall not be equal when the name differs", tag1.equals(tag2));
      assertFalse("Tag shall not be equeal when the type differs", tag1.equals(tag3));

   }

}