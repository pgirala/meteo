package com.meteo.security;

import io.jmix.core.DataManager;
import io.jmix.core.security.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
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
    public List<? extends UserDetails> loadUsersByUsername(String username) {
        return dataManager.load(User.class)
                .query("select u from User u where u.username = :username")
                .parameter("username", username)
                .list();
    }

    @Override
    public void addAuthoritiesToUser(UserDetails userDetails, Collection<? extends GrantedAuthority> authorities) {
        if (userDetails instanceof User user) {
            user.setAuthorities(authorities);
        }
    }

    @Override
    public UserDetails createSystemUser(Collection<? extends GrantedAuthority> authorities) {
        User systemUser = new User();
        systemUser.setUsername("system");
        systemUser.setAuthorities(authorities);
        return systemUser;
    }

    @Override
    public UserDetails createAnonymousUser(Collection<? extends GrantedAuthority> authorities) {
        User anonymousUser = new User();
        anonymousUser.setUsername("anonymous");
        anonymousUser.setAuthorities(authorities);
        return anonymousUser;
    }
}
