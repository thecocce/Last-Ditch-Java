package com.gaugestructures.last_ditch.systems;

import com.badlogic.gdx.Gdx;
import com.gaugestructures.last_ditch.C;

public class TimeSystem extends GameSystem {
    private boolean active = true;
    private float gameDelta = 0, rate = 1f, elapsed = 0;
    private float minute = 0;
    private int hour = 0, day = 1, month = 1, year = 3127;

    public void toggleActive() {
        active = !active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public String getTime() {
        if(hour < 10) {
            if(minute < 10) {
                return String.format("0%d:0%d", hour, (int)minute);
            } else {
                return String.format("0%d:%d", hour, (int)minute);
            }
        } else {
            if(minute < 10) {
                return String.format("%d:0%d", hour, (int)minute);
            } else {
                return String.format("%d:%d", hour, (int)minute);
            }
        }
    }

    public String getDate() {
        if(month < 10) {
            if(day < 10) {
                return String.format("0%d.0%d.%d", day, month, year);
            } else {
                return String.format("%d.0%d.%d", day, month, year);
            }
        } else {
            if(day < 10) {
                return String.format("0%d.%d.%d", day, month, year);
            } else {
                return String.format("%d.%d.%d", day, month, year);
            }
        }
    }

    public void update() {
        if(active) {
            gameDelta = rate * C.BOX_STEP;
            minute += gameDelta;
            elapsed += gameDelta;

            if(minute > 60) {
                minute = 0;
                hour += 1;

                if(hour > 23) {
                    hour = 0;
                    day += 1;

                    if(day > 30) {
                        day = 1;
                        month += 1;

                        if(month > 12) {
                            month = 1;
                            year += 1;
                        }
                    }
                }
            }
        }
    }

    public float getGameDelta() {
        return gameDelta;
    }

    public float getRate() {
        return rate;
    }
}
