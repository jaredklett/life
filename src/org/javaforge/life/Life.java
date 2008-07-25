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
import java.util.Random;

/**
 * My version of the Conway's Game of Life.
 *
 * @author Jared Klett
 * @version 1.0 (4/14/1996)
 * @version 2.0 (7/22/2008)
 */

public class Life implements Runnable {

    public static final int VACANT = 0;
    public static final int HERBIVORE = 1;
    public static final int PREDATOR = 2;

    private Window window;
    private int[][] world;

    private Dimension d;

    private int cellSize;
    private long delay = 100;
    private boolean running;
    private Image offscreen;
    private Graphics buffer;
    private Thread thread;
    private Label countdown;
    private Label speed;
    private Label sizeLabel;
    private Label updownLabel = new Label("Hit up/down to go faster/slower");
    private Label leftrightLabel = new Label("Hit right/left for bigger/smaller");
    private Label pLabel = new Label("Hit P to add a predator");
    public static final int DEFAULT_CELL_SIZE = 5;
    public static final int RESEED_LIMIT = 2000;

    public Life(Window window) {
        this.window = window;
        d = window.getSize();

        Font font = new Font("SansSerif", Font.BOLD, 14);

        countdown = new Label("Ready");
        countdown.setForeground(Color.DARK_GRAY);
        countdown.setBackground(Color.BLACK);
        countdown.setFont(font);

        speed = new Label("Ready");
        speed.setForeground(Color.GRAY);
        speed.setBackground(Color.BLACK);
        speed.setFont(font);

        sizeLabel = new Label("Ready");
        sizeLabel.setForeground(Color.WHITE);
        sizeLabel.setBackground(Color.BLACK);
        sizeLabel.setFont(font);

        pLabel.setForeground(Color.RED.darker());
        pLabel.setBackground(Color.BLACK);
        pLabel.setFont(font);

        updownLabel.setForeground(Color.GREEN.darker());
        updownLabel.setBackground(Color.BLACK);
        updownLabel.setFont(font);

        leftrightLabel.setForeground(Color.YELLOW.darker());
        leftrightLabel.setBackground(Color.BLACK);
        leftrightLabel.setFont(font);

        cellSize = DEFAULT_CELL_SIZE;
        init();
    }

    public void init() {
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
        world = new int[w / cellSize][h / cellSize];
        // Seed the world
        Random random = new Random();
        for (int[] aWorld : world) {
            for (int j = 0; j < aWorld.length; j++) {
                aWorld[j] = random.nextInt(PREDATOR);
            }
        }
        offscreen = window.createImage(w, h);
        buffer = offscreen.getGraphics();

        window.add(countdown);
        countdown.setBounds(0, 0, 100, 20);

        window.add(speed);
        speed.setBounds(0, 24, 100, 20);

        window.add(sizeLabel);
        sizeLabel.setBounds(0, 48, 100, 20);

        window.add(pLabel);
        pLabel.setBounds(0, d.height - 20, 200, 20);

        window.add(leftrightLabel);
        leftrightLabel.setBounds(0, d.height - 40, 300, 20);

        window.add(updownLabel);
        updownLabel.setBounds(0, d.height - 60, 300, 20);
    }

    public void reseed() {
        Random random = new Random();
        for (int[] aWorld : world) {
            for (int j = 0; j < aWorld.length; j++) {
                if (aWorld[j] == VACANT) {
                    aWorld[j] = random.nextInt(PREDATOR);
                } else if (aWorld[j] == PREDATOR) {
                    aWorld[j] = VACANT;
                }
            }
        }
    }

    public void addPredator() {
        Random random = new Random();
        int x = random.nextInt(world.length);
        int y = random.nextInt(world[x].length);
        while (world[x][y] != VACANT) {
            x = random.nextInt(world.length);
            y = random.nextInt(world[x].length);
        }
        setInWorld(x, y, PREDATOR);
    }

    public void run() {
        sizeLabel.setText(Integer.toString(cellSize));
        int c = 0;
        int reseedCount = 1;
        while (running) {
            if (c > (RESEED_LIMIT * reseedCount)) {
                reseed();
                c = 0;
                reseedCount++;
            }
            //countdown.setText("Next panspermic event in " + Integer.toString(RESEED_LIMIT - c) + " iterations");
            countdown.setText(Integer.toString((RESEED_LIMIT * reseedCount) - c));
            speed.setText(Long.toString(delay));
            updateWorld();
            drawWorld();
            Graphics g = window.getGraphics();
            g.drawImage(offscreen, (d.width - offscreen.getWidth(window)) / 2, (d.height - offscreen.getHeight(window)) / 2, window);
            try { Thread.sleep(delay); } catch (InterruptedException e) { /* ignored */ }
            c++;
        }
    }

