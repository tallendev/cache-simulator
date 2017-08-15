import java.util.LinkedList;
import java.util.Locale;

/**
 *
 *
 * Class:     CS 350 - Intro to Computer Organization
 * Project4:  Cache Simulator
 * @author    Trevor Griggs
 * @author    Tyler Allen
 * @version   05/02/2014
 */
public class Cache
{
    /* Size of a memory address */
    private static final int MEM_ADDR_SIZE = 32;

    /* Size of the line/block */
    private int line_size;

    /* Number of sets in the cache */ 
    private int num_sets;

    /* Offset */
    private int associativity;

    /* Size of the cache */
    private int cache_size;

    /* Number of hits */
    private int hits;

    /* Number of misses */
    private int misses;

    /* Number of times accessed */
    private int accesses;

    /* Size of the offset */
    private int offset_size;

    /* Size of the tag */
    private int tag_size;

    /* Size of an index */
    private int index_size;

    /* LinkedList of sets in the cache */
    private LinkedList<CacheSet> sets;

    /* LinkedList of the data supplied */
    private LinkedList<CacheEntry> entries;

    /**
     * Constructor for Cache objects and initializes the fields.
     */
    public Cache(int num_sets, int associativity, int cache_size, int line_size)
    {
        this.num_sets = num_sets;
        this.associativity = associativity;
        this.cache_size = cache_size;
        this.hits = 0;
        this.misses = 0;
        this.accesses = 0;
        this.sets = new LinkedList<>();
        this.entries = new LinkedList<>();
        this.line_size = line_size;

        offset_size = (int) (Math.log(line_size)/(Math.log(2)));
        index_size = (int) (Math.log(num_sets)/Math.log(2));
        tag_size = MEM_ADDR_SIZE - associativity - index_size;
    }
    
    /**
     * This method pulls the tag from the given addr param.
     * tag = address_size - index_size - offset_size
     *
     * @param  addr - the address to get the tag from
     * @return long - returns the long with all the values zeroed out except
     *                for the tag
     */ 
    public long pullTag(long addr)
    {
        addr >>>= (offset_size + index_size);
        int rest = Long.SIZE - tag_size;
        return (addr << rest) >>> (rest);
    }

    /**
     * This method pulls the index from the given addr param.
     *
     * @param  addr - the address to get the index from
     * @return long - returns the long with all the values zeroed out except
     *                for the index
     */
    public long pullIndex(long addr)
    {
        addr >>>= offset_size;
        int rest = Long.SIZE - index_size;
        return (addr << rest) >>> (rest);
    }

    /**
     * This method pulls the offset from the given addr param.
     *
     * @param addr  - the address to get the offset from
     * @return long - returns the long with all the values zeroed out except
     *                for the index
     */
    public long pullOffset(long addr)
    {
        int rest = Long.SIZE - offset_size;
        return (addr << rest) >>> (rest);
    }

    /**
     * This method
     *
     * @param access  -
     * @param address - the address for the entry
     * @param tag     - the tag of the entry
     * @param index   - the index of the entry
     * @param offset  - the offset for the entry
     */
    public void makeCacheEntry(Access access, long address, long tag,
                               long index, long offset)
    {
        accesses++;
        CacheSet set = null;

        // loop for the
        for (int i = 0; i < sets.size(); i++)
        {
            CacheSet temp_set = sets.get(i);

            if (temp_set.index == index)
            {
                set = temp_set;
                i = sets.size();
            }

        }

        if (set == null)
        {
            set = new CacheSet(index);
            sets.add(set);
        }

        CacheSet.CacheBlock block = set.insert(tag, access);

        // checks if there was a hit or miss
        if (block.status == Status.miss)
        {
            misses++;
        }
        else
        {
            hits++;
        }

        entries.add(new CacheEntry(access, address, tag, offset, index,
                                   block.status, block.memrefs));
    }

    /**
     *
     */
    private class CacheSet
    {
        /* Represents the index of a set. */
        private long index;

        /* LinkedList of blocks/lines of a cache */
        private LinkedList<CacheBlock> blocks;

        /**
         * Constructor that initializes the fields of a CacheSet object.
         *
         * @param index - the index of the set in the cache
         */
        private CacheSet(long index)
        {
            this.index = index;
            this.blocks = new LinkedList<>();
        }

