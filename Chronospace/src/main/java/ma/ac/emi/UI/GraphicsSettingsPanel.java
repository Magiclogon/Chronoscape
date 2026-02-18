package ma.ac.emi.UI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.Hashtable;

import ma.ac.emi.glgraphics.post.config.PostFXConfig;
import ma.ac.emi.glgraphics.post.config.PostFXConfigLoader;

public class GraphicsSettingsPanel extends JPanel {
    
    // --- ROGUELIKE PALETTE (matching ShopUI) ---
    private static final Color BG_DARK      = new Color(18, 18, 24);
    private static final Color PANEL_BG     = new Color(30, 30, 38);
    private static final Color BORDER_LIGHT = new Color(180, 180, 190);
    private static final Color TEXT_MAIN    = new Color(235, 235, 245);
    private static final Color TEXT_GRAY    = new Color(150, 150, 160);
    private static final Color ACCENT_GOLD  = new Color(255, 215, 0);
    private static final Color ACCENT_RED   = new Color(220, 60, 60);
    private static final Color ACCENT_GREEN = new Color(80, 200, 100);
    private static final Color ACCENT_BLUE  = new Color(100, 150, 255);

    // --- FONTS ---
    private static final String FONT_NAME = "ByteBounce";
    private static final Font FONT_HEADER = new Font(FONT_NAME, Font.PLAIN, 28);
    private static final Font FONT_BODY   = new Font(FONT_NAME, Font.PLAIN, 18);
    private static final Font FONT_SMALL  = new Font(FONT_NAME, Font.PLAIN, 14);
    
    private PostFXConfig config;
    private GraphicsSettingsCallback callback;
    private Runnable goBackAction;
    private boolean hasUnsavedChanges = false;
    
    // Controls
    private JSlider rSlider, gSlider, bSlider, aSlider;
    private JSlider brightnessSlider, contrastSlider, saturationSlider, hueSlider, valueSlider;
    private JCheckBox colorCorrectionEnabled;
    private JCheckBox bloomEnabled;
    private JSlider bloomThresholdSlider, bloomBlurRadiusSlider, bloomIntensitySlider;
    private JSpinner bloomDownscaleSpinner;
    private JCheckBox glowEnabled;
    private JSlider glowBlurRadiusSlider, glowIntensitySlider, glowSaturationBoostSlider;
    private JSpinner glowDownscaleSpinner;
    
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
        
        add(createHeader(), BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_DARK);
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        contentPanel.add(createColorCorrectionPanel());
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(createBloomPanel());
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(createGlowPanel());
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBackground(BG_DARK);
        scrollPane.getViewport().setBackground(BG_DARK);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(scrollPane, BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
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
        
        RetroButton saveButton = new RetroButton("SAVE", ACCENT_BLUE, Color.BLACK);
        saveButton.setPreferredSize(new Dimension(140, 40));
        saveButton.addActionListener(e -> saveToFile());
        
        RetroButton resetButton = new RetroButton("RESET", ACCENT_RED, Color.WHITE);
        resetButton.setPreferredSize(new Dimension(140, 40));
        resetButton.addActionListener(e -> resetToDefaults());
        
        RetroButton backButton = new RetroButton("< BACK", BORDER_LIGHT, Color.BLACK);
        backButton.setPreferredSize(new Dimension(140, 40));
        backButton.addActionListener(e -> closeWindow());
        
        footer.add(applyButton);
        footer.add(saveButton);
        footer.add(resetButton);
        footer.add(backButton);
        
        return footer;
    }
    
