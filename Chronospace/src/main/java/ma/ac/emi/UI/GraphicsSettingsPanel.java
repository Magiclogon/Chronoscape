package ma.ac.emi.UI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import ma.ac.emi.UI.MenuStyle;
import ma.ac.emi.UI.component.RetroButton;
import ma.ac.emi.UI.component.RetroScrollBar;
import ma.ac.emi.UI.component.SettingsPanel;
import ma.ac.emi.glgraphics.post.config.PostFXConfig;
import ma.ac.emi.glgraphics.post.config.PostFXConfigLoader;

public class GraphicsSettingsPanel extends JPanel implements SettingsPanel {

    public enum GraphicsPreset {
        FAST("FAST", "Fast & Smooth", "Minimal effects for maximum FPS"),
        BALANCED("BALANCED", "Balanced", "Good balance of visuals and performance"),
        ULTRA("ULTRA", "Ultra", "Maximum visual fidelity"),
        CUSTOM("CUSTOM", "Custom", "User-defined settings");
        public final String id, displayName, description;
        GraphicsPreset(String id, String displayName, String description) {
            this.id = id; this.displayName = displayName; this.description = description;
        }
    }

    private PostFXConfig config;
    private GraphicsSettingsCallback callback;
    private Runnable goBackAction;
    private boolean hasUnsavedChanges   = false;
    private boolean ignoreSliderChanges = false;
    private GraphicsPreset currentPreset = GraphicsPreset.BALANCED;
    private ButtonGroup presetButtonGroup;
    private JSlider renderScaleSlider, bloomIntensitySlider, glowIntensitySlider;

    public GraphicsSettingsPanel(PostFXConfig config) { this(config, null); }

    public GraphicsSettingsPanel(PostFXConfig config, GraphicsSettingsCallback callback) {
        this.config   = config;
        this.callback = callback;

        setLayout(new BorderLayout());
        setBackground(MenuStyle.BG_PANEL);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(MenuStyle.BG_PANEL);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(MenuStyle.BG_PANEL);
        contentPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel titleInner = new JLabel("GRAPHICS SETTINGS", SwingConstants.CENTER);
        titleInner.setFont(MenuStyle.FONT_HEADER);
        titleInner.setForeground(MenuStyle.TEXT_BORDER);
        titleInner.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleInner);
        contentPanel.add(Box.createVerticalStrut(6));
        contentPanel.add(makeSeparatorPanel());
        contentPanel.add(Box.createVerticalStrut(20));

        contentPanel.add(makeSectionLabel("QUALITY PRESETS"));
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(createPresetsBlock());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(makeSeparatorPanel());
        contentPanel.add(Box.createVerticalStrut(20));

        contentPanel.add(makeSectionLabel("ADVANCED SETTINGS"));
        contentPanel.add(Box.createVerticalStrut(14));

        renderScaleSlider = createSlider(0.25f, 1.0f, config.renderScale, 0.05f);
        contentPanel.add(makeSliderRow("RENDER SCALE", renderScaleSlider));
        contentPanel.add(Box.createVerticalStrut(14));

        bloomIntensitySlider = createSlider(0.0f, 2.0f, config.bloom != null ? config.bloom.intensity : 0.05f, 0.01f);
        contentPanel.add(makeSliderRow("BLOOM INTENSITY", bloomIntensitySlider));
        contentPanel.add(Box.createVerticalStrut(14));

        glowIntensitySlider = createSlider(0.0f, 2.0f, config.glow != null ? config.glow.intensity : 0.5f, 0.01f);
        contentPanel.add(makeSliderRow("GLOW INTENSITY", glowIntensitySlider));
        contentPanel.add(Box.createVerticalStrut(20));

        RetroButton moreBtn = new RetroButton("MORE OPTIONS >", RetroButton.Style.OUTLINE, MenuStyle.ACCENT_PURPLE);
        moreBtn.setPreferredSize(new Dimension(220, MenuStyle.BTN_HEIGHT_SM));
        moreBtn.setMaximumSize(new Dimension(220, MenuStyle.BTN_HEIGHT_SM));
        moreBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        moreBtn.addActionListener(e -> showFullAdvancedSettings());
        contentPanel.add(moreBtn);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(makeSeparatorPanel());
        contentPanel.add(Box.createVerticalStrut(16));

        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setBackground(MenuStyle.BG_PANEL);
        scroll.getViewport().setBackground(MenuStyle.BG_PANEL);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollBar vBar = new RetroScrollBar(JScrollBar.VERTICAL);
        vBar.setPreferredSize(new Dimension(14, 0));
        vBar.setUnitIncrement(40);
        vBar.setBlockIncrement(80);
        scroll.setVerticalScrollBar(vBar);
        card.add(scroll, BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);

