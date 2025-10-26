package com.fourstory.fourstory_api.service;

import com.fourstory.fourstory_api.dto.projection.CharacterRank;
import com.fourstory.fourstory_api.dto.response.CharacterDetailResponse;
import com.fourstory.fourstory_api.dto.response.CharacterResponse;
import com.fourstory.fourstory_api.dto.response.PageResponse;
import com.fourstory.fourstory_api.exception.BusinessException;
import com.fourstory.fourstory_api.exception.ErrorCode;
import com.fourstory.fourstory_api.mapper.CharacterMapper;
import com.fourstory.fourstory_api.mapper.PageMapper;
import com.fourstory.fourstory_api.model.tgame.Character;
import com.fourstory.fourstory_api.repository.tgame.CharacterRepository;
import com.fourstory.fourstory_api.repository.tgame.PvpPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CharacterService {

    private final CharacterRepository characterRepository;

    private final PvpPointRepository pvpPointRepository;

    private final CharacterMapper characterMapper;

    @Transactional(readOnly = true, transactionManager = "tgameTransactionManager")
    public CharacterDetailResponse getCharacterDetail(String name) {
        Character character = characterRepository.findByNameWithEquippedItems(name)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHARACTER_NOT_FOUND));

        Long rank = pvpPointRepository.findRankByCharId(character.getId());

        return characterMapper.characterToCharacterDetailResponse(character, rank);
    }

    @Transactional(readOnly = true, transactionManager = "tgameTransactionManager")
    public PageResponse<CharacterResponse> getCharacters(String name, Pageable pageable) {
        int pageSize = Math.min(pageable.getPageSize(), 10);
        pageable = PageRequest.of(pageable.getPageNumber(), pageSize, pageable.getSort());

        Page<Character> characters = characterRepository.findByNameOrderByTotalPointDesc(
                (name == null || name.isBlank()) ? null : name.trim(),
                pageable
        );

        List<Integer> ids = characters.getContent().stream().map(Character::getId).toList();

        if (ids.isEmpty()) {
            return PageMapper.toPageResponse(Page.empty(pageable));
        }

        Map<Integer, Long> ranksByCharacterId = pvpPointRepository.findRanksForCharIds(ids)
                .stream()
                .collect(Collectors.toUnmodifiableMap(CharacterRank::getId, CharacterRank::getRank));

        Page<CharacterResponse> charactersResponse = characters.map(
                ch -> characterMapper.characterToCharacterResponse(ch, ranksByCharacterId.get(ch.getId()))
        );

        return PageMapper.toPageResponse(charactersResponse);
    }
}
