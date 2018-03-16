// code by jph
package ch.ethz.idsc.gokart.gui;

import ch.ethz.idsc.retina.sys.AppCustomization;
import ch.ethz.idsc.retina.sys.ModuleAuto;
import ch.ethz.idsc.retina.sys.TabbedTaskGui;
import ch.ethz.idsc.retina.util.gui.WindowConfiguration;

/** Important: QuickStartGui only works when connected to the real gokart hardware.
 * 
 * the setup procedure for joystick control is detailed in
 * doc/gokart_software_setup.md */
enum QuickStartGui {
  ;
  public static void main(String[] args) {
    WindowConfiguration wc = AppCustomization.load(QuickStartGui.class, new WindowConfiguration());
    ModuleAuto.INSTANCE.runAll(RunTabbedTaskGui.MODULES_DEV);
    TabbedTaskGui taskTabGui = new TabbedTaskGui(RunTabbedTaskGui.PROPERTIES);
    // ---
    taskTabGui.tab("lab", RunTabbedTaskGui.MODULES_LAB);
    taskTabGui.tab("fuse", RunTabbedTaskGui.MODULES_FUSE);
    taskTabGui.tab("joy", RunTabbedTaskGui.MODULES_JOY);
    taskTabGui.tab("aut", RunTabbedTaskGui.MODULES_AUT);
    wc.attach(QuickStartGui.class, taskTabGui.jFrame);
    taskTabGui.jFrame.setVisible(true);
  }
}
