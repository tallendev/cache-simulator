import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * This is the 'main' file of the project. The main method is the 'driver' of
 * the project.
 *
 * Class:     CS 350 - Intro to Computer Organization
 * Project4:  Cache Simulator
 * @author    Trevor Griggs
 * @author    Tyler Allen
 * @version   05/02/2014
 */
public class CacheSimulator
{
    /* Error value for a usage error */
    public final static int USAGE_ERR = 10;

    /* The largest value the size of a set can be */
    public final static int MAX_SET_SIZE = 8;

    /* The largest number of sets there can be at once */
    public final static int MAX_NUM_SETS = (int) Math.pow(2, 13);

    /* The smallest a line can be */
    public final static int MIN_LINE_SIZE = 4;

    /**
     * This is the method that runs the project.
     *
     * @param args - unused
     */
    public static void main(String[] args)
    {
        // holds the scanner we will use for input/output
        Scanner scan = new Scanner(System.in);
        // handles the pattern of the input from the .dat files
        Pattern p = Pattern.compile(".*:");
        scan.skip(p);
        // gets the next integer from the scanner
        int sets = scan.nextInt();

        // if more sets than the max number allowed then print a usage message
        if (sets > MAX_NUM_SETS)
        {
            usage("Num sets too large: " + sets + " > " + MAX_NUM_SETS);
        }
        // power of 2 formula
        else if ((sets & -(sets)) != sets)
        {
            usage("Sets is not a power of 2: " + sets);
        }
        scan.nextLine();
        scan.skip(p);
        // gets the next integer from the scanner
        int set_size = scan.nextInt();

        // if more than max size allowed then print a usage message
        if (set_size > MAX_SET_SIZE)
        {
            usage("Set size too large: " + sets + " > " + MAX_SET_SIZE);
        }

        scan.nextLine();
        scan.skip(p);
        // get the next integer from the scanner
        int line_size = scan.nextInt();

        // if less than min size allowed then print a usage message
        if (line_size < MIN_LINE_SIZE)
        {
            usage("Line size too small: " + line_size + " > " + MIN_LINE_SIZE);
        }
        // power of 2 formula
        else if ((line_size & -(line_size)) != line_size)
        {
            usage("Line_Size is not a power of 2: " + line_size);
        }

        scan.nextLine();
        // figure out if something else to do with line_size
        Cache cache = new Cache(sets, set_size, sets * line_size * set_size,
                                line_size);
        // new Access object to keep track of reads and writes
        Access access = null;
        // int size = -1;
        long mem_addr = -1;

        // loop until no more input
        while (scan.hasNextLine())
        {
            // gets the pieces from around the ':' character
            String strs[] = scan.nextLine().split(":");
            access = Access.getAccess(strs[0]);

            // if equal to 'null' then print a usage message
            if (access == null)
            {
                usage("Invalid access parameter: " + strs[0]);
            }

            // size = Integer.parseInt(strs[1]); // what do
            mem_addr = Long.decode("0x" + strs[2]);
            cache.makeCacheEntry(access, mem_addr, cache.pullTag(mem_addr),
                                 cache.pullIndex(mem_addr),
                                 cache.pullOffset(mem_addr));
        }

        cache.printCache();
    }

    /**
     * Builds and prints usage messages.
     *
     * @param err - the error message to print to stderr
     */
    private static void usage(String err)
    {
        String program = System.getProperty("sun.java.command");

        if (err != null)
        {
            System.err.println(err);
        }

        if (program.contains(" "))
            program = program.substring(0, program.indexOf(" "));
        System.err.println("Usage: java " + program + " < <inputfile>");
        System.exit(USAGE_ERR);
    }
}