    public void updateWorld() {
        int[][] tempworld = new int[world.length][world[0].length];
        for (int i = 0; i < world.length; i++) {
            System.arraycopy(world[i], 0, tempworld[i], 0, world[i].length);
        }
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[i].length; j++) {
                int left = i - 1;
                int right = i + 1;
                // TODO: replace with mod?
                if (i == 0) {
                    left = world.length - 1;
                } else if (i == world.length - 1) {
                    right = 0;
                }
                int top = j - 1;
                int bottom = j + 1;
                if (j == 0) {
                    top = world[i].length - 1;
                } else if (j == world[i].length - 1) {
                    bottom = 0;
                }
                if (world[i][j] == PREDATOR) {
                    if (tempworld[left][top] == HERBIVORE) {
                        setInWorld(left, top, PREDATOR);
                    } else if (tempworld[i][top] == HERBIVORE) {
                        setInWorld(i, top, PREDATOR);
                    } else if (tempworld[right][top] == HERBIVORE) {
                        setInWorld(right, top, PREDATOR);
                    } else if (tempworld[left][j] == HERBIVORE) {
                        setInWorld(left, j, PREDATOR);
                    } else if (tempworld[right][j] == HERBIVORE) {
                        setInWorld(right, j, PREDATOR);
                    } else if (tempworld[left][bottom] == HERBIVORE) {
                        setInWorld(left, bottom, PREDATOR);
                    } else if (tempworld[i][bottom] == HERBIVORE) {
                        setInWorld(i, bottom, PREDATOR);
                    } else if (tempworld[right][bottom] == HERBIVORE) {
                        setInWorld(right, bottom, PREDATOR);
                    } else {
                        Random random = new Random();
                        int[] xdir = new int[] { left, right };
                        int[] ydir = new int[] { top, bottom };
                        int xrand = random.nextInt(xdir.length);
                        int yrand = random.nextInt(ydir.length);
                        setInWorld(xdir[xrand], ydir[yrand], PREDATOR);
                    }
                    setInWorld(i, j, VACANT);
                } else {
                    int neighbors = 0;
                    if (tempworld[left][top] == HERBIVORE) {
                        neighbors++;
                    }
                    if (tempworld[i][top] == HERBIVORE) {
                        neighbors++;
                    }
                    if (tempworld[right][top] == HERBIVORE) {
                        neighbors++;
                    }
                    if (tempworld[left][j] == HERBIVORE) {
                        neighbors++;
                    }
                    if (tempworld[right][j] == HERBIVORE) {
                        neighbors++;
                    }
                    if (tempworld[left][bottom] == HERBIVORE) {
                        neighbors++;
                    }
                    if (tempworld[i][bottom] == HERBIVORE) {
                        neighbors++;
                    }
                    if (tempworld[right][bottom] == HERBIVORE) {
                        neighbors++;
                    }
                    if ((neighbors < 2) || (neighbors > 3)) {
                        setInWorld(i, j, VACANT);
                    } else if (neighbors == 3) {
                        setInWorld(i, j, HERBIVORE);
                    }
                }
            }
        }
    }

    public void drawWorld() {
        buffer.setColor(Color.black);
        buffer.fillRect(0, 0, d.width, d.height);
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[i].length; j++) {
                if (world[i][j] == HERBIVORE) {
                    buffer.setColor(Color.CYAN);
                    buffer.fillOval(j * cellSize, i * cellSize, cellSize, cellSize);
                } else if (world[i][j] == PREDATOR) {
                    buffer.setColor(Color.RED);
                    buffer.fillOval(j * cellSize, i * cellSize, cellSize, cellSize);
                }
            }
        }
    }

    public void start() {
        setRunning(true);
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        setRunning(false);
        thread.interrupt();
        try { thread.join(1000); } catch (InterruptedException e) { /* ignored */ }
        buffer.dispose();
        offscreen.flush();
        buffer = null;
        offscreen = null;
        //window.remove(countdown);
        //window.remove(speed);
    }

    public long getDelay() {
        return delay;
    }

    public int getCellSize() {
        return cellSize;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setDelay(long delay) {
        if (delay >= 10)
            this.delay = delay;
    }

    public void setCellSize(int cellSize) {
        // TODO: no magic numbers
        if (cellSize > 1 && cellSize < 16) {
            this.cellSize = cellSize;
            stop();
            init();
            start();
        }
    }

    public synchronized void setInWorld(int x, int y, int cell) {
        world[x][y] = cell;
    }

} // class Life