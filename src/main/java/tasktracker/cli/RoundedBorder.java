package tasktracker.cli;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.AbstractBorder;
import com.googlecode.lanterna.gui2.Border;
import com.googlecode.lanterna.gui2.Component;
import com.googlecode.lanterna.gui2.TextGUIGraphics;

final class RoundedBorder extends AbstractBorder {

    @Override
    protected Border.BorderRenderer createDefaultRenderer() {
        return new RoundedBorderRenderer();
    }

    private static final class RoundedBorderRenderer implements Border.BorderRenderer {

        @Override
        public TerminalSize getPreferredSize(Border border) {
            Component child = ((AbstractBorder) border).getComponent();
            TerminalSize childSize = child == null ? TerminalSize.ZERO : child.getPreferredSize();
            return childSize.withRelativeColumns(2).withRelativeRows(2);
        }

        @Override
        public TerminalPosition getWrappedComponentTopLeftOffset() {
            return TerminalPosition.OFFSET_1x1;
        }

        @Override
        public TerminalSize getWrappedComponentSize(TerminalSize borderSize) {
            return borderSize
                    .withRelativeColumns(-Math.min(2, borderSize.getColumns()))
                    .withRelativeRows(-Math.min(2, borderSize.getRows()));
        }

        @Override
        public void drawComponent(TextGUIGraphics graphics, Border border) {
            Component child = ((AbstractBorder) border).getComponent();
            if (child == null) {
                return;
            }
            TerminalSize size = graphics.getSize();
            int cols = size.getColumns();
            int rows = size.getRows();

            graphics.setForegroundColor(VisualStyle.ACCENT);

            graphics.setCharacter(0, 0, '╭');
            graphics.setCharacter(cols - 1, 0, '╮');
            graphics.setCharacter(0, rows - 1, '╰');
            graphics.setCharacter(cols - 1, rows - 1, '╯');
            for (int c = 1; c < cols - 1; c++) {
                graphics.setCharacter(c, 0, '─');
                graphics.setCharacter(c, rows - 1, '─');
            }
            for (int r = 1; r < rows - 1; r++) {
                graphics.setCharacter(0, r, '│');
                graphics.setCharacter(cols - 1, r, '│');
            }

            TextGUIGraphics inner = graphics.newTextGraphics(
                    getWrappedComponentTopLeftOffset(),
                    getWrappedComponentSize(size));
            child.draw(inner);
        }
    }
}
