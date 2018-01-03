package org.nutz.boot.starter.caffeine;
public class CacheStrategy {

    public static final String DEFAULT = "$DEFAULT";

    /**
     * Name相同，即被认为是同一个缓存策略
     */
    private final String name;

    /**
     * 缓存最大数量,最大空闲时间,最大存活时间 三个指标仅当>0时生效，否则表示无限
     */
    private long maxSize, maxIdle, maxLive;

    public CacheStrategy(String name) {
        super();
        this.name = name;
    }

    public CacheStrategy(String name, long maxSize, long maxIdle, long maxLive) {
        this(name);
        this.maxSize = maxSize;
        this.maxIdle = maxIdle;
        this.maxLive = maxLive;
    }

    public String getName() {
        return name;
    }

    public long getMaxSize() {
        return maxSize;
    }

    public long getMaxIdle() {
        return maxIdle;
    }

    public long getMaxLive() {
        return maxLive;
    }

    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }

    public void setMaxIdle(long maxIdle) {
        this.maxIdle = maxIdle;
    }

    public void setMaxLive(long maxLive) {
        this.maxLive = maxLive;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CacheStrategy other = (CacheStrategy) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "CacheStrategy [name=" + name + ", maxSize=" + maxSize + ", maxIdle=" + maxIdle + ", maxLive=" + maxLive + "]";
    }

}
