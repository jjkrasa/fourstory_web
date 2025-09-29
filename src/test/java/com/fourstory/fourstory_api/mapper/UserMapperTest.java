// Updated test for UserMapper using reflection (avoids Lombok getters at compile time)
package com.fourstory.fourstory_api.mapper;

import com.fourstory.fourstory_api.dto.request.RegisterRequest;
import com.fourstory.fourstory_api.model.tglobal.Role;
import com.fourstory.fourstory_api.model.tglobal.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void registerRequestToUser_shouldMapsFields_whenRequestIsNotNull() throws Exception {
        RegisterRequest req = RegisterRequest.builder()
                .userName("testUser")
                .email("user@example.com")
                .password("P@ssw0rd")
                .confirmPassword("P@ssw0rd")
                .build();

        User user = mapper.registerRequestToUser(req);

        assertNotNull(user);
        assertNull(user.getId());
        assertEquals(req.getUserName(), getField(user, "userName"));
        assertEquals(req.getEmail(), user.getEmail());
        assertEquals(req.getEmail(), user.getRegistrationEmail());
        assertNull(user.getPassword());
        assertEquals(Boolean.FALSE, user.getEmailVerified());
        assertEquals(Role.USER, user.getRole());
        assertEquals((byte)0, user.getCheckFlag());
        assertNull(user.getFirstLogin());
        assertNull(user.getLastLogin());
    }

    @Test
    void registerRequestToUser_shouldReturnNull_whenRequestIsNull() {
        assertNull(mapper.registerRequestToUser(null));
    }

    private Object getField(Object obj, String name) throws Exception {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return f.get(obj);
    }
}
