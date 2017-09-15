// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.HexStrings;

public class SteerGetEvent {
  public static final int LENGTH = 44;
  public final byte[] data;

  public SteerGetEvent(ByteBuffer byteBuffer) {
    int length = byteBuffer.remaining();
    data = new byte[length];
    byteBuffer.get(data);
  }

  public String toInfoString() {
    return HexStrings.from(data);
  }
}