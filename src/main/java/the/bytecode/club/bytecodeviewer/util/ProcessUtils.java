package the.bytecode.club.bytecodeviewer.util;

import the.bytecode.club.bytecodeviewer.BytecodeViewer;
import the.bytecode.club.bytecodeviewer.translation.TranslatedStrings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static the.bytecode.club.bytecodeviewer.Constants.NL;

/**
 * @author Konloch
 * @since 10/2/2024
 */
public class ProcessUtils
{
    public static StringBuilder mergeLogs(StringBuilder out, StringBuilder err, int exitCode)
    {
        StringBuilder logs = new StringBuilder();

        if(out.toString().trim().length() >= 1)
            logs.append(TranslatedStrings.PROCESS2).append(" out:")
                .append(out).append(NL).append(NL);

        if(err.toString().trim().length() >= 1)
            logs.append(TranslatedStrings.PROCESS2).append(" err:")
                .append(err).append(NL).append(NL);

        logs.append(TranslatedStrings.ERROR2).append(NL).append(NL);
        logs.append(TranslatedStrings.EXIT_VALUE_IS).append(" ")
            .append(exitCode).append(NL).append(NL);

        return logs;
    }

    public static void readProcessToStringBuilder(Process process, StringBuilder out, StringBuilder err) throws IOException
    {
        //Read out dir output
        try (InputStream is = process.getInputStream();
             InputStreamReader isr = new InputStreamReader(is);
             BufferedReader br = new BufferedReader(isr))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                out.append(NL).append(line);
            }
        }
        catch (IOException ignore)
        {
        }

        try (InputStream is = process.getErrorStream();
             InputStreamReader isr = new InputStreamReader(is);
             BufferedReader br = new BufferedReader(isr))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                err.append(NL).append(line);
            }
        }
        catch (IOException ignore)
        {
        }
    }

    public static void readProcessToStringBuilderAsync(Process process, StringBuilder out, StringBuilder err) throws IOException
    {
        try (InputStream is = process.getInputStream();
             InputStreamReader isr = new InputStreamReader(is);
             BufferedReader br = new BufferedReader(isr))
        {
            BytecodeViewer.getTaskManager().delayLoop(25, task ->
            {
                if(!process.isAlive())
                    task.stop();

                try
                {
                    String line;

                    while ((line = br.readLine()) != null)
                    {
                        out.append(NL).append(line);
                    }
                }
                catch (IOException ignore)
                {
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            });
        }

        try (InputStream is = process.getErrorStream();
             InputStreamReader isr = new InputStreamReader(is);
             BufferedReader br = new BufferedReader(isr))
        {
            BytecodeViewer.getTaskManager().delayLoop(25, task ->
            {
                if(!process.isAlive())
                    task.stop();

                try
                {
                    String line;

                    while ((line = br.readLine()) != null)
                    {
                        err.append(NL).append(line);
                    }
                }
                catch (IOException ignore)
                {
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void runDecompilerExternal(String[] args, boolean exceptionToGUI) throws IOException, InterruptedException
    {
        try
        {
            ProcessBuilder pb = new ProcessBuilder(args);
            Process p = pb.start();
            BytecodeViewer.createdProcesses.add(p);
            p.waitFor();
        }
        catch (Exception e)
        {
            if(exceptionToGUI)
                BytecodeViewer.handleException(e);
            else
                throw e;
        }
    }
}
