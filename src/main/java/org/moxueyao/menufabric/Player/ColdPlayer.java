package org.moxueyao.menufabric.Player;

public class ColdPlayer {

    private final int interval;
    private final String name;
    private long lastClock;

    public ColdPlayer(String name, int interval) {
        this.name = name;
        this.interval = interval;
        this.lastClock = 0;
    }

    public String getName() {
        return name;
    }

    /**
     * 判断是否冷却
     */
    public boolean isCold() {
        long now = System.currentTimeMillis();
        if (now - this.lastClock > 1000L * interval) {
            this.lastClock = now;
            return false;
        }
        return true;
    }

    /**
     * 获取冷却时间
     */
    public int getColdDown() {
        long now = System.currentTimeMillis();
        long cd = this.lastClock + 1000L * interval - now;
        int time = 0;
        if (cd > 0) {
            time =  (int) cd / 1000;
        }
        if(time == 0)
            return 1;
        return time;
    }

    public void reSetColdDown() {
        this.lastClock = 0;
    }
}
