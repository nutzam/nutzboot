package io.nutz.demo.simple.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.subject.PrincipalCollection;
import org.nutz.integration.shiro.AbstractSimpleAuthorizingRealm;
import org.nutz.integration.shiro.SimpleShiroToken;
import org.nutz.ioc.loader.annotation.IocBean;

import io.nutz.demo.simple.bean.User;

@IocBean(name="shiroRealm", fields="dao")
public class SimpleAuthorizingRealm extends AbstractSimpleAuthorizingRealm {
	
	
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		// null usernames are invalid
		if (principals == null) {
			throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
		}
		long userId = ((Number) principals.getPrimaryPrincipal()).longValue();
		User user = dao().fetch(User.class, userId);
		if (user == null)
			return null;
		SimpleAuthorizationInfo auth = new SimpleAuthorizationInfo();
		auth.addRole(user.getName());
		auth.addStringPermission("user:list");
        return auth;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		SimpleShiroToken upToken = (SimpleShiroToken) token;

		User user = dao().fetch(User.class, (Long)upToken.getPrincipal());
		if (user == null)
			return null;
		return new SimpleAccount(user.getId(), user.getPassword(), getName());
	}

	public SimpleAuthorizingRealm() {
		this(null, null);
	}

	public SimpleAuthorizingRealm(CacheManager cacheManager, CredentialsMatcher matcher) {
		super(cacheManager, matcher);
		setAuthenticationTokenClass(SimpleShiroToken.class);
	}

	public SimpleAuthorizingRealm(CacheManager cacheManager) {
		this(cacheManager, null);
	}

	public SimpleAuthorizingRealm(CredentialsMatcher matcher) {
		this(null, matcher);
	}
	
}
