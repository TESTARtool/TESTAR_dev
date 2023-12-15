package strategynodes.data;

public class Weight
{
    private final int WEIGHT;

    public Weight(Integer weight)
    { this.WEIGHT = (weight > 0) ? weight : 1; }

    public int GetWeight()
    { return this.WEIGHT; }

    @Override
    public String toString()
    { return Integer.toString(WEIGHT); }
}
