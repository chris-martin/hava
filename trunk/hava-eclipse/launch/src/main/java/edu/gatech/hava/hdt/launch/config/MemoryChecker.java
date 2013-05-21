package edu.gatech.hava.hdt.launch.config;

/**
 * Class used to monitor memory usage (as reported by the JVM).
 */
public class MemoryChecker {

    private final float maxUsage;

    /**
     * Constructor.
     *
     * @param maxUsage the max memory usage threshold as
     *                 a float between 0 and 1.
     *
     * @throws IllegalArgumentException if maxUsage is not
     *                                  between 0 and 1
     */
    public MemoryChecker(final float maxUsage) {

        if (maxUsage < 0 || maxUsage > 1) {
            throw new IllegalArgumentException(
                    "maxUsage must be between 0 and 1");
        }

        this.maxUsage = maxUsage;

    }

    /**
     * Constructor.
     *
     * @param maxPercent the max memory usage threshold as
     *                   a percentage between 0 and 100
     *
     * @throws IllegalArgumentException if maxPercent is not
     *                                  between 0 and 100
     */
    public MemoryChecker(final int maxPercent) {

        if (maxPercent < 0 || maxPercent > 100) {
            throw new IllegalArgumentException(
                    "maxPercent must be between 0 and 100");
        }

        maxUsage = ((float) maxPercent) / 100f;

    }

    /**
     * Determines whether the fraction of available memory used
     * exceeds this memory checker's threshold for maximum usage.
     *
     * @return true if memory usage is sufficiently high,
     *         false otherwise
     */
    public boolean usageExceedsMaximum() {

        return getMemoryUsage() > maxUsage;

    }

    private float getMemoryUsage() {

        Runtime runtime = Runtime.getRuntime();

        long max = runtime.maxMemory();
        long total = runtime.totalMemory();
        long free = runtime.freeMemory();

        float usage = 1f - ((max - total + free) / (float) max);

        return usage;

    }

}
