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

import junit.framework.TestCase;

/**
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 */
public class StyleUtilsTest extends TestCase
{
    public void testToHTML()
    {
        assertEquals("foo", StyleUtils.toHTML("foo"));
        assertEquals("<span style=\"font-weight: bold\">foo</span>", StyleUtils.toHTML("\u0002foo"));
        assertEquals("<span style=\"color: blue\">f<span style=\"color: white\">o<span style=\"color: red\">o</span></span></span>", StyleUtils.toHTML("\u0005f\u0018o\u0014o"));
        assertEquals("<span style=\"color: blue\">f</span>oo", StyleUtils.toHTML("\u0005f\u0005oo"));
        assertEquals("<span style=\"color: blue\">foo<span style=\"color: red\">bar</span>foo</span>bar", StyleUtils.toHTML("\u0005foo\u0014bar\u0014foo\u0005bar"));
    }
}
