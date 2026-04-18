package org.example;

import java.awt.*;

/**
 * Inventra design tokens.
 *
 * Palette rationale (dark-theme color theory):
 *   BG_DEEP   – near-black base (#0D0F14)  — gives depth, avoids harsh pure-black
 *   BG_PANEL  – slightly lighter (#161B24)  — card / panel surfaces
 *   BG_ROW    – subtle stripe (#1C2230)     — table row alt
 *   ACCENT    – vivid teal (#00C8A0)        — 60-30-10 rule accent, high contrast on dark
 *   ACCENT2   – warm amber (#F5A623)        — secondary accent for warnings / totals
 *   DANGER    – muted red  (#E05C5C)        — destructive actions, low-stock alerts
 *   TEXT_PRI  – off-white  (#E8EDF5)        — primary text, less harsh than #FFFFFF
 *   TEXT_SEC  – slate      (#7A8499)        — labels, hints
 *   BORDER    – dark line  (#252D3D)        — subtle separators
 */
public final class Theme {
    private Theme() {}

    public static final Color BG_DEEP   = new Color(0x0D0F14);
    public static final Color BG_PANEL  = new Color(0x161B24);
    public static final Color BG_ROW    = new Color(0x1C2230);
    public static final Color ACCENT    = new Color(0x00C8A0);
    public static final Color ACCENT2   = new Color(0xF5A623);
    public static final Color DANGER    = new Color(0xE05C5C);
    public static final Color TEXT_PRI  = new Color(0xE8EDF5);
    public static final Color TEXT_SEC  = new Color(0x7A8499);
    public static final Color BORDER    = new Color(0x252D3D);

    // Slightly transparent accent for hover fills
    public static final Color ACCENT_DIM = new Color(0x00C8A0, false); // reuse, tint via alpha
    public static final Color HOVER_ROW  = new Color(0x1E2D3D);

    // Typography
    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEAD   = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_MONO   = new Font("Consolas",  Font.PLAIN, 13);
}
