package minicraft.network;

import java.io.Serializable;

public class WorldCreate implements Serializable{
    private long seed;
    private int worldSize;

    public WorldCreate(long seed, int worldSize)
    {
        this.seed = seed;
        this.worldSize = worldSize;
    }

    public long getSeed()
    {
        return seed;
    }
}
