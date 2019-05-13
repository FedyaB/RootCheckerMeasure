package edu.amd.spbstu.rootcheckermeasure;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import edu.amd.spbstu.rootcheckermeasure.experiments.ExperimentListener;
import edu.amd.spbstu.rootcheckermeasure.experiments.TimeExperiment;
import edu.amd.spbstu.rootcheckermeasure.experiments.features.Feature;
import edu.amd.spbstu.rootcheckermeasure.experiments.features.app_start_time.HideTimeFeature;
import edu.amd.spbstu.rootcheckermeasure.experiments.features.app_start_time.NormTimeFeature;

/**
 * REFERENCE:
 * * MEASUREMENT is a tuple of features that reflects instant device configuration
 * * * Features used in a measurement is set up manually (predefined in the experiment object)
 * * * Only time measurements are used at the moment so TimeExperiment object is used
 * * EXPERIMENT is a process of gathering measurements.
 * * * Can be started by pushing 'start' button
 * * * Each experiment gathers info about current configuration
 * * * The number of configuration measurements can be set manually (with SeekBar)
 * * * Info gathered from experiment is written to external storage folder
 * * * * If permission was not granted then 'start' button is disabled
 * * EXPERIMENT RUN = MEASUREMENT
 */

public class MainActivity extends AppCompatActivity implements ExperimentListener {
    private static final int WRITE_REQUEST_CODE = 1; // A code for a permission to write to an external storage

    private int[] seekBarValues; // Possible experiments number, used in the seek bar
    private boolean experimentRunning; // Current data gathering status
    private TimeExperiment experiment; // An experiment object

    /**
     * Measurements number seek bar initialization
     */
    private void initializeSeekBar() {
        SeekBar seekBar = findViewById(R.id.seekBar);

        seekBar.setMax(seekBarValues.length - 1);
        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener()
                {
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {}

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {}

                    /**
                     * Update current number of measurements on the screen
                     * @param seekBar The seek bar with measurements quantity
                     * @param progress Current seek bar index
                     * @param fromUser NOT USED
                     */
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress,
                                                  boolean fromUser) {
                        TextView selectedRuns = findViewById(R.id.seekBarSelectedRuns);
                        String message = Integer.toString(seekBarValues[progress]);
                        selectedRuns.setText(message);
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seekBarValues = getResources().getIntArray(R.array.seek_bar_values); // Possible measurements number is predefined (see res)
        initializeSeekBar();

        // Setting up initial number of measurements (since listener is attached only to SB position change)
        TextView currentRuns = findViewById(R.id.seekBarSelectedRuns);
        currentRuns.setText(Integer.toString(seekBarValues[0]));

        // Check for external storage permission since experiment info is written to external storage folder
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_REQUEST_CODE);

        experimentRunning = false;
        findViewById(R.id.runTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View run_button) {
                if (!experimentRunning) { // Double click prevention (only one experiment can be launched)
                    experimentRunning = true;
                    run_button.setEnabled(false);
                    onClickStartButton();
                }
            }
        });

        experiment = new TimeExperiment(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case WRITE_REQUEST_CODE:
                // If access was not granted then disable a launch button since experiments data can not be saved
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Button run = findViewById(R.id.runTest);
                    run.setEnabled(false);
                }
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy() {
        experiment.destroy();
        super.onDestroy();
    }

    /**
     * Write an experiment measurements to a predefined folder at external storage
     * * TSV convention is used
     * * A file is placed at an external storage in a predefined folder (see res).
     * * A filename is modified with current timestamp so a couple of experiments in a row may be launched
     * @param features List of Feature objects (Feature object is a collection of all the data about one of the features)
     * @param filename A name of the file to which a measurement is written
     */
    private void writeExperimentData(List<Feature> features, String filename) {
        try {
            File measurementsDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), getString(R.string.write_dir_name));
            if (!measurementsDir.exists())
                measurementsDir.mkdirs();
            File measurements = new File(measurementsDir, filename + Long.toString(System.currentTimeMillis()) + ".txt");
            FileWriter fileWriter = new FileWriter(measurements);

            // Write header
            for (Feature feature : features)
                fileWriter.write(feature.getFeatureName() + "\t");
            fileWriter.write("\n");

            // Write measurements
            int entriesNum = features.get(0).size();
            for (int i = 0; i < entriesNum; ++i) {
                for (Feature feature : features)
                    fileWriter.write(feature.getString(i) + "\t");
                fileWriter.write("\n");
            }
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            //Do nothing
        }
    }

    /**
     * Write measurements and end the experiment
     * @param features
     */
    @Override
    public void onExperimentReady(List<Feature> features) {
        writeExperimentData(features, getString(R.string.experiment_file_name));

        // Update current experiment status to the 'done'
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView text = findViewById(R.id.status);
                text.setText(R.string.status_done);
                experimentRunning = false;
                Button run_button = findViewById(R.id.runTest);
                run_button.setEnabled(true);
            }
        });
    }

    /**
     * Update current experiment status (show current measurement number)
     * @param run A current measurement number
     * @param runs A total number of measurements
     */
    @Override
    public void onExperimentRunReady(int run, int runs) {
        final String status =  getString(R.string.experiment_name) + " " + Integer.toString(run + 1) + "/" + Integer.toString(runs);
        final int currentRun = run;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView statusView = findViewById(R.id.status);
                statusView.setText(status);
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setProgress(currentRun);
            }
        });
    }

    /**
     * Launch an experiment on 'start' button click
     * * The experiment routine is launched on another thread since being time consuming
     */
    public void onClickStartButton() {
        SeekBar seekBar = findViewById(R.id.seekBar);
        final int runs = seekBarValues[seekBar.getProgress()];
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(runs - 1);
        progressBar.setProgress(0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                experiment.run(runs);
            }
        }).start();
    }
}
