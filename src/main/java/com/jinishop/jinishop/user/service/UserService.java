package com.jinishop.jinishop.user.service;

import com.jinishop.jinishop.user.domain.User;
import com.jinishop.jinishop.user.domain.UserRole;
import com.jinishop.jinishop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    // id로 회원 조회
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없음. id=" + userId));
    }

    // email로 회원 조회
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("이메일로 회원을 찾을 수 없음. email=" + email));
    }

    // 회원 생성
    @Transactional
    public User createUser(String email, String rawPassword, String name) {
        // password는 나중에 암호화 로직 추가할 예정
        User user = User.builder()
                .email(email)
                .password(rawPassword)
                .name(name)
                .role(UserRole.USER)
                .build();

        return userRepository.save(user);
    }
}