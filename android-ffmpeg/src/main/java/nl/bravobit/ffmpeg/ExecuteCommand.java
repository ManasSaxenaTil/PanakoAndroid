package nl.bravobit.ffmpeg;

import java.util.Map;

public class ExecuteCommand {

    private final String[] cmd;
    private Map<String, String> environment;
    private final ShellCommand shellCommand;
    private Process process;

    ExecuteCommand(String[] cmd, Map<String, String> environment) {
        this.cmd = cmd;
        this.environment = environment;
        this.shellCommand = new ShellCommand();
    }

    protected CommandResult doInBackground(Void... params) {
        try {
            process = shellCommand.run(cmd, environment);
            if (process == null) {
                return CommandResult.getDummyFailureResponse();
            }
            Log.d("Running publishing updates method");
            //checkAndUpdateProcess();
            return CommandResult.getOutputFromProcess(process);
        } catch (Exception e) {
            Log.e("Error running FFmpeg binary", e);
        } finally {
            Util.destroyProcess(process);
        }
        return CommandResult.getDummyFailureResponse();
    }

}
