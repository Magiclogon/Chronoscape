package ma.ac.emi.UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import ma.ac.emi.UI.component.RetroButton;

public class SoundSettingsPanel extends JPanel {

    private static final String FONT_NAME  = "ByteBounce";
    private static final Color  BG_DARK    = new Color(18, 18, 24);
    private static final Color  PANEL_BG   = new Color(30, 30, 38);
    private static final Color  BORDER_COL = new Color(180, 180, 190);
    private static final Color  ACCENT     = new Color(80, 200, 100);
    private static final Color  ACCENT_RED = new Color(220, 60, 60);
    private static final Color  MUTED      = new Color(80, 80, 90);

    private boolean isMuted = false;

    public SoundSettingsPanel() {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(PANEL_BG);
        card.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 0, 12, 0);
        gbc.gridx = 0; gbc.weightx = 1.0;

        JLabel title = new JLabel("SOUND SETTINGS", SwingConstants.CENTER);
        title.setFont(new Font(FONT_NAME, Font.PLAIN, 36));
        title.setForeground(BORDER_COL);
        gbc.gridy = 0; card.add(title, gbc);

        gbc.gridy = 1; card.add(makeSeparator(), gbc);
        gbc.gridy = 2; card.add(makeSliderRow("MASTER VOLUME", 100, val -> {}), gbc);
        gbc.gridy = 3; card.add(makeSliderRow("MUSIC",          80, val -> {}), gbc);
        gbc.gridy = 4; card.add(makeSliderRow("SFX",           100, val -> {}), gbc);
        gbc.gridy = 5; card.add(makeSeparator(), gbc);
        gbc.gridy = 6; card.add(makeMuteRow(), gbc);

        add(card, BorderLayout.CENTER);
    }

    private JPanel makeSliderRow(String label, int initialValue, java.util.function.IntConsumer onChange) {
        JPanel row = new JPanel(new BorderLayout(16, 0));
        row.setBackground(PANEL_BG);
        row.setBorder(new EmptyBorder(10, 16, 10, 16));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font(FONT_NAME, Font.PLAIN, 22));
        lbl.setForeground(BORDER_COL);
        lbl.setPreferredSize(new Dimension(180, 30));

        JSlider slider = new JSlider(0, 100, initialValue);
        slider.setBackground(PANEL_BG);
        slider.setFocusable(false);
        slider.setUI(new javax.swing.plaf.basic.BasicSliderUI(slider) {
            @Override public void paintTrack(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Rectangle t = trackRect; int cy = t.y + t.height / 2;
                g2.setColor(MUTED);  g2.fillRoundRect(t.x, cy-3, t.width, 6, 6, 6);
                int filled = thumbRect.x - t.x + thumbRect.width / 2;
                g2.setColor(ACCENT); g2.fillRoundRect(t.x, cy-3, filled, 6, 6, 6);
            }
            @Override public void paintThumb(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE); g2.fillOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
                g2.setColor(ACCENT); g2.setStroke(new BasicStroke(2));
                g2.drawOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);
            }
        });

        JLabel valueLabel = new JLabel(initialValue + "%", SwingConstants.RIGHT);
        valueLabel.setFont(new Font(FONT_NAME, Font.PLAIN, 20));
        valueLabel.setForeground(ACCENT);
        valueLabel.setPreferredSize(new Dimension(55, 30));

        slider.addChangeListener(e -> {
            valueLabel.setText(slider.getValue() + "%");
            onChange.accept(slider.getValue());
        });

        row.add(lbl, BorderLayout.WEST); row.add(slider, BorderLayout.CENTER); row.add(valueLabel, BorderLayout.EAST);
        return row;
    }

    private JPanel makeMuteRow() {
        JPanel row = new JPanel(new BorderLayout(16, 0));
        row.setBackground(PANEL_BG);
        row.setBorder(new EmptyBorder(10, 16, 10, 16));

        JLabel lbl = new JLabel("MUTE ALL");
        lbl.setFont(new Font(FONT_NAME, Font.PLAIN, 22));
        lbl.setForeground(BORDER_COL);

        // Use RetroButton as a stateful toggle
        RetroButton muteBtn = new RetroButton("OFF", RetroButton.Style.OUTLINE, MUTED);
        muteBtn.setPreferredSize(new Dimension(90, 38));
        muteBtn.addActionListener(e -> {
            isMuted = !isMuted;
            muteBtn.setText(isMuted ? "ON" : "OFF");
            muteBtn.setToggled(isMuted);
            // SoundManager.getInstance().setMuted(isMuted);
        });

        row.add(lbl,     BorderLayout.WEST);
        row.add(muteBtn, BorderLayout.EAST);
        return row;
    }

    private JSeparator makeSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(60, 60, 70));
        sep.setBackground(BG_DARK);
        return sep;
    }
}