package com.fourstory.fourstory_api.service;

import com.fourstory.fourstory_api.repository.tglobal.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

}
