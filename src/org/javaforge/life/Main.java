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
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

/**
 * My version of the Conway's Game of Life.
 *
 * @author	Jared Klett
 * @version	1.0 (4/14/1996)
 * @version 2.0 (7/22/2008)
 */

public class Main extends Frame {

    private Life life;
    private Window window;
    private GraphicsDevice device;

    public Main() {
        super();
        GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
        device = genv.getDefaultScreenDevice();
        if (!device.isFullScreenSupported()) {
            JOptionPane.showMessageDialog(this, "Sorry, but full screen is not supported on this computer.\nPerhaps you need to upgrade your Java virtual machine.", "Full screen not available", JOptionPane.WARNING_MESSAGE);
            System.exit(1);
        }
    }

    public void addNotify() {
        super.addNotify();
        window = new Window(this);
        window.setBackground(Color.black);
        window.setLayout(new BorderLayout());
        device.setFullScreenWindow(window);
    }

    public void begin() {
        life = new Life(window);
        window.addMouseListener(
                new MouseListener() {
                    public void mouseClicked(MouseEvent e) {
                        device.setFullScreenWindow(null);
                        life.setRunning(false);
                        System.exit(0);
                    }
                    public void mouseEntered(MouseEvent e) {}
                    public void mouseExited(MouseEvent e) {}
                    public void mousePressed(MouseEvent e) {}
                    public void mouseReleased(MouseEvent e) {}
                }
        );
        life.setRunning(true);
        life.start();
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.addNotify();
        main.begin();
    }

} // class Main