package clockapp;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.Color;

/** Light and dark colour palettes plus a helper to style buttons consistently. */
public enum Theme {
    LIGHT(0xF5F7FA, 0xFFFFFF, 0x1F2933, 0x7B8794, 0x2563EB, 0xFFFFFF, 0xD9E0E8),
    DARK(0x12161C, 0x1C222B, 0xE8ECF1, 0x8A94A3, 0x4F8EF7, 0x0B0F14, 0x2D3643);

    public final Color background;
    public final Color surface;
    public final Color foreground;
    public final Color muted;
    public final Color accent;
    public final Color accentText;
    public final Color border;

    Theme(int background, int surface, int foreground, int muted,
          int accent, int accentText, int border) {
        this.background = new Color(background);
        this.surface = new Color(surface);
        this.foreground = new Color(foreground);
        this.muted = new Color(muted);
        this.accent = new Color(accent);
        this.accentText = new Color(accentText);
        this.border = new Color(border);
    }

    public Theme toggle() {
        return this == LIGHT ? DARK : LIGHT;
    }

    /** Applies a flat, theme-aware look to a button. */
    public void styleButton(JButton button, boolean primary) {
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFocusPainted(false);
        button.setBackground(primary ? accent : surface);
        button.setForeground(primary ? accentText : foreground);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primary ? accent : border),
                BorderFactory.createEmptyBorder(8, 24, 8, 24)));
    }
}