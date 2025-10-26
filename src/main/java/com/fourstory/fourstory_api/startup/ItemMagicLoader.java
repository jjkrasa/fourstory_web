package com.fourstory.fourstory_api.startup;

import com.fourstory.fourstory_api.model.tgame.ItemMagic;
import com.fourstory.fourstory_api.repository.tgame.ItemMagicRepository;
import com.fourstory.fourstory_api.service.ItemMagicWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(value = "app.startup.item-magic.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class ItemMagicLoader implements ApplicationRunner {

    private final ItemMagicRepository itemMagicRepository;

    private final ItemMagicWriter itemMagicWriter;

    @Override
    @Transactional(readOnly = true, transactionManager = "tgameTransactionManager")
    public void run(ApplicationArguments args) throws Exception {
        Map<Byte, ItemMagic> map = itemMagicRepository.findAll()
                .stream()
                .collect(Collectors.toUnmodifiableMap(ItemMagic::getMagicId, im -> im));

        itemMagicWriter.replaceMap(map);
    }
}
