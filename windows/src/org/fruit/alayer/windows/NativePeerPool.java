/***************************************************************************************************
*
* Copyright (c) 2013, 2014, 2015, 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
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


/**
 *  @author Sebastian Bauersfeld
 */
package org.fruit.alayer.windows;

public final class NativePeerPool {

	public enum PeerType{ IUnknown; }

	public static final NativePeer InvalidPeer = new InvalidPeer();
	
	public interface NativePeer{
		long access();
		void release();
		boolean valid();
	}
	
	private final static class InvalidPeer implements NativePeer{
		private InvalidPeer(){};
		public long access() { throw new IllegalStateException("InvalidPeer access!"); }
		public void release() { }
		public boolean valid() { return false; }	
	}
	
	public final class StdPeer implements NativePeer{
		private long ptr;
		private StdPeer pred, succ;
		private final PeerType type;

		private StdPeer(StdPeer predecessor, StdPeer successor, long ptr, PeerType type){ 
			this.ptr = ptr;
			succ = successor;
			pred = predecessor;
			this.type = type;
		}
		
		public long access() throws IllegalStateException {
			if(ptr == 0)
				throw new IllegalStateException("Peer has already been released!");
			return ptr;
		}

		public boolean valid(){ return ptr != 0; }

		public void release(){
			if(ptr == 0)
				return;

			switch(type){
			case IUnknown: Windows.IUnknown_Release(ptr);
			}
			ptr = 0;
			
			if(pred != null)
				pred.succ = succ;
			if(succ != null)
				succ.pred = pred;
			if(this == NativePeerPool.this.first)	// we've been the first one, so make the second the new first
				NativePeerPool.this.first = succ;
		}
	}

	private StdPeer first = null;

	public NativePeer register(long ptr, PeerType type){
		if(ptr == 0)
			return InvalidPeer;
		
		StdPeer ret = new StdPeer(null, first, ptr, type);
		if(first != null)
			first.pred = ret;
		first = ret;
		return ret;
	}

	public void release(){
		while(first != null)
			first.release();
	}

	public void finalize(){ release(); }
}
