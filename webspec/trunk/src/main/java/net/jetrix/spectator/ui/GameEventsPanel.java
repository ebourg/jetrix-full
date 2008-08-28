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
import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import net.jetrix.User;
import net.jetrix.messages.*;
import net.jetrix.spectator.PlayerResolver;

/**
 * Text panel displaying the events occuring during the game.
 * 
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class GameEventsPanel extends JScrollPane
{
    private StyledDocument doc;
    private PlayerResolver resolver;

    public GameEventsPanel()
    {
        setPreferredSize(new Dimension(185, 250));
        setBorder(BorderFactory.createEtchedBorder());

        JTextPane textPane = new JTextPane();
        textPane.setPreferredSize(getPreferredSize());
        textPane.setEditable(false);
        textPane.getInsets().set(0, 0, 0, 0);

        setViewportView(textPane);

        doc = textPane.getStyledDocument();
    }

    /**
     * Clear the list of game events.
     */
    public void clear()
    {
        try
        {
            doc.remove(0, doc.getLength());
        }
        catch (BadLocationException e)
        {
            e.printStackTrace();
        }
    }

    public void append(SpecialMessage message)
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<purple><b>");

        if (message instanceof AddLineMessage)
        {
            buffer.append("Add Line");
        }
        else if (message instanceof ClearLineMessage)
        {
            buffer.append("Clear Line");
        }
        else if (message instanceof NukeFieldMessage)
        {
            buffer.append("Nuke Field");
        }
        else if (message instanceof BlockQuakeMessage)
        {
            buffer.append("Blockquake");
        }
        else if (message instanceof BlockBombMessage)
        {
            buffer.append("Block Bomb");
        }
        else if (message instanceof ClearSpecialsMessage)
        {
            buffer.append("Clear Special Blocks");
        }
        else if (message instanceof GravityMessage)
        {
            buffer.append("Block Gravity");
        }
        else if (message instanceof RandomClearMessage)
        {
            buffer.append("Random Blocks Clear");
        }
        else if (message instanceof SwitchFieldsMessage)
        {
            buffer.append("Switch Fields");
        }
        else if (message instanceof OneLineAddedMessage)
        {
            buffer.append("1 Line Added to All");
        }
        else if (message instanceof TwoLinesAddedMessage)
        {
            buffer.append("2 Lines Added to All");
        }
        else if (message instanceof FourLinesAddedMessage)
        {
            buffer.append("4 Lines Added to All");
        }

        buffer.append("</b></purple>");

        if (message.getSlot() != 0)
        {
            buffer.append(" on <b><darkBlue>");
            User user = resolver.getPlayer(message.getSlot() - 1);
            if (user != null)
            {
                buffer.append(user.getName());
            }
            buffer.append("</darkBlue></b>");
        }

        buffer.append(" from <b><darkBlue>");
        User user = resolver.getPlayer(message.getFromSlot() - 1);
        if (user != null)
        {
            buffer.append(user.getName());
        }
        buffer.append("</darkBlue></b>");

        TetrinetColor.append(doc, buffer.toString());

        BoundedRangeModel bar = getVerticalScrollBar().getModel();
        bar.setValue(bar.getMaximum());
    }

    public void setPlayerResolver(PlayerResolver resolver)
    {
        this.resolver = resolver;
    }
}
