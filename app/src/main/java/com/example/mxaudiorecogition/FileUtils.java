package com.example.mxaudiorecogition;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * An utility class for file interaction.
 * 
 * @author Joren Six
 */
public final class FileUtils {
    static final Logger LOG = Logger.getLogger(FileUtils.class.getName());

    // Disable the default constructor.
    private FileUtils() {
    }

    /**
     * Joins path elements using the systems path separator. e.g. "/tmp" and
     * "com.random.test.wav" combined together should yield /tmp/com.random.test.wav
     * on UNIX.
     * 
     * @param path
     *            The path parts part.
     * @return Each element from path joined by the systems path separator.
     */
    private static String combine(final String... path) {
        File file = new File(path[0]);
        for (int i = 1; i < path.length; i++) {
            file = new File(file, path[i]);
        }
        return file.getPath();
    }

    /**
     * @return The path where the program is executed.
     */
    public static String runtimeDirectory() {
        String runtimePath = "";
        try {
            runtimePath = new File(".").getCanonicalPath();
        } catch (final IOException e) {
            LOG.log(Level.SEVERE, "Could not find runtime path, strange.", e);
        }
        return runtimePath;
    }

    /**
     * Writes a file to disk. Uses the string contents as content. Failures are
     * logged.
     * 
     * @param contents
     *            The contents of the file.
     * @param name
     *            The name of the file to create.
     */
    public static void writeFile(final String contents, final String name) {
        writeFile(contents, name, false);
    }

    private static void writeFile(final String contents, final String name, final boolean append) {
        BufferedWriter outputStream = null;
        PrintWriter output = null;
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(name, append);
            outputStream = new BufferedWriter(fileWriter);
            output = new PrintWriter(outputStream);
            output.print(contents);
            outputStream.flush();
            output.close();
        } catch (final IOException e) {
            LOG.log(Level.SEVERE, "Could not write file " + name, e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (output != null) {
                    output.close();
                }
            } catch (final IOException e) {
                LOG.log(Level.SEVERE, "Failed to close file: " + name, e);
            }
        }
    }

    /**
     * Appends a string to a file on disk. Fails silently.
     * 
     * @param contents
     *            The contents of the file.
     * @param name
     *            The name of the file to create.
     */
    public static void appendFile(final String contents, final String name) {
        writeFile(contents, name, true);
    }

    /**
     * Reads the contents of a file.
     * 
     * @param name
     *            the name of the file to read
     * @return the contents of the file if successful, an empty string otherwise.
     */
    public static String readFile(final String name) {
        FileReader fileReader = null;
        final StringBuilder contents = new StringBuilder();
        try {
            final File file = new File(name);
            if (!file.exists()) {
                throw new IllegalArgumentException("File " + name + " does not exist");
            }
            fileReader = new FileReader(file);
            final BufferedReader reader = new BufferedReader(fileReader);
            String inputLine = reader.readLine();
            while (inputLine != null) {
                contents.append(inputLine).append("\n");
                inputLine = reader.readLine();
            }
            reader.close();
        } catch (final IOException i1) {
            LOG.severe("Can't open file:" + name);
        }
        return contents.toString();
    }

    /**
     * <p>
     * Return a list of files in directory that satisfy pattern. Pattern should be a
     * valid regular expression not a 'unix glob pattern' so in stead of
     * <code>*.wav</code> you could use <code>.*\.wav</code>
     * </p>
     * <p>
     * E.g. in a directory <code>home</code> with the files
     * <code>com.test.txt</code>, <code>blaat.wav</code> and <code>foobar.wav</code>
     * the pattern <code>.*\.wav</code> matches <code>blaat.wav</code> and
     * <code>foobar.wav</code>
     * </p>
     * 
     * @param directory
     *            A readable directory.
     * @param pattern
     *            A valid regular expression.
     * @param recursive
     *            A boolean defining if directories should be traversed recursively.
     * @return a list of filenames matching the pattern for directory.
     * @exception Error
     *                an error is thrown if the directory is not ... a directory.
     * @exception java.util.regex.PatternSyntaxException
     *                Unchecked exception thrown to indicate a syntax error in a
     *                regular-expression pattern.
     */
    public static List<String> glob(final String directory, final String pattern, final boolean recursive) {
        final List<String> matchingFiles = new ArrayList<String>();
        final Pattern p = Pattern.compile(pattern);
        final File dir = new File(new File(directory).getAbsolutePath());
        glob(dir, p, recursive, matchingFiles);
        // sort alphabetically
        Collections.sort(matchingFiles);
        return matchingFiles;
    }

    private static void glob(final File directory, final Pattern pattern, final boolean recursive,
                             List<String> matchingFiles) {

        if (!directory.isDirectory()) {
            throw new IllegalArgumentException(directory + " is not a directory");
        }
        for (final String file : directory.list()) {
            File filePath = new File(FileUtils.combine(directory.getAbsolutePath(), file));
            if (recursive && filePath.isDirectory()) {
                glob(filePath, pattern, recursive, matchingFiles);
            } else {
                if (pattern.matcher(file).matches() && file != null) {
                    matchingFiles.add(filePath.getAbsolutePath());
                }
            }
        }
    }

    /**
     * Returns the path for a file.<br>
     * <code>path("/home/user/random.jpg") == "/home/user"</code><br>
     * Uses the correct pathSeparator depending on the operating system. On windows
     * c:/com.test/ is not c:\com.test\
     * 
     * @param fileName
     *            The name of the file using correct path separators.
     * @return The path of the file.
     */
    public static String path(final String fileName) {
        final int sep = fileName.lastIndexOf(File.separatorChar);
        return fileName.substring(0, sep);
    }

    /**
     * Checks if a file exists.
     * 
     * @param fileName
     *            the name of the file to check.
     * @return true if and only if the file or directory denoted by this abstract
     *         pathname exists; false otherwise
     */
    public static boolean exists(final String fileName) {
        return new File(fileName).exists();
    }

    /**
     * Creates a directory and parent directories if needed.
     * 
     * @param path
     *            the path of the directory to create
     * @return true if the directory was created (possibly with parent directories)
     *         , false otherwise
     */
    public static boolean mkdirs(final String path) {
        return new File(path).mkdirs();
    }



    /**
     * Removes a file from disk.
     * 
     * @param fileName
     *            the file to remove
     * @return true if and only if the file or directory is successfully deleted;
     *         false otherwise
     */
    public static boolean rm(final String fileName) {
        return new File(fileName).delete();
    }

    /**
     * Tests whether the file denoted by this abstract pathname is a directory.
     * 
     * @param inputFile
     *            A pathname string.
     * @return true if and only if the file denoted by this abstract pathname exists
     *         and is a directory; false otherwise.
     */
    public static boolean isDirectory(final String inputFile) {
        return new File(inputFile).isDirectory();
    }

    /**
     * Returns an identifier for a resource. It is either based on the hashCode of a
     * string or on the name of the resource. If a resource is called e.g.
     * <code>1855.mp3</code>, the number part is used as identifier.
     * 
     * @param resource
     *            The resource to get an identifier for.
     * @return an identifier.
     */
    public static int getIdentifier(String resource) {
        String fileName = new File(resource).getName();
        String tokens[] = fileName.split("\\.(?=[^\\.]+$)");
        int identifier;
        if (tokens.length == 2 && tokens[0].matches("\\d+")) {
            identifier = Integer.valueOf(tokens[0]);
        } else {
            int hashCode = Math.abs(fileName.hashCode());
            int minValue = Integer.MAX_VALUE / 2;
            identifier = minValue + hashCode / 2;
        }
        return identifier;
    }

}
