/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package signapplication;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 *
 * @author Arno
 */
public class SignApplication {
    private PrivateKey privateKey;
    private byte[] input;
    Signature signature;
    byte[] signatureData;
    
    public SignApplication(String signedBy){
        try{
            //lees private key
            retrievePrivateKey();
            //lees INPUT.EXT
            getInputFile();
            //voor de output:
            //Maak handtekening aan hand van SHA1withRSA
            getSignature();
            //schrijf weg:
            saveFile(signedBy);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    public void retrievePrivateKey()throws Exception{
        byte[] keyBytes = Files.readAllBytes(new File("../privateKey").toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        privateKey = kf.generatePrivate(spec);
    }
    
    public void getInputFile()throws Exception{
        input = Files.readAllBytes(new File("../INPUT.EXT").toPath());
    }
    
    public void getSignature() throws Exception{
        signature = Signature.getInstance("SHA1withRSA");
        signature.initSign(privateKey);
        signature.update("bert".getBytes());
        signatureData = signature.sign();
    }
    
    public void saveFile(String author)throws Exception{
        FileOutputStream fOS = new FileOutputStream("../INPUT(SIGNEDBY" + author + ").EXT");
        //Schrijf Lengte van handtekening weg
        fOS.write(signatureData.length);  
        //schrijf de digitale handtekening zelf weg
        for (int i = 0; i < signatureData.length ; i++)
        {
            System.out.println(signatureData[i]);
        }
        fOS.write(signatureData);
        //schrijf inhoudt van INPUT.EXT weg
        fOS.write(input);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new SignApplication("bert");
    }
    
}
