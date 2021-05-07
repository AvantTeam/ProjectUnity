package unity.type;

import arc.math.*;
import arc.math.Interp.*;

public class AntiCheatVariables{
    private final static Interp defaultIn = new Pow(2);

    /** Minimum damage before curving down. */
    public final float damageThreshold;
    public final float maxDamageThreshold;
    public final Interp curveType;
    /** Damage can't be higher than this value */
    public final float maxDamageTaken;
    /** Starts resisting if damage is higher than this value */
    public final float resistStart;
    public final float resistScl;
    public final float resistDuration;
    /** How long after getting damage before the resist start decreasing. */
    public final float resistTime;
    public final float invincibilityDuration;
    public final int invincibilityArray;

    public AntiCheatVariables(float dt, float mdthr, Interp ct, float mdtkn, float rStrt, float rScl, float rd, float rt, float inD, int inf){
        damageThreshold = dt;
        maxDamageThreshold = mdthr;
        curveType = ct;
        maxDamageTaken = mdtkn;
        resistStart = rStrt;
        resistScl = rScl;
        resistDuration = rd;
        resistTime = rt;
        invincibilityDuration = inD;
        invincibilityArray = inf;
    }

    public AntiCheatVariables(float dt, float mdthr, float mdtkn, float rStrt, float rScl, float rd, float rt, float inD, int inf){
        this(dt, mdthr, defaultIn, mdtkn, rStrt, rScl, rd, rt, inD, inf);
    }
}
