package edu.amd.spbstu.rootcheckermeasure.experiments.features;

import android.app.Activity;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.amd.spbstu.rootcheckermeasure.R;

/**
 * Shell command execution time feature
 * * The command demands root access (a.e. 'su')
 */
public class ShellTimeFeature implements Feature {
    private List<Long> stamps;
    private long timeStamp;
    private Activity activity;

    public ShellTimeFeature(Activity activity) {
        this.activity = activity;
        stamps = new ArrayList<>();
    }

    @Override
    public void runRoutine() {
        boolean rooted = callShell();
        // Consider execution time as zero if command graph differs from the one used on not-rooted device (means device is rooted)
        stamps.add(rooted ? 0 : timeStamp);
    }

    /**
     * Launch shell command routine
     * @return A device is rooted (determined on different command flow graph than expected) or an error has occured
     */
    private boolean callShell() {
        try {
            return suExecutionCheck();
        } catch (IOException | InterruptedException e) {
            return true;
        }
    }

    /**
     * Exit the SU terminal
     * @param suProcess A shell with SU terminal
     */
    private void suWriteExitSequence(Process suProcess) throws IOException {
        try (DataOutputStream outputStream = new DataOutputStream(suProcess.getOutputStream())) {
            outputStream.writeBytes("exit\n");
            outputStream.flush();
        }
    }

    /**
     * A SU command execution time measurement routine
     * @return Check whether CFG differs from expected one
     */
    private boolean suExecutionCheck() throws IOException, InterruptedException {
        Process suProcess;
        try {
            timeStamp = System.nanoTime();
            suProcess = Runtime.getRuntime().exec("su");
        } catch (IOException e) {
            timeStamp = System.nanoTime() - timeStamp;
            return false;
        }
        suWriteExitSequence(suProcess);
        suProcess.waitFor();
        return true;
    }

    @Override
    public String getString(int i) {
        return Long.toString(stamps.get(i));
    }

    @Override
    public String getFeatureName() {
        return activity.getString(R.string.feature_shell);
    }

    @Override
    public int size() {
        return stamps.size();
    }

    @Override
    public void clear() {
        stamps.clear();
    }

    @Override
    public void destroy() {
        //Do nothing
    }
}
