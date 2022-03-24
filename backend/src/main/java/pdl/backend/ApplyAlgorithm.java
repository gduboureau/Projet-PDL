package pdl.backend;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.Optional;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import boofcv.struct.border.BorderType;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;

public class ApplyAlgorithm {
    static ObjectMapper mapper = new ObjectMapper();
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
        }else if (algo.equals("gaussFilter")){
            int kernel[][] = { { 1, 2, 3, 2, 1 }, { 2, 6, 8, 6, 2 }, { 3, 8, 10, 8, 3 }, { 2, 6, 8, 6, 2 }, { 1, 2, 3, 2, 1 } };
            imageProcessing.convolution(image, output, kernel);
        }

        if (algo.equals("meanFilter") || algo.equals("gradientSobel") || algo.equals("GrayOutAColorImage") || algo.equals("ColorFilter") || algo.equals("gaussFilter")){
            img.get().setData(PlanarImageConverter.PlanarImageToBytes(output));
        }else{
            img.get().setData(PlanarImageConverter.PlanarImageToBytes(image));
        }
        
        InputStream inputStream = new ByteArrayInputStream(img.get().getData());
        return ResponseEntity.ok().contentType(org.springframework.http.MediaType.IMAGE_JPEG).body(new InputStreamResource(inputStream));
    }

    public static ResponseEntity<?> checkAlgoParameters(String algo, String p1){
        boolean needParam = true;
        
        if (algo.equals("Histogram") || algo.equals("gradientSobel") || algo.equals("GrayOutAColorImage") || algo.equals("gaussFilter")){
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

    static List<String> getAlgo(){
        List<String> algo = new ArrayList<String>();
        algo.add("Brightness");
        algo.add("Histogram");
        algo.add("meanFilter");
        algo.add("gradientSobel");
        algo.add("GrayOutAColorImage");
        algo.add("ColorFilter");
        algo.add("gaussFilter");
        return algo;
    }

    static ArrayNode algorithms(){
        List<String> algo = getAlgo();
        ArrayNode nodes = mapper.createArrayNode();
        for (String algorithm : algo){
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("name",algorithm);
            if (algorithm.equals("Brightness") || algorithm.equals("meanFilter") || algorithm.equals("ColorFilter")){
                objectNode.put("hasParameters",true);
            }else{
                objectNode.put("hasParameters",false);
            }
            nodes.add(objectNode);
        }
        return nodes;
    }
}
