package com.fourstory.fourstory_api.controller;

import com.fourstory.fourstory_api.dto.response.CharacterDetailResponse;
import com.fourstory.fourstory_api.dto.response.CharacterResponse;
import com.fourstory.fourstory_api.dto.response.PageResponse;
import com.fourstory.fourstory_api.service.CharacterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/characters")
@RequiredArgsConstructor
public class CharacterController {

    private final CharacterService characterService;

    @GetMapping("/{name}")
    public ResponseEntity<CharacterDetailResponse> findByNameWithEquippedItems(@PathVariable("name") String name) {
        return ResponseEntity.ok(characterService.getCharacterDetail(name));
    }

    @GetMapping
    public ResponseEntity<PageResponse<CharacterResponse>> getCharacters(
            @RequestParam(name = "name", required = false) String name,
            @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(characterService.getCharacters(name, pageable));
    }
}
