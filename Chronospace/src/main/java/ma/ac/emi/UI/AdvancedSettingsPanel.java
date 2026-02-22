package ma.ac.emi.UI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.Hashtable;

import ma.ac.emi.glgraphics.post.config.PostFXConfig;

/**
 * Advanced settings panel with all the detailed controls
 * Shown in a separate dialog when user clicks "MORE OPTIONS"
 */
public class AdvancedSettingsPanel extends JPanel {
    
    private static final Color BG_DARK      = new Color(18, 18, 24);
    private static final Color PANEL_BG     = new Color(30, 30, 38);
    private static final Color BORDER_LIGHT = new Color(180, 180, 190);
    private static final Color TEXT_MAIN    = new Color(235, 235, 245);
    private static final Color TEXT_GRAY    = new Color(150, 150, 160);
    private static final Color ACCENT_GOLD  = new Color(255, 215, 0);
    private static final Color ACCENT_RED   = new Color(220, 60, 60);

    private static final String FONT_NAME = "ByteBounce";
    private static final Font FONT_HEADER = new Font(FONT_NAME, Font.PLAIN, 24);
    private static final Font FONT_BODY   = new Font(FONT_NAME, Font.PLAIN, 16);
    private static final Font FONT_SMALL  = new Font(FONT_NAME, Font.PLAIN, 12);
    
    // All the advanced controls
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
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(BG_DARK);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel warning = new JLabel("⚠ EXPERT MODE - Modify at your own risk");
        warning.setFont(FONT_HEADER);
        warning.setForeground(ACCENT_RED);
        warning.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(warning);
        add(Box.createVerticalStrut(15));
        
        add(createColorCorrectionPanel(config));
        add(Box.createVerticalStrut(10));
        add(createBloomPanel(config));
        add(Box.createVerticalStrut(10));
        add(createGlowPanel(config));
    }
    
    private JPanel createColorCorrectionPanel(PostFXConfig config) {
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
    
    private JPanel createBloomPanel(PostFXConfig config) {
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
    
    private JPanel createGlowPanel(PostFXConfig config) {
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
    
    /**
     * Apply all the advanced settings back to the config
     */
    public void applyToConfig(PostFXConfig config) {
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
    
    // ===== UI HELPER METHODS =====
    
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
        });
        
        panel.add(labelComponent, BorderLayout.WEST);
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
        
        component.setPreferredSize(new Dimension(80, 25));
        
        panel.add(labelComponent, BorderLayout.WEST);
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
}