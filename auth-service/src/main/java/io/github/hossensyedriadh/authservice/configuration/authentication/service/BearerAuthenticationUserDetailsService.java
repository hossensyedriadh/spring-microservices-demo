package io.github.hossensyedriadh.authservice.configuration.authentication.service;

import io.github.hossensyedriadh.authservice.repository.r2dbc.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.Serial;
import java.util.Collection;
import java.util.Collections;

@Service
public class BearerAuthenticationUserDetailsService implements ReactiveUserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public BearerAuthenticationUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Find the {@link UserDetails} by username.
     *
     * @param username the username to look up
     * @return the {@link UserDetails}. Cannot be null
     */
    @Override
    public Mono<UserDetails> findByUsername(String username) {
        boolean isUsername = username.matches("^[a-zA-Z_]{4,50}$");
        boolean isEmail = username.matches("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+$");

        if (isUsername) {
            return this.userRepository.findById(username).flatMap(user -> {
                UserDetails userDetails = new UserDetails() {
                    @Serial
                    private static final long serialVersionUID = -4527327298392996507L;

                    @Override
                    public Collection<? extends GrantedAuthority> getAuthorities() {
                        return Collections.singletonList(new SimpleGrantedAuthority(user.getAuthority().toString()));
                    }

                    @Override
                    public String getPassword() {
                        return user.getPassword();
                    }

                    @Override
                    public String getUsername() {
                        return user.getUsername();
                    }

                    @Override
                    public boolean isAccountNonExpired() {
                        return true;
                    }

                    @Override
                    public boolean isAccountNonLocked() {
                        return true;
                    }

                    @Override
                    public boolean isCredentialsNonExpired() {
                        return true;
                    }

                    @Override
                    public boolean isEnabled() {
                        return true;
                    }
                };

                return Mono.just(User.withUserDetails(userDetails).build());
            }).switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found: " + username)));
        } else if (isEmail) {
            return this.userRepository.findAll().filter(u -> u.getEmail().equals(username)).elementAt(0).flatMap(user -> {
                UserDetails userDetails = new UserDetails() {
                    @Serial
                    private static final long serialVersionUID = 4796032593620561639L;

                    @Override
                    public Collection<? extends GrantedAuthority> getAuthorities() {
                        return Collections.singletonList(new SimpleGrantedAuthority(user.getAuthority().toString()));
                    }

                    @Override
                    public String getPassword() {
                        return user.getPassword();
                    }

                    @Override
                    public String getUsername() {
                        return user.getUsername();
                    }

                    @Override
                    public boolean isAccountNonExpired() {
                        return true;
                    }

                    @Override
                    public boolean isAccountNonLocked() {
                        return true;
                    }

                    @Override
                    public boolean isCredentialsNonExpired() {
                        return true;
                    }

                    @Override
                    public boolean isEnabled() {
                        return true;
                    }
                };

                return Mono.just(User.withUserDetails(userDetails).build());
            }).switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found: " + username)));
        } else {
            throw new UsernameNotFoundException("User not found: " + username);
        }
    }
}
