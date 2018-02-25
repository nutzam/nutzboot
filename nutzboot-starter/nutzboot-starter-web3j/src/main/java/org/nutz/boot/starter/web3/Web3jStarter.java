package org.nutz.boot.starter.web3;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.nutz.boot.annotation.PropDoc;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Files;
import org.nutz.lang.Streams;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.http.HttpService;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;

@IocBean
public class Web3jStarter {
	
    private static final Log log = Logs.get();
    
	protected static final String PRE = "web3j.";
    
    @PropDoc(value = "类型", defaultValue="http")
    public static final String PROP_TYPE = PRE + "type";
    
    @PropDoc(value = "以太坊节点URL", defaultValue="http://localhost:8545/")
    public static final String PROP_HTTP_URL = PRE + "http.url";
    
    @PropDoc(value = "http调试模式,显示通信内容", defaultValue="true")
    public static final String PROP_HTTP_DEBUG = PRE + "http.debug";
    
    @PropDoc(value = "http是否保留原始响应的内容", defaultValue="false")
    public static final String PROP_HTTP_INCLUDE_RAW_RESPONSES = PRE + "http.includeRawResponses";
    
    @Inject
    protected PropertiesProxy conf;
    
    @Inject("refer:$ioc")
    protected Ioc ioc;
    
    @IocBean(name="web3jService")
    public Web3jService createWeb3jService() {
        String url = conf.get(PROP_HTTP_URL, HttpService.DEFAULT_URL);
        boolean debug = conf.getBoolean(PROP_HTTP_DEBUG, true);
        boolean includeRawResponses = conf.getBoolean(PROP_HTTP_INCLUDE_RAW_RESPONSES, false);
        if (debug)
            return new HttpService(url, includeRawResponses);
        return new HttpService(url, new OkHttpClient.Builder().build(), includeRawResponses);
    }
	
	@IocBean(name="web3j")
	public Web3j createWeb3j() {
	    return Web3j.build(ioc.get(Web3jService.class, "web3jService"));
	}
	
    @IocBean(name="web3jAdmin")
    public Admin createWeb3jAdmin() {
        return Admin.build(ioc.get(Web3jService.class, "web3jService"));
    }
	
	@IocBean(name="web3jCredentials")
	public Map<String, Web3jAccount> loadCredentials() throws IOException, CipherException {
	    Map<String, Web3jAccount> accounts = new HashMap<>();
	    for (String key : conf.keys()) {
            // web3j.accounts.wendal.password=123456
            // web3j.accounts.wendal.keystore.path=xxx/xxx/xxx
            // web3j.accounts.wendal.address=xxxxxxxxxx
            if (key.startsWith(PRE + "accounts.") && key.endsWith(".password")) {
                Web3jAccount account = new Web3jAccount();
                account.setName(key.substring((PRE + "accounts.").length(), key.length() - ".password".length()));
                log.debug("loading account name=" + account.getName());
                account.setPassword(conf.get(PRE + "accounts." + account.getName() + ".password"));
                if (conf.has(PRE + "accounts." + account.getName() + ".keystore.path")) {
                    String path = conf.get(PRE + "accounts." + account.getName() + ".keystore.path");
                    byte[] source = null;
                    try {
                        InputStream ins = getClass().getClassLoader().getResourceAsStream(path);
                        if (ins != null) {
                            source = Streams.readBytesAndClose(ins);
                        }
                    }
                    catch (Throwable e) {
                    }
                    if (source == null) {
                        source = Files.readBytes(path);
                    }
                    if (source == null) {
                        throw new FileNotFoundException("not such keystore path=" + path);
                    }
                    WalletFile wf = objectMapper.readValue(source, WalletFile.class);
                    account.setCredentials(Credentials.create(Wallet.decrypt(account.getPassword(), wf)));
                    account.setAddress(account.getCredentials().getAddress());
                }
                accounts.put(account.getName(), account);
            }
        }
	    return accounts;
	}

	//
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