        /**
         *
         *
         * @param tag
         * @param access
         * @return
         */
        private CacheBlock insert(long tag, Access access)
        {
            //
            CacheBlock block = null;
            //
            CacheBlock temp_block  = null;
            //
            boolean dirty = access == Access.W;

            //
            for (int i = 0; i < blocks.size(); i++)
            {
                temp_block = blocks.get(i);

                if (temp_block.tag == tag)
                {
                    block = temp_block;
                    i = blocks.size();
                }
            }

            int memrefs = 0;

            if (block == null)
            {
                memrefs++;

                if (blocks.size() == associativity)
                {

                    if (blocks.remove(0).dirty)
                    {
                        memrefs++;
                    }

                    block = new CacheBlock(tag, Status.miss, memrefs, dirty);
                }
                else
                {
                    block = new CacheBlock(tag, Status.miss, memrefs, dirty);
                }

                blocks.add(block);
            }
            else
            {
                blocks.remove(block);
                blocks.add(block);

                // sets the dirty bit
                if (!block.dirty)
                {
                    block.dirty = dirty;
                }

                block.memrefs = memrefs;
                block.status = Status.hit;
            }

            return block;
        }

        /**
         *
         */
        private class CacheBlock
        {
            /*  */
            private long tag;

            /*  */
            private Status status;

            /*  */
            private int memrefs;

            /*  */
            private boolean dirty;

            /**
             *
             *
             * @param tag
             * @param status
             * @param memrefs
             * @param dirty
             */
            private CacheBlock(long tag, Status status, int memrefs,
                               boolean dirty)
            {
                this.tag = tag;
                this.status = status;
                this.memrefs = memrefs;
                this.dirty = dirty;
            }

            /**
             * Returns the value of the 'dirty' field as a string.
             *
             * @return String - string representation of the value of the
             *                  'dirty' field
             */
            public String toString()
            {
                return "" + dirty;
            }
        }
    }

    /**
     *
     */
    private class CacheEntry
    {
        /*  */
        private Access access;

        /*  */
        private long address;

        /*  */
        private long tag;

        /*  */
        private long index;

        /*  */
        private long offset;

        /*  */
        private Status stat;

        /*  */
        private long memrefs;

        /**
         *
         *
         * @param access
         * @param address
         * @param tag
         * @param offset
         * @param index
         * @param stat
         * @param memrefs
         */
        public CacheEntry(Access access, long address, long tag, long offset,
                          long index, Status stat, long memrefs)
        {
            this.access = access;
            this.address = address;
            this.tag = tag;
            this.offset = offset;
            this.stat = stat;
            this.memrefs = memrefs;
            this.index = index;
        }

        /**
         * This method returns the string representation of the fields of the
         * CacheEntry object.
         *
         * @return String - string representation of all the fields of the
         *                  CacheEntry object.
         */
        public String toString()
        {
            return String.format(Locale.ENGLISH,
                                 "%6s %8x %7d %5d %6d %6s %7d\n",
                                 access, address, tag, index, offset, stat,
                                 memrefs);
        }
    }

    /**
     * This method prints the formatted results from running the program.
     */
    public void printCache()
    {
        System.out.printf("Cache Configuration\n\n\t%d %d-way set " +
                          "associative entries\n\tof line size %d " +
                          "bytes\n\n", num_sets, associativity, line_size);
        System.out.printf("%-6s %-8s %-7s %-5s %-6s %-6s %-7s\n", "Access",
                          "Address", "  Tag", "Index", "Offset", "Status",
                          "Memrefs");
        System.out.printf("%6s %8s %7s %5s %6s %6s %7s\n", "------", "--------",
                          "-------", "-----", "------", "------", "-------");

        // creates a new StringBuilder object for appending
        StringBuilder build = new StringBuilder();

        // for each entry in entries, call the toString method on each
        for (CacheEntry entry : entries)
        {
            build.append(entry.toString());
        }

        System.out.println(build);
        System.out.println("\nSimulation Summary Statistics\n" +
                           "-----------------------------");
        System.out.printf("Total hits       : %-6d\n" +
                          "Total misses     : %-6d\n" +
                          "Total accesses   : %-6d\n" +
                          "Hit ratio        : %-6f\n" +
                          "Miss ratio       : %-6f\n",
                          hits, misses, accesses, ((double) hits)/accesses,
                          ((double) misses)/accesses);
    }
}
