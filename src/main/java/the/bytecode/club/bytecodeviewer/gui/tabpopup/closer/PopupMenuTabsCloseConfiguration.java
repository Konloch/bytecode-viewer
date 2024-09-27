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

package the.bytecode.club.bytecodeviewer.gui.tabpopup.closer;

/**
 * PopupMenu items configuration of close tabs
 *
 * @author su
 */
public class PopupMenuTabsCloseConfiguration
{
    private boolean close;
    private boolean closeOthers;
    private boolean closeAll;
    private boolean closeLefts;
    private boolean closeRights;

    public PopupMenuTabsCloseConfiguration(Builder builder)
    {
        super();
        this.close = builder.close;
        this.closeOthers = builder.closeOthers;
        this.closeAll = builder.closeAll;
        this.closeLefts = builder.closeLefts;
        this.closeRights = builder.closeRights;
    }

    public boolean isClose()
    {
        return close;
    }

    public void close(boolean close)
    {
        this.close = close;
    }

    public boolean isCloseOthers()
    {
        return closeOthers;
    }

    public void setCloseOthers(boolean closeOthers)
    {
        this.closeOthers = closeOthers;
    }

    public boolean isCloseAll()
    {
        return closeAll;
    }

    public void setCloseAll(boolean closeAll)
    {
        this.closeAll = closeAll;
    }

    public boolean isCloseLefts()
    {
        return closeLefts;
    }

    public void setCloseLefts(boolean closeLefts)
    {
        this.closeLefts = closeLefts;
    }

    public boolean isCloseRights()
    {
        return closeRights;
    }

    public void setCloseRights(boolean closeRights)
    {
        this.closeRights = closeRights;
    }

    public static class Builder
    {
        private boolean close;
        private boolean closeOthers;
        private boolean closeAll;
        private boolean closeLefts;
        private boolean closeRights;

        public Builder close(boolean close)
        {
            this.close = close;
            return this;
        }

        public Builder closeOthers(boolean closeOthers)
        {
            this.closeOthers = closeOthers;
            return this;
        }

        public Builder closeAll(boolean closeAll)
        {
            this.closeAll = closeAll;
            return this;
        }

        public Builder closeLefts(boolean closeLefts)
        {
            this.closeLefts = closeLefts;
            return this;
        }

        public Builder closeRights(boolean closeRights)
        {
            this.closeRights = closeRights;
            return this;
        }

        public PopupMenuTabsCloseConfiguration build()
        {
            return new PopupMenuTabsCloseConfiguration(this);
        }

        public PopupMenuTabsCloseConfiguration buildFull()
        {
            return this.close(true).closeOthers(true).closeAll(true).closeLefts(true).closeRights(true).build();
        }
    }
}
