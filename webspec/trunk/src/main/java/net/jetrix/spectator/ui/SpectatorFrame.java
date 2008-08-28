/**
 * Jetrix TetriNET Spectator
 * Copyright (C) 2005  Emmanuel Bourg
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package net.jetrix.spectator.ui;

import java.awt.Dimension;
import java.awt.HeadlessException;
import javax.swing.JFrame;
import javax.swing.JMenuBar;

import net.jetrix.agent.TSpecAgent;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class SpectatorFrame extends JFrame
{
    private ChannelMenu menu;
    private SpectatorPanel panel;

    public SpectatorFrame(TSpecAgent agent) throws HeadlessException
    {
        // build the frame
        setTitle("Jetrix TetriNET Spectator");
        setPreferredSize(new Dimension(640, 520));

        // add the menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setVisible(true);
        setJMenuBar(menuBar);

        menu = new ChannelMenu(agent);
        menuBar.add(menu);

        /*menu = new JMenu("Commands");
        menuBar.add(menu);

        menu.add(new JMenuItem("help"));
        menu.add(new JMenuItem("who"));*/

        // add the content
        panel = new SpectatorPanel();
        getContentPane().add(panel);
    }

    public SpectatorPanel getSpectatorPanel()
    {
        return panel;
    }

    public void dispose()
    {
        if (menu != null)
        {
            menu.stop();
        }
        super.dispose();
    }

}
