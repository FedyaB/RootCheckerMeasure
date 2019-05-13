package edu.amd.spbstu.rootcheckermeasure.experiments;

import android.app.Activity;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import edu.amd.spbstu.rootcheckermeasure.MainActivity;
import edu.amd.spbstu.rootcheckermeasure.experiments.features.Feature;
import edu.amd.spbstu.rootcheckermeasure.experiments.features.PackageManagerFeature;
import edu.amd.spbstu.rootcheckermeasure.experiments.features.ShellTimeFeature;
import edu.amd.spbstu.rootcheckermeasure.experiments.features.app_start_time.HideTimeFeature;
import edu.amd.spbstu.rootcheckermeasure.experiments.features.app_start_time.NormTimeFeature;

/**
 * Experiment, all features of which are time features
 */
public class TimeExperiment {
    private static Class<?>[] defaultFeaturesClasses; // Classes of features used by default
    private static Class<?>[][] defaultFeaturesClassesConstructors; // Constructors of features used by default

    // Setting features used by default
    static {
        defaultFeaturesClasses = new Class[]{HideTimeFeature.class, NormTimeFeature.class, PackageManagerFeature.class, ShellTimeFeature.class};
        defaultFeaturesClassesConstructors = new Class[][] {
                new Class<?>[] {MainActivity.class},
                new Class<?>[] {MainActivity.class},
                new Class<?>[] {Activity.class},
                new Class<?>[] {Activity.class}
        };
    }

    private List<Feature> featuresArray; // A list of Feature objects (each object is a collection of a single feature measurements)
    private MainActivity currentActivity; // A current activity (needs to be an Activity to be passed to Features constructors, needs to be ExperimentListener since it is an experiment)
    private Class<?>[] featuresClasses; // Classes of features used in a current experiment
    private Class<?>[][] featuresClassesConstructors; // Constructors of features used in an experiment

    public TimeExperiment(MainActivity activity) {
        this.currentActivity = activity;
        this.featuresClasses = defaultFeaturesClasses;
        this.featuresClassesConstructors = defaultFeaturesClassesConstructors;
    }

    /**
     * Use custom set if features
     * @param featuresClasses Classes of features to be used
     * @param featuresClassesConstructors Constructors of features to be used
     */
    public void useFeatures(Class<?>[] featuresClasses, Class<?>[][] featuresClassesConstructors) {
        this.featuresClasses = featuresClasses;
        this.featuresClassesConstructors = featuresClassesConstructors;
    }

    /**
     * Create features objects from classes used in a current experiment
     */
    private void createFeatures() {
        this.featuresArray = new ArrayList<>();
        for (int i = 0; i < featuresClasses.length; ++i) {
            try {
                featuresArray.add((Feature)featuresClasses[i].getConstructor(featuresClassesConstructors[i]).newInstance(currentActivity));
            } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
                //Do nothing
            }
        }
    }

    /**
     * Clear features storage
     */
    private void clearFeatures() {
        for (Feature feature : featuresArray)
            feature.clear();
    }

    /**
     * Run the experiment
     * @param runs A total number of runs
     */
    public void run(int runs) {
        createFeatures();
        clearFeatures();
        // For each feature run it's gatherer routine
        for (int i = 0; i < runs; ++i) {
            for (Feature feature : featuresArray)
                feature.runRoutine();
            currentActivity.onExperimentRunReady(i, runs);
        }
        currentActivity.onExperimentReady(featuresArray);
    }

    public void destroy() {
        for (Feature feature : featuresArray)
            feature.destroy();
    }
}
