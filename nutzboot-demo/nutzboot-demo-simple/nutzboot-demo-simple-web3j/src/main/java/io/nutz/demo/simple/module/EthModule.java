package io.nutz.demo.simple.module;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.boot.starter.web3.Web3jAccount;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.utils.Convert;

@Ok("json:{compact:false,quoteName:true, ignoreNull:true, locked:'credentials'}")
@IocBean
@At("/web3j")
public class EthModule {
    
    private static final Log log = Logs.get();

    @Inject
    protected Web3j web3j;
    
    @Inject
    protected Admin web3jAdmin;
    
    @Inject
    protected PropertiesProxy conf;

    @Inject
    protected Map<String, Web3jAccount> web3jCredentials;

    @At("/remote/accounts")
    public NutMap remoteAccounts() {
        Map<String, Web3jAccount> accounts = new HashMap<>();
        try {
            List<String> accountAddrs = web3j.ethAccounts().send().getAccounts();
            for (String address : accountAddrs) {
                Web3jAccount account = new Web3jAccount();
                account.setBanlance(web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send().getBalance());
                account.setAddress(address);
                account.setName(address.substring(0, 8));
                accounts.put(account.getName(), account);
            }
            return new NutMap("ok", true).setv("data", accounts);
        }
        catch (IOException e) {
            log.info("something happen", e);
            return new NutMap("msg", e.getMessage());
        }
    }
    

    @At("/local/accounts")
    public NutMap localeAccounts(@Param("updateBalance") Boolean updateBalance) {
        NutMap re = new NutMap();
        try {
            if (updateBalance == null || updateBalance) {
                for (String key : web3jCredentials.keySet()) {
                    String address = web3jCredentials.get(key).getAddress();
                    EthGetBalance egb = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
                    if (!egb.hasError())
                        web3jCredentials.get(key).setBanlance(egb.getBalance());
                }
                
            }
            return new NutMap("ok", true).setv("data", web3jCredentials);
        }
        catch (IOException e) {
            log.info("something happen", e);
            return re.setv("msg", e.getMessage());
        }
    }
    
    //@POST
    @At("/eth/sendTransaction/?/?")
    public NutMap sendTransaction(String from, String to, @Param("wei")double wei) {
        // from 必须是本地账号
        // to 必须是address
        
        NutMap re = new NutMap();
        // 检查转账金额
        if (wei < 0.01) {
            re.put("msg", "起码转账 0.01 eth");
            return re;
        }
        if (wei > 100) {
            re.put("msg", "最多转账 100 eth");
            return re;
        }
        Web3jAccount account = web3jCredentials.get(from);
        if (account == null) {
            return re.setv("msg", "不存在这个本地账号: " + from);
        }
        // 发起转账
        BigInteger value =  Convert.toWei(new BigDecimal(wei), Convert.Unit.ETHER).toBigInteger();
        Transaction transaction = Transaction.createEtherTransaction(account.getAddress(), null, null, null, to, value);
        try {
            EthSendTransaction est = web3jAdmin.personalSendTransaction(transaction, account.getPassword()).send();
            String hash = est.getTransactionHash();
            return re.setv("ok", true).setv("hash", hash);
        }
        catch (Exception e) {
            log.warn("转账失败!!!", e);
            return re.setv("msg", e.getMessage());
        }
    }
}
