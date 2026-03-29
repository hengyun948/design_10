package com.property.config;

import com.property.system.entity.SysRole;
import com.property.system.entity.SysUser;
import com.property.system.mapper.SysRoleMapper;
import com.property.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (roleMapper.selectCount(null) == 0) {
            initRoles();
        }
        if (userMapper.selectCount(null) == 0) {
            initUsers();
        }
    }

    private void initRoles() {
        String[][] roles = {
                {"ADMIN", "系统管理员"},
                {"PROPERTY_STAFF", "物业人员"},
                {"REPAIR_WORKER", "维修人员"},
                {"OWNER", "业主"}
        };
        for (String[] r : roles) {
            SysRole role = new SysRole();
            role.setRoleCode(r[0]);
            role.setRoleName(r[1]);
            role.setStatus(1);
            roleMapper.insert(role);
        }
        log.info("初始化角色数据完成");
    }

    private void initUsers() {
        String encodedPwd = passwordEncoder.encode("123456");
        Object[][] users = {
                {"admin", encodedPwd, "系统管理员", "13800000001", "ADMIN"},
                {"property1", encodedPwd, "物业张经理", "13800000002", "PROPERTY_STAFF"},
                {"worker1", encodedPwd, "维修李师傅", "13800000003", "REPAIR_WORKER"},
                {"owner1", encodedPwd, "业主王先生", "13800000004", "OWNER"},
                {"owner2", encodedPwd, "业主李女士", "13800000005", "OWNER"}
        };
        for (Object[] u : users) {
            SysUser user = new SysUser();
            user.setUsername((String) u[0]);
            user.setPassword((String) u[1]);
            user.setRealName((String) u[2]);
            user.setPhone((String) u[3]);
            user.setRoleCode((String) u[4]);
            user.setStatus(1);
            userMapper.insert(user);
        }
        log.info("初始化用户数据完成，默认密码: 123456");
    }
}
