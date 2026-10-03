package com.kalaconnect.repository;

import com.kalaconnect.model.User;
import com.kalaconnect.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserRepositoryTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository = new UserRepositoryImpl(jdbcTemplate);
    }

    @Test
    public void testFindByEmail_Success() {
        User user = new User();
        user.setId(1L);
        user.setName("Shanti Devi");
        user.setEmail("shanti@artisan.org");
        user.setRole(UserRole.ARTISAN);

        when(jdbcTemplate.queryForObject(anyString(), any(SqlParameterSource.class), any(UserRowMapper.class)))
                .thenReturn(user);

        Optional<User> result = userRepository.findByEmail("shanti@artisan.org");
        assertTrue(result.isPresent());
        assertEquals("Shanti Devi", result.get().getName());
        assertEquals(UserRole.ARTISAN, result.get().getRole());
    }

    @Test
    public void testExistsByEmail() {
        when(jdbcTemplate.queryForObject(anyString(), any(SqlParameterSource.class), eq(Integer.class)))
                .thenReturn(1);

        boolean exists = userRepository.existsByEmail("artisan@test.com");
        assertTrue(exists);
    }
}
