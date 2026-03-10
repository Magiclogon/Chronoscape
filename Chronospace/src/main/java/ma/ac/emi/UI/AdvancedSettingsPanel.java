package ma.ac.emi.UI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

import ma.ac.emi.UI.component.RetroScrollBar;
import ma.ac.emi.glgraphics.post.config.PostFXConfig;

public class AdvancedSettingsPanel extends JPanel {

    private static final Color BG_DARK      = new Color(18, 18, 24);
    private static final Color PANEL_BG     = new Color(30, 30, 38);
    private static final Color BORDER_LIGHT = new Color(180, 180, 190);
    private static final Color TEXT_MAIN    = new Color(235, 235, 245);
    private static final Color TEXT_GRAY    = new Color(150, 150, 160);
    private static final Color ACCENT_GOLD  = new Color(255, 215, 0);
    private static final Color ACCENT_RED   = new Color(220, 60, 60);
    private static final Color ACCENT_GREEN = new Color(80, 200, 100);
    private static final Color MUTED        = new Color(80, 80, 90);

    private static final String FONT_NAME = "ByteBounce";
    private static final Font FONT_HEADER = new Font(FONT_NAME, Font.PLAIN, 28);
    private static final Font FONT_BODY   = new Font(FONT_NAME, Font.PLAIN, 18);
    private static final Font FONT_SMALL  = new Font(FONT_NAME, Font.PLAIN, 14);

    private JSlider rSlider, gSlider, bSlider, aSlider;
    private JSlider brightnessSlider, contrastSlider, saturationSlider, hueSlider, valueSlider;
    private JCheckBox colorCorrectionEnabled;

    private JCheckBox bloomEnabled;
    private JSlider bloomThresholdSlider, bloomBlurRadiusSlider, bloomIntensitySlider;
    private JSpinner bloomDownscaleSpinner;

    private JCheckBox glowEnabled;
    private JSlider glowBlurRadiusSlider, glowIntensitySlider, glowSaturationBoostSlider;
    private JSpinner glowDownscaleSpinner;

