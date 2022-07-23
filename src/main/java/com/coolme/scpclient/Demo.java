package com.coolme.scpclient;


import com.coolme.scpclient.service.impl.ScpServiceImpl;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyPair;
import java.security.Security;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.scp.client.ScpClient;
import org.apache.sshd.scp.client.ScpClient.Option;
import org.apache.sshd.scp.client.ScpClientCreator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

public class Demo {

  private static String host = "10.211.55.20";
  private static String username = "parallels";
  private static Integer port = 22;

  private static String local = "/Users/coolme/IdeaProjects/ScpClient/ScpClient/src/main/resources/";
  private static String remote = "/home/parallels/rece";
  private static String idRsaPath = "/Users/coolme/IdeaProjects/ScpClient/ScpClient/src/main/resources/id_rsa_ubuntu";

  public static boolean send(String local, String remote) {

    try (SshClient client = SshClient.setUpDefaultClient()) {
      client.start();

      try (ClientSession session = client.connect(username, host, port)
          .verify()
          .getSession()) {

        File privateKeyFile = new File(ScpServiceImpl.class.getClassLoader()
            .getResource("id_rsa_ubuntu")
            .getFile());

        KeyPair keyPair = getKeyPair(privateKeyFile);

        session.addPublicKeyIdentity(keyPair);
        // 校验用户名和密码的有效性
        boolean isSuccess = session.auth()
            .verify(10000L)
            .isSuccess();

        // 认证成功
        if (isSuccess) {

          ScpClientCreator creator = ScpClientCreator.instance();
          // 创建 SCP 客户端
          ScpClient scpClient = creator.createScpClient(session);

          // ScpClient.Option.Recursive：递归copy，可以将子文件夹和子文件遍历copy
          scpClient.upload(local, remote, Option.Recursive);
          return true;
        }
        return false;
      }
    } catch (Exception e) {

    }

    return false;
  }


  public static KeyPair getKeyPair(File file) throws IOException {
    Security.addProvider(new BouncyCastleProvider());
    PEMParser pemParser = new PEMParser(new FileReader(file));
    JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
    PEMKeyPair pemKeyPair = (PEMKeyPair) pemParser.readObject();
    return converter.getKeyPair(pemKeyPair);
  }

  public static void main(String[] args) {
    boolean result = send(local, remote);

    System.out.println("发送成功了吗? " + result);
  }
}
