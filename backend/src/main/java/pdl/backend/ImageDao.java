package pdl.backend;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Repository;

@Repository
public class ImageDao implements Dao<Image> {

  private final Map<Long, Image> images = new HashMap<>();

  public ImageDao() throws IOException {
    File imgDir = new File("src/main/resources/images");
    if (!imgDir.exists() || !imgDir.isDirectory()){
      System.err.println("'images' Directory doesn't exist");
    }else{
      Path pathDir = imgDir.toPath();
      Files.walk(pathDir)
        .filter(Files::isReadable)
        .filter(Files::isRegularFile)
        .forEach(imgFile -> loadImage(imgFile));
    }
  }

  private void loadImage(Path imgFile){
    if (FilenameUtils.getExtension(imgFile.getFileName().toString()).equals("jpg") || FilenameUtils.getExtension(imgFile.getFileName().toString()).equals("png")){
      byte[] fileContent;
      try {
        fileContent = Files.readAllBytes(imgFile);
        Optional<org.springframework.http.MediaType> MediaType = MediaTypeFactory.getMediaType(imgFile.getFileName().toString());
        Image img = new Image(imgFile.getFileName().toString(),MediaType.get(), fileContent);
        images.put(img.getId(), img);
		  } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public Optional<Image> retrieve(final long id) {
    return Optional.ofNullable(images.get(id));
  }

  @Override
  public List<Image> retrieveAll() {
    return new ArrayList<Image>(images.values());
  }

  @Override
  public void create(final Image img) {
    images.put(img.getId(), img);
  }

  @Override
  public void update(final Image img, final String[] params) {
    img.setName(Objects.requireNonNull(params[0], "Name cannot be null"));

    images.put(img.getId(), img);
  }

  @Override
  public void delete(final Image img) {
    images.remove(img.getId());
  }
}
