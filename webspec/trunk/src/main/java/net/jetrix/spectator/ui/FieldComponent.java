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

import net.jetrix.Field;
import net.jetrix.User;
import net.jetrix.config.Special;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static net.jetrix.Field.*;
import static net.jetrix.config.Special.*;

/**
 * A Swing component displaying a player field.
 *
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class FieldComponent extends JComponent
{
    private Image background;
    private Field field;
    private User player;
    private Map<Byte, Image> images;

    private int borderWidth = 1;
    private int titleHeight = 15;

    public FieldComponent()
    {
        ClassLoader loader = this.getClass().getClassLoader();

        // load the background image
        URL url = loader.getResource("images/background.png");
        background = new ImageIcon(url).getImage();

        // load the block images
        images = new HashMap<Byte, Image>();
        images.put(BLOCK_BLUE, new ImageIcon(loader.getResource("images/1.png")).getImage());
        images.put(BLOCK_YELLOW, new ImageIcon(loader.getResource("images/2.png")).getImage());
        images.put(BLOCK_GREEN, new ImageIcon(loader.getResource("images/3.png")).getImage());
        images.put(BLOCK_PURPLE, new ImageIcon(loader.getResource("images/4.png")).getImage());
        images.put(BLOCK_RED, new ImageIcon(loader.getResource("images/5.png")).getImage());

        for (Special special : Special.values())
        {
            images.put(((byte) special.getLetter()), new ImageIcon(loader.getResource("images/" + special.getLetter() + ".png")).getImage());
        }

        field = new Field();
    }

    public Field getField()
    {
        return field;
    }

    public void setField(Field field)
    {
        this.field = field;
        repaint();
    }

    public User getPlayer()
    {
        return player;
    }

    public void setPlayer(User player)
    {
        this.player = player;
    }

    public void paint(Graphics g)
    {
        // draw the border
        g.setColor(Color.black);
        Dimension size = getPreferredSize();
        g.drawRect(0, 0, (int) size.getWidth() - 1, (int) size.getHeight() - 1);

        // draw the title
        String text = null;
        if (player != null)
        {
            g.setColor(Color.black);
            g.setFont(new Font("sansserif", Font.PLAIN, 9));
            text = player.getName();
        }
        else
        {
            g.setColor(Color.gray);
            g.setFont(new Font("sansserif", Font.ITALIC, 9));
            text = "Empty";
        }

        int xpos = ((int) size.getWidth() - g.getFontMetrics().stringWidth(text)) / 2;
        int ypos = (titleHeight + g.getFontMetrics().getAscent()) / 2;
        g.drawString(text, xpos, ypos);

        // draw the separator
        g.setColor(Color.black);
        g.drawLine(0, titleHeight, (int) size.getWidth(), titleHeight);

        // draw the background
        g.drawImage(background, borderWidth, borderWidth + titleHeight, background.getWidth(null), background.getHeight(null), this);
        int blockWidth = background.getWidth(null) / Field.WIDTH;
        int blockHeight = background.getHeight(null) / Field.HEIGHT;

        // draw the field
        if (field != null)
        {
            for (int i = 0; i < Field.WIDTH; i++)
            {
                for (int j = 0; j < Field.HEIGHT; j++)
                {
                    Image image = images.get(field.getBlock(i, Field.HEIGHT - j - 1));
                    if (image != null)
                    {
                        g.drawImage(image, borderWidth + i * blockWidth, borderWidth + titleHeight + j * blockHeight, blockWidth, blockHeight, null);
                    }
                }
            }
        }
    }

    public Dimension getPreferredSize()
    {
        return new Dimension(background.getWidth(null) + 2 * borderWidth, background.getHeight(null) + 2 * borderWidth + titleHeight);
    }
}
