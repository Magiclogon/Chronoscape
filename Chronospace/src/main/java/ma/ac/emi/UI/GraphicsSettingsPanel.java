package ma.ac.emi.UI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicScrollBarUI;

import java.awt.*;
import java.util.Hashtable;

import ma.ac.emi.glgraphics.post.config.PostFXConfig;
import ma.ac.emi.glgraphics.post.config.PostFXConfigLoader;

public class GraphicsSettingsPanel extends JPanel {
    
    private static final Color BG_DARK      = new Color(18, 18, 24);
    private static final Color PANEL_BG     = new Color(30, 30, 38);
    private static final Color BORDER_LIGHT = new Color(180, 180, 190);
    private static final Color TEXT_MAIN    = new Color(235, 235, 245);
    private static final Color TEXT_GRAY    = new Color(150, 150, 160);
    private static final Color ACCENT_GOLD  = new Color(255, 215, 0);
    private static final Color ACCENT_RED   = new Color(220, 60, 60);
    private static final Color ACCENT_GREEN = new Color(80, 200, 100);
    private static final Color ACCENT_BLUE  = new Color(100, 150, 255);
    private static final Color ACCENT_PURPLE = new Color(180, 100, 255);

    private static final String FONT_NAME = "ByteBounce";
    private static final Font FONT_HEADER = new Font(FONT_NAME, Font.PLAIN, 32);
    private static final Font FONT_BODY   = new Font(FONT_NAME, Font.PLAIN, 20);
    private static final Font FONT_SMALL  = new Font(FONT_NAME, Font.PLAIN, 16);
    
    public enum GraphicsPreset {
        FAST("FAST", "Fast & Smooth", "Minimal effects for maximum FPS"),
        BALANCED("BALANCED", "Balanced", "Good balance of visuals and performance"),
        ULTRA("ULTRA", "Ultra", "Maximum visual fidelity"),
        CUSTOM("CUSTOM", "Custom", "User-defined settings");
        
        public final String id;
        public final String displayName;
        public final String description;
        
        GraphicsPreset(String id, String displayName, String description) {
            this.id = id;
            this.displayName = displayName;
            this.description = description;
        }
    }
    
    private PostFXConfig config;
    private GraphicsSettingsCallback callback;
    private Runnable goBackAction;
    private boolean hasUnsavedChanges = false;
    private boolean ignoreSliderChanges = false;
    
    private GraphicsPreset currentPreset = GraphicsPreset.BALANCED;
    private ButtonGroup presetButtonGroup;
    
    private JSlider renderScaleSlider;
    private JSlider bloomIntensitySlider;
    private JSlider glowIntensitySlider;
    
    public GraphicsSettingsPanel(PostFXConfig config) {
        this(config, null, null);
    }
    
    public GraphicsSettingsPanel(PostFXConfig config, GraphicsSettingsCallback callback) {
        this(config, callback, null);
    }
    
    public GraphicsSettingsPanel(PostFXConfig config, GraphicsSettingsCallback callback, Runnable goBackAction) {
        this.config = config;
        this.callback = callback;
        this.goBackAction = goBackAction;
        
        setLayout(new BorderLayout());
        setBackground(BG_DARK);
        
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setBackground(BG_DARK);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_DARK);
        mainPanel.setMaximumSize(new Dimension(400, Integer.MAX_VALUE));
        
        mainPanel.add(createHeader(), BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_DARK);
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        contentPanel.add(createPresetsPanel());
        contentPanel.add(Box.createVerticalStrut(15));
        
        contentPanel.add(createAdvancedPanel());
        contentPanel.add(Box.createVerticalStrut(15));
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBackground(BG_DARK);
        scrollPane.getViewport().setBackground(BG_DARK);
        scrollPane.setBorder(null);

        JScrollBar customVerticalBar = new RetroScrollBar(JScrollBar.VERTICAL);
        customVerticalBar.setPreferredSize(new Dimension(14, 0));
        customVerticalBar.setUnitIncrement(40);
        customVerticalBar.setBlockIncrement(80);

        scrollPane.setVerticalScrollBar(customVerticalBar);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(createFooter(), BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.ipadx = 400;
        
        wrapperPanel.add(mainPanel, gbc);
        add(wrapperPanel, BorderLayout.CENTER);
        
        detectCurrentPreset();
        updateUIForCurrentPreset();
    }
    