        detectCurrentPreset();
        updateUIForCurrentPreset();
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private JLabel makeSectionLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(MenuStyle.FONT_BODY);
        lbl.setForeground(MenuStyle.TEXT_BORDER);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private JPanel makeSeparatorPanel() {
        JPanel p = new JPanel();
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        p.setPreferredSize(new Dimension(0, 1));
        p.setBackground(new Color(60, 60, 70));
        p.setAlignmentX(Component.CENTER_ALIGNMENT);
        return p;
    }

    private JPanel createPresetsBlock() {
        JPanel block = new JPanel();
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setBackground(MenuStyle.BG_PANEL);
        block.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel hint = new JLabel("Choose a preset that matches your hardware:", SwingConstants.CENTER);
        hint.setFont(MenuStyle.FONT_SMALL);
        hint.setForeground(MenuStyle.TEXT_GRAY);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
        block.add(hint);
        block.add(Box.createVerticalStrut(12));

        presetButtonGroup = new ButtonGroup();
        for (GraphicsPreset preset : GraphicsPreset.values()) {
            PresetButton btn = new PresetButton(preset);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            presetButtonGroup.add(btn);
            block.add(btn);
            block.add(Box.createVerticalStrut(8));
            if (preset == currentPreset) btn.setSelected(true);
        }
        return block;
    }

    private JPanel makeSliderRow(String label, JSlider slider) {
        JPanel row = new JPanel(new BorderLayout(16, 0));
        row.setBackground(MenuStyle.BG_PANEL);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        row.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(MenuStyle.FONT_BODY);
        lbl.setForeground(MenuStyle.TEXT_BORDER);
        lbl.setPreferredSize(new Dimension(190, 30));

        JLabel valueLabel = new JLabel(getSliderValue(slider), SwingConstants.RIGHT);
        valueLabel.setFont(MenuStyle.FONT_BODY);
        valueLabel.setForeground(MenuStyle.ACCENT);
        valueLabel.setPreferredSize(new Dimension(55, 30));

        slider.setUI(new javax.swing.plaf.basic.BasicSliderUI(slider) {
            @Override public void paintTrack(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Rectangle t = trackRect; int cy = t.y + t.height / 2;
                g2.setColor(MenuStyle.BG_MUTED);
                g2.fillRoundRect(t.x, cy - 3, t.width, 6, 6, 6);
                int filled = thumbRect.x - t.x + thumbRect.width / 2;
                g2.setColor(MenuStyle.ACCENT);
                g2.fillRoundRect(t.x, cy - 3, filled, 6, 6, 6);
            }
            @Override public void paintThumb(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
                g2.setColor(MenuStyle.ACCENT);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
            }
        });

        slider.addChangeListener(e -> {
            valueLabel.setText(getSliderValue(slider));
            if (!ignoreSliderChanges && !slider.getValueIsAdjusting()) switchToCustomPreset();
            hasUnsavedChanges = true;
        });

        row.add(lbl,        BorderLayout.WEST);
        row.add(slider,     BorderLayout.CENTER);
        row.add(valueLabel, BorderLayout.EAST);
        return row;
    }

    // ── Preset logic (unchanged) ──────────────────────────────────────────

    private void updateUIForCurrentPreset() {
        for (java.util.Enumeration<AbstractButton> btns = presetButtonGroup.getElements(); btns.hasMoreElements();) {
            PresetButton b = (PresetButton) btns.nextElement();
            if (b.preset == currentPreset) { b.setSelected(true); break; }
        }
        ignoreSliderChanges = true;
        setSliderValue(renderScaleSlider,    config.renderScale);
        setSliderValue(bloomIntensitySlider, config.bloom != null ? config.bloom.intensity : 0f);
        setSliderValue(glowIntensitySlider,  config.glow  != null ? config.glow.intensity  : 0f);
        ignoreSliderChanges = false;
    }

