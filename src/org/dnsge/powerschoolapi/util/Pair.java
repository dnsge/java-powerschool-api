package org.dnsge.powerschoolapi.util;

/**
 * Simple class to hold a pair of objects
 * <p>
 * See <a href="https://stackoverflow.com/a/4777636/5483423">this</a> for original
 *
 * @param <L> Type of Left Object
 * @param <R> Type of Right Object
 */
public class Pair<L, R> {
    private L l;
    private R r;

    public Pair(L l, R r) {
        this.l = l;
        this.r = r;
    }

    public L getL() {
        return l;
    }

    public R getR() {
        return r;
    }

    public void setL(L l) {
        this.l = l;
    }

    public void setR(R r) {
        this.r = r;
    }
}