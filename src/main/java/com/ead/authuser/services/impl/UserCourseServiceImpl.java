package com.ead.authuser.services.impl;

import com.ead.authuser.repositories.UserCourseRepository;
import com.ead.authuser.services.UserCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserCourseServiceImpl implements UserCourseService {

    final UserCourseRepository userCourseRepository;
}
