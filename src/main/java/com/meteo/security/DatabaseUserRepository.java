package com.meteo.security;

import io.jmix.core.DataManager;
import io.jmix.core.security.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("meteo_UserRepository")
public class DatabaseUserRepository implements UserRepository {

    private final DataManager dataManager;

    public DatabaseUserRepository(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = dataManager.load(User.class)
                .query("select u from User u where u.username = :username")
                .parameter("username", username)
                .optional()
                .orElseThrow(() ->
                        new UsernameNotFoundException("User '" + username + "' not found"));

        return user;
    }

    @Override
    public List<User> getByUsernameLike(String username) {
        return dataManager.load(User.class)
                .query("select u from User u where u.username like :username order by u.username")
                .parameter("username", "%" + username + "%")
                .list();
    }

    @Override
    public UserDetails getAnonymousUser() {
        User anonymousUser = new User();
        anonymousUser.setUsername("anonymous");
        anonymousUser.setActive(false);
        return anonymousUser;
    }

    @Override
    public UserDetails getSystemUser() {
        User systemUser = new User();
        systemUser.setUsername("system");
        systemUser.setActive(true);
        return systemUser;
    }
}
