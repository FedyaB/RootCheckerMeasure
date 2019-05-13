package edu.amd.spbstu.rootcheckermeasure.experiments;

import java.util.List;

import edu.amd.spbstu.rootcheckermeasure.experiments.features.Feature;

/**
 * Features measuring experiment listener
 */
public interface ExperimentListener {
    /**
     * On single measurement is gathered
     * @param run A current measurement index
     * @param runs A total number of measurements
     */
    void onExperimentRunReady(int run, int runs);

    /**
     * On experiment is over
     * @param featuresStamps List of gathered features
     */
    void onExperimentReady(List<Feature> featuresStamps);
}