    private void applyPreset(GraphicsPreset preset) {
        if (preset == GraphicsPreset.CUSTOM) { currentPreset = preset; return; }
        currentPreset = preset; hasUnsavedChanges = true; ignoreSliderChanges = true;
        switch (preset) {
            case FAST:     applyFastPreset();     break;
            case BALANCED: applyBalancedPreset(); break;
            case ULTRA:    applyUltraPreset();    break;
            default: break;
        }
        ignoreSliderChanges = false;
    }

    private void applyFastPreset() {
        if (config.bloom != null) config.bloom.enabled = false;
        if (config.glow  != null) config.glow.enabled  = false;
        config.renderScale = 0.25f;
        setSliderValue(renderScaleSlider, 0.25f); setSliderValue(bloomIntensitySlider, 0f); setSliderValue(glowIntensitySlider, 0f);
    }
    private void applyBalancedPreset() {
        if (config.bloom != null) { config.bloom.enabled = true; config.bloom.intensity = 0.05f; }
        if (config.glow  != null) { config.glow.enabled  = true; config.glow.intensity  = 0.5f;  }
        config.renderScale = 0.4f;
        setSliderValue(renderScaleSlider, 0.4f); setSliderValue(bloomIntensitySlider, 0.05f); setSliderValue(glowIntensitySlider, 0.5f);
    }
    private void applyUltraPreset() {
        if (config.bloom != null) { config.bloom.enabled = true; config.bloom.intensity = 0.05f; }
        if (config.glow  != null) { config.glow.enabled  = true; config.glow.intensity  = 0.7f;  }
        config.renderScale = 1.0f;
        setSliderValue(renderScaleSlider, 1.0f); setSliderValue(bloomIntensitySlider, 0.05f); setSliderValue(glowIntensitySlider, 0.7f);
    }

    private void switchToCustomPreset() {
        if (currentPreset == GraphicsPreset.CUSTOM) return;
        currentPreset = GraphicsPreset.CUSTOM;
        for (java.util.Enumeration<AbstractButton> btns = presetButtonGroup.getElements(); btns.hasMoreElements();) {
            PresetButton b = (PresetButton) btns.nextElement();
            if (b.preset == GraphicsPreset.CUSTOM) { b.setSelected(true); break; }
        }
    }

    private void detectCurrentPreset() {
        float rs = config.renderScale;
        boolean bloomOn = config.bloom != null && config.bloom.enabled;
        boolean glowOn  = config.glow  != null && config.glow.enabled;
        float bi = config.bloom != null ? config.bloom.intensity : 0f;
        float gi = config.glow  != null ? config.glow.intensity  : 0f;
        if (Math.abs(rs-0.25f)<0.01f && !bloomOn && !glowOn)                                                           { currentPreset = GraphicsPreset.FAST;     return; }
        if (Math.abs(rs-0.4f) <0.01f && bloomOn && Math.abs(bi-0.05f)<0.01f && glowOn && Math.abs(gi-0.5f)<0.01f)     { currentPreset = GraphicsPreset.BALANCED; return; }
        if (Math.abs(rs-1.0f) <0.01f && bloomOn && Math.abs(bi-0.05f)<0.01f && glowOn && Math.abs(gi-0.7f)<0.01f)     { currentPreset = GraphicsPreset.ULTRA;    return; }
        currentPreset = GraphicsPreset.CUSTOM;
    }

    // ── Slider utils (unchanged) ──────────────────────────────────────────

    private JSlider createSlider(float min, float max, float value, float step) {
        int range = (int)((max-min)/step), current = (int)((value-min)/step);
        JSlider slider = new JSlider(0, range, current);
        slider.setBackground(MenuStyle.BG_PANEL);
        slider.putClientProperty("MIN_VALUE", min);
        slider.putClientProperty("MAX_VALUE", max);
        slider.putClientProperty("STEP", step);
        return slider;
    }
    private String getSliderValue(JSlider s) {
        float min = (Float)s.getClientProperty("MIN_VALUE"), step = (Float)s.getClientProperty("STEP");
        float val = min + s.getValue()*step;
        return step>=1 ? String.format("%.0f",val) : step>=0.1f ? String.format("%.1f",val) : String.format("%.2f",val);
    }
    private float getSliderFloatValue(JSlider s) {
        float min = (Float)s.getClientProperty("MIN_VALUE"), step = (Float)s.getClientProperty("STEP");
        return min + s.getValue()*step;
    }
    private void setSliderValue(JSlider s, float value) {
        float min = (Float)s.getClientProperty("MIN_VALUE"), step = (Float)s.getClientProperty("STEP");
        s.setValue((int)((value-min)/step));
    }

