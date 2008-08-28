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
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import net.jetrix.spectator.ui.SpectatorFrame;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class Spectator
{
    private static final String HOSTNAME = "tetrinet.no";
    private static final String PASSWORD = "rien";

    private static SpectatorAgent agent;

    public static void main(String[] argv) throws Exception
    {
        // agent setup
        String name = "WebSpec-" + ((int) (Math.random() * 100000));
        agent = new SpectatorAgent(name, PASSWORD);

        // build the frame
        final SpectatorFrame frame = new SpectatorFrame(agent);
        frame.setSize(640, 520);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        toolkit.setDynamicLayout(true);
        Dimension screenSize = toolkit.getScreenSize();
        frame.setLocation((int) (screenSize.getWidth() - frame.getWidth()) / 2, (int) (screenSize.getHeight() - frame.getHeight()) / 2);

        agent.setPanel(frame.getSpectatorPanel());

        // register the listeners
        frame.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                frame.dispose();
                try
                {
                    agent.disconnect();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        });

        // show the frame
        frame.setVisible(true);

        // connect to the server
        agent.connect(HOSTNAME);
        agent.join("#lfjr");
    }

}
