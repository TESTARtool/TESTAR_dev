package org.testar.coverage;

import java.util.HashMap;
import java.util.Map;

/**
 * The Class CoverageCounters.
 */
public class CoverageData {
	
	/** The coverage counters. */
	private Map<CoverageCounter, Counters> coverageCounters = new HashMap<>();

	private int sequence;
	private int actions;

	public CoverageData(int sequence, int actions) {
	    this.sequence = sequence;
	    this.actions = actions;
	}

    /**
     * Gets the sequence.
     *
     * @return the sequence
     */
    public int getSequence() {
        return sequence;
    }

    /**
     * Gets the current action count.
     *
     * @return the action count
     */
    public int getActions() {
        return actions;
    }

	/**
	 * Add coverage counters.
	 *
	 * @param testId the test id
	 * @param sequence the sequence
	 * @param type the type
	 * @param missed the missed
	 * @param covered the covered
	 */
	public void add(CoverageCounter type, int missed, int covered) {
		coverageCounters.put(type, new Counters(missed, covered));
	}

	/**
	 * Gets the counters for a type.
	 *
	 * @param type the type
	 * @return the counters
	 */
	public Counters get(CoverageCounter type) {
		return coverageCounters.get(type);
	}

	/**
	 * Gets the coverage percentage.
	 *
	 * @param type the type
	 * @return the coverage percentage
	 */
	public int getCoveragePercentage(CoverageCounter type) {
		Counters counters = coverageCounters.get(type);
		if (counters != null) {
			return counters.getCoveragePercentage();
		}
		return 0;
	}

	/**
	 * The Class Counters.
	 */
	public class Counters {
		/** The missed. */
		private int missed;
		
		/** The covered. */
		private int covered;
	
		/** The total. */
		private int total;

		/**
		 * Instantiates a new counters.
		 *
		 * @param missed the missed
		 * @param covered the covered
		 */
		public Counters(int missed, int covered) {
			this.missed = missed;
			this.covered = covered;
			this.total = missed + covered;
		}

		/**
		 * Gets the missed.
		 *
		 * @return the missed
		 */
		public int getMissed() {
			return missed;
		}
				
		/**
		 * Gets the covered.
		 *
		 * @return the covered
		 */
		public int getCovered() {
			return covered;
		}
		
		/**
		 * Gets the total lines/branches/instructies etc..
		 *
		 * @return the total
		 */
		public int getTotal() {
			return total;
		}
		
		/**
		 * Gets the coverage percentage.
		 *
		 * @return the percentage
		 */
		public int getCoveragePercentage() {
			return (covered*100/total);
		}
	}
}
