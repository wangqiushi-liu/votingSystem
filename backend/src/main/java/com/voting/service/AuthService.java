package com.voting.service;

import com.voting.dto.WelinkUserInfo;
import com.voting.entity.User;
import com.voting.entity.Role;
import com.voting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    @Transactional
    public User loginOrRegister(WelinkUserInfo welinkUserInfo) {
        return userRepository.findByWelinkUserId(welinkUserInfo.getUserId())
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setWelinkUserId(welinkUserInfo.getUserId());
                    newUser.setName(welinkUserInfo.getName());
                    newUser.setDepartment(welinkUserInfo.getDepartment());
                    newUser.setAvatar(welinkUserInfo.getAvatar());
                    newUser.setRole(Role.VOTER);
                    return userRepository.save(newUser);
                });
    }

    public User getUserByWelinkUserId(String welinkUserId) {
        return userRepository.findByWelinkUserId(welinkUserId).orElse(null);
    }
}
