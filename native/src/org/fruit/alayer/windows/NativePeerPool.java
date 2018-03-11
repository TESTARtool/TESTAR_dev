/******************************************************************************************
 * COPYRIGHT:                                                                             *
 * Universitat Politecnica de Valencia 2013                                               *
 * Camino de Vera, s/n                                                                    *
 * 46022 Valencia, Spain                                                                  *
 * www.upv.es                                                                             *
 *                                                                                        * 
 * D I S C L A I M E R:                                                                   *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)      *
 * in the context of the european funded FITTEST project (contract number ICT257574)      *
 * of which the UPV is the coordinator. As the sole developer of this source code,        *
 * following the signed FITTEST Consortium Agreement, the UPV should decide upon an       *
 * appropriate license under which the source code will be distributed after termination  *
 * of the project. Until this time, this code can be used by the partners of the          *
 * FITTEST project for executing the tasks that are outlined in the Description of Work   *
 * (DoW) that is annexed to the contract with the EU.                                     *
 *                                                                                        * 
 * Although it has already been decided that this code will be distributed under an open  *
 * source license, the exact license has not been decided upon and will be announced      *
 * before the end of the project. Beware of any restrictions regarding the use of this    *
 * work that might arise from the open source license it might fall under! It is the      *
 * UPV's intention to make this work accessible, free of any charge.                      *
 *****************************************************************************************/

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
		public long access() { throw new IllegalStateException(); }
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