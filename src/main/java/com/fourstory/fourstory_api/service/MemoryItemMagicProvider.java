package com.fourstory.fourstory_api.service;

import com.fourstory.fourstory_api.model.tgame.ItemMagic;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MemoryItemMagicProvider implements ItemMagicProvider, ItemMagicWriter {

    private volatile Map<Byte, ItemMagic> itemMagicMap = Map.of();

    @Override
    public void replaceMap(Map<Byte, ItemMagic> map) {
        itemMagicMap = Map.copyOf(map);
    }

    @Override
    public ItemMagic getItemMagicOrNull(byte id) {
        return itemMagicMap.get(id);
    }
}
