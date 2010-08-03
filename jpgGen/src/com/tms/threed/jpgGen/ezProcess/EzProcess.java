package com.tms.threed.jpgGen.ezProcess;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Note: need to add parameter to specify timeout for IN and OUT streams. i.e. how long, after launching the process,
 * should i keep the IN and OUT threads alive?
 */
public class EzProcess {

    private static class MainTaskOutcome {

        private final Integer exitCode;
        private final Throwable exception;

        private MainTaskOutcome(Integer exitCode, Throwable exception) {
            this.exitCode = exitCode;
            this.exception = exception;
        }

        private MainTaskOutcome(Throwable exception) {
            this(null, exception);
        }

        private MainTaskOutcome(Integer exitCode) {
            this(exitCode, null);
        }

        public Integer getExitCode() {
            return exitCode;
        }

        public Throwable getException() {
            return exception;
        }

        public boolean isSuccess() {
            return exitCode != null && exitCode == 0;
        }

        @Override public String toString() {
            return getClass().getName() + ":  exitCode[" + exitCode + "]  exception[" + exception + "]";
        }
    }


    private final CommandBuilder commandBuilder;


    private Command command = null;
    private String err = null;
    private String out = null;
    private int exitCode = -1000;
    private boolean destroyed;
    private boolean logged;


    /**
     * Uses default streamGobblerThreadTimeout of 30 seconds
     * @param commandBuilder
     */
    public EzProcess(final CommandBuilder commandBuilder) {
        this.commandBuilder = commandBuilder;


    }


    /**
     * Uses 3 threads:
     *   1. run the process
     *   2. OUT stream gobble thread
     *   3. ERR stream gobble thread
     */
    public void executeSync(final ExecutorService executorService) throws EzProcessExecException {
        ExecOutcome outcome = executeSyncInternal(executorService);
        if (!outcome.isSuccess()) throw new EzProcessExecException(outcome);
    }

    private ExecOutcome executeSyncInternal(final ExecutorService executorService) {

        assert this.commandBuilder != null;
        assert executorService != null;

        try {
            MainTask mainTask = new MainTask(executorService, commandBuilder);
            try {
                MainTaskOutcome mainTaskOutcome = mainTask.waitForMainTaskToComplete();
                try {
                    GobblersOutcome gobblersOutcome = mainTask.waitForGobblersToComplete();
                    return new ExecOutcome(mainTaskOutcome, gobblersOutcome);
                } catch (Exception e) {
                    return new ExecOutcome(mainTaskOutcome, e);
                }

            } catch (Exception e) {
                return new ExecOutcome(e);
            }
        } catch (Throwable e) {
            return new ExecOutcome(e);
        }


    }

    /**
     * todo
     */
    private void cleanup() {
    }

    private boolean hasContent(String s) {
        return !isEmpty(s);
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().equals("");
    }


    private void log() {
        if (logged) return;
        if (hasContent(out) || hasContent(err) || exitCode != 0) {
            log.error("ExecResult.logIfErr:");
            log.error("\t command: " + command);
            log.error("\t exitCode: " + exitCode);
            log.error("\t out: " + out);
            log.error("\t err: " + err);
        }
        logged = true;
    }

    private static class MainTask {

        private static final int mainTaskTimeout = 30000; //millis

        private final Command command;
        private final Process process;
        private final Future<Integer> future;
        private final StreamGobblers gobblers;

        MainTask(final ExecutorService executorService, final CommandBuilder commandBuilder) throws IOException {

            command = commandBuilder.buildCommand();
            final ProcessBuilder processBuilder = new ProcessBuilder(command.toList());
            process = processBuilder.start();

            gobblers = new StreamGobblers(executorService, process);

            future = executorService.submit(new Callable<Integer>() {
                @Override public Integer call() throws Exception {
                    return process.waitFor();
                }

            });
        }


        MainTaskOutcome waitForMainTaskToComplete() {
            if (future.isDone()) throw new IllegalStateException();
            try {
                Integer exitCode = future.get(mainTaskTimeout, TimeUnit.SECONDS);
                return new MainTaskOutcome(exitCode);
            }
            catch (Throwable e) {
                destroyProcessIfNecessary();
                return new MainTaskOutcome(e);
            }
        }

        GobblersOutcome waitForGobblersToComplete() {
            return gobblers.waitForOutput();
        }


        private void destroyProcessIfNecessary() {
            if (process != null) {
                log.error("Destroying process for command[" + command + "]");
                try {
                    process.destroy();
                } catch (Exception e) {
                    log.error("Error while destroying process for command[" + command + "]", e);
                }
            }

        }
    }

    public static enum StreamType {
        STD_ERR, STD_OUT
    }

    private static class StreamGobbler implements Callable<String> {

        private final BufferedReader reader;
        private final StreamType type;

        private StringWriter stringWriter = new StringWriter();
        private PrintWriter out = new PrintWriter(stringWriter);
        private boolean quit;

