package Inventory;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;

final class Ui {

    static final Color INK = new Color(47, 21, 26);
    static final Color BURGUNDY = new Color(122, 36, 48);
    static final Color CREAM = new Color(246, 242, 236);
    static final Color CARD = Color.WHITE;
    static final Color LINE = new Color(226, 218, 208);
    static final Color MUTED = new Color(110, 98, 92);
    static final Color DANGER = new Color(153, 42, 48);
    static final Color NAV = new Color(72, 38, 44);

    private Ui() {
    }

    static Font text(int size) {
        return new Font("Segoe UI", Font.PLAIN, size);
    }

    static Font bold(int size) {
        return new Font("Segoe UI", Font.BOLD, size);
    }

    static void button(JButton button, Color background, Color foreground) {
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFont(bold(13));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(9, 16, 9, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    static void primary(JButton button) {
        button(button, BURGUNDY, Color.WHITE);
    }

    static void quiet(JButton button) {
        button(button, CARD, INK);
        button.setBorder(new CompoundBorder(
                new LineBorder(LINE, 1, true),
                new EmptyBorder(8, 14, 8, 14)));
    }

    static void danger(JButton button) {
        button(button, DANGER, Color.WHITE);
    }

    static void nav(JButton button, boolean selected) {
        button(button, selected ? CARD : NAV, selected ? BURGUNDY : Color.WHITE);
    }

    static void field(JComponent component) {
        component.setFont(text(14));
        component.setBackground(Color.WHITE);
        component.setBorder(new CompoundBorder(
                new LineBorder(LINE, 1, true),
                new EmptyBorder(7, 10, 7, 10)));
    }

    static void table(JTable table) {
        table.setFont(text(13));
        table.setRowHeight(32);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(LINE);
        table.setSelectionBackground(new Color(246, 232, 232));
        table.setSelectionForeground(INK);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable source, Object value,
                    boolean selected, boolean focus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        source, value, selected, focus, row, column);
                label.setBackground(INK);
                label.setForeground(Color.WHITE);
                label.setFont(bold(13));
                label.setBorder(new EmptyBorder(8, 10, 8, 10));
                label.setOpaque(true);
                return label;
            }
        });
        table.getTableHeader().setReorderingAllowed(false);
    }

    static JPanel card() {
        JPanel panel = new JPanel(new java.awt.BorderLayout());
        panel.setBackground(CARD);
        panel.setBorder(new CompoundBorder(
                new LineBorder(LINE, 1, true),
                new EmptyBorder(16, 16, 16, 16)));
        return panel;
    }

    static JLabel pageTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(bold(26));
        label.setForeground(INK);
        return label;
    }

    static JPanel page(String title, String note) {
        JPanel header = new JPanel(new java.awt.BorderLayout(0, 4));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(4, 4, 14, 4));
        header.add(pageTitle(title), java.awt.BorderLayout.NORTH);
        JLabel subtitle = new JLabel(note);
        subtitle.setFont(text(14));
        subtitle.setForeground(MUTED);
        header.add(subtitle, java.awt.BorderLayout.SOUTH);
        return header;
    }

    static void pad(JPanel panel) {
        panel.setBackground(CREAM);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 24, 24));
    }
}
