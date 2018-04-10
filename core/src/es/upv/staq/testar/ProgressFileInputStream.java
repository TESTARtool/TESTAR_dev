/***************************************************************************************************
*
* Copyright (c) 2017 Universitat Politecnica de Valencia - www.upv.es
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
    		System.out.println("[" + getClass().getSimpleName() + "] <" + this.fileName + "> remaining bytes = <" + remaining + "> progress (%) = <" + progress + ">");
    	}		
	}

}
