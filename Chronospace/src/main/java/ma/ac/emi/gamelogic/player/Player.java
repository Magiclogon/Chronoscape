package ma.ac.emi.gamelogic.player;

import ma.ac.emi.gamelogic.entity.LivingEntity;
import ma.ac.emi.gamelogic.shop.Inventory;

public class Player extends LivingEntity {
    private static Player instance;
    private String pseudoname;
    private double money;
    private Gender gender;
    private Inventory inventory;

    private Player() {
        this.inventory = new Inventory();
    }

    public static Player getInstance() {
        if (instance == null) {
            instance = new Player();
        }
        return instance;
    }

    public String getPseudoname() { return pseudoname; }
    public void setPseudoname(String pseudoname) { this.pseudoname = pseudoname; }

    public double getMoney() { return money; }
    public void setMoney(double money) { this.money = money; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public Inventory getInventory() { return inventory; }
}
