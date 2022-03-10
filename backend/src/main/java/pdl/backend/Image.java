package pdl.backend;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Image {
  private static Long count = Long.valueOf(0);
  private Long id;
  private String name;
  private byte[] data;
  private String size;
  private String type;

  public Image(final String name,final String type, final byte[] data) {
    id = count++;
    this.name = name;
    this.data = data;
    size = imageDim();
    this.type = type;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getSize(){
    return size;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public byte[] getData() {
    return data;
  }

  public String getType(){
    return type;
  }

  public String imageDim() {
    try {
      ByteArrayInputStream bais = new ByteArrayInputStream(data);
      BufferedImage image;
      image = ImageIO.read(bais);
      return image.getWidth() + "*" + image.getHeight() + "*" + image.getColorModel().getColorSpace().getNumComponents();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return "error";
  }

  public void setData(byte[] data){
    this.data = data;
  }
}
