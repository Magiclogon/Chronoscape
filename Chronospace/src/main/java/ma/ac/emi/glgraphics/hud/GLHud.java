package ma.ac.emi.glgraphics.hud;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import ma.ac.emi.fx.AssetsLoader;
import ma.ac.emi.fx.Sprite;
import ma.ac.emi.gamecontrol.GameController;
import ma.ac.emi.gamelogic.entity.BossEnnemy;
import ma.ac.emi.gamelogic.entity.Ennemy;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.gamelogic.shop.Inventory;
import ma.ac.emi.gamelogic.shop.WeaponItem;
import ma.ac.emi.gamelogic.shop.WeaponItemDefinition;
import ma.ac.emi.gamelogic.weapon.behavior.WeaponBehaviorDefinition;
import ma.ac.emi.gamelogic.weapon.behavior.passive.PassiveWeaponEffectDefinition;
import ma.ac.emi.gamelogic.weapon.behavior.passive.WeaponPassiveDefinition;
import ma.ac.emi.camera.Camera;
import ma.ac.emi.UI.FloatingTextRenderer;
import ma.ac.emi.glgraphics.GLGraphics;
import ma.ac.emi.math.Vector3D;
import ma.ac.emi.tiles.TileManager;
import ma.ac.emi.world.World;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

/**
 * Draws all HUD elements directly in OpenGL with DPI scaling support.
 */
public class GLHud {

    // ── Colors ────────────────────────────────────────────────────────────
    private static final float[] C_BLACK      = {0f,    0f,    0f,    1f};
    private static final float[] C_WHITE      = {1f,    1f,    1f,    1f};
    private static final float[] C_GOLD       = {1f,    0.84f, 0f,    1f};
    private static final float[] C_HP_DARK    = {0.59f, 0f,    0f,    1f};
    private static final float[] C_HP_LIGHT   = {0.9f,  0.2f,  0.2f,  1f};
    private static final float[] C_BORD_DARK  = {0.08f, 0.08f, 0.1f,  1f};
    private static final float[] C_BORD_LITE  = {0.78f, 0.78f, 0.82f, 1f};
    private static final float[] C_SLOT_BG    = {0.086f,0.086f,0.118f,0.86f};
    private static final float[] C_BOSS_BG    = {0.078f,0f,    0f,    0.78f};
    private static final float[] C_BOSS_FILL  = {0.54f, 0.012f,0.012f,1f};
    private static final float[] C_PASSIVE_HDR= {0.63f, 0.63f, 0.7f,  1f};
    private static final float[] C_PASSIVE_TXT= {0.78f, 0.78f, 0.82f, 1f};
    private static final float[] C_LIVE_LBL   = {0.51f, 0.51f, 0.59f, 1f};
    private static final float[] C_LIVE_VAL   = {0.47f, 0.86f, 0.47f, 1f};

    // ── Base Font sizes (scaled by dpiScale during render) ────────────────
    private static final float FS_HP     = 24f;
    private static final float FS_MONEY  = 32f;
    private static final float FS_AMMO   = 28f;
    private static final float FS_SLOT   = 14f;
    private static final float FS_PASS   = 16f;
    private static final float FS_BOSS   = 24f;
    private static final float FS_FPS    = 24f;

    // ── Resources ─────────────────────────────────────────────────────────
    private final BitmapFont font;
    private final Map<String, LoadTexResponse> iconTexCache = new HashMap<>();
    private int minimapTexId = 0;
    private int minimapSrcW  = -1;
    private int minimapSrcH  = -1;

    private int screenW, screenH;
    
    private enum CardState { HIDDEN, FADE_IN, HOLD, FADE_OUT }
    private CardState cardState = CardState.HIDDEN;
    private float cardAlpha = 0f;
    private float cardHoldTimer = 0f;
    private int cardWaveNumber = 1;
    private Runnable cardDoneCallback;
    
    private static final float CARD_HOLD_SECS = 3f;
    private static final float FADE_STEP = 0.02f; // approx 60fps step

    public GLHud(BitmapFont font) {
        this.font = font;
    }

    // ── Entry point ───────────────────────────────────────────────────────

