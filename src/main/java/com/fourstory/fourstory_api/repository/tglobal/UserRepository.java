package com.fourstory.fourstory_api.repository.tglobal;

import com.fourstory.fourstory_api.model.tglobal.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
