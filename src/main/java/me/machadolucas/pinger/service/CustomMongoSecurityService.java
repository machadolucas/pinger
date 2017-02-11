package me.machadolucas.pinger.service;

import me.machadolucas.pinger.entity.SysUser;
import me.machadolucas.pinger.repository.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomMongoSecurityService implements UserDetailsService {

    @Autowired
    private SysUserRepository sysUserRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        try {

            final SysUser sysUser = this.sysUserRepository.findByUsername(username);
            if (sysUser == null) {
                throw new UsernameNotFoundException(username + " not found.");
            }
            final List<SimpleGrantedAuthority> roles = new ArrayList<>();
            sysUser.getRoles().forEach(role -> {
                roles.add(new SimpleGrantedAuthority(role));
            });
            return new User(sysUser.getUsername(), sysUser.getPassword(), //
                    sysUser.isEnabled(), true, //
                    true, !sysUser.isLocked(), //
                    roles);

        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
