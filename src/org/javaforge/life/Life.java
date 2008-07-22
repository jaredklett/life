/*
 * @(#)Life.java
 *
 * Copyright (c) 1996-2008 by Jared Klett
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *  * Neither the name of javaforge.org nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.javaforge.life;

import java.awt.*;

/**
 * My version of the Conway's Game of Life.
 *
 * @author	Jared Klett
 * @version	1.0 (4/14/1996)
 * @version 2.0 (7/22/2008)
 */

public class Life implements Runnable {

    private Window window;
    private boolean[][] world;

    private Dimension d;

    private boolean running;
    private Image offscreen;
    private Graphics buffer;
    //private Label countdown;
    public static final int CELL_SIZE = 4;
    public static final int RESEED_LIMIT = 2000;

    public Life(Window window) {
        this.window = window;
        d = window.getSize();
        int w;
        int h;
        if (d.width < d.height) {
            w = d.width;
            h = d.width;
        } else {
            w = d.height;
            h = d.height;
        }

        // Create a new world
        world = new boolean[w / CELL_SIZE][h / CELL_SIZE];

        // Seed the world
        for (boolean[] aWorld : world) {
            for (int j = 0; j < aWorld.length; j++) {
                double random = Math.random();
                aWorld[j] = random < 0.1;
            }
        }

        offscreen = window.createImage(w, h);
        buffer = offscreen.getGraphics();
        //countdown = new Label("Ready");
        //countdown.setForeground(Color.WHITE);
        //Font font = new Font("SansSerif", Font.BOLD, 14);
        //countdown.setFont(font);
    }

    public void reseed() {
        for (boolean[] aWorld : world) {
            for (int j = 0; j < aWorld.length; j++) {
                if (!aWorld[j]) {
                    double random = Math.random();
                    aWorld[j] = random < 0.1;
                }
            }
        }
    }

    public void run() {
        int c = 0;
        //countdown.setBounds(0, 0, 100, 50);
        while (running) {
            if (c > RESEED_LIMIT) {
                reseed();
                c = 0;
            }
            //countdown.setText("Next panspermic event in " + Integer.toString(RESEED_LIMIT - c) + " iterations");
            updateWorld();
            drawWorld();
            Graphics g = window.getGraphics();
            g.drawImage(offscreen, (d.width - offscreen.getWidth(window)) / 2, (d.height - offscreen.getHeight(window)) / 2, window);
            try { Thread.sleep(10); } catch (InterruptedException e) { /* ignored */ }
            c++;
        }
    }

    public void updateWorld() {
        boolean[][] tempworld = new boolean[world.length][world[0].length];

        for(int i = 0; i < world.length; i++) {
            System.arraycopy(world[i], 0, tempworld[i], 0, world[i].length);
        }

        for(int i = 0; i < world.length; i++) {
            for(int j = 0; j < world[i].length; j++) {
                int left = i - 1;
                int right = i + 1;

                // TODO: replace with mod?
                if(i == 0) {
                    left = world.length - 1;
                } else if (i == world.length - 1) {
                    right = 0;
                }

                int top = j - 1;
                int bottom = j + 1;

                if(j == 0) {
                    top = world[i].length - 1;
                } else if(j == world[i].length - 1) {
                    bottom = 0;
                }

                int neighbors = 0;

                if(tempworld[left][top]) {
                    neighbors++;
                }

                if(tempworld[i][top]) {
                    neighbors++;
                }

                if(tempworld[right][top]) {
                    neighbors++;
                }

                if(tempworld[left][j]) {
                    neighbors++;
                }

                if(tempworld[right][j]) {
                    neighbors++;
                }

                if(tempworld[left][bottom]) {
                    neighbors++;
                }

                if(tempworld[i][bottom]) {
                    neighbors++;
                }

                if(tempworld[right][bottom]) {
                    neighbors++;
                }

                if((neighbors < 2) || (neighbors > 3)) {
                    world[i][j] = false;
                } else if (neighbors == 3) {
                    world[i][j] = true;
                }
            }
        }
    }

    public void drawWorld() {
        buffer.setColor(Color.black);
        buffer.fillRect (0, 0, d.width, d.height);

        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[i].length; j++) {
                if (world[i][j]) {
                    buffer.setColor(Color.cyan);
                    buffer.fillOval(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }
    }

    public void start() {
        new Thread(this).start();
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

} // class Life