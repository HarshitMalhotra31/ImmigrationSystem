package com.immigration.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.Enumeration;

/**
 * Centralized theme for the Immigration System UI.
 * Call UITheme.apply() once at startup to style the entire application.
 */
public class UITheme {

    // Color palette
    public static final Color PRIMARY = new Color(25, 42, 86);        // Deep navy
    public static final Color PRIMARY_LIGHT = new Color(40, 60, 110);
    public static final Color ACCENT = new Color(52, 152, 219);       // Bright blue
    public static final Color SUCCESS = new Color(39, 174, 96);       // Green
    public static final Color DANGER = new Color(231, 76, 60);        // Red
    public static final Color WARNING = new Color(243, 156, 18);      // Amber
    public static final Color BG_LIGHT = new Color(236, 240, 245);    // Light gray-blue
    public static final Color BG_CARD = new Color(255, 255, 255);     // White
    public static final Color TEXT_DARK = new Color(33, 37, 41);      // Near-black
    public static final Color TEXT_MUTED = new Color(108, 117, 125);  // Gray
    public static final Color BORDER = new Color(206, 212, 218);      // Subtle border

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_LABEL_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_TABLE = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_TABLE_HEADER = new Font("Segoe UI", Font.BOLD, 13);

    /**
     * Apply the theme to the entire Swing application via UIManager.
     */
    public static void apply() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Global font
        setGlobalFont(new FontUIResource("Segoe UI", Font.PLAIN, 13));

        // Panel backgrounds
        UIManager.put("Panel.background", BG_LIGHT);
        UIManager.put("OptionPane.background", BG_LIGHT);

        // Buttons
        UIManager.put("Button.font", FONT_BUTTON);
        UIManager.put("Button.arc", 8);

        // Text fields
        UIManager.put("TextField.font", FONT_LABEL);
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("PasswordField.font", FONT_LABEL);
        UIManager.put("PasswordField.background", Color.WHITE);

        // ComboBox
        UIManager.put("ComboBox.font", FONT_LABEL);
        UIManager.put("ComboBox.background", Color.WHITE);

        // Labels
        UIManager.put("Label.font", FONT_LABEL);
        UIManager.put("Label.foreground", TEXT_DARK);

        // Table
        UIManager.put("Table.font", FONT_TABLE);
        UIManager.put("Table.background", Color.WHITE);
        UIManager.put("Table.gridColor", BORDER);
        UIManager.put("Table.rowHeight", 28);
        UIManager.put("Table.selectionBackground", new Color(ACCENT.getRed(), ACCENT.getGreen(), ACCENT.getBlue(), 60));
        UIManager.put("Table.selectionForeground", TEXT_DARK);
        UIManager.put("TableHeader.font", FONT_TABLE_HEADER);
        UIManager.put("TableHeader.background", Color.BLACK);
        UIManager.put("TableHeader.foreground", Color.WHITE);

        // ScrollPane
        UIManager.put("ScrollPane.background", BG_LIGHT);

        // OptionPane
        UIManager.put("OptionPane.messageForeground", TEXT_DARK);
        UIManager.put("OptionPane.messageFont", FONT_LABEL);

        // TitledBorder
        UIManager.put("TitledBorder.font", FONT_LABEL_BOLD);
        UIManager.put("TitledBorder.titleColor", PRIMARY);
    }

    private static void setGlobalFont(FontUIResource font) {
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, font);
            }
        }
    }

    /**
     * Creates a styled header panel with gradient background.
     */
    public static JPanel createHeaderPanel(String title) {
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY, getWidth(), 0, PRIMARY_LIGHT);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setPreferredSize(new Dimension(0, 56));
        header.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 14));

        JLabel label = new JLabel(title);
        label.setFont(FONT_TITLE);
        label.setForeground(Color.WHITE);
        header.add(label);

        return header;
    }

    /**
     * Creates a polished styled button.
     */
    public static JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Hover effect
        Color hoverColor = bgColor.brighter();
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });

        return btn;
    }

    /**
     * Styles a JTable with nice header, alternating rows, etc.
     */
    public static void styleTable(JTable table) {
        table.setFont(FONT_TABLE);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(52, 152, 219, 50));
        table.setSelectionForeground(TEXT_DARK);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowHorizontalLines(true);

        // Alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 248, 252));
                }
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return c;
            }
        });

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(0, 36));
        header.setOpaque(true);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(Color.BLACK);
                c.setForeground(Color.WHITE);
                c.setFont(FONT_TABLE_HEADER);
                ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });
    }

    /**
     * Creates a styled form label.
     */
    public static JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_LABEL_BOLD);
        label.setForeground(TEXT_DARK);
        return label;
    }

    /**
     * Styles a text field.
     */
    public static JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(FONT_LABEL);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(6, 8, 6, 8)));
        return field;
    }

    /**
     * Styles a text field with default text.
     */
    public static JTextField createStyledTextField(String defaultText, int columns) {
        JTextField field = createStyledTextField(columns);
        field.setText(defaultText);
        return field;
    }
}
