package org.nutz.boot.starter.velocity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * copy jetx
 */
public class VelocityWebContext extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    public static final String APPLICATION = "application";
    public static final String SESSION = "session";
    public static final String REQUEST = "request";
    public static final String RESPONSE = "response";

    public static final String APPLICATION_SCOPE = "applicationScope";
    public static final String SESSION_SCOPE = "sessionScope";
    public static final String REQUEST_SCOPE = "requestScope";

    public static final String PARAM = "param";
    public static final String PARAM_VALUES = "paramValues";

    public static final String CONTEXT_PATH = "CONTEXT_PATH";
    public static final String WEBROOT_PATH = "WEBROOT_PATH";
    public static final String BASE_PATH = "BASE_PATH";
    public static final String WEBROOT = "WEBROOT"; // short name for WEBROOT_PATH

    //@formatter:off
    private enum TYPE {
        REQUEST_SCOPE,
        SESSION, SESSION_SCOPE,
        PARAM, PARAM_VALUES,
        CONTEXT_PATH, WEBROOT_PATH, BASE_PATH,
    }
    //@formatter:on

    //-------------------------------------------------------------
    // 在 分布式环境中，一般不用内置的 session 对象，禁掉后可以提升速度
    protected static boolean SESSION_ENABLED = !"false".equals(System.getProperty("JetWebContext.session.enabled"));

    public static void disableSession() {
        SESSION_ENABLED = false;
    }

    //-------------------------------------------------------------
    private final HttpServletRequest request;
    private final Map<String, Object> context;

    public VelocityWebContext(HttpServletRequest request, HttpServletResponse response) {
        this(request, response, null);
    }

    public VelocityWebContext(HttpServletRequest request, HttpServletResponse response, Map<String, Object> context) {
        this.request = request;
        this.context = context;

        put(REQUEST, request);
        put(REQUEST_SCOPE, TYPE.REQUEST_SCOPE);

        put(RESPONSE, response);

        if (SESSION_ENABLED) {
            put(SESSION, TYPE.SESSION);
            put(SESSION_SCOPE, TYPE.SESSION_SCOPE);
        }

        put(PARAM, TYPE.PARAM);
        put(PARAM_VALUES, TYPE.PARAM_VALUES);

        put(CONTEXT_PATH, TYPE.CONTEXT_PATH);
        put(WEBROOT_PATH, TYPE.WEBROOT_PATH);
        put(BASE_PATH, TYPE.BASE_PATH);
        put(WEBROOT, TYPE.WEBROOT_PATH);
    }

    @Override
    public Object get(Object key) {
        String name = (String) key;
        if (name == null) return null;

        Object value;

        if (context != null) {
            value = context.get(name);
            if (value != null) {
                return value;
            }
        }

        value = super.get(name);
        if (value != null) {
            if (value instanceof TYPE) {
                value = createImplicitWebObject((TYPE) value);
                put(name, value); // resolved
            }
            return value;
        }

        value = request.getAttribute(name);
        if (value != null) {
            return value;
        }

        if (SESSION_ENABLED) {
            // fixed: cannot create session after response has been committed
            HttpSession session = request.getSession(false);
            if (session != null) {
                value = session.getAttribute(name);
                if (value != null) {
                    return value;
                }
            }
        }

        return request.getServletContext().getAttribute(name);
    }

    private Object createImplicitWebObject(TYPE type) {
        switch (type) {
            case REQUEST_SCOPE:
                return new RequestAttributeMap(request);
            case SESSION:
                return request.getSession();
            case SESSION_SCOPE:
                return new SessionAttributeMap(request);
            case PARAM:
                return new RequestParameterMap(request);
            case PARAM_VALUES:
                return new RequestParameterValuesMap(request);
            case CONTEXT_PATH:
                return request.getContextPath();
            case WEBROOT_PATH:
                return getWebrootPath();
            case BASE_PATH:
                return getWebrootPath().concat("/");
            default:
                return null;
        }
    }

    private String getWebrootPath() {
        StringBuilder sb = new StringBuilder();
        String schema = request.getScheme();
        int port = request.getServerPort();
        sb.append(schema);
        sb.append("://");
        sb.append(request.getServerName());
        if (!(port == 80 && "http".equals(schema)) && !(port == 443 && "https".equals(schema))) {
            sb.append(':').append(request.getServerPort());
        }
        sb.append(request.getContextPath());
        return sb.toString();
    }

    public class RequestAttributeMap extends StringEnumeratedMap<Object> {
        private final HttpServletRequest request;

        public RequestAttributeMap(HttpServletRequest request) {
            this.request = request;
        }

        @Override
        protected Enumeration<String> getAttributeNames() {
            return request.getAttributeNames();
        }

        @Override
        protected Object getAttribute(String key) {
            return request.getAttribute(key);
        }

        @Override
        protected void setAttribute(String key, Object value) {
            request.setAttribute(key, value);
        }

        @Override
        protected void removeAttribute(String key) {
            request.removeAttribute(key);
        }
    }

    public class RequestParameterMap extends StringEnumeratedMap<String> {
        private final HttpServletRequest request;

        public RequestParameterMap(HttpServletRequest request) {
            this.request = request;
        }

        @Override
        protected Enumeration<String> getAttributeNames() {
            return request.getParameterNames();
        }

        @Override
        protected String getAttribute(String key) {
            return request.getParameter(key);
        }

        @Override
        protected void setAttribute(String key, String value) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected void removeAttribute(String key) {
            throw new UnsupportedOperationException();
        }
    }

    public class RequestParameterValuesMap extends StringEnumeratedMap<String[]> {
        private final HttpServletRequest request;

        public RequestParameterValuesMap(HttpServletRequest request) {
            this.request = request;
        }

        @Override
        protected Enumeration<String> getAttributeNames() {
            return request.getParameterNames();
        }

        @Override
        protected String[] getAttribute(String key) {
            return request.getParameterValues(key);
        }

        @Override
        protected void setAttribute(String key, String[] value) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected void removeAttribute(String key) {
            throw new UnsupportedOperationException();
        }
    }

    public class SessionAttributeMap  extends StringEnumeratedMap<Object> {
        private final HttpSession session;

        public SessionAttributeMap(HttpSession session) {
            this.session = session;
        }

        public SessionAttributeMap(HttpServletRequest request) {
            this(request.getSession());
        }

        @Override
        protected Enumeration<String> getAttributeNames() {
            return session.getAttributeNames();
        }

        @Override
        protected Object getAttribute(String key) {
            return session.getAttribute(key);
        }

        @Override
        protected void setAttribute(String key, Object value) {
            session.setAttribute(key, value);
        }

        @Override
        protected void removeAttribute(String key) {
            session.removeAttribute(key);
        }
    }

    public abstract class StringEnumeratedMap<V> implements Map<String, V> {

        private volatile Map<String, V> map;

        @Override
        public boolean containsKey(Object key) {
            return get(key) != null;
        }

        @Override
        public boolean containsValue(Object value) {
            return getAsMap().containsValue(value);
        }

        @Override
        public V get(Object key) {
            return getAttribute(key.toString());
        }

        @Override
        public boolean isEmpty() {
            return getAsMap().isEmpty();
        }

        @Override
        public int size() {
            return getAsMap().size();
        }

        @Override
        public Set<Entry<String, V>> entrySet() {
            return getAsMap().entrySet();
        }

        @Override
        public Set<String> keySet() {
            return getAsMap().keySet();
        }

        @Override
        public Collection<V> values() {
            return getAsMap().values();
        }

        @Override
        public synchronized V put(String key, V value) {
            map = null;
            V previous = get(key);
            setAttribute(key, value);
            return previous;
        }

        @Override
        public synchronized void putAll(Map<? extends String, ? extends V> m) {
            map = null;
            for (Map.Entry<? extends String, ? extends V> e : m.entrySet()) {
                setAttribute(e.getKey(), e.getValue());
            }
        }

        @Override
        public synchronized V remove(Object key) {
            map = null;
            V value = get(key);
            removeAttribute(key.toString());
            return value;
        }

        @Override
        public synchronized void clear() {
            map = null;
            Enumeration<String> keys = getAttributeNames();
            while (keys.hasMoreElements()) {
                removeAttribute(keys.nextElement());
            }
        }

        protected abstract Enumeration<String> getAttributeNames();

        protected abstract V getAttribute(String name);

        protected abstract void setAttribute(String name, V value);

        protected abstract void removeAttribute(String name);

        // double check for map
        protected Map<String, V> getAsMap() {
            Map<String, V> result = map;
            if (result == null) {
                synchronized (this) {
                    result = map;
                    if (result == null) {
                        map = (result = initialize());
                    }
                }
            }
            return result;
        }

        private Map<String, V> initialize() {
            Map<String, V> map = new HashMap<String, V>();
            for (Enumeration<String> e = getAttributeNames(); e.hasMoreElements(); ) {
                String key = e.nextElement();
                V value = getAttribute(key);
                map.put(key, value);
            }
            return map;
        }
    }
}
