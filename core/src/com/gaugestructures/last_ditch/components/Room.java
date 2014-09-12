package com.gaugestructures.last_ditch.components;

import java.util.ArrayList;
import java.util.Random;

public class Room extends Component {
    private Random rnd = new Random();
    private int x, y, x1, y1, x2, y2, w, h;

    public Room(int x, int y, int w, int h) {
        this.w = w;
        this.h = h;
        this.x1 = this.x = x;
        this.y1 = this.y = y;
        this.x2 = x + w;
        this.y2 = y + h;
    }

    public boolean intersects(int x1, int y1, int x2, int y2) {
        return !(this.x2 < x1 || x2 < this.x1 ||
                 this.y2 < y1 || y2 < this.y1);
    }

    public void expand(ArrayList<Room> rooms, Room master) {
        boolean check = false;
        int direction = rnd.nextInt(4);

        for (Room room : rooms) {
            if (room == this) {
                continue;
            }

            switch (direction) {
                case 0:
                    if (room.intersects(x1 - 1, y1, x2, y2) || x1 - 1 < master.getX1()) {
                        check = true;
                    }
                    break;
                case 1:
                    if (room.intersects(x1, y1 - 1, x2, y2) || y1 - 1 < master.getY1()) {
                        check = true;
                    }
                    break;
                case 2:
                    if (room.intersects(x1, y1, x2 + 1, y2) || x2 + 1 < master.getX2()) {
                        check = true;
                    }
                    break;
                case 3:
                    if (room.intersects(x1, y1, x2, y2 + 1) || y2 + 1 < master.getY2()) {
                        check = true;
                    }
                    break;
            }
        }

        if (!check) {
            switch (direction) {
                case 0:
                    x1 -= 1;
                    break;
                case 1:
                    y1 -= 1;
                    break;
                case 2:
                    x2 += 1;
                    break;
                case 3:
                    y2 += 1;
                    break;
            }
        }
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x1 = this.x = x;
        w = x2 - x1;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y1 = this.y = y;
        h = y2 - y1;
    }

    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
       this.x1 = this.x = x1;
       w = x2 - x1;
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        this.y1 = this.y = y1;
        h = y2 - y1;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
        w = x2 - x1;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
        h = y2 - y1;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
        x2 = x1 + w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
        y2 = y1 + h;
    }
}
