package com.fourstory.fourstory_api.service;

import com.fourstory.fourstory_api.model.tgame.CharacterItem;
import com.fourstory.fourstory_api.model.tgame.Item;
import com.fourstory.fourstory_api.model.tgame.ItemMagic;
import com.fourstory.fourstory_api.model.tgame.enums.MagicOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ItemBonusCalculator {

    private final ItemMagicProvider itemMagicProvider;

    public Map<String, Short> calculateOptions(CharacterItem characterItem) {
        Map<String, Short> options = new LinkedHashMap<>();

        Item item = characterItem.getItem();

        addOptionIfPresent(options, characterItem.getMagic1(), characterItem.getValue1(), item);
        addOptionIfPresent(options, characterItem.getMagic2(), characterItem.getValue2(), item);
        addOptionIfPresent(options, characterItem.getMagic3(), characterItem.getValue3(), item);
        addOptionIfPresent(options, characterItem.getMagic4(), characterItem.getValue4(), item);
        addOptionIfPresent(options, characterItem.getMagic5(), characterItem.getValue5(), item);
        addOptionIfPresent(options, characterItem.getMagic6(), characterItem.getValue6(), item);

        return options.isEmpty() ? null : options;
    }

    private void addOptionIfPresent(Map<String, Short> options, byte magicId, short value, Item item) {
        if (magicId == 0) return;

        short option = calculateOption(magicId, value, item);

        MagicOptions magic = MagicOptions.getMagicOptions(magicId);

        if (magic != null) {
            options.put(magic.getLabel(), option);
        }
    }

    private short calculateOption(byte magicId, short bonusDbValue, Item item) {
        ItemMagic itemMagic = itemMagicProvider.getItemMagicOrNull(magicId);
        Byte rvType = itemMagic.getRvType();

        float revision = item.revisionByRvType(rvType);
        float raw = (revision * bonusDbValue * itemMagic.getMaxValue()) / 100.0f;
        int rounded = (int)raw;

        return (short)Math.max(rounded, 1);
    }
}
