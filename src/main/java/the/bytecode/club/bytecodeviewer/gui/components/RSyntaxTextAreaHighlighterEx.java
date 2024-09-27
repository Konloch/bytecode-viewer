/*
 * BSD 3-Clause "New" or "Revised" License
 *
 * Copyright (c) 2021, Robert Futrell All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 * 		Redistributions of source code must retain the above copyright notice, this list of conditions and the
 * following disclaimer.
 *		Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution.
 *		Neither the name of the author nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 *  INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package the.bytecode.club.bytecodeviewer.gui.components;

import org.fife.ui.rsyntaxtextarea.DocumentRange;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaHighlighter;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;
import org.fife.ui.rtextarea.SmartHighlightPainter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Extension from RSyntaxTextArea
 */
public class RSyntaxTextAreaHighlighterEx extends RSyntaxTextAreaHighlighter
{
    private final List<SyntaxLayeredHighlightInfoImpl> markedOccurrences = new ArrayList<>();
    private static final Color DEFAULT_PARSER_NOTICE_COLOR = Color.RED;

    public Object addMarkedOccurrenceHighlight(int start, int end, @NotNull SmartHighlightPainter p) throws BadLocationException
    {
        Document doc = textArea.getDocument();
        TextUI mapper = textArea.getUI();
        // Always layered highlights for marked occurrences.
        SyntaxLayeredHighlightInfoImpl i = new SyntaxLayeredHighlightInfoImpl();
        p.setPaint(UIManager.getColor("ScrollBar.thumb"));
        i.setPainter(p);
        i.setStartOffset(doc.createPosition(start));
        // HACK: Use "end-1" to prevent chars the user types at the "end" of
        // the highlight to be absorbed into the highlight (default Highlight
        // behavior).
        i.setEndOffset(doc.createPosition(end - 1));
        markedOccurrences.add(i);
        mapper.damageRange(textArea, start, end);
        return i;
    }

    @Override
    public List<DocumentRange> getMarkedOccurrences()
    {
        List<DocumentRange> list = new ArrayList<>(markedOccurrences.size());
        for (HighlightInfo info : markedOccurrences)
        {
            int start = info.getStartOffset();
            int end = info.getEndOffset() + 1; // HACK

            if (start <= end)
            {
                // Occasionally a Marked Occurrence can have a lost end offset
                // but not start offset (replacing entire text content with
                // new content, and a marked occurrence is on the last token
                // in the document).
                DocumentRange range = new DocumentRange(start, end);
                list.add(range);
            }
        }

        return list;
    }

    public void clearMarkOccurrencesHighlights()
    {
        // Don't remove via an iterator; since our List is an ArrayList, this
        // implies tons of System.arrayCopy()s
        for (HighlightInfo info : markedOccurrences)
        {
            repaintListHighlight(info);
        }

        markedOccurrences.clear();
    }

    @Override
    public void paintLayeredHighlights(Graphics g, int lineStart, int lineEnd, Shape viewBounds, JTextComponent editor, View view)
    {
        paintListLayered(g, lineStart, lineEnd, viewBounds, editor, view, markedOccurrences);
        super.paintLayeredHighlights(g, lineStart, lineEnd, viewBounds, editor, view);
    }

    private static class SyntaxLayeredHighlightInfoImpl extends LayeredHighlightInfoImpl
    {
        private ParserNotice notice;

        @Override
        public Color getColor()
        {
            Color color = null;
            if (notice != null)
            {
                color = notice.getColor();

                if (color == null)
                    color = DEFAULT_PARSER_NOTICE_COLOR;
            }

            return color;
        }

        @Override
        public String toString()
        {
            return "[SyntaxLayeredHighlightInfoImpl: startOffs=" + getStartOffset() + ", endOffs=" + getEndOffset() + ", color=" + getColor() + "]";
        }
    }
}
