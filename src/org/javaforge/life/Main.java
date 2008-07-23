/*
 * @(#)Main.java
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

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * My version of the Conway's Game of Life.
 *
 * @author Jared Klett
 * @version	1.0 (4/14/1996)
 * @version 2.0 (7/22/2008)
 */

public class Main {

    private Life life;
    private JFrame frame;
    private GraphicsDevice device;

    public Main() {
        GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
        device = genv.getDefaultScreenDevice();
        if (!device.isFullScreenSupported()) {
            JOptionPane.showMessageDialog(frame, "Sorry, but full screen is not supported on this computer.\nPerhaps you need to upgrade your Java virtual machine.", "Full screen not available", JOptionPane.WARNING_MESSAGE);
            System.exit(1);
        }
        frame = new JFrame();
        frame.setUndecorated(true);
        frame.getContentPane().setBackground(Color.BLACK);
        frame.addKeyListener(
                new KeyListener() {
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_UP)
                            life.setDelay(life.getDelay() - 10);
                        else if (e.getKeyCode() == KeyEvent.VK_DOWN)
                            life.setDelay(life.getDelay() + 10);
                        else if (e.getKeyCode() == KeyEvent.VK_LEFT)
                            life.setCellSize(life.getCellSize() - 1);
                        else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                            life.setCellSize(life.getCellSize() + 1);
                    }
                    public void keyReleased(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                            life.setRunning(false);
                            device.setFullScreenWindow(null);
                            System.exit(0);
                        }
                    }
                    public void keyTyped(KeyEvent e) { }
                }
        );
        device.setFullScreenWindow(frame);
    }

    public void begin() {
        life = new Life(frame);
        //life.setRunning(true);
        life.start();
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.begin();
    }

} // class Main