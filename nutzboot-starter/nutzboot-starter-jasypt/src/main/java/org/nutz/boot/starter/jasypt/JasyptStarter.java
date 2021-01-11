package org.nutz.boot.starter.jasypt;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.boot.config.impl.PropertiesConfigureLoader;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.Collection;

@IocBean(create="init")
public class JasyptStarter {

    private static final Log log = Logs.get();

    private static String ENCRYPT_PREFIX = "ENC(";

    private static String ENCRYPT_SUFFIX = ")";

    protected static final String PRE = "jasypt.encryptor.";

    @PropDoc(value = "加密密码")
    public static final String PASSWORD = PRE + "password";

    @PropDoc(value = "加密算法")
    public static final String ALGORITHM = PRE + "algorithm";

    @PropDoc(value = "缓冲池大小")
    public static final String POOL_SIZE = PRE + "pool-size";


    @Inject("refer:$ioc")
    protected Ioc ioc;

    public void init() {
        //TODO 初始化
    }


    @IocBean(create="init")
    public JasyptPropertiesConfigureLoader jasyptPropertiesConfigureLoader(){
        return new JasyptPropertiesConfigureLoader();
    }


    public static class JasyptPropertiesConfigureLoader extends PropertiesConfigureLoader {

        private StringEncryptor stringEncryptor;

        @Override
        public void init() throws Exception {
            super.init();
            stringEncryptor = createPBEDefault();
            Collection<String> keys = conf.getKeys();

            if(Lang.isNotEmpty(keys)){

                for (String key : keys){

                    String property = conf.get(key);

                    if(Lang.isNotEmpty(property) && isEncrypted(property)){
                        
                        String val = unwrapEncryptedValue(property);
                        try {
                            conf.put(key, stringEncryptor.decrypt(val));
                        }catch (Exception e){
                            throw new DecryptionException("The key:"+key +" decrypt failed",e);
                        }
                    }
                }
            }
        }

        /**
         * 是否是加密串
         */
        public boolean isEncrypted(String property) {
            if (property == null) {
                return false;
            }
            final String trimmedValue = property.trim();
            return (trimmedValue.startsWith(ENCRYPT_PREFIX) &&
                    trimmedValue.endsWith(ENCRYPT_SUFFIX));
        }
        /**
         * 获取加密串内容
         */
        public String unwrapEncryptedValue(String property) {
            return property.substring(
                    ENCRYPT_PREFIX.length(),
                    (property.length() - ENCRYPT_SUFFIX.length()));
        }

        /**
         * 加解密算法
         */
        private StringEncryptor createPBEDefault() {
            PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
            SimpleStringPBEConfig config = new SimpleStringPBEConfig();
            config.setPassword(getRequired(PASSWORD));
            config.setAlgorithm(get(ALGORITHM, "PBEWITHHMACSHA512ANDAES_256"));
            config.setKeyObtentionIterations(1000);
            config.setPoolSize(get(POOL_SIZE, "1"));
            config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
            config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
            config.setStringOutputType("base64");
            encryptor.setConfig(config);
            return encryptor;
        }


        private String getRequired(String key) {
            String value = conf.get(key);
            if (Strings.isBlank(value)) {
                throw new IllegalStateException(String.format("Required Encryption configuration property missing: %s", key));
            }
            return value;
        }

        private String get(String key, String defaultValue) {
            String value = conf.get(key);
            if (Strings.isBlank(value)) {
                value = defaultValue;
            }
            return value;
        }

    }

    public static class DecryptionException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public DecryptionException(final String message) {
            super(message);
        }

        public DecryptionException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }


}
