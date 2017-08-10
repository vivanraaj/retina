// code by jph
package ch.ethz.idsc.retina.dvs.io.aps;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Objects;

/** sends content of log file in realtime via DatagramSocket */
public class ApsStandaloneServer implements ApsBlockListener, AutoCloseable {
  // ---
  public static final int COLUMNS = 8;
  public static final int PORT = 14321;
  public final int length;
  // ---
  private DatagramSocket datagramSocket = null;
  private DatagramPacket datagramPacket = null;

  public ApsStandaloneServer(ApsBlockCollector apsBlockCollector) {
    apsBlockCollector.setListener(this);
    ByteBuffer byteBuffer = apsBlockCollector.byteBuffer();
    byte[] data = byteBuffer.array();
    length = data.length;
    try {
      datagramSocket = new DatagramSocket();
      // datagramSocket.setTimeToLive(1); // same LAN
      // datagramSocket.setLoopbackMode(false);
      // datagramSocket.setTrafficClass(0x10 + 0x08); // low delay
      datagramPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), PORT);
      System.out.println("CONNECTION APS OK");
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  @Override
  public void apsBlockReady() {
    datagramPacket.setLength(length); // TODO try if once is sufficient
    try {
      datagramSocket.send(datagramPacket);
    } catch (IOException exception) {
      System.err.println("packet not sent");
    }
  }

  @Override
  public void close() throws Exception {
    if (Objects.nonNull(datagramSocket))
      datagramSocket.close();
  }
}
