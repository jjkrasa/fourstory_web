package com.fourstory.fourstory_api.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CharacterResponse {

    private String name;

    private Byte level;

    private String strClass;

    private String country;

    private String guild;

    private Long honor;

    private Long rank;
}
