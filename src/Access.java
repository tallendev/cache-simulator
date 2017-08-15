/**
 * This Access class keeps track of reads and writes.
 *
 * Class:    CS 350 - Intro to Computer Organization
 * Project4: Cache Simulator
 * @author   Trevor Griggs
 * @author   Tyler Allen
 * @version  05/02/2014
 */
public enum Access
{
    R("read"),
    W("write");
    
    /* String that holds either 'read' or 'write' */
    private String str;

    /** 
     * Constructor that initializes the str field to the parameter it was 
     * given.
     *
     * @param str - string to initialize the str field to
     */
    private Access(String str)
    {
        this.str = str;
    }

    /**
     * Returns an Access object with the value 'R' or 'W'. The value that the 
     * object is assigned depends on the str param given.  
     *
     * @param  str    - string to initialize the str field to
     * @return Access - a new Access object 
     */
    public static Access getAccess(String str)
    {
        // The Access variable to return
        Access ret = null;

        // Switch that assigns the Access object 'R' or 'W' based on str.
        switch (str.toUpperCase())
        {
            case "R":
                ret = R;
                break;

            case "W":
                ret = W;
                break;
        }

        return ret;
    }
    
    /**
     * Returns the str field.
     *
     * @return String - str field
     */
    public String toString()
    {
        return str;
    }
}