    public AdvancedSettingsPanel(PostFXConfig config) {
    	setLayout(new BorderLayout());
        setBackground(BG_DARK);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(PANEL_BG);
        card.setBorder(new EmptyBorder(24, 30, 24, 30));

        // Warning title
        JLabel warning = new JLabel("EXPERT MODE", SwingConstants.CENTER);
        warning.setFont(FONT_HEADER);
        warning.setForeground(ACCENT_RED);
        warning.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(warning);

        JLabel subWarning = new JLabel("Modify at your own risk", SwingConstants.CENTER);
        subWarning.setFont(FONT_SMALL);
        subWarning.setForeground(TEXT_GRAY);
        subWarning.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(subWarning);
        card.add(Box.createVerticalStrut(16));
        card.add(makeSeparatorPanel());
        card.add(Box.createVerticalStrut(20));

        card.add(makeSectionLabel("COLOR CORRECTION"));
        card.add(Box.createVerticalStrut(10));
        card.add(makeCheckboxRow(colorCorrectionEnabled = makeCheckbox("Enabled",
                config.colorCorrection != null && config.colorCorrection.enabled)));
        card.add(Box.createVerticalStrut(8));

        if (config.colorCorrection != null) {
            rSlider = createSlider(0f, 2f, config.colorCorrection.r, 0.01f);
            gSlider = createSlider(0f, 2f, config.colorCorrection.g, 0.01f);
            bSlider = createSlider(0f, 2f, config.colorCorrection.b, 0.01f);
            aSlider = createSlider(0f, 2f, config.colorCorrection.a, 0.01f);
            brightnessSlider  = createSlider(-1f, 1f,   config.colorCorrection.brightness,  0.01f);
            contrastSlider    = createSlider(0f,  3f,   config.colorCorrection.contrast,    0.01f);
            saturationSlider  = createSlider(0f,  3f,   config.colorCorrection.saturation,  0.01f);
            hueSlider         = createSlider(-180f, 180f, config.colorCorrection.hue,       1f);
            valueSlider       = createSlider(-1f, 1f,   config.colorCorrection.value,       0.01f);

            card.add(makeSliderRow("RED",        rSlider));
            card.add(makeSliderRow("GREEN",      gSlider));
            card.add(makeSliderRow("BLUE",       bSlider));
            card.add(makeSliderRow("ALPHA",      aSlider));
            card.add(makeSliderRow("BRIGHTNESS", brightnessSlider));
            card.add(makeSliderRow("CONTRAST",   contrastSlider));
            card.add(makeSliderRow("SATURATION", saturationSlider));
            card.add(makeSliderRow("HUE SHIFT",  hueSlider));
            card.add(makeSliderRow("VALUE",      valueSlider));
        }

        card.add(Box.createVerticalStrut(20));
        card.add(makeSeparatorPanel());
        card.add(Box.createVerticalStrut(20));

        card.add(makeSectionLabel("BLOOM"));
        card.add(Box.createVerticalStrut(10));
        card.add(makeCheckboxRow(bloomEnabled = makeCheckbox("Enabled",
                config.bloom != null && config.bloom.enabled)));
        card.add(Box.createVerticalStrut(8));

        if (config.bloom != null) {
            bloomDownscaleSpinner = makeSpinner(config.bloom.downscale);
            bloomThresholdSlider  = createSlider(0f, 1f,  config.bloom.threshold,  0.01f);
            bloomBlurRadiusSlider = createSlider(0f, 10f, config.bloom.blurRadius, 0.1f);
            bloomIntensitySlider  = createSlider(0f, 2f,  config.bloom.intensity,  0.01f);

            card.add(makeSpinnerRow("DOWNSCALE",  bloomDownscaleSpinner));
            card.add(makeSliderRow("THRESHOLD",   bloomThresholdSlider));
            card.add(makeSliderRow("BLUR RADIUS", bloomBlurRadiusSlider));
            card.add(makeSliderRow("INTENSITY",   bloomIntensitySlider));
        }

        card.add(Box.createVerticalStrut(20));
        card.add(makeSeparatorPanel());
        card.add(Box.createVerticalStrut(20));

        card.add(makeSectionLabel("GLOW"));
        card.add(Box.createVerticalStrut(10));
        card.add(makeCheckboxRow(glowEnabled = makeCheckbox("Enabled",
                config.glow != null && config.glow.enabled)));
        card.add(Box.createVerticalStrut(8));

        if (config.glow != null) {
            glowDownscaleSpinner      = makeSpinner(config.glow.downscale);
            glowBlurRadiusSlider      = createSlider(0f, 10f, config.glow.blurRadius, 0.01f);
            glowIntensitySlider       = createSlider(0f, 2f,  config.glow.intensity,  0.01f);
            glowSaturationBoostSlider = createSlider(1f, 3f,
                    config.glow.saturationBoost != 0 ? config.glow.saturationBoost : 1.5f, 0.1f);

            card.add(makeSpinnerRow("DOWNSCALE",  glowDownscaleSpinner));
            card.add(makeSliderRow("BLUR RADIUS", glowBlurRadiusSlider));
            card.add(makeSliderRow("INTENSITY",   glowIntensitySlider));
            card.add(makeSliderRow("SATURATION",  glowSaturationBoostSlider));
        }

        card.add(Box.createVerticalStrut(24));

        // Center card horizontally in a wrapper
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(BG_DARK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor  = GridBagConstraints.NORTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0; // don't stretch vertically — let card be natural height
        gbc.fill    = GridBagConstraints.NONE;
        wrapper.add(card, gbc);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBackground(BG_DARK);
        scroll.getViewport().setBackground(BG_DARK);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JScrollBar vBar = new RetroScrollBar(JScrollBar.VERTICAL);
        vBar.setPreferredSize(new Dimension(14, 0));
        vBar.setUnitIncrement(40);
        vBar.setBlockIncrement(80);
        scroll.setVerticalScrollBar(vBar);

        add(scroll, BorderLayout.CENTER);
    }

    // ── Apply ─────────────────────────────────────────────────────────────

    public void applyToConfig(PostFXConfig config) {
        if (config.colorCorrection != null) {
            config.colorCorrection.enabled    = colorCorrectionEnabled.isSelected();
            config.colorCorrection.r          = getSliderFloat(rSlider);
            config.colorCorrection.g          = getSliderFloat(gSlider);
            config.colorCorrection.b          = getSliderFloat(bSlider);
            config.colorCorrection.a          = getSliderFloat(aSlider);
            config.colorCorrection.brightness = getSliderFloat(brightnessSlider);
            config.colorCorrection.contrast   = getSliderFloat(contrastSlider);
            config.colorCorrection.saturation = getSliderFloat(saturationSlider);
            config.colorCorrection.hue        = getSliderFloat(hueSlider);
            config.colorCorrection.value      = getSliderFloat(valueSlider);
        }
        if (config.bloom != null) {
            config.bloom.enabled    = bloomEnabled.isSelected();
            config.bloom.downscale  = (Integer) bloomDownscaleSpinner.getValue();
            config.bloom.threshold  = getSliderFloat(bloomThresholdSlider);
            config.bloom.blurRadius = getSliderFloat(bloomBlurRadiusSlider);
            config.bloom.intensity  = getSliderFloat(bloomIntensitySlider);
        }
        if (config.glow != null) {
            config.glow.enabled         = glowEnabled.isSelected();
            config.glow.downscale       = (Integer) glowDownscaleSpinner.getValue();
            config.glow.blurRadius      = getSliderFloat(glowBlurRadiusSlider);
            config.glow.intensity       = getSliderFloat(glowIntensitySlider);
            config.glow.saturationBoost = getSliderFloat(glowSaturationBoostSlider);
        }
    }

    // ── UI helpers ────────────────────────────────────────────────────────

    private JLabel makeSectionLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(FONT_BODY);
        lbl.setForeground(BORDER_LIGHT);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private JPanel makeSeparatorPanel() {
        JPanel p = new JPanel();
        p.setBackground(new Color(60, 60, 70));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        p.setPreferredSize(new Dimension(0, 2));
        p.setAlignmentX(Component.CENTER_ALIGNMENT);
        return p;
    }

    private JPanel makeSliderRow(String label, JSlider slider) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(PANEL_BG);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        row.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_BODY);
        lbl.setForeground(BORDER_LIGHT);
        lbl.setPreferredSize(new Dimension(160, 28));

