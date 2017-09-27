// code by jph
package ch.ethz.idsc.retina.lcm.mod;

import ch.ethz.idsc.retina.dev.linmot.LinmotSocket;
import ch.ethz.idsc.retina.dev.misc.MiscSocket;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.lcm.autobox.LinmotLcmServer;
import ch.ethz.idsc.retina.lcm.autobox.MiscLcmServer;
import ch.ethz.idsc.retina.lcm.autobox.RimoLcmServer;
import ch.ethz.idsc.retina.lcm.autobox.SteerLcmServer;
import ch.ethz.idsc.retina.sys.AbstractModule;

public class AutoboxLcmServerModule extends AbstractModule {
  @Override
  protected void first() throws Exception {
    RimoSocket.INSTANCE.addGetListener(RimoLcmServer.INSTANCE);
    RimoSocket.INSTANCE.addPutListener(RimoLcmServer.INSTANCE);
    // ---
    LinmotSocket.INSTANCE.addGetListener(LinmotLcmServer.INSTANCE);
    LinmotSocket.INSTANCE.addPutListener(LinmotLcmServer.INSTANCE);
    // ---
    SteerSocket.INSTANCE.addGetListener(SteerLcmServer.INSTANCE);
    SteerSocket.INSTANCE.addPutListener(SteerLcmServer.INSTANCE);
    // ---
    MiscSocket.INSTANCE.addGetListener(MiscLcmServer.INSTANCE);
    MiscSocket.INSTANCE.addPutListener(MiscLcmServer.INSTANCE);
  }

  @Override
  protected void last() {
    RimoSocket.INSTANCE.removeGetListener(RimoLcmServer.INSTANCE);
    RimoSocket.INSTANCE.removePutListener(RimoLcmServer.INSTANCE);
    // ---
    LinmotSocket.INSTANCE.removeGetListener(LinmotLcmServer.INSTANCE);
    LinmotSocket.INSTANCE.removePutListener(LinmotLcmServer.INSTANCE);
    // ---
    SteerSocket.INSTANCE.removeGetListener(SteerLcmServer.INSTANCE);
    SteerSocket.INSTANCE.removePutListener(SteerLcmServer.INSTANCE);
    // ---
    MiscSocket.INSTANCE.removeGetListener(MiscLcmServer.INSTANCE);
    MiscSocket.INSTANCE.removePutListener(MiscLcmServer.INSTANCE);
  }
}