    private void updateUIForCurrentPreset() {
        for (java.util.Enumeration<AbstractButton> buttons = presetButtonGroup.getElements(); 
             buttons.hasMoreElements();) {
            PresetButton button = (PresetButton) buttons.nextElement();
            if (button.preset == currentPreset) {
                button.setSelected(true);
                break;
            }
        }
        
        ignoreSliderChanges = true;
        setSliderValue(renderScaleSlider, config.renderScale);
        setSliderValue(bloomIntensitySlider, config.bloom != null ? config.bloom.intensity : 0.0f);
        setSliderValue(glowIntensitySlider, config.glow != null ? config.glow.intensity : 0.0f);
        ignoreSliderChanges = false;
    }
    
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(10, 10, 15));
        header.setBorder(new CompoundBorder(
            new LineBorder(BORDER_LIGHT, 0, false),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel title = new JLabel("GRAPHICS SETTINGS");
        title.setFont(FONT_HEADER);
        title.setForeground(ACCENT_GOLD);
        header.add(title, BorderLayout.WEST);
        
        JPanel separator = new JPanel();
        separator.setPreferredSize(new Dimension(0, 4));
        separator.setBackground(BORDER_LIGHT);
        header.add(separator, BorderLayout.SOUTH);
        
        return header;
    }
    
    private JPanel createPresetsPanel() {
        JPanel panel = createSectionPanel("QUALITY PRESETS");
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(PANEL_BG);
        
        JLabel helpText = new JLabel("Choose a preset that matches your hardware:");
        helpText.setFont(FONT_SMALL);
        helpText.setForeground(TEXT_GRAY);
        helpText.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(helpText);
        content.add(Box.createVerticalStrut(15));
        
        presetButtonGroup = new ButtonGroup();
        
        for (GraphicsPreset preset : GraphicsPreset.values()) {
            PresetButton button = new PresetButton(preset);
            button.setAlignmentX(Component.CENTER_ALIGNMENT); 
            
            presetButtonGroup.add(button);
            content.add(button);
            content.add(Box.createVerticalStrut(10));
            
            if (preset == currentPreset) {
                button.setSelected(true);
            }
        }
        
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createAdvancedPanel() {
        JPanel panel = createSectionPanel("ADVANCED SETTINGS");
        
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(PANEL_BG);
        
        content.add(Box.createVerticalStrut(20));
        
        renderScaleSlider = createSlider(0.25f, 1.0f, 0.4f, 0.05f);
        content.add(createLabeledSlider("Render Scale", renderScaleSlider, ""));
        
        content.add(Box.createVerticalStrut(20));
        
        bloomIntensitySlider = createSlider(0.0f, 2.0f, 
            config.bloom != null ? config.bloom.intensity : 0.05f, 0.01f);
        content.add(createLabeledSlider("Bloom Intensity", bloomIntensitySlider, ""));
        
        content.add(Box.createVerticalStrut(20));

        glowIntensitySlider = createSlider(0.0f, 2.0f, 
            config.glow != null ? config.glow.intensity : 0.5f, 0.01f);
        content.add(createLabeledSlider("Glow Intensity", glowIntensitySlider, ""));

        content.add(Box.createVerticalStrut(40));

        RetroButton moreAdvancedButton = new RetroButton("MORE OPTIONS >", ACCENT_PURPLE, Color.WHITE);
        moreAdvancedButton.setPreferredSize(new Dimension(200, 35));
        moreAdvancedButton.setMaximumSize(new Dimension(200, 35));
        moreAdvancedButton.addActionListener(e -> showFullAdvancedSettings());
        content.add(moreAdvancedButton);
        
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        footer.setBackground(new Color(10, 10, 15));
        footer.setBorder(new CompoundBorder(
            new LineBorder(BORDER_LIGHT, 2, false),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        RetroButton applyButton = new RetroButton("APPLY", ACCENT_GREEN, Color.BLACK);
        applyButton.setPreferredSize(new Dimension(140, 40));
        applyButton.addActionListener(e -> applyChanges());
        
        RetroButton resetButton = new RetroButton("RESET", ACCENT_RED, Color.WHITE);
        resetButton.setPreferredSize(new Dimension(140, 40));
        resetButton.addActionListener(e -> resetToDefaults());
        
        RetroButton backButton = new RetroButton("< BACK", BORDER_LIGHT, Color.BLACK);
        backButton.setPreferredSize(new Dimension(140, 40));
        backButton.addActionListener(e -> closeWindow());
        
        footer.add(applyButton);
        footer.add(resetButton);
        footer.add(backButton);
        
        return footer;
    }
    
    private void showFullAdvancedSettings() {
        JDialog advancedDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Advanced Graphics Settings", true);
        advancedDialog.setLayout(new BorderLayout());
        
        AdvancedSettingsPanel advancedPanel = new AdvancedSettingsPanel(config);
        
        JScrollPane scroll = new JScrollPane(advancedPanel);
        scroll.setPreferredSize(new Dimension(700, 600));
        advancedDialog.add(scroll, BorderLayout.CENTER);
        
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.setBackground(BG_DARK);
        
        RetroButton okButton = new RetroButton("OK", ACCENT_GREEN, Color.BLACK);
        okButton.addActionListener(e -> {
            advancedPanel.applyToConfig(config);
            hasUnsavedChanges = true;
            switchToCustomPreset();
            advancedDialog.dispose();
        });
        
        RetroButton cancelButton = new RetroButton("CANCEL", ACCENT_RED, Color.WHITE);
        cancelButton.addActionListener(e -> advancedDialog.dispose());
        
        buttons.add(okButton);
        buttons.add(cancelButton);
        advancedDialog.add(buttons, BorderLayout.SOUTH);
        
        advancedDialog.pack();
        advancedDialog.setLocationRelativeTo(this);
        advancedDialog.setVisible(true);
    }
    
    private void applyPreset(GraphicsPreset preset) {
        if (preset == GraphicsPreset.CUSTOM) {
            currentPreset = preset;
            return;
        }
        
        currentPreset = preset;
        hasUnsavedChanges = true;
        
        ignoreSliderChanges = true;
        
        switch (preset) {
            case FAST:
                applyFastPreset();
                break;
            case BALANCED:
                applyBalancedPreset();
                break;
            case ULTRA:
                applyUltraPreset();
                break;
            default:
                break;
        }
        
        ignoreSliderChanges = false;
    }
    
    private void applyFastPreset() {
        if (config.colorCorrection != null) {
            config.colorCorrection.enabled = true;
            config.colorCorrection.r = 1.0f;
            config.colorCorrection.g = 1.0f;
            config.colorCorrection.b = 1.2f;
            config.colorCorrection.a = 1.0f;
            config.colorCorrection.brightness = 0.0f;
            config.colorCorrection.contrast = 1.0f;
            config.colorCorrection.hue = 0.0f;
            config.colorCorrection.saturation = 1.2f;
            config.colorCorrection.value = 0.5f;
        }
        if (config.bloom != null) {
            config.bloom.enabled = false;
        }
        if (config.glow != null) {
            config.glow.enabled = false;
        }
        config.renderScale = 0.25f;
        setSliderValue(renderScaleSlider, 0.25f);
        setSliderValue(bloomIntensitySlider, 0.0f);
        setSliderValue(glowIntensitySlider, 0.0f);
    }
    
    private void applyBalancedPreset() {
        if (config.colorCorrection != null) {
            config.colorCorrection.enabled = true;
            config.colorCorrection.r = 1.0f;
            config.colorCorrection.g = 1.0f;
            config.colorCorrection.b = 1.2f;
            config.colorCorrection.a = 1.0f;
            config.colorCorrection.brightness = 0.0f;
            config.colorCorrection.contrast = 1.0f;
            config.colorCorrection.hue = 0.0f;
            config.colorCorrection.saturation = 1.2f;
            config.colorCorrection.value = 0.5f;
        }
        if (config.bloom != null) {
            config.bloom.enabled = true;
            config.bloom.downscale = 2;
            config.bloom.intensity = 0.05f;
            config.bloom.threshold = 0.05f;
            config.bloom.blurRadius = 1;
        }
        if (config.glow != null) {
            config.glow.enabled = true;
            config.glow.downscale = 2;
            config.glow.blurRadius = 0.05f;
            config.glow.intensity = 0.5f;
            config.glow.saturationBoost = 1.5f;
        }
        config.renderScale = 0.4f;
        setSliderValue(renderScaleSlider, 0.4f);
        setSliderValue(bloomIntensitySlider, 0.05f);
        setSliderValue(glowIntensitySlider, 0.5f);
    }
    
    private void applyUltraPreset() {
        if (config.colorCorrection != null) {
            config.colorCorrection.enabled = true;
            config.colorCorrection.r = 1.0f;
            config.colorCorrection.g = 1.0f;
            config.colorCorrection.b = 1.2f;
            config.colorCorrection.a = 1.0f;
            config.colorCorrection.brightness = 0.0f;
            config.colorCorrection.contrast = 1.0f;
            config.colorCorrection.hue = 0.0f;
            config.colorCorrection.saturation = 1.2f;
            config.colorCorrection.value = 0.5f;
        }
        if (config.bloom != null) {
            config.bloom.enabled = true;
            config.bloom.downscale = 2;
            config.bloom.intensity = 0.05f;
            config.bloom.threshold = 0.05f;
            config.bloom.blurRadius = 1;
        }
        if (config.glow != null) {
            config.glow.enabled = true;
            config.glow.downscale = 2;
            config.glow.blurRadius = 0.1f;
            config.glow.intensity = 0.7f;
            config.glow.saturationBoost = 1.5f;
        }
        config.renderScale = 1.0f;
        setSliderValue(renderScaleSlider, 1.0f);
        setSliderValue(bloomIntensitySlider, 0.05f);
        setSliderValue(glowIntensitySlider, 0.7f);
    }
    
    private void switchToCustomPreset() {
        if (currentPreset != GraphicsPreset.CUSTOM) {
            currentPreset = GraphicsPreset.CUSTOM;
            
            for (java.util.Enumeration<AbstractButton> buttons = presetButtonGroup.getElements(); 
                 buttons.hasMoreElements();) {
                PresetButton button = (PresetButton) buttons.nextElement();
                if (button.preset == GraphicsPreset.CUSTOM) {
                    button.setSelected(true);
                    break;
                }
            }
        }
    }
    
    private void detectCurrentPreset() {
        float currentRenderScale = config.renderScale;
        boolean bloomEnabled = config.bloom != null && config.bloom.enabled;
        boolean glowEnabled = config.glow != null && config.glow.enabled;
        
        float bloomIntensity = config.bloom != null ? config.bloom.intensity : 0.0f;
        float glowIntensity = config.glow != null ? config.glow.intensity : 0.0f;
        
        if (Math.abs(currentRenderScale - 0.25f) < 0.01f && !bloomEnabled && !glowEnabled) {
            currentPreset = GraphicsPreset.FAST;
            return;
        }
        
        if (Math.abs(currentRenderScale - 0.4f) < 0.01f && 
            bloomEnabled && Math.abs(bloomIntensity - 0.05f) < 0.01f &&
            glowEnabled && Math.abs(glowIntensity - 0.5f) < 0.01f) {
            currentPreset = GraphicsPreset.BALANCED;
            return;
        }
        
        if (Math.abs(currentRenderScale - 1.0f) < 0.01f && 
            bloomEnabled && Math.abs(bloomIntensity - 0.05f) < 0.01f &&
            glowEnabled && Math.abs(glowIntensity - 0.7f) < 0.01f) {
            currentPreset = GraphicsPreset.ULTRA;
            return;
        }
        
        currentPreset = GraphicsPreset.CUSTOM;
    }
    
    private JPanel createSectionPanel(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(PANEL_BG);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 800));
        
        Border line = new LineBorder(BORDER_LIGHT, 2);
        Border titled = BorderFactory.createTitledBorder(line, " " + title + " ",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                FONT_BODY, BORDER_LIGHT);
        p.setBorder(new CompoundBorder(titled, new EmptyBorder(10, 10, 10, 10)));
        
        return p;
    }
    
    private JSlider createSlider(float min, float max, float value, float step) {
        int range = (int)((max - min) / step);
        int currentValue = (int)((value - min) / step);
        
        JSlider slider = new JSlider(0, range, currentValue);
        slider.setBackground(PANEL_BG);
        slider.setForeground(ACCENT_GOLD);
        
        slider.putClientProperty("MIN_VALUE", min);
        slider.putClientProperty("MAX_VALUE", max);
        slider.putClientProperty("STEP", step);
        
        slider.addChangeListener(e -> {
            if (!ignoreSliderChanges && !slider.getValueIsAdjusting()) {
                switchToCustomPreset();
            }
            hasUnsavedChanges = true;
        });
        
        return slider;
    }
    
    private JPanel createLabeledSlider(String label, JSlider slider, String hint) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL_BG);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        JPanel topRow = new JPanel(new BorderLayout(10, 5));
        topRow.setBackground(PANEL_BG);
        
        JLabel labelComponent = new JLabel(label.toUpperCase());
        labelComponent.setFont(FONT_BODY);
        labelComponent.setForeground(TEXT_GRAY);
        
        JLabel valueLabel = new JLabel(getSliderValue(slider));
        valueLabel.setFont(FONT_BODY);
        valueLabel.setForeground(ACCENT_GOLD);
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        slider.addChangeListener(e -> {
            valueLabel.setText(getSliderValue(slider));
        });
        
        topRow.add(labelComponent, BorderLayout.WEST);
        topRow.add(slider, BorderLayout.CENTER);
        topRow.add(valueLabel, BorderLayout.EAST);
        
        panel.add(topRow);
        
        if (hint != null && !hint.isEmpty()) {
            JLabel hintLabel = new JLabel(hint);
            hintLabel.setFont(FONT_SMALL);
            hintLabel.setForeground(new Color(120, 120, 130));
            panel.add(hintLabel);
        }
        
        return panel;
    }
    
    private String getSliderValue(JSlider slider) {
        float min = (Float) slider.getClientProperty("MIN_VALUE");
        float max = (Float) slider.getClientProperty("MAX_VALUE");
        float step = (Float) slider.getClientProperty("STEP");
        
        float actualValue = min + slider.getValue() * step;
        
        if (step >= 1) {
            return String.format(java.util.Locale.US, "%.0f", actualValue);
        } else if (step >= 0.1) {
            return String.format(java.util.Locale.US, "%.1f", actualValue);
        } else {
            return String.format(java.util.Locale.US, "%.2f", actualValue);
        }
    }
    
    private float getSliderFloatValue(JSlider slider) {
        float min = (Float) slider.getClientProperty("MIN_VALUE");
        float step = (Float) slider.getClientProperty("STEP");
        return min + slider.getValue() * step;
    }
    
    private void setSliderValue(JSlider slider, float value) {
        float min = (Float) slider.getClientProperty("MIN_VALUE");
        float step = (Float) slider.getClientProperty("STEP");
        int sliderValue = (int)((value - min) / step);
        slider.setValue(sliderValue);
    }
    
    private void closeWindow() {
        if (hasUnsavedChanges) {
            int result = JOptionPane.showConfirmDialog(
                this,
                "You have unsaved changes. Save before going back?",
                "Unsaved Changes",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (result == JOptionPane.YES_OPTION) {
                applyChanges(); // Apply will now also save
                doGoBack();
            } else if (result == JOptionPane.NO_OPTION) {
                doGoBack();
            }
        } else {
            doGoBack();
        }
    }
    
    private void doGoBack() {
        if (goBackAction != null) {
            goBackAction.run();
        }
    }
    
    private void applyChanges() {
        applyAdvancedSettingsToConfig();
        
        // Save to file automatically
        try {
            PostFXConfigLoader.save(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Apply to renderer if callback available
        if (callback != null) {
            callback.onSettingsChanged(config);
        }
        
        hasUnsavedChanges = false;
    }
    
    private void resetToDefaults() {
        applyPreset(GraphicsPreset.BALANCED);
        updateUIForCurrentPreset();
    }
    
    private void applyAdvancedSettingsToConfig() {
        if (config.bloom != null) {
            config.bloom.intensity = getSliderFloatValue(bloomIntensitySlider);
        }
        if (config.glow != null) {
            config.glow.intensity = getSliderFloatValue(glowIntensitySlider);
        }
        config.renderScale = getSliderFloatValue(renderScaleSlider);
    }
    
    // ===== CUSTOM COMPONENTS =====
    
    private class PresetButton extends JRadioButton {
        private GraphicsPreset preset;
        private boolean isHovered = false;

        public PresetButton(GraphicsPreset preset) {
            super();
            this.preset = preset;

            setLayout(new BorderLayout(15, 5));
            setOpaque(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            Dimension size = new Dimension(500, 70);
            setPreferredSize(size);
            setMaximumSize(size);
            setMinimumSize(size);

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setOpaque(false);

            JLabel nameLabel = new JLabel(preset.displayName.toUpperCase());
            nameLabel.setFont(FONT_BODY);
            nameLabel.setForeground(ACCENT_GOLD);

            JLabel descLabel = new JLabel(preset.description);
            descLabel.setFont(FONT_SMALL);
            descLabel.setForeground(TEXT_GRAY);

            textPanel.add(nameLabel);
            textPanel.add(descLabel);

            add(textPanel, BorderLayout.CENTER);

            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) { isHovered = true; repaint(); }
                public void mouseExited(java.awt.event.MouseEvent e) { isHovered = false; repaint(); }
            });

            addActionListener(e -> {
                if (isSelected()) {
                    applyPreset(preset);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

            if (isSelected()) {
                g2.setColor(new Color(45, 45, 55)); 
            } else if (isHovered) {
                g2.setColor(new Color(35, 35, 43)); 
            } else {
                g2.setColor(PANEL_BG);
            }
            g2.fillRect(0, 0, getWidth(), getHeight());

            if (isSelected()) {
                g2.setColor(ACCENT_GOLD);
                g2.setStroke(new BasicStroke(3));
            } else if (isHovered) {
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
            } else {
                g2.setColor(BORDER_LIGHT);
                g2.setStroke(new BasicStroke(2));
            }
            g2.drawRect(1, 1, getWidth() - 3, getHeight() - 3);

            g2.dispose();
        }
    }
    
    private class RetroButton extends JButton {
        private Color normalColor;
        private Color hoverColor;

        public RetroButton(String text, Color bg, Color fg) {
            super(text);
            this.normalColor = bg;
            this.hoverColor = bg.brighter();

            setFont(FONT_BODY);
            setForeground(fg);
            setBackground(bg);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

            if (getModel().isPressed()) g2.setColor(normalColor.darker());
            else if (getModel().isRollover()) g2.setColor(hoverColor);
            else g2.setColor(normalColor);

            g2.fillRect(2, 2, getWidth()-4, getHeight()-4);

            g2.setColor(BORDER_LIGHT);
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(1, 1, getWidth()-3, getHeight()-3);

            super.paintComponent(g);
        }
    }
    
    private static class RetroScrollBarUI extends BasicScrollBarUI {
        
        private static final Color SCROLL_THUMB_COLOR = new Color(255, 215, 0);
        private static final Color SCROLL_TRACK_COLOR = new Color(10, 10, 15);
        private static final Color SCROLL_BORDER_COLOR = new Color(180, 180, 190);
        private static final Color SCROLL_BG_COLOR = new Color(18, 18, 24);
        
        public RetroScrollBarUI() {
            super();
        }
        
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = SCROLL_THUMB_COLOR;
            this.trackColor = SCROLL_TRACK_COLOR;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
        @Override
        protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !c.isEnabled()) return;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            
            g2.setColor(SCROLL_THUMB_COLOR);
            g2.fillRect(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4);
            
            g2.setColor(SCROLL_BORDER_COLOR);
            g2.drawRect(thumbBounds.x + 2, thumbBounds.y + 2, thumbBounds.width - 4, thumbBounds.height - 4);
            
            g2.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(SCROLL_TRACK_COLOR); 
            g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            
            g2.setColor(new Color(30, 30, 38));
            g2.drawLine(trackBounds.x, trackBounds.y, trackBounds.x, trackBounds.y + trackBounds.height);
            g2.dispose();
        }
        
        @Override
        protected void installDefaults() {
            super.installDefaults();
            scrollbar.setOpaque(true);
            scrollbar.setFocusable(false);
            scrollbar.setBackground(SCROLL_BG_COLOR);
        }
        
        @Override
        public void uninstallUI(JComponent c) {
            // Keep our UI installed
        }
        
        @Override
        protected void paintDecreaseHighlight(Graphics g) {}

        @Override
        protected void paintIncreaseHighlight(Graphics g) {}
    }
    
    private class RetroScrollBar extends JScrollBar {
        public RetroScrollBar(int orientation) {
            super(orientation);
            setUI(new RetroScrollBarUI());
            putClientProperty("Nimbus.Overrides", new javax.swing.UIDefaults());
            putClientProperty("Nimbus.Overrides.InheritDefaults", Boolean.FALSE);
        }

        @Override
        public void updateUI() {
            setUI(new RetroScrollBarUI());
        }
        
        @Override
        public void setUI(javax.swing.plaf.ScrollBarUI ui) {
            super.setUI(new RetroScrollBarUI());
        }
    }
}