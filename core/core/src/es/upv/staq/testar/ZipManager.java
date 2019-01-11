/***************************************************************************************************
*
* Copyright (c) 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* 1. Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
* 2. Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
* 3. Neither the name of the copyright holder nor the names of its
* contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
* ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
*******************************************************************************************************/

package es.upv.staq.testar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Memory compression utility for serializable objects.
 *
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class ZipManager {

  private static byte[] convertToBytes(Object object) {
      try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
           ObjectOutput out = new ObjectOutputStream(bos)) {
          out.writeObject(object);
          return bos.toByteArray();
      } catch (IOException e) {
        return null;
      }
  }

  private static Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
    try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
       ObjectInput in = new ObjectInputStream(bis)) {
      return in.readObject();
    }
  }

  public static Object compress(Object o) {
    byte[] bytes = ZipManager.convertToBytes(o);
    if (bytes == null)
      return o;
    try {
      Deflater def = new Deflater(Deflater.BEST_COMPRESSION);
      def.setInput(bytes);
      ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length);
      def.finish();
      byte[] buff = new byte[1024];
      int count;
      while (!def.finished()) {
        count = def.deflate(buff);
        baos.write(buff,0,count);
      }
      baos.close();
      return baos.toByteArray();
    } catch (IOException e) {
      return o;
    }
  }

  public static Object uncompress(byte[] bytes) {
    try {
      Inflater inf = new Inflater();
      inf.setInput(bytes);
      ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length * 1024);
      byte[] buff = new byte[1024];
      int count;
      while (!inf.finished()) {
        count = inf.inflate(buff);
        baos.write(buff,0,count);
      }
      inf.end();
      return convertFromBytes(baos.toByteArray());
    } catch (Exception e) {
      e.printStackTrace();
      return null; // cannot uncompress!
    }
  }

}
