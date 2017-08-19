/**********************************************************************************************
 *                                                                                            *
 * COPYRIGHT (2017):                                                                          *
 * Universitat Politecnica de Valencia                                                        *
 * Camino de Vera, s/n                                                                        *
 * 46022 Valencia, Spain                                                                      *
 * www.upv.es                                                                                 *
 *                                                                                            * 
 * D I S C L A I M E R:                                                                       *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)          *
 * in the context of the STaQ (Software Testing and Quality) research group: staq.dsic.upv.es *
 * This software is distributed FREE of charge under the TESTAR license, as an open           *
 * source project under the BSD3 license (http://opensource.org/licenses/BSD-3-Clause)        *                                                                                        * 
 *                                                                                            *
 **********************************************************************************************/

package org.fruit.alayer;

import java.util.HashMap;
import java.util.Map;

/**
 * SUT UI soft automation caching.
 * Provides capability to Delay release of cached UI automation elements. 
 * 
 * @author Urko Rueda Molina (alias: urueda)
 *
 */
public abstract class AutomationCache {

	private static final long AUTOMATION_CACHE_AGE_MAX = 32000, // ms
							  AUTOMATION_CACHE_AGE_MIN = 2000; // ms
	private static final int AUTOMATION_CACHE_LIMIT = 256; // number of cached elements

	private class CachedAutomationElement{
		private long cacheAge;
		private long hwnd, cachedElement; // pointers	
		private Rect hwndShape;
		private AWTCanvas scrshot;
		public CachedAutomationElement(long hwnd, long cachedElement, Rect hwndShape, AWTCanvas scrshot){
			this.cacheAge = System.currentTimeMillis();
			this.hwnd = hwnd; this.cachedElement = cachedElement;
			this.hwndShape = hwndShape; this.scrshot = scrshot;
		}
		public long getCacheAge(){ return (System.currentTimeMillis() - this.cacheAge); }
		public long getHwnd(){ return this.hwnd; }
		public long getCachedElement(){ return this.cachedElement; }		
		public Rect getHwndShape(){ return this.hwndShape; }
		public AWTCanvas getScrshot(){ return this.scrshot; }
	}
	
	private int cacheHits = 0, cacheMisses = 0;
	private float SCRSHOT_SIMILARITY_THRESHOLD = Float.MIN_VALUE; // default is disabled
	private transient Map<Long,CachedAutomationElement> automationCache = new HashMap<Long,CachedAutomationElement>(); // hwnd (pointer) x CachedAutomationElement
	
	/**
	 * 
	 */
	public AutomationCache(){
		try {
			String propertyValue = System.getProperty("SCRSHOT_SIMILARITY_THRESHOLD");
			if (propertyValue != null)
				SCRSHOT_SIMILARITY_THRESHOLD = new Float(propertyValue).floatValue();
		} catch (Exception e){
			System.out.println("Automation cache caught exception <" + e.getMessage() + ">");
		}
	}
	
	/**
	 * 
	 * @param sst
	 */
	public void setScreenshotSimilarityThreshold(float sst){
		this.SCRSHOT_SIMILARITY_THRESHOLD = sst;
	}
	
