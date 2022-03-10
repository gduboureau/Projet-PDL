package pdl.backend;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;

public class ApplyAlgorithm {

    public static ResponseEntity<?> ChooseAlgorithm(Optional<Image> img, String algo, String p1) throws NumberFormatException, IOException{
        if (algo.equals("Histogram") || algo.equals("gradientSobel") || algo.equals("GrayOutAColorImage") && p1 != null){
            return new ResponseEntity<>("one of the parameters mentioned does not exist for the chosen algorithm", HttpStatus.BAD_REQUEST);  
        }
        
        int param1 = Integer.parseInt(p1);


        if(algo.equals("Brightness")) 
            Brightness(img, param1);
        else if (algo.equals("Histogram"))
            Histogram(img);
        else if (algo.equals("meanFilter"))
            meanFilter(img, param1);
        else if (algo.equals("gradientSobel"))
            gradientSobel(img);  
        else if (algo.equals("GrayOutAColorImage"))
            GrayOutAColorImage(img); 
        else if (algo.equals("GrayOutAColorImage"))
            ColorFilter(img,param1); 
        else
            return new ResponseEntity<>("Algorithm" + algo + " doesn't exist.", HttpStatus.BAD_REQUEST);  
    
        InputStream inputStream = new ByteArrayInputStream(img.get().getData());
        return ResponseEntity.ok().contentType(org.springframework.http.MediaType.IMAGE_JPEG).body(new InputStreamResource(inputStream));
    }

    public static void Brightness(Optional<Image> img, int delta) throws IOException{
        Planar<GrayU8> image = PlanarImageConverter.BytesToPlanarImage(img.get().getData());
        imageProcessing.BrightnessModifier(image, delta);        
        img.get().setData(PlanarImageConverter.PlanarImageToBytes(image));
    }

    public static void Histogram(Optional<Image> img) throws IOException{
        Planar<GrayU8> image = PlanarImageConverter.BytesToPlanarImage(img.get().getData());
        imageProcessing.HistogramEqualization(image);        
        img.get().setData(PlanarImageConverter.PlanarImageToBytes(image));
    }

    public static void meanFilter(Optional<Image> img,int size) throws IOException{
        Planar<GrayU8> image = PlanarImageConverter.BytesToPlanarImage(img.get().getData());
        Planar<GrayU8> output = image.createSameShape();
        imageProcessing.meanFilterSimple(image,output,size);        
        img.get().setData(PlanarImageConverter.PlanarImageToBytes(output));
    }

    //Comment choisir le kernel ?

    /*public static void convolution(Optional<Image> img,int size) throws IOException{
        Planar<GrayU8> image = PlanarImageConverter.BytesToPlanarImage(img.get().getData());
        Planar<GrayU8> output = image.createSameShape();
        imageProcessing.meanFilterSimple(image,output,size);        
        img.get().setData(PlanarImageConverter.PlanarImageToBytes(output));
    }*/ 

    public static void gradientSobel(Optional<Image> img) throws IOException{
        Planar<GrayU8> image = PlanarImageConverter.BytesToPlanarImage(img.get().getData());
        Planar<GrayU8> output = image.createSameShape();
        imageProcessing.gradientImageSobel(image,output);        
        img.get().setData(PlanarImageConverter.PlanarImageToBytes(output));
    }

    public static void GrayOutAColorImage(Optional<Image> img) throws IOException{
        Planar<GrayU8> image = PlanarImageConverter.BytesToPlanarImage(img.get().getData());
        Planar<GrayU8> output = image.createSameShape();
        imageProcessing.GrayOutAColorImage(image,output);        
        img.get().setData(PlanarImageConverter.PlanarImageToBytes(output));
    }

    public static void ColorFilter(Optional<Image> img, int hue) throws IOException{
        Planar<GrayU8> image = PlanarImageConverter.BytesToPlanarImage(img.get().getData());
        Planar<GrayU8> output = image.createSameShape();
        imageProcessing.ColorFilter(image,output,hue);        
        img.get().setData(PlanarImageConverter.PlanarImageToBytes(output));
    }

}