    private JPanel createColorCorrectionPanel() {
        JPanel panel = createSectionPanel("COLOR CORRECTION");
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(PANEL_BG);
        
        colorCorrectionEnabled = createStyledCheckbox("Enabled", 
            config.colorCorrection != null && config.colorCorrection.enabled);
        content.add(colorCorrectionEnabled);
        content.add(Box.createVerticalStrut(10));
        
        if (config.colorCorrection != null) {
            rSlider = createSlider(0.0f, 2.0f, config.colorCorrection.r, 0.01f);
            gSlider = createSlider(0.0f, 2.0f, config.colorCorrection.g, 0.01f);
            bSlider = createSlider(0.0f, 2.0f, config.colorCorrection.b, 0.01f);
            aSlider = createSlider(0.0f, 2.0f, config.colorCorrection.a, 0.01f);
            brightnessSlider = createSlider(-1.0f, 1.0f, config.colorCorrection.brightness, 0.01f);
            contrastSlider = createSlider(0.0f, 3.0f, config.colorCorrection.contrast, 0.01f);
            saturationSlider = createSlider(0.0f, 3.0f, config.colorCorrection.saturation, 0.01f);
            hueSlider = createSlider(-180f, 180f, config.colorCorrection.hue, 1f);
            valueSlider = createSlider(-1.0f, 1.0f, config.colorCorrection.value, 0.01f);
            
            content.add(createLabeledSlider("Red", rSlider));
            content.add(createLabeledSlider("Green", gSlider));
            content.add(createLabeledSlider("Blue", bSlider));
            content.add(createLabeledSlider("Alpha", aSlider));
            content.add(createLabeledSlider("Brightness", brightnessSlider));
            content.add(createLabeledSlider("Contrast", contrastSlider));
            content.add(createLabeledSlider("Saturation", saturationSlider));
            content.add(createLabeledSlider("Hue Shift", hueSlider));
            content.add(createLabeledSlider("Value", valueSlider));
        }
        
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createBloomPanel() {
        JPanel panel = createSectionPanel("BLOOM");
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(PANEL_BG);
        
        bloomEnabled = createStyledCheckbox("Enabled", 
            config.bloom != null && config.bloom.enabled);
        content.add(bloomEnabled);
        content.add(Box.createVerticalStrut(10));
        
        if (config.bloom != null) {
            SpinnerNumberModel downscaleModel = new SpinnerNumberModel(
                config.bloom.downscale, 1, 8, 1
            );
            bloomDownscaleSpinner = new JSpinner(downscaleModel);
            styleSpinner(bloomDownscaleSpinner);
            content.add(createLabeledComponent("Downscale", bloomDownscaleSpinner));
            
            bloomThresholdSlider = createSlider(0.0f, 1.0f, config.bloom.threshold, 0.01f);
            bloomBlurRadiusSlider = createSlider(0.0f, 10.0f, config.bloom.blurRadius, 0.1f);
            bloomIntensitySlider = createSlider(0.0f, 2.0f, config.bloom.intensity, 0.01f);
            
            content.add(createLabeledSlider("Threshold", bloomThresholdSlider));
            content.add(createLabeledSlider("Blur Radius", bloomBlurRadiusSlider));
            content.add(createLabeledSlider("Intensity", bloomIntensitySlider));
        }
        
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createGlowPanel() {
        JPanel panel = createSectionPanel("GLOW");
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(PANEL_BG);
        
        glowEnabled = createStyledCheckbox("Enabled", 
            config.glow != null && config.glow.enabled);
        content.add(glowEnabled);
        content.add(Box.createVerticalStrut(10));
        
        if (config.glow != null) {
            SpinnerNumberModel downscaleModel = new SpinnerNumberModel(
                config.glow.downscale, 1, 8, 1
            );
            glowDownscaleSpinner = new JSpinner(downscaleModel);
            styleSpinner(glowDownscaleSpinner);
            content.add(createLabeledComponent("Downscale", glowDownscaleSpinner));
            
            glowBlurRadiusSlider = createSlider(0.0f, 10.0f, config.glow.blurRadius, 0.01f);
            glowIntensitySlider = createSlider(0.0f, 2.0f, config.glow.intensity, 0.01f);
            glowSaturationBoostSlider = createSlider(1.0f, 3.0f, 
                config.glow.saturationBoost != 0 ? config.glow.saturationBoost : 1.5f, 0.1f);
            
            content.add(createLabeledSlider("Blur Radius", glowBlurRadiusSlider));
            content.add(createLabeledSlider("Intensity", glowIntensitySlider));
            content.add(createLabeledSlider("Saturation", glowSaturationBoostSlider));
        }
        
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createSectionPanel(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(PANEL_BG);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 600));
        
        Border line = new LineBorder(BORDER_LIGHT, 2);
        Border titled = BorderFactory.createTitledBorder(line, " " + title + " ",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                FONT_BODY, BORDER_LIGHT);
        p.setBorder(new CompoundBorder(titled, new EmptyBorder(10, 10, 10, 10)));
        
        return p;
    }
    
    private JCheckBox createStyledCheckbox(String text, boolean selected) {
        JCheckBox cb = new JCheckBox(text.toUpperCase(), selected);
        cb.setFont(FONT_BODY);
        cb.setForeground(TEXT_MAIN);
        cb.setBackground(PANEL_BG);
        cb.setFocusPainted(false);
        cb.addActionListener(e -> hasUnsavedChanges = true);
        return cb;
    }
    
    private void styleSpinner(JSpinner spinner) {
        spinner.setFont(FONT_BODY);
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor) editor;
            spinnerEditor.getTextField().setFont(FONT_BODY);
            spinnerEditor.getTextField().setForeground(TEXT_MAIN);
            spinnerEditor.getTextField().setBackground(new Color(40, 40, 48));
            spinnerEditor.getTextField().setCaretColor(TEXT_MAIN);
            spinnerEditor.getTextField().setBorder(new LineBorder(BORDER_LIGHT, 1));
        }
        spinner.addChangeListener(e -> hasUnsavedChanges = true);
    }
    
    private JSlider createSlider(float min, float max, float value, float step) {
        int range = (int)((max - min) / step);
        int currentValue = (int)((value - min) / step);
        
        JSlider slider = new JSlider(0, range, currentValue);
        slider.setBackground(PANEL_BG);
        slider.setForeground(ACCENT_GOLD);
        
        // Store min/max as client properties for later retrieval
        slider.putClientProperty("MIN_VALUE", min);
        slider.putClientProperty("MAX_VALUE", max);
        slider.putClientProperty("STEP", step);
        
        return slider;
    }
    
    private JPanel createLabeledSlider(String label, JSlider slider) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(PANEL_BG);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        JLabel labelComponent = new JLabel(label.toUpperCase());
        labelComponent.setFont(FONT_BODY);
        labelComponent.setForeground(TEXT_GRAY);
        labelComponent.setPreferredSize(new Dimension(130, 25));
        
        JLabel valueLabel = new JLabel(getSliderValue(slider));
        valueLabel.setFont(FONT_BODY);
        valueLabel.setForeground(ACCENT_GOLD);
        valueLabel.setPreferredSize(new Dimension(70, 25));
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        slider.addChangeListener(e -> {
            valueLabel.setText(getSliderValue(slider));
            hasUnsavedChanges = true;
        });
        
        // Dots separator
        JLabel dots = new JLabel(" . . . . . . . . ");
        dots.setFont(FONT_SMALL);
        dots.setForeground(new Color(60, 60, 70));
        dots.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(PANEL_BG);
        leftPanel.add(labelComponent, BorderLayout.WEST);
        leftPanel.add(dots, BorderLayout.CENTER);
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(slider, BorderLayout.CENTER);
        panel.add(valueLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createLabeledComponent(String label, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(PANEL_BG);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        JLabel labelComponent = new JLabel(label.toUpperCase());
        labelComponent.setFont(FONT_BODY);
        labelComponent.setForeground(TEXT_GRAY);
        labelComponent.setPreferredSize(new Dimension(130, 25));
        
        JLabel dots = new JLabel(" . . . . . . . . ");
        dots.setFont(FONT_SMALL);
        dots.setForeground(new Color(60, 60, 70));
        
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(PANEL_BG);
        leftPanel.add(labelComponent, BorderLayout.WEST);
        leftPanel.add(dots, BorderLayout.CENTER);
        
        component.setPreferredSize(new Dimension(80, 25));
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(component, BorderLayout.EAST);
        
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
                saveToFile();
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
        applyChangesToConfig();
        
        if (callback != null) {
            callback.onSettingsChanged(config);
        }
        
        hasUnsavedChanges = false;
    }
    
    private void resetToDefaults() {
        if (config.colorCorrection != null) {
            config.colorCorrection.r = 1.0f;
            config.colorCorrection.g = 1.0f;
            config.colorCorrection.b = 1.2f;
            config.colorCorrection.a = 1.0f;
            config.colorCorrection.brightness = 0.0f;
            config.colorCorrection.contrast = 1.0f;
            config.colorCorrection.saturation = 1.2f;
            config.colorCorrection.hue = 0.0f;
            config.colorCorrection.value = 0.5f;
        }
        
        if (config.bloom != null) {
            config.bloom.downscale = 2;
            config.bloom.threshold = 0.05f;
            config.bloom.blurRadius = 1.0f;
            config.bloom.intensity = 0.05f;
        }
        
        if (config.glow != null) {
            config.glow.downscale = 2;
            config.glow.blurRadius = 0.05f;
            config.glow.intensity = 0.5f;
            config.glow.saturationBoost = 1.5f;
        }
        
        updateUIFromConfig();
        hasUnsavedChanges = true;
    }
    
    private void updateUIFromConfig() {
        if (config.colorCorrection != null) {
            colorCorrectionEnabled.setSelected(config.colorCorrection.enabled);
            setSliderValue(rSlider, config.colorCorrection.r);
            setSliderValue(gSlider, config.colorCorrection.g);
            setSliderValue(bSlider, config.colorCorrection.b);
            setSliderValue(aSlider, config.colorCorrection.a);
            setSliderValue(brightnessSlider, config.colorCorrection.brightness);
            setSliderValue(contrastSlider, config.colorCorrection.contrast);
            setSliderValue(saturationSlider, config.colorCorrection.saturation);
            setSliderValue(hueSlider, config.colorCorrection.hue);
            setSliderValue(valueSlider, config.colorCorrection.value);
        }
        
        if (config.bloom != null) {
            bloomEnabled.setSelected(config.bloom.enabled);
            bloomDownscaleSpinner.setValue(config.bloom.downscale);
            setSliderValue(bloomThresholdSlider, config.bloom.threshold);
            setSliderValue(bloomBlurRadiusSlider, config.bloom.blurRadius);
            setSliderValue(bloomIntensitySlider, config.bloom.intensity);
        }
        
        if (config.glow != null) {
            glowEnabled.setSelected(config.glow.enabled);
            glowDownscaleSpinner.setValue(config.glow.downscale);
            setSliderValue(glowBlurRadiusSlider, config.glow.blurRadius);
            setSliderValue(glowIntensitySlider, config.glow.intensity);
            setSliderValue(glowSaturationBoostSlider, config.glow.saturationBoost);
        }
    }
    
    private void saveToFile() {
        try {
            applyChangesToConfig();
            PostFXConfigLoader.save(config);
            hasUnsavedChanges = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void applyChangesToConfig() {
        if (config.colorCorrection != null) {
            config.colorCorrection.enabled = colorCorrectionEnabled.isSelected();
            config.colorCorrection.r = getSliderFloatValue(rSlider);
            config.colorCorrection.g = getSliderFloatValue(gSlider);
            config.colorCorrection.b = getSliderFloatValue(bSlider);
            config.colorCorrection.a = getSliderFloatValue(aSlider);
            config.colorCorrection.brightness = getSliderFloatValue(brightnessSlider);
            config.colorCorrection.contrast = getSliderFloatValue(contrastSlider);
            config.colorCorrection.saturation = getSliderFloatValue(saturationSlider);
            config.colorCorrection.hue = getSliderFloatValue(hueSlider);
            config.colorCorrection.value = getSliderFloatValue(valueSlider);
        }
        
        if (config.bloom != null) {
            config.bloom.enabled = bloomEnabled.isSelected();
            config.bloom.downscale = (Integer) bloomDownscaleSpinner.getValue();
            config.bloom.threshold = getSliderFloatValue(bloomThresholdSlider);
            config.bloom.blurRadius = getSliderFloatValue(bloomBlurRadiusSlider);
            config.bloom.intensity = getSliderFloatValue(bloomIntensitySlider);
        }
        
        if (config.glow != null) {
            config.glow.enabled = glowEnabled.isSelected();
            config.glow.downscale = (Integer) glowDownscaleSpinner.getValue();
            config.glow.blurRadius = getSliderFloatValue(glowBlurRadiusSlider);
            config.glow.intensity = getSliderFloatValue(glowIntensitySlider);
            config.glow.saturationBoost = getSliderFloatValue(glowSaturationBoostSlider);
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
}