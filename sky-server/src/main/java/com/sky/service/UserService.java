package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.vo.UserLoginVO;

public interface UserService {

    UserLoginVO loginIn(UserLoginDTO userLoginDTO);
}
