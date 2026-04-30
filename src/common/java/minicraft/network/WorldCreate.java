package minicraft.network;

import java.io.Serializable;

public class WorldCreate implements Serializable{
    private static final long serialVersionUID = 1L;

    private final long seed;
    private final int worldSize;

    public WorldCreate(long seed, int worldSize)
    {
        this.seed = seed;
        this.worldSize = worldSize;
    }

    public long getSeed()
    {
        return seed;
    }

    public int getWorldSize()
    {
        return worldSize;
    }

    @Override
    public String toString() {
        return "WorldCreate{" +
            "seed=" + seed +
            ", worldSize=" + worldSize +
            '}';
    }
}