    public void render(GL3 gl, GLGraphics g, Camera camera, int sw, int sh, float dpiScale) {
        this.screenW = sw;
        this.screenH = sh;

        gl.glDisable(GL3.GL_DEPTH_TEST);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        
        Player    player = Player.getInstance();
        Inventory inv    = player.getInventory();

        g.beginHUD(gl, sw, sh);

        drawHPBar(gl, g, player, dpiScale);        
        drawBossHud(gl, g, dpiScale);
        drawMoney(gl, player, dpiScale);
        drawAmmo(gl, player, dpiScale);
        drawWeaponSlots(gl, g, inv, player.getWeaponIndex(), dpiScale);
        drawMinimap(gl, g, player, dpiScale);
        drawFPS(gl, dpiScale);
        if (cardState != CardState.HIDDEN && cardAlpha > 0) {
            drawWaveCard(gl, g, sw, sh, dpiScale, font);
        }

        if (camera != null)
            FloatingTextRenderer.renderGL(gl, font, camera, sw, sh, dpiScale);

        g.endHUD(gl);
    }
    
    public void showWaveCard(int waveNumber, Runnable onDone) {
        this.cardWaveNumber = waveNumber;
        this.cardDoneCallback = onDone;
        this.cardAlpha = 0f;
        this.cardHoldTimer = 0f;
        this.cardState = CardState.FADE_IN;
    }
    
    public void update(double delta) {
        switch (cardState) {
            case FADE_IN -> {
                cardAlpha += FADE_STEP * 1.5f;
                if (cardAlpha >= 1f) { cardAlpha = 1f; cardState = CardState.HOLD; }
            }
            case HOLD -> {
                cardHoldTimer += delta;
                if (cardHoldTimer >= CARD_HOLD_SECS) cardState = CardState.FADE_OUT;
            }
            case FADE_OUT -> {
                cardAlpha -= FADE_STEP * 1.5f;
                if (cardAlpha <= 0f) {
                    cardAlpha = 0f;
                    cardState = CardState.HIDDEN;
                    if (cardDoneCallback != null) {
                        Runnable cb = cardDoneCallback;
                        cardDoneCallback = null;
                        SwingUtilities.invokeLater(cb);
                    }
                }
            }
            default -> {}
        }
    }
    
    private void drawWaveCard(GL3 gl, GLGraphics g, int sw, int sh, float dpiScale, BitmapFont font) {
        // Scale panel dimensions
        int panelW = (int)(sw * 0.4f);
        int panelH = (int)(90 * dpiScale);
        int panelX = (sw - panelW) / 2;
        int panelY = (sh - panelH) / 4;

//        // Background Panel
//        g.drawQuadHUD(gl, panelX, panelY, panelW, panelH, 
//                new float[]{0f, 0f, 0f, cardAlpha * 0.75f});
//        
//        // Border (Green-ish matching your original snippet)
//        g.drawQuadOutlineHUD(gl, panelX, panelY, panelW, panelH, (int)(2 * dpiScale), 
//                new float[]{0.31f, 0.78f, 0.39f, cardAlpha});

        if (font != null) {
            String text = "WAVE " + cardWaveNumber;
            float size = 72f * dpiScale;
            float tw = font.measureWidth(text, size);
            float lh = font.lineHeight(size);
            float tx = (sw - tw) / 2f;
            float ty = panelY + (panelH - lh) / 2f;

            // Shadow
            font.drawText(gl, text, tx + (2 * dpiScale), ty + (2 * dpiScale), size,
                    0f, 0f, 0f, cardAlpha * 0.7f, sw, sh);
            // Main Text
            font.drawText(gl, text, tx, ty, size,
            		C_GOLD[0], C_GOLD[1], C_GOLD[2], cardAlpha, sw, sh);
//            font.drawText(gl, text, tx, ty, size,
//            		0.31f, 0.78f, 0.39f, cardAlpha, sw, sh);
        }
    }

    // ── HP bar ────────────────────────────────────────────────────────────

