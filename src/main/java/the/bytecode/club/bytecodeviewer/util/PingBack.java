/***************************************************************************
 * Bytecode Viewer (BCV) - Java & Android Reverse Engineering Suite        *
 * Copyright (C) 2014 Konloch - Konloch.com / BytecodeViewer.com           *
 *                                                                         *
 * This program is free software: you can redistribute it and/or modify    *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation, either version 3 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. *
 ***************************************************************************/

package the.bytecode.club.bytecodeviewer.util;

import com.konloch.httprequest.HTTPRequest;
import the.bytecode.club.bytecodeviewer.Configuration;

import java.net.URL;

/**
 * Pings back to bytecodeviewer.com to be added into the total running statistics
 *
 * @author Konloch
 * @since May 1, 2015
 */
public class PingBack implements Runnable
{
    @Override
    public void run()
    {
        try
        {
            new HTTPRequest(new URL("https://bytecodeviewer.com/add.php")).read();
        }
        catch (Exception e)
        {
            Configuration.pingback = false;
        }
    }
}
