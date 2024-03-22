package game.connection.packets.wrappers.containers.images;


import game.connection.packets.messages.VectorM;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;


public class ArrayRealVectorImage extends ArrayRealVector {
    private static final double precision = 100.0;

    @NotNull
    public static VectorM getMessage(@NotNull ArrayRealVector vector) {
        VectorM.Builder builder =
                VectorM.newBuilder();
        for (int i = 0; i < vector.getDimension(); i++) {
            builder.addCoordinate((int) (vector.getEntry(i) * precision));
        }
        return builder.build();
    }

    @NotNull
    public static ArrayRealVector getVector(@NotNull VectorM message) {
        ArrayRealVector res = new ArrayRealVector(message.getCoordinateList().size());
        for (int i=0;i<res.getDimension();i++) {
            res.setEntry(i,message.getCoordinateList().get(i) / precision);
        }
        return res;
    }
}
