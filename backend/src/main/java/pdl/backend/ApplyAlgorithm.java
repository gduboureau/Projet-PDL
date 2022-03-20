package pdl.backend;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import boofcv.struct.border.BorderType;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;

public class ApplyAlgorithm {

    public static ResponseEntity<?> ChooseAlgorithm(Optional<Image> img, String algo, String p1) throws NumberFormatException, IOException{
        
        if (checkAlgoParameters(algo, p1) != null){
            return checkAlgoParameters(algo, p1);
        }
        
        Planar<GrayU8> image = PlanarImageConverter.BytesToPlanarImage(img.get().getData());
        Planar<GrayU8> output = image.createSameShape();
        
        if(algo.equals("Brightness")){
            int param1 = Integer.parseInt(p1);
            imageProcessing.BrightnessModifier(image, param1);
        }
        else if (algo.equals("Histogram"))
            imageProcessing.HistogramEqualization(image);
        else if (algo.equals("meanFilter")){
            int param1 = Integer.parseInt(p1);
            imageProcessing.meanFilterWithBorders(image, output, param1, BorderType.NORMALIZED);
        }
        else if (algo.equals("gradientSobel"))
            imageProcessing.gradientImageSobel(image,output);
        else if (algo.equals("GrayOutAColorImage"))
            imageProcessing.GrayOutAColorImage(image,output);
        else if (algo.equals("ColorFilter")){
            int param1 = Integer.parseInt(p1);
            imageProcessing.ColorFilter(image,output,param1);
        }

        if (algo.equals("meanFilter") || algo.equals("gradientSobel") || algo.equals("GrayOutAColorImage") || algo.equals("ColorFilter")){
            img.get().setData(PlanarImageConverter.PlanarImageToBytes(output));
        }else{
            img.get().setData(PlanarImageConverter.PlanarImageToBytes(image));
        }
        
        InputStream inputStream = new ByteArrayInputStream(img.get().getData());
        return ResponseEntity.ok().contentType(org.springframework.http.MediaType.IMAGE_JPEG).body(new InputStreamResource(inputStream));
    }

    public static ResponseEntity<?> checkAlgoParameters(String algo, String p1){
        boolean needParam = true;
        
        if (algo.equals("Histogram") || algo.equals("gradientSobel") || algo.equals("GrayOutAColorImage")){
            needParam = false;
        }else if (algo.equals("Brightness") || algo.equals("ColorFilter") || algo.equals("meanFilter")){
            needParam = true;
        }else{
            return new ResponseEntity<>("Algorithm " + algo + " doesn't exist.", HttpStatus.BAD_REQUEST);
        }
        
        if (needParam && p1 == null){
            return new ResponseEntity<>("missing parameter", HttpStatus.BAD_REQUEST);  
        }
        if (!needParam && p1 != null){
            return new ResponseEntity<>("one of the parameters mentioned does not exist for the chosen algorithm", HttpStatus.BAD_REQUEST);  
        }
        if (needParam){
            try {
                Integer.parseInt(p1);
                }
            catch (Exception e) {
                return new ResponseEntity<>("parameter is unvalid", HttpStatus.BAD_REQUEST);  
            }
        }
        return null;
    }

    //Comment choisir le kernel ?

    /*public static void convolution(Optional<Image> img,int size) throws IOException{
        Planar<GrayU8> image = PlanarImageConverter.BytesToPlanarImage(img.get().getData());
        Planar<GrayU8> output = image.createSameShape();
        imageProcessing.meanFilterSimple(image,output,size);
        img.get().setData(PlanarImageConverter.PlanarImageToBytes(output));
    }*/
}
