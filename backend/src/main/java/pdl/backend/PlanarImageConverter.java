package pdl.backend;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;

public class PlanarImageConverter {
    
    static int type;

    public static Planar<GrayU8> BytesToPlanarImage(byte[] bytes) throws IOException{
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        BufferedImage input = ImageIO.read(bais);
        type = input.getType();
        Planar<GrayU8> image = ConvertBufferedImage.convertFromPlanar(input, null, true, GrayU8.class);
        return image;
    }

    public static byte[] PlanarImageToBytes(Planar<GrayU8> input) throws IOException{
        var output = new BufferedImage(input.width, input.height, type); 
        ConvertBufferedImage.convertTo_U8(input, output, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(output, "jpg", baos);
        byte[] bytes = baos.toByteArray();
        return bytes;
    }
}