    private void drawHPBar(GL3 gl, GLGraphics g, Player player, float dpiScale) {
        int bw = (int)(0.25f * screenW);
        int bh = (int)(30 * dpiScale);
        int x  = (int)(0.03f * screenW);
        int y  = (int)(0.05f * screenH);

        float pct = (float) Math.max(0, Math.min(1, player.getHp() / player.getHpMax()));

        g.drawQuadHUD(gl, x, y, bw, bh, C_BORD_DARK);
        int fw = (int)((bw - 6 * dpiScale) * pct);
        if (fw > 0) {
            int padding = (int)(3 * dpiScale);
            g.drawQuadHUD(gl, x + padding, y + padding, fw, bh - padding * 2, C_HP_DARK);
            g.drawQuadHUD(gl, x + padding, y + padding, fw, (bh - padding * 2) / 2, C_HP_LIGHT);
        }
        g.drawQuadOutlineHUD(gl, x, y, bw, bh, (int)(2 * dpiScale), C_BORD_LITE);

        String t  = (int)player.getHp() + " / " + (int)player.getHpMax();
        float scaledFS = FS_HP * dpiScale;
        float tw = font.measureWidth(t, scaledFS);
        float tx = x + (bw - tw) / 2f;
        float ty = y + (bh - font.lineHeight(scaledFS)) / 2f;
        drawTextShadow(gl, t, tx, ty, scaledFS, C_WHITE, dpiScale);
    }

    // ── Money ─────────────────────────────────────────────────────────────

    private void drawMoney(GL3 gl, Player player, float dpiScale) {
        String t = "GOLD: " + (int)player.getMoney();
        float x = (int)(0.03f * screenW);
        float y = (int)(0.05f * screenH) + (40 * dpiScale);
        drawTextShadow(gl, t, x, y, FS_MONEY * dpiScale, C_GOLD, dpiScale);
    }

    // ── Ammo ──────────────────────────────────────────────────────────────

    private void drawAmmo(GL3 gl, Player player, float dpiScale) {
        if (player.getActiveWeapon() == null) return;
        var wep = player.getActiveWeapon();
        var def = (WeaponItemDefinition) wep.getWeaponItem().getItemDefinition();
        int mag = def.getMagazineSize();
        if (mag <= 0) return;

        int   ammo = wep.getAmmo();
        float ratio = (float) ammo / mag;
        float[] col = ratio > 0.5f ? new float[]{0.39f,0.86f,0.39f,1f}
                    : ratio > 0.2f ? new float[]{0.86f,0.78f,0.24f,1f}
                    :                new float[]{0.86f,0.24f,0.24f,1f};

        String t = ammo + " / " + mag;
        float x = (int)(0.03f * screenW);
        float scaledMoneyH = font.lineHeight(FS_MONEY * dpiScale);
        float y = (int)(0.05f * screenH) + (40 * dpiScale) + scaledMoneyH + (4 * dpiScale);
        drawTextShadow(gl, t, x, y, FS_AMMO * dpiScale, col, dpiScale);
    }

    // ── Weapon slots ──────────────────────────────────────────────────────