	/**
	 * 
	 * @param hwnd
	 * @return The pointer to the cached automation element. Long.MIN_VALUE otherwise (cache miss).
	 */
	public long getCachedAutomationElement(long hwnd, long pAutomation, long pCacheRequest){
		if (SCRSHOT_SIMILARITY_THRESHOLD == Float.MIN_VALUE){
			//this.cacheMisses++;
			return Long.MIN_VALUE;
		}
		System.out.println("Automation cache SIZE <" + automationCache.size() + "> HITS <" + cacheHits + "> MISSES  <" + cacheMisses + ">");
		for (CachedAutomationElement ac : automationCache.values().toArray(new CachedAutomationElement[automationCache.size()])){
			if (ac.getCacheAge() > AUTOMATION_CACHE_AGE_MAX || isSoftCacheCandidate(ac)) // soft reference implementation
				releaseCachedAutomationElement(ac);
		}
		long uiaPtr = nativeGetAutomationElementFromHandle(pAutomation, hwnd);
		if (uiaPtr == 0){ // failed to retrieve automation element?
			System.out.println("Failed to retrieve automation element - bypassing caching");
			this.cacheMisses++;
			return Long.MIN_VALUE;
		}
		long hwndShape[] = nativeGetAutomationElementBoundingRectangl(uiaPtr, false);
		nativeReleaseAutomationElement(uiaPtr);
		Rect hwndRect = null;
		if (hwndShape != null)
			hwndRect = Rect.fromCoordinates(hwndShape[0], hwndShape[1], hwndShape[2], hwndShape[3]);
		if (hwndRect == null || Rect.area(hwndRect) == 0){
			this.cacheMisses++;
			return Long.MIN_VALUE;
		}
		try {
			AWTCanvas scrshot = AWTCanvas.fromScreenshot(hwndRect, AWTCanvas.StorageFormat.PNG, 1);
			CachedAutomationElement ac = automationCache.get(new Long(hwnd));
			if (ac != null){
				if (ac.getHwndShape().equals(hwndRect) && scrshot.compareImage(ac.getScrshot()) >= SCRSHOT_SIMILARITY_THRESHOLD){
					this.cacheHits++;
					return ac.getCachedElement(); // get from cache
				} else
					releaseCachedAutomationElement(ac); // force cache purge
			}
			// perform caching
			long r = nativeGetAutomationElementFromHandleBuildCache(pAutomation, hwnd, pCacheRequest);
			if (r != 0){ // do not cache if element request failed (r == 0)
				CachedAutomationElement caching = new CachedAutomationElement(hwnd, r, hwndRect, scrshot);
				automationCache.put(new Long(hwnd),caching);
			}
			this.cacheMisses++;
			return r;
		} catch (Exception e) {
			System.out.println("Widget-tree build cache caught exception: " + e.getMessage());
			this.cacheMisses++;
			return Long.MIN_VALUE;
		}
	}	
	
	/**
	 * 
	 * @param ac
	 * @return
	 */
	public boolean isSoftCacheCandidate(CachedAutomationElement ac){
		if (automationCache.size() <= AUTOMATION_CACHE_LIMIT)
			return false;
		return ac.getCacheAge() > AUTOMATION_CACHE_AGE_MIN; // ms
	}
	
	/**
	 * 
	 * @param cachedElement
	 */
	public void releaseCachedAutomationElement(CachedAutomationElement cachedElement){
		automationCache.remove(new Long(cachedElement.getHwnd()));
		nativeReleaseAutomationElement(cachedElement.getCachedElement());
	}
	
	/**
	 * 
	 */
	public synchronized void releaseCachedAutomationElements(){
		for (CachedAutomationElement ac : automationCache.values().toArray(new CachedAutomationElement[automationCache.size()]))
			releaseCachedAutomationElement(ac);
	}
	
	/**
	 * Releases a previously cached automation element.
	 * Requisite: The automation element has been cached and its pointer has not been released.
	 * @param elementPtr
	 */
	public abstract void nativeReleaseAutomationElement(long elementPtr);
	
	/**
	 * Retrieves the pointer to the automation element for a UI.
	 * @param automationPtr
	 * @param hwndPtr
	 */
	public abstract long nativeGetAutomationElementFromHandle(long automationPtr, long hwndPtr);;
		
	/**
	 * 
	 * @param cachedAutomationElementPtr
	 * @param fromCache
	 * @return
	 */
	public abstract long[] nativeGetAutomationElementBoundingRectangl(long cachedAutomationElementPtr, boolean fromCache);

	/**
	 * Builds a cached automation element for the UI and returns a pointer to it. 
	 * @param automationPtr
	 * @param hwndPtr
	 * @param cacheRequestPtr
	 * @return
	 */
	public abstract long nativeGetAutomationElementFromHandleBuildCache(long automationPtr, long hwndPtr, long cacheRequestPtr);

	@Override
	public void finalize(){
		releaseCachedAutomationElements();
	}
	
}
