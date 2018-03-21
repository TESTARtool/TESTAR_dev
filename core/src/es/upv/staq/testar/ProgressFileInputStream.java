/*****************************************************************************************
 *                                                                                       *
 * COPYRIGHT (2017):                                                                     *
 * Universitat Politecnica de Valencia                                                   *
 * Camino de Vera, s/n                                                                   *
 * 46022 Valencia, Spain                                                                 *
 * www.upv.es                                                                            *
 *                                                                                       * 
 * D I S C L A I M E R:                                                                  *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)     *
 * in the context of the TESTAR development team (www.testar.org):                       *
 * This graph project is distributed FREE of charge under the TESTAR license, as an open *
 * source project under the BSD3 licence (http://opensource.org/licenses/BSD-3-Clause)   *
 *                                                                                       * 
 *****************************************************************************************/

package es.upv.staq.testar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Adds progress feedback.
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public class ProgressFileInputStream  extends FileInputStream {

	private String fileName;
	private long fileLength;
	
	private float progress, lastProgress;
	
	public ProgressFileInputStream(File file) throws FileNotFoundException {
		super(file);
		this.fileName = file.getName();
		this.fileLength = file.length();
		this.lastProgress = 0f;
	}
	
	@Override
	public int available() throws IOException{
		int remainingBytes = super.available();
		printStreamProgress(remainingBytes);
		return remainingBytes;
	}

	private void printStreamProgress(int remaining){
    	progress = (float)(this.fileLength - remaining) / (float)this.fileLength * 100f;
    	if (progress - lastProgress > 1f){
    		lastProgress = progress;
    		System.out.println("<" + this.fileName + "> remaining bytes = <" + remaining + "> progress (%) = <" + progress + ">");
    	}		
	}

}
