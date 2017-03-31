package simpleci.worker.system;

import com.jcraft.jsch.*;
import simpleci.worker.job.JobOutputProcessor;

import java.io.*;

public class SshClient {
    private static final int SSH_PORT = 22;
    private Session session;

    public SshClient(String host, String user, String password) throws JSchException {
        if(!Utils.waitForPort(host, SSH_PORT, 10, 1000)) {
            throw new JSchException(String.format("Cant connect to %s:%d", host, SSH_PORT));
        }

        JSch jsch = new JSch();

        session = jsch.getSession(user, host, SSH_PORT);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setPassword(password);
        session.connect();
    }

    public void uploadFile(InputStream stream, String path, int mode) throws JSchException, SftpException {
        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp channelSftp = (ChannelSftp) channel;
        channelSftp.put(stream, path);
        channelSftp.chmod(mode, path);
        channelSftp.exit();
    }

    public void downloadFile(String remotePath, String localPath) throws JSchException, SftpException {
        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp channelSftp = (ChannelSftp) channel;
        channelSftp.get(remotePath, localPath);
        channelSftp.exit();
    }

    public int executeCmd(String cmd, JobOutputProcessor sshOutputProcessor) throws JSchException, IOException {
        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");

        channelExec.setPty(true);
        channelExec.setCommand(cmd);
        channelExec.connect();
        InputStream inputStream = channelExec.getInputStream();
        Reader reader = new InputStreamReader(inputStream);

        char[] buf = new char[1024];
        int numRead;

        // use while(true) to completely wait until closing channel
        while(true) {
            while ((numRead = reader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                if (sshOutputProcessor != null) {
                    sshOutputProcessor.output(readData);
                }
            }
            if(channelExec.isClosed()) {
                break;
            }
        }

        int exitCode = channelExec.getExitStatus();
        channelExec.disconnect();
        return exitCode;
    }

    public boolean fileExist(String file) {
        try {
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp channelSftp = (ChannelSftp) channel;
            channelSftp.lstat(file);
            return true;
        } catch (SftpException | JSchException e) {
            return false;
        }
    }

    public void close() {
        session.disconnect();
    }


}
