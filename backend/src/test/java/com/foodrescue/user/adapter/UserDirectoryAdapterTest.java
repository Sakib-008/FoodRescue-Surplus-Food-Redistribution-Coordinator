package com.foodrescue.user.adapter;

import com.foodrescue.contract.UserDirectory;
import com.foodrescue.user.entity.User;
import com.foodrescue.user.repository.UserRepository;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/** Adapter pattern: the repository (adaptee) is used through the UserDirectory interface (target). */
@ExtendWith(MockitoExtension.class)
class UserDirectoryAdapterTest {

    @Mock UserRepository repository;

    @Test
    void translatesUserEntitiesIntoDisplayNames() {
        User ana = User.builder().fullName("Ana's Kitchen").build();
        ana.setId(7L);
        User hope = User.builder().fullName("Hope Shelter").build();
        hope.setId(9L);
        when(repository.findAllById(List.of(7L, 9L))).thenReturn(List.of(ana, hope));

        UserDirectory directory = new UserDirectoryAdapter(repository);

        assertEquals(Map.of(7L, "Ana's Kitchen", 9L, "Hope Shelter"), directory.displayNames(List.of(7L, 9L)));
    }
}
