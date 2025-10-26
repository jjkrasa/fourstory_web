package com.fourstory.fourstory_api.service;

import com.fourstory.fourstory_api.model.tgame.ItemMagic;

public interface ItemMagicProvider {
    ItemMagic getItemMagicOrNull(byte id);
}
