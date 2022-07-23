package com.coolme.scpclient.service.impl;

import com.coolme.scpclient.service.ScpService;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyPair;
import java.security.Security;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.scp.client.CloseableScpClient;
import org.apache.sshd.scp.client.ScpClient;
import org.apache.sshd.scp.client.ScpClient.Option;
import org.apache.sshd.scp.client.ScpClientCreator;
import org.apache.sshd.scp.client.SimpleScpClientImpl;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author coolme
 */
@Service
public class ScpServiceImpl implements ScpService {

  @Value("${scp.host}")
  private String host;

  @Value("${scp.port}")
  private int port;

  @Value("${scp.username}")
  private String userName;

  @Value("${scp.private.key.path}")
  private String privateKeyPath;

  @Override

  public boolean send(String filePath, String targetPath, Option... options) {

    SshClient sshClient = SshClient.setUpDefaultClient();

    try (SimpleScpClientImpl simpleScpClient = new SimpleScpClientImpl(
        SshClient.wrapAsSimpleClient(sshClient));) {
      sshClient.start();

      File privateKeyFile = new File(
          ScpServiceImpl.class.getClassLoader().getResource(privateKeyPath).getFile());
      KeyPair keyPair = getKeyPair(privateKeyFile);

      CloseableScpClient scpLogin = simpleScpClient.scpLogin(host, port, userName,
          keyPair);
      try (ClientSession session = scpLogin.getSession();) {
        ScpClientCreator scpClientCreator = simpleScpClient.getScpClientCreator();
        ScpClient scpClient = scpClientCreator.createScpClient(session);

        scpClient.upload(filePath, targetPath, options);
        return true;
      }
    } catch (IOException e) {
      return false;
    }

  }

  public static void main(String[] args) {


  }


  public static KeyPair getKeyPair(File file) throws IOException {
    Security.addProvider(new BouncyCastleProvider());
    PEMParser pemParser = new PEMParser(new FileReader(file));
    JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
    PEMKeyPair pemKeyPair = (PEMKeyPair) pemParser.readObject();
    return converter.getKeyPair(pemKeyPair);
  }
}
