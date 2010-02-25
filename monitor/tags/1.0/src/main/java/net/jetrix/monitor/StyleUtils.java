/**
 * Jetrix TetriNET Server
 * Copyright (C) 2008  Emmanuel Bourg
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

package net.jetrix.monitor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class StyleUtils
{
    /** TetriNET to CSS style mapping */
    private static final Map<Character, String> STYLES = new HashMap<Character, String>();
    static
    {
        STYLES.put('\u0014', "color: red");
        STYLES.put('\u0004', "color: black");
        STYLES.put('\u000c', "color: green");
        STYLES.put('\u000e', "color: lime");
        STYLES.put('\u0011', "color: navy");
        STYLES.put('\u0005', "color: blue");
        STYLES.put('\u0003', "color: aqua");
        STYLES.put('\u0017', "color: teal");
        STYLES.put('\u0019', "color: yellow");
        STYLES.put('\u0012', "color: olive");
        STYLES.put('\u0010', "color: maroon");
        STYLES.put('\u000f', "color: silver");
        STYLES.put('\u0015', "color: silver");
        STYLES.put('\u000b', "color: gray");
        STYLES.put('\u0006', "color: gray");
        STYLES.put('\u0008', "color: fuchsia");
        STYLES.put('\u0013', "color: purple");
        STYLES.put('\u0002', "font-weight: bold");
        STYLES.put('\u0016', "font-style: italic");
        STYLES.put('\u001f', "text-decoration: underlined");
        STYLES.put('\u0018', "color: white");
    }

    public static String toHTML(String text)
    {
        StringBuilder result = new StringBuilder();

        Deque<Character> styleStack = new ArrayDeque<Character>();

        for (char c : text.toCharArray())
        {
            if (STYLES.keySet().contains(c))
            {
                if (styleStack.isEmpty() || styleStack.peek().charValue() != c)
                {
                    styleStack.push(c);
                    result.append("<span style=\"" + STYLES.get(c) + "\">");
                }
                else
                {
                    styleStack.pop();
                    result.append("</span>");
                }
            }
            else
            {
                result.append(c);
            }            
        }

        while (styleStack.pollFirst() != null)
        {
            result.append("</span>");
        }

        return result.toString();
    }

    /**
     * Remove the style markers from the specified string.
     *
     * @param text
     */
    public static String strip(String text)
    {
        StringBuilder result = new StringBuilder();

        for (char c : text.toCharArray())
        {
            if (!STYLES.keySet().contains(c))
            {
                result.append(c);
            }
        }

        return result.toString();
    }
}
