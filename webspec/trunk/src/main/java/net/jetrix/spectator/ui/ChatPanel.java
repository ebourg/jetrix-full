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

import net.jetrix.messages.*;
import net.jetrix.spectator.PlayerResolver;
import net.jetrix.User;

import javax.swing.*;
import javax.swing.text.StyledDocument;
import java.awt.*;

/**
 * Text panel displaying the party line.
 * 
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class ChatPanel extends JScrollPane
{
    private StyledDocument doc;
    private PlayerResolver resolver;

    public ChatPanel()
    {
        getInsets().set(0, 0, 0, 0);

        setPreferredSize(new Dimension(420, 250));
        setBorder(BorderFactory.createEtchedBorder());

        JTextPane textPane = new JTextPane();
        textPane.setPreferredSize(getPreferredSize());
        textPane.setEditable(false);
        textPane.getInsets().set(0, 0, 0, 0);

        setViewportView(textPane);

        doc = textPane.getStyledDocument();
    }

    public void setPlayerResolver(PlayerResolver resolver)
    {
        this.resolver = resolver;
    }

    public void append(TextMessage message)
    {
        if (message instanceof GmsgMessage)
        {
            TetrinetColor.append(doc, "<i><gray>" + message.getText());
        }
        else if (message instanceof PlineActMessage)
        {
            TetrinetColor.append(doc, "<purple>* <b>" + message.getSlot() + "</b> " + message.getText());
        }
        else
        {
            StringBuffer buffer = new StringBuffer();

            if (message.getSlot() == 0)
            {
                buffer.append("<<b><u>Server</u></b>> ");
            }
            else
            {
                User user = resolver.getPlayer(message.getSlot() - 1);
                if (user != null)
                {
                    buffer.append("<<b><u>" + user.getName() + "</u></b>> ");
                }
            }

            if (message.getText() != null)
            {
                buffer.append(message.getText());
            }

            TetrinetColor.append(doc, buffer.toString());
        }

        BoundedRangeModel bar = getVerticalScrollBar().getModel();
        bar.setValue(bar.getMaximum());
    }

    public void append(NewGameMessage message)
    {
        TetrinetColor.append(doc, "<red>*** The Game Has <b>Started</b>");
    }

    public void append(EndGameMessage message)
    {
        TetrinetColor.append(doc, "<red>*** The Game Has <b>Ended</b>");
    }

    public void append(IngameMessage m)
    {
        TetrinetColor.append(doc, "<red>*** The Game is in <b>Progress</b>");
    }

    public void append(JoinMessage m)
    {
        User user = resolver.getPlayer(m.getSlot() - 1);
        if (user != null)
        {
            TetrinetColor.append(doc, "<green>*** <b>" + user.getName() + "</b> has joined");
        }
    }

    public void append(LeaveMessage m)
    {
        User user = resolver.getPlayer(m.getSlot() - 1);
        if (user != null)
        {
            TetrinetColor.append(doc, "<green>*** <b>" + user.getName() + "</b> has left");
        }
    }
}
