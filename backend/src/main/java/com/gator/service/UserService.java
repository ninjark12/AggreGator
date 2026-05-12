package com.gator.service;

import com.gator.dto.CreateUserRequest;
import com.gator.model.User;
import com.gator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User create(CreateUserRequest req) {
        if (userRepository.existsByName(req.name())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "User '" + req.name() + "' already exists");
        }
        return userRepository.save(User.builder().name(req.name()).build());
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public User findByName(String name) {
        return userRepository.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
