package com.tyut.tmall.realm;

import com.tyut.tmall.pojo.User;
import com.tyut.tmall.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ClassName JPARealm
 * @Description TODO
 * @Author 王琛
 * @Date 2019/9/25 10:15
 * @Version 1.0
 */
public class JPARealm extends AuthorizingRealm {
    @Autowired
    private UserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo s = new SimpleAuthorizationInfo();

        return s;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //通过token获取传入的参数
        String username = token.getPrincipal().toString();
        //
        User user= userService.getByName(username);
        //获取加密的密码
        String passwordInDB = user.getPassword();
        //获取盐，通过数据库自己进行比较
        String salt = user.getSalt();
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(username,passwordInDB, ByteSource.Util.bytes(salt),getName());
        return authenticationInfo;
    }
}