    // ── Advanced settings dialog ──────────────────────────────────────────

    private void showFullAdvancedSettings() {
        JDialog dialog = new JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), "Advanced Graphics Settings", true);
        dialog.setLayout(new BorderLayout());
        AdvancedSettingsPanel adv = new AdvancedSettingsPanel(config);
        adv.setPreferredSize(new Dimension(700, 600));
        dialog.add(adv, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        btns.setBackground(MenuStyle.BG_DARK);
        RetroButton ok     = new RetroButton("OK",     RetroButton.Style.SOLID,  MenuStyle.ACCENT,    Color.BLACK);
        RetroButton cancel = new RetroButton("CANCEL", RetroButton.Style.DANGER, MenuStyle.ACCENT_RED, Color.WHITE);
        ok.setPreferredSize(new Dimension(120, MenuStyle.BTN_HEIGHT_SM));
        cancel.setPreferredSize(new Dimension(120, MenuStyle.BTN_HEIGHT_SM));
        ok.addActionListener(e -> { adv.applyToConfig(config); hasUnsavedChanges = true; switchToCustomPreset(); dialog.dispose(); });
        cancel.addActionListener(e -> dialog.dispose());
        btns.add(ok); btns.add(cancel);
        dialog.add(btns, BorderLayout.SOUTH);
        dialog.pack(); dialog.setLocationRelativeTo(this); dialog.setVisible(true);
    }

    // ── Apply / reset ─────────────────────────────────────────────────────

    private void applyChangesInternal() {
        if (config.bloom != null) config.bloom.intensity = getSliderFloatValue(bloomIntensitySlider);
        if (config.glow  != null) config.glow.intensity  = getSliderFloatValue(glowIntensitySlider);
        config.renderScale = getSliderFloatValue(renderScaleSlider);
        try { PostFXConfigLoader.save(config); } catch (Exception e) { e.printStackTrace(); }
        if (callback != null) callback.onSettingsChanged(config);
        hasUnsavedChanges = false;
    }

    @Override public void applyChanges()    { applyChangesInternal(); }
    @Override public void resetToDefaults() { applyPreset(GraphicsPreset.BALANCED); updateUIForCurrentPreset(); }

    private void closeWindow() {
        if (hasUnsavedChanges) {
            int r = JOptionPane.showConfirmDialog(this, "You have unsaved changes. Save before going back?",
                    "Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (r == JOptionPane.YES_OPTION)    { applyChangesInternal(); if (goBackAction != null) goBackAction.run(); }
            else if (r == JOptionPane.NO_OPTION) { if (goBackAction != null) goBackAction.run(); }
        } else { if (goBackAction != null) goBackAction.run(); }
    }

    // ── PresetButton ──────────────────────────────────────────────────────

    private class PresetButton extends JRadioButton {
        GraphicsPreset preset;
        boolean isHovered = false;

        PresetButton(GraphicsPreset preset) {
            this.preset = preset;
            setLayout(new BorderLayout(15, 5));
            setOpaque(false); setFocusPainted(false); setBorderPainted(false); setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(600, 64));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
            setMinimumSize(new Dimension(0, 64));

            JPanel text = new JPanel();
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
            text.setOpaque(false);
            JLabel name = new JLabel(preset.displayName.toUpperCase());
            name.setFont(MenuStyle.FONT_BODY);
            name.setForeground(MenuStyle.ACCENT);
            JLabel desc = new JLabel(preset.description);
            desc.setFont(MenuStyle.FONT_SMALL);
            desc.setForeground(MenuStyle.TEXT_GRAY);
            text.add(name); text.add(desc);
            add(text, BorderLayout.CENTER);

            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) { isHovered = true;  repaint(); }
                public void mouseExited (java.awt.event.MouseEvent e) { isHovered = false; repaint(); }
            });
            addActionListener(e -> { if (isSelected()) applyPreset(preset); });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isSelected() ? new Color(45, 45, 55) : isHovered ? new Color(35, 35, 43) : MenuStyle.BG_PANEL);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            g2.setColor(isSelected() ? MenuStyle.ACCENT : isHovered ? Color.WHITE : MenuStyle.TEXT_BORDER);
            g2.setStroke(new BasicStroke(isSelected() ? 3f : 2f));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 8, 8);
            g2.dispose();
        }
    }
}