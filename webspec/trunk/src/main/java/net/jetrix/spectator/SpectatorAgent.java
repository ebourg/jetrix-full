/**
 * Jetrix TetriNET Server
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

import java.io.IOException;

import net.jetrix.Field;
import net.jetrix.Message;
import net.jetrix.User;
import net.jetrix.agent.TSpecAgent;
import net.jetrix.messages.EndGameMessage;
import net.jetrix.messages.FieldMessage;
import net.jetrix.messages.IngameMessage;
import net.jetrix.messages.JoinMessage;
import net.jetrix.messages.LeaveMessage;
import net.jetrix.messages.NewGameMessage;
import net.jetrix.messages.SpecialMessage;
import net.jetrix.messages.TextMessage;
import net.jetrix.spectator.ui.SpectatorPanel;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class SpectatorAgent extends TSpecAgent
{
    private SpectatorPanel panel;

    protected void send(String message) throws IOException
    {
        super.send(message);
    }

    public void onMessage(Message m)
    {
        if (m instanceof TextMessage)
        {
            panel.getChatPanel().append((TextMessage) m);
        }
    }

    public SpectatorAgent(String name, String password)
    {
        super(name, password);
    }

    public void setPanel(SpectatorPanel panel)
    {
        this.panel = panel;
    }

    public void onMessage(FieldMessage m)
    {
        Field field = panel.getField(m.getSlot() - 1);
        field.update(m);
        panel.repaint();
    }

    public void onMessage(JoinMessage m)
    {
        User player = new User();
        player.setName(m.getName());
        panel.setPlayer(m.getSlot() - 1, player);
        panel.repaint();
        panel.getChatPanel().append(m);
    }

    public void onMessage(LeaveMessage m)
    {
        panel.getChatPanel().append(m);
        panel.setPlayer(m.getSlot() - 1, null);
        panel.repaint();
    }

    public void onSpecial(SpecialMessage m)
    {
        panel.getGameEventsPanel().append(m);
    }

    public void onMessage(NewGameMessage m)
    {
        panel.clearFields();
        panel.getGameEventsPanel().clear();
        panel.getChatPanel().append(m);
    }

    public void onMessage(EndGameMessage m)
    {
        panel.getChatPanel().append(m);
    }

    public void onMessage(IngameMessage m)
    {
        panel.getChatPanel().append(m);
    }
}
