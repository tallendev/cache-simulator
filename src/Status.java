/**
 * This Status class keeps track of hits and misses.
 *
 * Class:    CS 350 - Intro to Computer Organization
 * Project4: Cache Simulator
 * @author   Trevor Griggs
 * @author   Tyler Allen
 * @version  05/02/2014
 */
public enum Status
{
    hit("hit"),
    miss("miss");

    /* String that holds either 'hit' or 'miss' */
    private String str;

    /**
     * Constructor that initializes the str field to the parameter it was
     * given.
     *
     * @param str - string to initialize the str field to
     */
    private Status(String str)
    {
        this.str = str;
    }

    /**
     * Returns a Status object with the value 'hit' or 'miss'. The value that
     * the object is assigned depends on the str param given.
     *
     * @param  str    - string to initialize the str field to
     * @return Status - a new Status object
     */
    public Status getStatus(String str)
    {
        // The Status variable to return
        Status ret = null;

        // Switch that assigns the Status object 'hit' or 'miss' based on str.
        switch (str.toLowerCase())
        {
            case "hit":
                ret = hit;
            break;

            case "miss":
                ret = miss;
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