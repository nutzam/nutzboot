package io.nutz.demo.simple;

import java.io.IOException;

import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;

@IocBean(create="init")
public class MainLauncher {
    
    private static final Log log = Logs.get();
    
    @Inject
    protected Web3j web3j;
    
    public void init() throws IOException {
        Web3ClientVersion web3ClientVersion = web3j.web3ClientVersion().send();
        String clientVersion = web3ClientVersion.getWeb3ClientVersion();
        log.info("clientVersion=" + clientVersion);
    }

    // 本地账号余额: http://127.0.0.1:8080/web3j/local/accounts
    // 全部账号余额: http://127.0.0.1:8080/web3j/remote/accounts
    // 转账3eth: http://127.0.0.1:8080/web3j/eth/sendTransaction/acc1/0x81e25b765a6e51754f65b0f72382e6b8e0caddf3/?wei=3
    // 注意: 转账可能需要等到30s才会完成!!

	public static void main(String[] args) throws Exception {
		new NbApp().setPrintProcDoc(true).run();
	}

}
