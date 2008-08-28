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

package net.jetrix.spectator;

import java.awt.Dimension;
import java.io.IOException;
import javax.swing.JApplet;
import javax.swing.JMenuBar;

import net.jetrix.spectator.ui.ChannelMenu;
import net.jetrix.spectator.ui.SpectatorPanel;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class SpectatorApplet extends JApplet
{
    private ChannelMenu menu;
    private SpectatorAgent agent;

    public void init()
    {
        // agent setup
        String name = getParameter("username");
        String password = getParameter("password");

        if (name == null)
        {
            // generate a random name if no name was provided
            name = "WebSpec-" + ((int) (Math.random() * 100000));
        }
        agent = new SpectatorAgent(name, password);

        // build the frame
        setPreferredSize(new Dimension(640, 520));

        if (!"false".equals(getParameter("menu")))
        {
            // add the menu bar
            JMenuBar menuBar = new JMenuBar();
            menuBar.setVisible(true);
            setJMenuBar(menuBar);

            menu = new ChannelMenu(agent);
            menuBar.add(menu);
        }

        // add the content
        SpectatorPanel panel = new SpectatorPanel();
        getContentPane().add(panel);

        agent.setPanel(panel);
    }

    public void start()
    {
        // connect to the server
        try
        {
            agent.connect(getParameter("hostname"));

            String channel = getParameter("channel");
            if (channel != null && channel.trim().length() > 0)
            {
                agent.join(channel.trim());
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void stop()
    {
        if (menu != null)
        {
            menu.stop();
        }

        try
        {
            agent.disconnect();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
