// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ interface MPCPreviewableTrack {
  MPCPathParameter getPathParameterPreview(int previewSize, Tensor PositionW, Scalar padding);

  MPCPathParameter getPathParameterPreview(int previewSize, Tensor PositionW, Scalar padding, Scalar QPFactor);
}