    private void drawWeaponSlots(GL3 gl, GLGraphics g, Inventory inv, int activeIdx, float dpiScale) {
        final int SLOT = (int)(64 * dpiScale);
        final int GAP  = (int)(10 * dpiScale);
        final int PAD  = (int)(16 * dpiScale);

        WeaponItem[] eq     = inv.getEquippedWeapons();
        Player       player = Player.getInstance();
        int startX = (int)(0.03f * screenW);
        int slotsY = screenH - SLOT - PAD;
        int totalW = Inventory.MAX_EQU * SLOT + (Inventory.MAX_EQU - 1) * GAP;

        float scaledPassFS = FS_PASS * dpiScale;

        // ── Passive strip above slots ─────────────────────────────────────
        WeaponItem activeItem = (activeIdx >= 0 && activeIdx < Inventory.MAX_EQU)
                ? eq[activeIdx] : null;
        if (activeItem != null) {
            WeaponItemDefinition def = player.getActiveWeapon() != null
                    ? (WeaponItemDefinition) player.getActiveWeapon().getWeaponItem().getItemDefinition()
                    : (WeaponItemDefinition) activeItem.getItemDefinition();

            List<String> passives  = collectPassiveLines(def);
            List<String> liveStats = collectLiveStats(player, def);

            if (!passives.isEmpty() || !liveStats.isEmpty()) {
                int lineH  = (int) font.lineHeight(scaledPassFS);
                int hdrLines = passives.isEmpty() ? 0 : 1;
                int total  = hdrLines + passives.size() + liveStats.size();
                int stripH = total * lineH + (int)(14 * dpiScale);
                int stripY = slotsY - stripH - (int)(6 * dpiScale);

                float maxW = font.measureWidth("PASSIVE", scaledPassFS);
                for (String l : passives)
                    maxW = Math.max(maxW, font.measureWidth("  " + l, scaledPassFS));
                for (String s : liveStats)
                    maxW = Math.max(maxW, font.measureWidth(s, scaledPassFS));
                int stripW = (int) Math.max(totalW, maxW + (16 * dpiScale));

                g.drawQuadHUD(gl, startX - (int)(4 * dpiScale), stripY, stripW + (int)(8 * dpiScale), stripH,
                        new float[]{0.047f, 0.047f, 0.071f, 0.82f});
                g.drawQuadOutlineHUD(gl, startX - (int)(4 * dpiScale), stripY, stripW + (int)(8 * dpiScale), stripH, 1,
                        new float[]{0.31f, 0.31f, 0.39f, 0.39f});

                float ty = stripY + (7 * dpiScale);
                if (!passives.isEmpty()) {
                    font.drawText(gl, "PASSIVE", startX, ty, scaledPassFS,
                            C_PASSIVE_HDR, screenW, screenH);
                    ty += lineH;
                    for (String l : passives) {
                        font.drawText(gl, "  " + l, startX, ty, scaledPassFS,
                                C_PASSIVE_TXT, screenW, screenH);
                        ty += lineH;
                    }
                }
                for (String stat : liveStats) {
                    int colon = stat.indexOf(':');
                    if (colon != -1) {
                        String lbl = stat.substring(0, colon + 1) + " ";
                        String val = stat.substring(colon + 1).trim();
                        float  lw  = font.measureWidth(lbl, scaledPassFS);
                        font.drawText(gl, lbl, startX,      ty, scaledPassFS, C_LIVE_LBL, screenW, screenH);
                        font.drawText(gl, val, startX + lw, ty, scaledPassFS, C_LIVE_VAL, screenW, screenH);
                    } else {
                        font.drawText(gl, stat, startX, ty, scaledPassFS, C_LIVE_VAL, screenW, screenH);
                    }
                    ty += lineH;
                }
            }
        }

        // ── Slot boxes ────────────────────────────────────────────────────
        float scaledSlotFS = FS_SLOT * dpiScale;
        for (int i = 0; i < Inventory.MAX_EQU; i++) {
            int     sx  = startX + i * (SLOT + GAP);
            boolean act = (i == activeIdx && eq[i] != null);

            g.drawQuadHUD(gl, sx, slotsY, SLOT, SLOT, C_SLOT_BG);

            float[] bc = act          ? C_WHITE
                       : eq[i] != null ? rarityColor(eq[i])
                       : new float[]{0.22f, 0.22f, 0.25f, 1f};
            g.drawQuadOutlineHUD(gl, sx, slotsY, SLOT, SLOT, act ? (int)(3 * dpiScale) : (int)(2 * dpiScale), bc);

            font.drawText(gl, String.valueOf(i + 1), sx + (int)(4 * dpiScale), slotsY + (int)(2 * dpiScale), scaledSlotFS,
                    act ? C_WHITE : new float[]{0.35f,0.35f,0.39f,1f}, screenW, screenH);

            if (eq[i] != null) {
                WeaponItemDefinition def = (WeaponItemDefinition) eq[i].getItemDefinition();
                LoadTexResponse response = getOrLoadIconTex(gl, def);
                if (response != null && response.tex != null) {
                	int iconPad  = (int)(10 * dpiScale);
					int iconSize = SLOT - iconPad * 2;
					double scale = Math.min((double) iconSize / response.iconWidth,
					                        (double) iconSize / response.iconHeight);
					int drawW = (int)(response.iconWidth  * scale);
					int drawH = (int)(response.iconHeight * scale);
					int iconX = sx + (SLOT - drawW) / 2;
					int iconY = slotsY + iconPad + (iconSize - drawH) / 2;
                    g.drawSpriteHUD(gl, response.tex, iconX, iconY, drawW, drawH);
                }

                String name = def.getName();
                if (name.length() > 7) name = name.substring(0, 7) + ".";
                float nw = font.measureWidth(name, scaledSlotFS);
                font.drawText(gl, name, sx + (SLOT - nw) / 2f,
                        slotsY + SLOT - font.lineHeight(scaledSlotFS) - (int)(2 * dpiScale),
                        scaledSlotFS, act ? C_WHITE : new float[]{0.7f,0.7f,0.74f,1f},
                        screenW, screenH);
            } else {
                String empty = "EMPTY";
                float ew = font.measureWidth(empty, scaledSlotFS);
                float eh = font.lineHeight(scaledSlotFS);
                font.drawText(gl, empty, sx + (SLOT - ew) / 2f, slotsY + (SLOT - eh) / 2f,
                        scaledSlotFS, new float[]{0.22f,0.22f,0.25f,1f}, screenW, screenH);
            }
        }
    }

