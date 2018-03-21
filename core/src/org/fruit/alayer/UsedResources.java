package org.fruit.alayer;

/**
 * Container to save used resources for an action.
 */
public class UsedResources {

    private final long userCPU;
    private final long systemCPU;
    private final double RAMBase;
    private final double RAMPeak;

    /**
     * Constructs new instance of a UsedResources object
     * @param userCPU cpu time spent in user space.
     * @param systemCPU cpu time spent in system space
     * @param ramBase minimum used memory.
     * @param ramPeak Max Memory usage of the SUT during test execution.
     */
    public UsedResources(long userCPU, long systemCPU, double ramBase, double ramPeak) {
        this.userCPU =userCPU;
        this.systemCPU = systemCPU;
        this.RAMBase = ramBase;
        this.RAMPeak = ramPeak;
    }

    @Override
    public String toString() {
        return "{ userCPU:" + userCPU + ",systemCPU: " + systemCPU + ",ramBase:" + RAMBase + ",ramPeak:" + RAMPeak + "}";
    }
}
