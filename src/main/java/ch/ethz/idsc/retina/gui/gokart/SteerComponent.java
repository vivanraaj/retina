// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import ch.ethz.idsc.retina.dev.steer.SteerColumnTracker;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.dev.steer.SteerGetEvent;
import ch.ethz.idsc.retina.dev.steer.SteerPositionControl;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.util.data.Word;
import ch.ethz.idsc.retina.util.gui.SliderExt;
import ch.ethz.idsc.retina.util.gui.SpinnerLabel;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

class SteerComponent extends AutoboxTestingComponent<SteerGetEvent, SteerPutEvent> {
  public static final int RESOLUTION = 1000;
  // public static final double MAX_TORQUE = 0.5; // FIXME not relevant
  // ---
  public final SteerInitButton steerInitButton = new SteerInitButton();
  private final JToggleButton jToggleController = new JToggleButton("controller");
  private final SpinnerLabel<Word> spinnerLabelLw = new SpinnerLabel<>();
  private final SliderExt sliderExtTorque;
  private final JTextField[] jTextField = new JTextField[11];
  private final SteerPositionControl steerPositionControl = new SteerPositionControl();
  private final JButton stepLeft = new JButton("step Left");
  private final JButton stepRight = new JButton("step Right");
  private final JButton resetSteps = new JButton("reset Steps");
  private final JTextField torquePut;

  public SteerComponent() {
    { // calibration and controller
      JToolBar jToolBar = createRow("command");
      jToolBar.add(steerInitButton.getComponent());
      // ---
      jToolBar.add(jToggleController);
    }
    {
      JToolBar jToolBar = createRow("command");
      spinnerLabelLw.setList(SteerPutEvent.COMMANDS);
      spinnerLabelLw.setValueSafe(SteerPutEvent.CMD_ON);
      spinnerLabelLw.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    { // command speed
      JToolBar jToolBar = createRow("position");
      sliderExtTorque = SliderExt.wrap(new JSlider(-RESOLUTION, RESOLUTION, 0));
      sliderExtTorque.addToComponent(jToolBar);
    }
    {
      // JToolBar jToolBar = createRow("Torque");
      torquePut = createReading("torque");
    }
    {
      JToolBar jToolBar = createRow("step");
      jToolBar.add(stepLeft);
      stepLeft.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          // TODO clean up
          sliderExtTorque.jSlider.setValue((int) (-RESOLUTION * SteerConfig.GLOBAL.stepPercent.number().doubleValue()));
        }
      });
      jToolBar.add(stepRight);
      stepRight.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          sliderExtTorque.jSlider.setValue((int) (+RESOLUTION * SteerConfig.GLOBAL.stepPercent.number().doubleValue()));
        }
      });
      jToolBar.add(resetSteps);
      resetSteps.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          sliderExtTorque.jSlider.setValue(0);
        }
      });
    }
    addSeparator();
    { // reception
      jTextField[0] = createReading("motAsp_CANInput");
      jTextField[1] = createReading("motAsp_Qual");
      jTextField[2] = createReading("tsuTrq_CANInput");
      jTextField[3] = createReading("tsuTrq_Qual");
      jTextField[4] = createReading("refMotTrq_CANInput");
      jTextField[5] = createReading("estMotTrq_CANInput");
      jTextField[6] = createReading("estMotTrq_Qual");
      jTextField[7] = createReading("gcpRelRckPos");
      jTextField[8] = createReading("gcpRelRckQual");
      jTextField[9] = createReading("gearRat");
      jTextField[10] = createReading("halfRckPos");
    }
  }

  @Override
  public void getEvent(SteerGetEvent steerGetEvent) {
    final boolean isCalibrated = SteerSocket.INSTANCE.getSteerColumnTracker().isCalibrated();
    final Scalar angle = isCalibrated ? SteerSocket.INSTANCE.getSteerColumnTracker().getSteeringValue() : RealScalar.ZERO;
    final String descr = isCalibrated ? "" + angle : "CALIBRATION MISS";
    jToggleController.setEnabled(isCalibrated);
    // ---
    jTextField[0].setText("" + steerGetEvent.motAsp_CANInput);
    jTextField[1].setText("" + steerGetEvent.motAsp_Qual);
    jTextField[2].setText("" + steerGetEvent.tsuTrq_CANInput);
    jTextField[3].setText("" + steerGetEvent.tsuTrq_Qual);
    jTextField[4].setText("" + steerGetEvent.refMotTrq_CANInput);
    jTextField[5].setText("" + steerGetEvent.estMotTrq_CANInput);
    jTextField[6].setText("" + steerGetEvent.estMotTrq_Qual);
    jTextField[7].setText("" + steerGetEvent.getGcpRelRckPos() + " " + descr);
    if (isCalibrated) {
      // TODO reintroduce
      // Color color = ColorFormat.toColor(ColorDataGradients.THERMOMETER.apply(RealScalar.of((angle + 1) / 2)));
      // jTextField[7].setBackground(color);
    }
    jTextField[8].setText("" + steerGetEvent.gcpRelRckQual);
    jTextField[9].setText("" + steerGetEvent.gearRat);
    jTextField[10].setText("" + steerGetEvent.halfRckPos);
  }

  @Override
  public void putEvent(SteerPutEvent putEvent) {
    torquePut.setText("" + putEvent.getTorque());
  }

  @Override
  public Optional<SteerPutEvent> putEvent() {
    if (SteerSocket.INSTANCE.getSteerColumnTracker().isCalibrated()) {
      final Scalar currAngle = SteerSocket.INSTANCE.getSteerColumnTracker().getSteeringValue(); // SCE
      Scalar desPos = RationalScalar.of(-sliderExtTorque.jSlider.getValue(), RESOLUTION).multiply(SteerColumnTracker.MAX_SCE);
      // System.out.println("here " + desPos);
      // System.out.println(desPos);
      Scalar errPos = desPos.subtract(currAngle);
      Scalar torqueCmd = steerPositionControl.iterate(errPos);
      ControllerInfoPublish.publish(desPos, currAngle); // TODO not permanent, this is only for tuning
      if (jToggleController.isSelected()) {
        return Optional.of(new SteerPutEvent( //
            spinnerLabelLw.getValue(), //
            SteerPutEvent.NEWTON_METER.apply(torqueCmd).number().doubleValue()));
      }
    }
    return Optional.empty();
  }
}
