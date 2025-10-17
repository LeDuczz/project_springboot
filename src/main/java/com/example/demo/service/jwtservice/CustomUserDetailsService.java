package com.example.demo.service.jwtservice;

import com.example.demo.entity.PermissionEntity;
import com.example.demo.entity.RoleEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity user = null;
        try{
            UUID uuid = UUID.fromString(username);
            user = userRepository.findByUserId(uuid)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + username));
        }catch (IllegalArgumentException | UsernameNotFoundException e){
            user = userRepository.findByPhoneNumber(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with phone number: " + username));
        }

        Set<GrantedAuthority> authorities = new HashSet<>();
        if(user.getRole() != null){
            for(RoleEntity role : user.getRole()){
                authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
                for(PermissionEntity permission : role.getPermissions()){
                    authorities.add(new SimpleGrantedAuthority(permission.getPermissionName()));
                }
            }
        }

        return new User(user.getUserId().toString(),
                user.getPasswordHash() == null ? "" : user.getPasswordHash(),
                authorities);
    }
}