    // ── Minimap ───────────────────────────────────────────────────────────

    private static final double MM_SCALE = 0.20;

    private void drawMinimap(GL3 gl, GLGraphics g, Player player, float dpiScale) {
        try {
            World world = GameController.getInstance().getWorldManager().getCurrentWorld();
            if (world == null) return;
            TileManager tm = world.getTileManager();
            if (tm == null || tm.getMapCache() == null) return;
            BufferedImage mapImg = tm.getMapCache().getSprite();
            if (mapImg == null) return;

            int pad = (int)(20 * dpiScale);
            int mapW = mapImg.getWidth();
            int mapH = mapImg.getHeight();
            double sc = (screenW * MM_SCALE) / mapW;
            int mW = (int)(mapW * sc);
            int mH = (int)(mapH * sc);
            int mX = screenW - mW - pad;
            int mY = pad;

            g.drawQuadHUD(gl, mX, mY, mW, mH, new float[]{0.04f,0.04f,0.06f,0.7f});
            ensureMinimapTex(gl, mapImg);
            if (minimapTexId != 0) g.drawSpriteHUD(gl, minimapTexId, mX, mY, mW, mH);
            g.drawQuadOutlineHUD(gl, mX, mY, mW, mH, (int)(2 * dpiScale), C_BORD_LITE);

            List<Ennemy> enemies = world.getWaveManager().getCurrentEnemies();
            if (enemies != null) {
                for (Ennemy e : enemies) {
                    if (e == null || e.getHp() <= 0) continue;
                    Vector3D pos = e.getPos();
                    int ex = mX + (int)(pos.getX() * sc);
                    int ey = mY + (int)(pos.getY() * sc);
                    if (e instanceof BossEnnemy) {
                        int bs = (int)(9 * dpiScale);
                        g.drawQuadHUD(gl, ex - bs/2, ey - bs/2, bs, bs, new float[]{1f,0.2f,0.59f,1f});
                    } else {
                        int es = (int)(5 * dpiScale);
                        g.drawQuadHUD(gl, ex - es/2, ey - es/2, es, es, new float[]{0.86f,0.08f,0.08f,1f});
                    }
                }
            }

            Vector3D pp = player.getPos();
            int px = mX + (int)(pp.getX() * sc);
            int py = mY + (int)(pp.getY() * sc);
            int ps = (int)(7 * dpiScale);
            g.drawQuadHUD(gl, px - ps/2, py - ps/2, ps, ps, new float[]{0.2f,1f,0.2f,1f});

        } catch (Exception e) {
            System.err.println("GLHud minimap error: " + e.getMessage());
        }
    }

    // ── FPS ───────────────────────────────────────────────────────────────

    private void drawFPS(GL3 gl, float dpiScale) {
        String t = "FPS: " + (int) GameController.getInstance().getFPS();
        float scaledFS = FS_FPS * dpiScale;
        float w = font.measureWidth(t, scaledFS);
        font.drawText(gl, t, screenW - w - (8 * dpiScale),
                screenH - font.lineHeight(scaledFS) - (4 * dpiScale),
                scaledFS, C_GOLD, screenW, screenH);
    }

    // ── Boss bar ──────────────────────────────────────────────────────────

