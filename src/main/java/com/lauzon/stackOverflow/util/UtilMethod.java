package com.lauzon.stackOverflow.util;

import com.lauzon.stackOverflow.entity.UserEntity;
import com.lauzon.stackOverflow.service.impl.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UtilMethod {

    private final AuthServiceImpl authService;

    public UserEntity getCurrentUser() {
        return authService.getCurrentUser();
    }
}