        JLabel valLbl = new JLabel(sliderText(slider), SwingConstants.RIGHT);
        valLbl.setFont(FONT_BODY);
        valLbl.setForeground(ACCENT_GREEN);
        valLbl.setPreferredSize(new Dimension(60, 28));

        slider.setUI(new javax.swing.plaf.basic.BasicSliderUI(slider) {
            @Override public void paintTrack(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cy = trackRect.y + trackRect.height / 2;
                g2.setColor(MUTED);
                g2.fillRoundRect(trackRect.x, cy - 3, trackRect.width, 6, 6, 6);
                int filled = thumbRect.x - trackRect.x + thumbRect.width / 2;
                g2.setColor(ACCENT_GREEN);
                g2.fillRoundRect(trackRect.x, cy - 3, filled, 6, 6, 6);
            }
            @Override public void paintThumb(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
                g2.setColor(ACCENT_GREEN);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
            }
        });

        slider.addChangeListener(e -> valLbl.setText(sliderText(slider)));

        row.add(lbl,    BorderLayout.WEST);
        row.add(slider, BorderLayout.CENTER);
        row.add(valLbl, BorderLayout.EAST);
        return row;
    }

    private JPanel makeSpinnerRow(String label, JSpinner spinner) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(PANEL_BG);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        row.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_BODY);
        lbl.setForeground(BORDER_LIGHT);
        lbl.setPreferredSize(new Dimension(160, 28));

        spinner.setPreferredSize(new Dimension(80, 28));

        row.add(lbl,     BorderLayout.WEST);
        row.add(spinner, BorderLayout.EAST);
        return row;
    }

    private JPanel makeCheckboxRow(JCheckBox cb) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setBackground(PANEL_BG);
        row.setAlignmentX(Component.CENTER_ALIGNMENT);
        row.add(cb);
        return row;
    }

    private JCheckBox makeCheckbox(String text, boolean selected) {
        JCheckBox cb = new JCheckBox(text.toUpperCase(), selected);
        cb.setFont(FONT_BODY);
        cb.setForeground(TEXT_MAIN);
        cb.setBackground(PANEL_BG);
        cb.setFocusPainted(false);
        return cb;
    }

    private JSpinner makeSpinner(int value) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, 1, 8, 1));
        spinner.setFont(FONT_BODY);
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setFont(FONT_BODY);
            tf.setForeground(TEXT_MAIN);
            tf.setBackground(new Color(40, 40, 48));
            tf.setCaretColor(TEXT_MAIN);
            tf.setBorder(new LineBorder(BORDER_LIGHT, 1));
        }
        return spinner;
    }

    private JSlider createSlider(float min, float max, float value, float step) {
        int range   = (int)((max - min) / step);
        int current = (int)((value - min) / step);
        JSlider slider = new JSlider(0, range, current);
        slider.setBackground(PANEL_BG);
        slider.putClientProperty("MIN_VALUE", min);
        slider.putClientProperty("MAX_VALUE", max);
        slider.putClientProperty("STEP", step);
        return slider;
    }

    private String sliderText(JSlider slider) {
        float min  = (Float) slider.getClientProperty("MIN_VALUE");
        float step = (Float) slider.getClientProperty("STEP");
        float val  = min + slider.getValue() * step;
        return step >= 1f ? String.format("%.0f", val)
             : step >= 0.1f ? String.format("%.1f", val)
             : String.format("%.2f", val);
    }

    private float getSliderFloat(JSlider slider) {
        float min  = (Float) slider.getClientProperty("MIN_VALUE");
        float step = (Float) slider.getClientProperty("STEP");
        return min + slider.getValue() * step;
    }
}