    private void drawBossHud(GL3 gl, GLGraphics g, float dpiScale) {
        try {
            World world = GameController.getInstance().getWorldManager().getCurrentWorld();
            if (world == null || world.getWaveManager() == null) return;

            BossEnnemy boss = null;
            for (Ennemy e : world.getWaveManager().getCurrentEnemies()) {
                if (e instanceof BossEnnemy && e.getHp() > 0) { boss = (BossEnnemy) e; break; }
            }
            if (boss == null) return;

            int barW = (int)(screenW * 0.4f);
            int barH = (int)(25 * dpiScale);
            int bx   = (screenW - barW) / 2;
            int by   = (int)(80 * dpiScale);
            float pct = (float) Math.max(0, Math.min(1, boss.getHp() / boss.getHpMax()));

            g.drawQuadHUD(gl, bx, by, barW, barH, C_BOSS_BG);

            int fw = (int)(barW * pct);
            if (fw > 0) {
                g.drawQuadHUD(gl, bx, by, fw, barH, C_BOSS_FILL);
                g.drawQuadHUD(gl, bx, by, fw, barH / 2, new float[]{1f,1f,1f,0.12f});
            }

            int markerW = (int)(3 * dpiScale);
            int xP1 = bx + (int)(barW * 0.333f);
            int xP2 = bx + (int)(barW * 0.666f);
            g.drawQuadHUD(gl, xP1, by - (int)(2 * dpiScale), markerW, barH + (int)(4 * dpiScale), new float[]{0,0,0,0.7f});
            g.drawQuadHUD(gl, xP2, by - (int)(2 * dpiScale), markerW, barH + (int)(4 * dpiScale), new float[]{0,0,0,0.7f});

            g.drawQuadOutlineHUD(gl, bx, by, barW, barH, (int)(3 * dpiScale), C_BLACK);
            g.drawQuadOutlineHUD(gl, bx - (int)(2 * dpiScale), by - (int)(2 * dpiScale), barW + (int)(4 * dpiScale), barH + (int)(4 * dpiScale), (int)(2 * dpiScale),
                    new float[]{0.78f,0.78f,0.78f,0.39f});

            String name = "BOSS";
            float scaledFS = FS_BOSS * dpiScale;
            float  nw   = font.measureWidth(name, scaledFS);
            float  nx   = bx + (barW - nw) / 2f;
            float  ny   = by - font.lineHeight(scaledFS) - (2 * dpiScale);
            drawTextShadow(gl, name, nx, ny, scaledFS, C_WHITE, dpiScale);

        } catch (Exception e) {
            System.err.println("GLHud boss bar error: " + e.getMessage());
        }
    }

    // ... (collectPassiveLines and collectLiveStats methods remain identical) ...

    private List<String> collectPassiveLines(WeaponItemDefinition def) {
        List<String> lines = new ArrayList<>();
        for (WeaponBehaviorDefinition b : def.getBehaviorDefinitions()) {
            String desc = null;
            if (b instanceof PassiveWeaponEffectDefinition p) desc = p.describe();
            else if (b instanceof WeaponPassiveDefinition p)   desc = p.describe();
            if (desc != null) lines.add(desc);
        }
        return lines;
    }

    private List<String> collectLiveStats(Player player, WeaponItemDefinition def) {
        boolean hasDodge = false, hasDefense = false, hasSpeed = false,
                hasStr = false, hasRegen = false, hasMag = false;

        for (WeaponBehaviorDefinition b : def.getBehaviorDefinitions()) {
            if (b instanceof PassiveWeaponEffectDefinition p) {
                switch (p.getStat().toLowerCase()) {
                    case "dodge"        -> hasDodge   = true;
                    case "defense"      -> hasDefense = true;
                    case "speed"        -> hasSpeed   = true;
                    case "strength"     -> hasStr     = true;
                    case "health_regen" -> hasRegen   = true;
                }
            } else if (b instanceof WeaponPassiveDefinition p) {
                for (String s : p.getAffectedStats()) switch (s) {
                    case "dodge"        -> hasDodge   = true;
                    case "defense"      -> hasDefense = true;
                    case "speed"        -> hasSpeed   = true;
                    case "strength"     -> hasStr     = true;
                    case "health_regen" -> hasRegen   = true;
                    case "magazine"     -> hasMag     = true;
                }
            }
        }

        List<String> stats = new ArrayList<>();
        if (hasDodge)   stats.add(String.format("DODGE: %.0f%%", player.getDodge() * 100));
        if (hasDefense) stats.add(String.format("ARMOR: %.0f",   player.getDefense()));
        if (hasSpeed)   stats.add(String.format("SPEED: %.0f",   player.getSpeed()));
        if (hasStr)     stats.add(String.format("DMG:   %.1f",   def.getDamage()));
        if (hasRegen)   stats.add(String.format("REGEN: %.1f/s", player.getRegenerationSpeed()));
        if (hasMag)     stats.add(String.format("MAG:   %d",     def.getMagazineSize()));
        return stats;
    }

