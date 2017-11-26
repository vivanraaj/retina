// code by jph
package ch.ethz.idsc.retina.dev.davis.app;

import ch.ethz.idsc.retina.dev.davis.DavisDevice;
import ch.ethz.idsc.retina.dev.davis._240c.Davis240c;
import ch.ethz.idsc.retina.lcm.davis.DavisLcmClient;
import ch.ethz.idsc.retina.util.StartAndStoppable;

public class DavisDetailViewer implements StartAndStoppable {
  private final DavisLcmClient davisLcmClient;
  public final DavisViewerFrame davisViewerFrame;

  public DavisDetailViewer(String cameraId, int period_us) {
    DavisDevice davisDevice = Davis240c.INSTANCE;
    davisLcmClient = new DavisLcmClient(cameraId);
    davisViewerFrame = new DavisViewerFrame(davisDevice);
    // handle dvs
    AccumulatedEventsGrayImage accumulatedEventsImage = new AccumulatedEventsGrayImage(davisDevice, period_us);
    davisLcmClient.davisDvsDatagramDecoder.addDvsListener(accumulatedEventsImage);
    davisLcmClient.davisDvsDatagramDecoder.addDvsListener(davisViewerFrame.davisTallyProvider.dvsListener);
    accumulatedEventsImage.addListener(davisViewerFrame.davisViewerComponent.dvsImageListener);
    // handle aps
    davisLcmClient.davisSigDatagramDecoder.addListener(davisViewerFrame.davisViewerComponent.sigListener);
    davisLcmClient.davisSigDatagramDecoder.addListener(davisViewerFrame.davisTallyProvider.sigListener);
    // handle aps
    davisLcmClient.davisRstDatagramDecoder.addListener(davisViewerFrame.davisViewerComponent.rstListener);
    davisLcmClient.davisRstDatagramDecoder.addListener(davisViewerFrame.davisTallyProvider.rstListener);
    // handle dif
    DavisImageBuffer davisImageBuffer = new DavisImageBuffer();
    davisLcmClient.davisRstDatagramDecoder.addListener(davisImageBuffer);
    SignalResetDifference signalResetDifference = SignalResetDifference.amplified(davisImageBuffer);
    davisLcmClient.davisSigDatagramDecoder.addListener(signalResetDifference);
    signalResetDifference.addListener(davisViewerFrame.davisViewerComponent.difListener);
    // handle imu
    davisLcmClient.davisImuLcmDecoder.addListener(davisViewerFrame.davisViewerComponent);
  }

  @Override
  public void start() {
    // start to listen
    davisLcmClient.startSubscriptions();
  }

  @Override
  public void stop() {
    davisLcmClient.stopSubscriptions();
    davisViewerFrame.jFrame.setVisible(false);
    davisViewerFrame.jFrame.dispose();
  }
}