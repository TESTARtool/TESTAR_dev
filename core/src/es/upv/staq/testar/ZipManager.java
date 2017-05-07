/*****************************************************************************************
 *                                                                                       *
 * COPYRIGHT (2015):                                                                     *
 * Universitat Politecnica de Valencia                                                   *
 * Camino de Vera, s/n                                                                   *
 * 46022 Valencia, Spain                                                                 *
 * www.upv.es                                                                            *
 *                                                                                       * 
 * D I S C L A I M E R:                                                                  *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)     *
 * in the context of the TESTAR Proof of Concept project:                                *
 *               "UPV, Programa de Prueba de Concepto 2014, SP20141402"                  *
 * This software is distributed FREE of charge under the TESTAR license, as an open      *
 * source project under the BSD3 licence (http://opensource.org/licenses/BSD-3-Clause)   *                                                                                        * 
 *                                                                                       *
 *****************************************************************************************/

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
	    } catch (IOException e){
	    	return null;
	    }
	}

	private static Object convertFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
		try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			 ObjectInput in = new ObjectInputStream(bis)) {
			return in.readObject();
		} 
	}
	
	public static Object compress(Object o){
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
			while (!def.finished()){
				count = def.deflate(buff);
				baos.write(buff,0,count);
			}
			baos.close();
			return baos.toByteArray();
		} catch (IOException e) {
			return o;
		}
	}
	
	public static Object uncompress(byte[] bytes){
		try {
			Inflater inf = new Inflater();
			inf.setInput(bytes);
			ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length * 1024);
			byte[] buff = new byte[1024];
			int count;
			while (!inf.finished()){
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