    private float[] rarityColor(WeaponItem item) {
        return switch (item.getItemDefinition().getRarity()) {
            case LEGENDARY -> new float[]{1f,    0.84f, 0f,    1f};
            case EPIC      -> new float[]{0.63f, 0.13f, 0.94f, 1f};
            case RARE      -> new float[]{0.25f, 0.41f, 0.88f, 1f};
            case COMMON    -> new float[]{0.2f,  0.8f,  0.2f,  1f};
            default        -> new float[]{0.31f, 0.31f, 0.35f, 1f};
        };
    }

    private void drawTextShadow(GL3 gl, String text, float x, float y, float size, float[] col, float dpiScale) {
        float offset = 2 * dpiScale;
        font.drawText(gl, text, x + offset, y + offset, size, C_BLACK, screenW, screenH);
        font.drawText(gl, text, x,          y,          size, col,     screenW, screenH);
    }

    // ... (getOrLoadIconTex, ensureMinimapTex, uploadARGB, dispose remain identical) ...

    private LoadTexResponse getOrLoadIconTex(GL3 gl, WeaponItemDefinition def) {
        String id = def.getId();
        if (iconTexCache.containsKey(id)) return iconTexCache.get(id);
        String path = def.getIconPath();
        if (path == null || path.isBlank()) { iconTexCache.put(id, null); return null; }
        Sprite sprite = AssetsLoader.getSprite(path);
        if (sprite == null) { iconTexCache.put(id, null); return null; }
        int tex = uploadARGB(gl, sprite.getSprite(), GL3.GL_LINEAR);
        LoadTexResponse response = new LoadTexResponse(tex, sprite.getWidth(), sprite.getHeight());
        iconTexCache.put(id, response);
        return response;
    }

    private static class LoadTexResponse {
        public Integer tex;
        public int iconWidth, iconHeight;
        LoadTexResponse(Integer tex, int iconWidth, int iconHeight){
            this.tex = tex;
            this.iconHeight = iconHeight;
            this.iconWidth = iconWidth;
        }
    }

    private void ensureMinimapTex(GL3 gl, BufferedImage img) {
        if (img.getWidth() == minimapSrcW && img.getHeight() == minimapSrcH && minimapTexId != 0) return;
        if (minimapTexId != 0) gl.glDeleteTextures(1, new int[]{minimapTexId}, 0);
        minimapTexId = uploadARGB(gl, img, GL3.GL_NEAREST);
        minimapSrcW  = img.getWidth();
        minimapSrcH  = img.getHeight();
    }

    private int uploadARGB(GL3 gl, BufferedImage src, int filter) {
        int w = src.getWidth();
        int h = src.getHeight();
        int[]  pixels = src.getRGB(0, 0, w, h, null, 0, w);
        byte[] rgba   = new byte[w * h * 4];
        for (int i = 0; i < pixels.length; i++) {
            int p = pixels[i];
            rgba[i * 4    ] = (byte)((p >> 16) & 0xFF);
            rgba[i * 4 + 1] = (byte)((p >>  8) & 0xFF);
            rgba[i * 4 + 2] = (byte)( p        & 0xFF);
            rgba[i * 4 + 3] = (byte)((p >> 24) & 0xFF);
        }
        int[] ids = new int[1];
        gl.glGenTextures(1, ids, 0);
        int texId = ids[0];
        gl.glBindTexture(GL3.GL_TEXTURE_2D, texId);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, filter);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER, filter);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE);
        gl.glTexImage2D(GL3.GL_TEXTURE_2D, 0, GL3.GL_RGBA8, w, h, 0,
                GL3.GL_RGBA, GL3.GL_UNSIGNED_BYTE, ByteBuffer.wrap(rgba));
        gl.glBindTexture(GL3.GL_TEXTURE_2D, 0);
        return texId;
    }

    public void dispose(GL3 gl) {
        if (minimapTexId != 0) {
            gl.glDeleteTextures(1, new int[]{minimapTexId}, 0);
            minimapTexId = 0;
        }
        for (LoadTexResponse response : iconTexCache.values()) {
            if (response != null && response.tex != null) gl.glDeleteTextures(1, new int[]{response.tex}, 0);
        }
        iconTexCache.clear();
    }
}