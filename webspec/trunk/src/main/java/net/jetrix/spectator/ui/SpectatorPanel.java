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

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.FlowLayout;

import net.jetrix.Field;
import net.jetrix.User;
import net.jetrix.spectator.PlayerResolver;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class SpectatorPanel extends JPanel implements PlayerResolver
{
    private FieldComponent[] fields = new FieldComponent[6];
    private ChatPanel chat;
    private GameEventsPanel events;

    public SpectatorPanel()
    {
        setDoubleBuffered(true);

        // initialiaze the fields
        setLayout(new FlowLayout());
        for (int i = 0; i < 6; i++)
        {
            fields[i] = new FieldComponent();
            add(fields[i]);
        }

        // initialiaze the player list
        /*JList players = new JList();
        players.setPreferredSize(new Dimension(100, 200));
        players.setBorder(BorderFactory.createEtchedBorder());
        add(players);*/

        // initialize the spectator list

        // initialize the chat window
        chat = new ChatPanel();
        chat.setSize(new Dimension(420, 250));
        chat.setPlayerResolver(this);
        add(chat);

        // initialize the game message window
        events = new GameEventsPanel();
        events.setSize(new Dimension(185, 250));
        events.setBorder(BorderFactory.createEtchedBorder());
        events.setPlayerResolver(this);
        add(events);
    }

    public Field getField(int i)
    {
        return (fields[i] != null && (i >=0 && i <= 5)) ? fields[i].getField() : null;
    }

    public void setField(int i, Field field)
    {
        if (fields[i] != null)
        {
            fields[i].setField(field);
        }
    }

    public User getPlayer(int i)
    {
        return ((i >=0 && i <= 5) && fields[i] != null) ? fields[i].getPlayer() : null;
    }

    public void setPlayer(int i, User player)
    {
        if (fields[i] != null)
        {
            fields[i].setPlayer(player);
        }
    }

    public void clearFields()
    {
        for (FieldComponent field : fields)
        {
            if (field != null)
            {
                field.getField().clear();
            }
        }
    }

    public ChatPanel getChatPanel()
    {
        return chat;
    }

    public GameEventsPanel getGameEventsPanel()
    {
        return events;
    }


}
