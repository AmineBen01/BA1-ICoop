package ch.epfl.cs107.icoop.handler;


import ch.epfl.cs107.play.areagame.handler.Inventory;

import java.util.ArrayList;


public class ICoopInventory extends Inventory {
    private int currentItemId;
    private final ArrayList<ICoopItem> items;

    public ICoopInventory() {
        super("defaultPocket");
        currentItemId = 0;
        items = new ArrayList<>();
    }

    public void addPocketItem(ICoopItem item, int quantity) {
        if (super.addPocketItem(item, quantity)) {
            for (int i = 0; i < quantity; ++i) {
                items.add(item);
            }
        }
    }

    public boolean removePocketItem(ICoopItem item, int quantity) {
        if (super.removePocketItem(item, quantity)) {
            for (int i = 0; i < quantity; i++) {
                if (items.get(currentItemId + i) == item) {
                    items.removeLast();
                }
            }
            return true;
        }
        return false;
    }

    public ICoopItem getNextItem() {
        if (currentItemId < items.size() - 1) {
            currentItemId += 1;
            return items.get(currentItemId);
        } else {
            currentItemId = 0;
            return items.getFirst();
        }
    }

    public ICoopItem getPreviousItem() {
        if (currentItemId > 0) {
            currentItemId -= 1;
            return items.get(currentItemId);
        } else {
            currentItemId = 0;
            return items.getFirst();
        }
    }
}
