package edu.amd.spbstu.rootcheckermeasure.experiments.features;

/**
 * A collection of measurements made during an experiment for a single feature and its' gatherer
 */
public interface Feature {
    /**
     * Run the routine which collects a single measurement of a feature
     */
    void runRoutine();

    /**
     * Get a measurement by index as a string
     * @param i A measurement index
     * @return A measurement  as a string
     */
    String getString(int i);

    /**
     * Clear the storage
     */
    void clear();

    /**
     * Get current collection size
     * @return A collection size
     */
    int size();

    /**
     * Get the name of a feature
     * @return The name of a feature
     */
    String getFeatureName();

    /**
     * Routine to be called on activity destroy
     */
    void destroy();
}
