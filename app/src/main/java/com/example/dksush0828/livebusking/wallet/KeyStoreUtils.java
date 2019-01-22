package com.example.dksush0828.livebusking.wallet;

import android.content.Context;
import android.util.Log;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import com.example.dksush0828.livebusking.R;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KeyStoreUtils {


    public static final String DEFAULT_KEY = "DEFAULT";

    public static String genKeyStoreToFiles(ECKeyPair ecKeyPair, Context context){
        try{
            File file = getKeyStorePathFile(context);
            String fileName = WalletUtils.generateWalletFile(DEFAULT_KEY, ecKeyPair, file, false);
            String address = "0x"+fileName.substring(fileName.lastIndexOf("--")+2, fileName.lastIndexOf("."));
            Log.v("generate Wallet File", fileName);
            Log.v("generate Wallet address", address);

            return address;


        }catch (CipherException | IOException e){
            e.printStackTrace();
        }
        return null;
    }



    public static File getKeyStorePathFile(Context context){
        try{
            //File file = new File(KEYSTORE_PATH); : 키 저장소(없으면 만든다)
            //getFilesDir : 일반파일 저장하기.
            File file = new File(context.getFilesDir().getPath()+"/keystore");
            if(!file.exists()){
                file.mkdir();
            }
            Log.v("Key_Files",file.getAbsolutePath());
            return file;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    // 보증서????
    public static Credentials getCredentials(String targetAddress, Context context) throws FileNotFoundException {
        File keyStorePath = new File(context.getFilesDir().getPath()+"/keystore");

        // 파일 이름에는 0x 가 붙어 있지 않기 때문에 0x 를 없앤다.
        // String targetAddr = targetAddress.replace("0x","");
        String targetAddr = targetAddress.replaceFirst("0x","");

        File[] files = keyStorePath.listFiles();
        for(File file : files){
            String name = file.getName();
            String address = name.substring(name.lastIndexOf("--")+2, name.lastIndexOf("."));
            if(targetAddr.equals(address)){
                ObjectMapper mapper = new ObjectMapper();

                try{
                    WalletFile walletFile = mapper.readValue(file,WalletFile.class);
                    return Credentials.create(Wallet.decrypt(DEFAULT_KEY,walletFile));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        throw new FileNotFoundException("not found keyStore");
    }
}
