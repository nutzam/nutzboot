package io.nutz.demo.simple.shiro;

import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.nutz.dao.Dao;
import org.nutz.integration.shiro.AbstractSimpleAuthorizingRealm;
import org.nutz.integration.shiro.SimpleShiroToken;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import io.nutz.demo.simple.bean.User;

@IocBean(name="shiroRealm")
public class SimpleAuthorizingRealm extends AuthorizingRealm {

	@Inject
	Dao dao;

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		// null usernames are invalid
		if (principals == null) {
			throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
		}
		long userId = ((Number) principals.getPrimaryPrincipal()).longValue();
		User user = dao.fetch(User.class, userId);
		if (user == null)
			return null;
		SimpleAuthorizationInfo auth = new SimpleAuthorizationInfo();
		auth.addRole(user.getName());
		auth.addStringPermission("user:list");
		return auth;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;

		User user = dao.fetch(User.class, upToken.getUsername());
		if (user == null) {
			return null;
		}
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user,
				user.getPassword().toCharArray(), ByteSource.Util.bytes(user.getSalt()), getName());
		info.setCredentialsSalt(ByteSource.Util.bytes(user.getSalt()));
//        info.
		return info;
	}

	public SimpleAuthorizingRealm() {
		this(null, null);
	}

	public SimpleAuthorizingRealm(CacheManager cacheManager, CredentialsMatcher matcher) {
		super(cacheManager, matcher);
		HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
		hashedCredentialsMatcher.setHashAlgorithmName("SHA-256");
		hashedCredentialsMatcher.setHashIterations(1024);
		// 这一行决定hex还是base64
		hashedCredentialsMatcher.setStoredCredentialsHexEncoded(false);
		// 设置token类型是关键!!!
		setCredentialsMatcher(hashedCredentialsMatcher);
		setAuthenticationTokenClass(UsernamePasswordToken.class);
	}

	public SimpleAuthorizingRealm(CacheManager cacheManager) {
		this(cacheManager, null);
	}

	public SimpleAuthorizingRealm(CredentialsMatcher matcher) {
		this(null, matcher);
	}

}
