package com.fourstory.fourstory_api.service;

import com.fourstory.fourstory_api.model.tgame.ItemMagic;

import java.util.Map;

public interface ItemMagicWriter {
    void replaceMap(Map<Byte, ItemMagic> map);
}
