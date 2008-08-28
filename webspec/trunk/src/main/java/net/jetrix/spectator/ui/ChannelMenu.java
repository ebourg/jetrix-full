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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import net.jetrix.GameState;
import net.jetrix.agent.ChannelInfo;
import net.jetrix.agent.QueryAgent;
import net.jetrix.agent.TSpecAgent;
import net.jetrix.messages.CommandMessage;

/**
 * Menu containing the list of the channels available on the server.
 * The channels are refreshed automatically 
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ChannelMenu extends JMenu
{
    private Timer timer = new Timer();
    private List<ChannelInfo> channels;
    private String channelName;
    private TSpecAgent agent;

    public ChannelMenu(TSpecAgent agent)
    {
        this.agent = agent;

        setText("Channels");
        setEnabled(false);

        // start the timer refreshing the menu
        timer.schedule(new TimerTask()
        {
            public void run()
            {
                try
                {
                    refreshMenu();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }, 1000, 10000);

        addMenuListener(new ChannelMenuListener());
    }

    public void stop()
    {
        timer.cancel();
    }

    private void refreshMenu() throws IOException
    {
        if (agent.getHostname() != null)
        {
            // retrieve the channel information
            QueryAgent qagent = new QueryAgent();
            qagent.connect(agent.getHostname());
            channels = qagent.getChannels();
            qagent.disconnect();

            if (channels != null && !channels.isEmpty())
            {
                setEnabled(true);
            }
        }
    }

    private void rebuild()
    {
        if (channels != null)
        {
            // build a new button group
            ButtonGroup group = new ButtonGroup();
            for (ChannelInfo channel : channels)
            {
                JRadioButtonMenuItem item = new JRadioButtonMenuItem();
                item.setText(channel.getName() + " (" + channel.getPlayernum() + " / " + channel.getPlayermax() + ")");
                item.setActionCommand(channel.getName());

                if (channel.getStatus() != GameState.STOPPED.getValue())
                {
                    // highlight active channels
                    item.setForeground(Color.GREEN);
                }
                else if (channel.isEmpty())
                {
                    // highlight non empty channels
                    item.setForeground(Color.LIGHT_GRAY);
                }

                if (channel.getName().equals(channelName))
                {
                    item.setSelected(true);
                }

                group.add(item);
            }

            // define the action listener
            final ActionListener actionListener = new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    String chan = e.getActionCommand();
                    channelName = chan;

                    CommandMessage command = new CommandMessage();
                    command.setCommand("join");
                    command.addParameter("#" + chan);
                    try
                    {
                        agent.send(command);
                    }
                    catch (IOException e1)
                    {
                        e1.printStackTrace();
                    }
                }
            };

            // rebuild the menu
            removeAll();

            Enumeration<AbstractButton> it = group.getElements();
            while (it.hasMoreElements())
            {
                AbstractButton button = it.nextElement();
                button.addActionListener(actionListener);
                add(button);
            }
        }

        // todo: preserve the selected item
    }

    class ChannelMenuListener implements MenuListener
    {
        public void menuSelected(MenuEvent e)
        {
            rebuild();
        }

        public void menuDeselected(MenuEvent e) { }

        public void menuCanceled(MenuEvent e) { }
    }
}
