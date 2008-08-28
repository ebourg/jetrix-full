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

import static java.awt.Color.*;

import net.jetrix.protocols.TetrinetProtocol;
import net.jetrix.Protocol;

import javax.swing.text.*;
import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class TetrinetColor
{
    public static final Color LIGHT_GREEN = new Color(128, 128, 255);
    public static final Color DARK_BLUE = new Color(0, 0, 128);
    public static final Color AQUA = new Color(0, 128, 128);
    public static final Color KAKI = new Color(128, 128, 0);
    public static final Color BROWN = new Color(128, 0, 0);
    public static final Color PURPLE = new Color(128, 0, 128);
    public static final Color GREEN = new Color(0, 128, 0);

    private static void updateStyle(Style style, char code)
    {
        switch (code)
        {
            case '\u0014': // red
                StyleConstants.setForeground(style, RED);
                break;
            case '\u0004': // black
                StyleConstants.setForeground(style, BLACK);
                break;
            case '\u000c': // green
                StyleConstants.setForeground(style, GREEN);
                break;
            case '\u000e': // lightGreen
                StyleConstants.setForeground(style, LIGHT_GREEN);
                break;
            case '\u0011': // darkBlue
                StyleConstants.setForeground(style, DARK_BLUE);
                break;
            case '\u0005': // blue
                StyleConstants.setForeground(style, Color.BLUE);
                break;
            case '\u0003': // cyan
                StyleConstants.setForeground(style, CYAN);
                break;
            case '\u0017': // aqua
                StyleConstants.setForeground(style, AQUA);
                break;
            case '\u0019': // yellow
                StyleConstants.setForeground(style, YELLOW);
                break;
            case '\u0012': // kaki
                StyleConstants.setForeground(style, KAKI);
                break;
            case '\u0010': // brown
                StyleConstants.setForeground(style, BROWN);
                break;
            case '\u000f': // lightGray
                StyleConstants.setForeground(style, LIGHT_GRAY);
                break;
            case '\u0006': // gray
                StyleConstants.setForeground(style, GRAY);
                break;
            case '\u0008': // magenta
                StyleConstants.setForeground(style, MAGENTA);
                break;
            case '\u0013': // purple
                StyleConstants.setForeground(style, PURPLE);
                break;
            case '\u0002': // b
                StyleConstants.setBold(style, true);
                break;
            case '\u0016': // i
                StyleConstants.setItalic(style, true);
                break;
            case '\u001f': // u
                StyleConstants.setUnderline(style, true);
                break;
            case '\u0018': // white
                StyleConstants.setForeground(style, WHITE);
                break;
        }
    }

    public static void append(StyledDocument document, String text)
    {
        Protocol protocol = new TetrinetProtocol();
        text = protocol.applyStyle(text);

        List<Character> styles = new ArrayList<Character>();
        StringBuffer chunk = new StringBuffer();

        try
        {
            for (int i = 0; i < text.length(); i++)
            {
                char c = text.charAt(i);
                if (isStyleCode(c))
                {
                    Style style = getComputedStyle(styles);
                    document.insertString(document.getLength(), chunk.toString(), style);
                    chunk = new StringBuffer();

                    if (!styles.remove(new Character(c)))
                    {
                        styles.add(c);
                    }
                }
                else
                {
                    chunk.append(c);
                }
            }

            if (chunk.length() > 0)
            {
                Style style = getComputedStyle(styles);
                document.insertString(document.getLength(), chunk.toString(), style);
            }

            document.insertString(document.getLength(), "\n", null);
        }
        catch (BadLocationException e)
        {
            e.printStackTrace();
        }
    }

    public static boolean isStyleCode(char c)
    {
        return c < '\u0020';
    }

    private static Style getComputedStyle(List<Character> styles)
    {
        Style style = getDefaultStyle();

        if (!styles.isEmpty())
        {
            for (Character code : styles)
            {
                TetrinetColor.updateStyle(style, code);
            }
        }

        return style;
    }

    public static Style getDefaultStyle()
    {
        Style style = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setBold(style, false);
        StyleConstants.setUnderline(style, false);
        StyleConstants.setItalic(style, false);
        StyleConstants.setForeground(style, Color.BLACK);
        StyleConstants.setFontSize(style, 11);

        return style;
    }
}
