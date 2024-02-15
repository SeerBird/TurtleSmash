package game.connection.packets.containers.images;

import game.util.Maths;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class ArrayRealVectorImage implements Serializable {
    @Serial
    private static final long serialVersionUID = 45500;
    private static final int decimalPrecision = 10;
    ArrayList<Short> values;

    public ArrayRealVectorImage(@NotNull ArrayRealVector vector) {
        values = new ArrayList<>();
        for (int i = 0; i < vector.getDimension(); i++) {
            values.add((short) (vector.getEntry(i) * 10));
        }
    }

    public ArrayRealVector restoreVector() {
        ArrayRealVector res = new ArrayRealVector(values.size());
        for (short value : values) {
            res.append((double) value / decimalPrecision);
        }
        return res;
    }
}
