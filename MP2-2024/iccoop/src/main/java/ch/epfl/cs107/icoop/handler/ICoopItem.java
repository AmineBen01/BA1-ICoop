package ch.epfl.cs107.icoop.handler;

import ch.epfl.cs107.play.areagame.handler.InventoryItem;


public enum ICoopItem implements InventoryItem {
    Sword("sword", "sword.icon"),
    Explosive("explosive", "explosive"),
    WaterKey("waterKey", "key_blue"),
    FireKey("fireKey", "key_red"),
    WaterStaff("waterStaff", "staff_water.icon"),
    FireStaff("fireStaff", "staff_fire.icon");

    private final String itemName;
    private final String spriteName;
    private final int pocketId;

    ICoopItem(String itemName, String spriteName) {
        this.itemName = itemName;
        this.spriteName = spriteName;
        this.pocketId = 0;
    }

    /**
     * @return (int): pocket id, the item is referred to
     */
    @Override
    public int getPocketId() {
        return pocketId;
    }

    /**
     * @return (String): name of the item, not null
     */
    @Override
    public String getName() {
        return itemName;
    }

    public String getSpriteName() {
        return spriteName;
    }
}
