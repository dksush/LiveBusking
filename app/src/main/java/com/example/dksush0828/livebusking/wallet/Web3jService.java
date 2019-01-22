package com.example.dksush0828.livebusking.wallet;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.http.HttpService;

public class Web3jService {

    private static final Web3j ourInstance = Web3jFactory.build(new HttpService("https://ropsten.infura.io/0x558f95017aB5D78571a738B93CBF5067f62976C9"));


    public static Web3j getInstance() {
        return ourInstance;
    }

    private Web3jService() {
    }

}
