package com.coolme.scpclient.service;

import org.apache.sshd.scp.client.ScpClient.Option;

/**
 * @author coolme
 */
public interface ScpService {

  /**
   * @param filePath   File path
   * @param targetPath Target path of server
   * @param options    {@link org.apache.sshd.scp.client.ScpClient.Option}
   * @return The result
   */
  boolean send(String filePath, String targetPath, Option... options);
}
