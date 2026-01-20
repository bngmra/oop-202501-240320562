package com.upb.agripos.dao;

import com.upb.agripos.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public interface UserDAO {
    User findByUsername(String username) throws Exception;
    User authenticate(String username, String password) throws Exception;
}
