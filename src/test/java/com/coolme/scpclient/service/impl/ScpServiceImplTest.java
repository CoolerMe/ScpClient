package com.coolme.scpclient.service.impl;

import com.coolme.scpclient.service.ScpService;
import org.apache.sshd.scp.client.ScpClient.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class ScpServiceImplTest {


  @Autowired
  private ScpService scpService;

  @Test
  public void send() {

    String local = "/Users/coolme/IdeaProjects/ScpClient/ScpClient/src/main/resources/";
    String remote = "/home/parallels/rece";
    boolean send = scpService.send(local, remote, Option.Recursive, Option.TargetIsDirectory);
    Assertions.assertTrue(send);

  }
}