        private StreamGobbler(InputStream is, StreamType type) {
            this.reader = new BufferedReader(new InputStreamReader(is));
            this.type = type;
        }

        public void quit() {
            this.quit = true;
        }

        public String call() throws Exception {
            try {
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        if (quit) break;
                    } else {
                        out.println(type + ": " + line);
                        out.flush();
                    }
                }
                return stringWriter.toString();
            }
            finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        log.error("Problem closing StreamGobbler.reader", e);
                    }
                }
            }
        }

    }

    private static class StreamGobblers {

        private static final int gobblerTimeout = 20; //millis

        private final StreamGobbler outGobbler;
        private final StreamGobbler errGobbler;

        private final Future<String> outFuture;
        private final Future<String> errFuture;

        /**
         * Start Gobbling
         */
        public StreamGobblers(final ExecutorService executor, final Process process) {
            assert process != null;
            assert executor != null;

            outGobbler = new StreamGobbler(process.getInputStream(), StreamType.STD_OUT);
            errGobbler = new StreamGobbler(process.getErrorStream(), StreamType.STD_ERR);


            outFuture = executor.submit(outGobbler);
            errFuture = executor.submit(errGobbler);
        }

        private void quit() {
            outGobbler.quit();
            errGobbler.quit();
        }

        GobblersOutcome waitForOutput() {
            quit();
            try {
                String strOut = outFuture.get(gobblerTimeout, TimeUnit.MILLISECONDS);
                String strErr = errFuture.get(gobblerTimeout, TimeUnit.MILLISECONDS);
                return new GobblersOutcome(strOut, strErr);
            } catch (Throwable e) {
                return new GobblersOutcome(e);
            }
        }


    }

    private static class GobblersOutcome {

        private final String stdOut;
        private final String stdErr;
        private final Throwable exception;

        private GobblersOutcome(String stdOut, String stdErr, Throwable exception) {
            this.stdOut = stdOut;
            this.stdErr = stdErr;
            this.exception = exception;
        }

        private GobblersOutcome(String stdOut, String stdErr) {
            this(stdOut, stdErr, null);
        }

        private GobblersOutcome(Throwable exception) {
            this(null, null, exception);
        }

        public String getStdOut() {
            return stdOut;
        }

        public String getStdErr() {
            return stdErr;
        }

        public Throwable getException() {
            return exception;
        }

        public boolean hasErrorMessage() {
            return stdErr != null || stdErr.trim().length() > 0;
        }

        public boolean isSuccess() {
            return exception == null && !hasErrorMessage();
        }

        @Override public String toString() {
            return getClass().getName() + "\t stdOut[" + stdOut + "]\t stdErr[" + stdErr + "]\t exception[" + exception + "]";
        }
    }

    public class EzProcessExecException extends RuntimeException {

        private final ExecOutcome execOutcome;

        public EzProcessExecException(ExecOutcome execOutcome) {
            this.execOutcome = execOutcome;
        }

        public ExecOutcome getExecOutcome() {
            return execOutcome;
        }

        @Override public String toString() {
            return getClass().getName() + ": " + execOutcome.toString();
        }
    }

    public static class ExecOutcome {

        private final Throwable exception;
        private final MainTaskOutcome mainTaskOutcome;
        private final GobblersOutcome gobblersOutcome;

        public ExecOutcome(MainTaskOutcome mainTaskOutcome, GobblersOutcome gobblersOutcome) {
            this.exception = null;
            this.mainTaskOutcome = mainTaskOutcome;
            this.gobblersOutcome = gobblersOutcome;
        }

        public ExecOutcome(Throwable exception) {
            this.exception = exception;
            this.mainTaskOutcome = null;
            this.gobblersOutcome = null;
        }

        public ExecOutcome(MainTaskOutcome mainTaskOutcome, Throwable exception) {
            this.mainTaskOutcome = mainTaskOutcome;
            this.exception = exception;
            this.gobblersOutcome = null;
        }

        public MainTaskOutcome getMainTaskOutcome() {
            return mainTaskOutcome;
        }

        public GobblersOutcome getGobblersOutcome() {
            return gobblersOutcome;
        }

        public boolean isSuccess() {
            if (mainTaskOutcome == null) return false;
            return mainTaskOutcome.isSuccess();
        }

        public Throwable getException() {
            return exception;
        }

        public void print() {
            System.out.println("exception = [" + exception + "]");
            System.out.println("mainTaskOutcome = [" + mainTaskOutcome + "]");
            System.out.println("gobblersOutcome = [" + gobblersOutcome + "]");
        }

        @Override public String toString() {
            return getClass() + ":\t exception[" + exception + "]\t mainTaskOutcome = [" + mainTaskOutcome + "]\t gobblersOutcome = [" + gobblersOutcome + "]";
        }
    }


    private static final Log log = LogFactory.getLog(EzProcess.class);

}