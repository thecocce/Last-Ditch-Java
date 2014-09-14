package com.gaugestructures.last_ditch.systems;

import com.gaugestructures.last_ditch.C;

public class TimeSystem extends GameSystem {
    private boolean active = true;
    private float game_delta = 0, rate = 1f, elapsed = 0;
    private int minute = 0, hour = 0, day = 1, month = 1, year = 3127;

    public void set_active(boolean active) {
        this.active = active;
    }

    public boolean is_active() {
        return active;
    }

    public String get_time() {
        if(hour < 10) {
            if(minute < 10) {
                return String.format("0%d:0%d", hour, minute);
            } else {
                return String.format("0%d:%d", hour, minute);
            }
        } else {
            if(minute < 10) {
                return String.format("%d:0%d", hour, minute);
            } else {
                return String.format("%d:%d", hour, minute);
            }
        }
    }

    public String get_date() {
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
            game_delta = rate * C.BOX_STEP;
            minute += game_delta;
            elapsed += game_delta;

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
}
