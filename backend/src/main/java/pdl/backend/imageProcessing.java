package pdl.backend;

import java.util.Comparator;
import java.util.stream.Stream;

//import boofcv.alg.enhance.EnhanceImageOps;
//import boofcv.alg.misc.ImageStatistics;
//import boofcv.concurrency.BoofConcurrency;
import boofcv.struct.border.BorderType;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;

public class imageProcessing {

	/*
	 * This method change the brihtness of the image passed as a parameter.
	 * 
	 * @param input : the image.
	 * 
	 * @param delta : an integer that will be added to the value of each pixel.
	 */

	public static void BrightnessModifier(Planar<GrayU8> image, int delta) {
		for (int y = 0; y < image.height; ++y) {
			for (int x = 0; x < image.width; ++x) {
				for (int channel = 0; channel < image.getNumBands(); channel++) {
					int newGL = image.getBand(channel).get(x, y) + delta;
					newGL = (newGL < 0) ? 0 : newGL;
					newGL = (newGL > 255) ? 255 : newGL;
					image.getBand(channel).set(x, y, newGL);
				}
			}
		}
	}


	/*
	 * This method applies histogram equalization to the image passed as a parameter
	 * More precisely :
	 * - calculates the histogram of the image
	 * - calculates the cumulative histogram
	 * - use cumulative histogram as LUT
	 * 
	 * @param image : the image
	 */

	public static void HistogramEqualization(Planar<GrayU8> image) {
		boolean isInColor = (image.getNumBands() == 3);
		int hist[] = new int[256], HistogramCumulative[] = new int[256];
		for (int y = 0; y < image.height; ++y) {
			for (int x = 0; x < image.width; ++x) {
				if (isInColor) {
					int rgb[] = { 0, 0, 0 };
					float hsv[] = { 0, 0, 0 };
					for (int channel = 0; channel < 3; channel++) {
						rgb[channel] = image.getBand(channel).get(x, y);
					}
					rgbToHsv(rgb[0], rgb[1], rgb[2], hsv);
					hist[(int) hsv[2]]++;
				} else {
					int gl = image.getBand(0).get(x, y);
					hist[gl]++;
				}
			}
		}

		HistogramCumulative[0] = hist[0];

		for (int i = 1; i < hist.length; i++) {
			HistogramCumulative[i] = hist[i] + HistogramCumulative[i - 1];
		}

		for (int y = 0; y < image.height; ++y) {
			for (int x = 0; x < image.width; ++x) {
				if (isInColor) {
					int rgb[] = { 0, 0, 0 };
					float hsv[] = { 0, 0, 0 };
					for (int channel = 0; channel < 3; channel++) {
						rgb[channel] = image.getBand(channel).get(x, y);
					}
					rgbToHsv(rgb[0], rgb[1], rgb[2], hsv);
					hsv[2] = HistogramCumulative[(int) hsv[2]];
					hsvToRgb(hsv[0], hsv[1], hsv[2], rgb);
					for (int channel = 0; channel < 3; channel++) {
						int k = rgb[channel];
						image.getBand(channel).set(x, y, (k * 255) / (image.width * image.height));
					}
				} else {
					int k = HistogramCumulative[image.getBand(0).get(x, y)];
					image.getBand(0).set(x, y, (k * 255) / (image.width * image.height));
				}
			}
		}
	}
	/*
   * This method applies a mean filter on the copy of the input image.
   * In this version, the borders of the image are not processed. For that,
   * we start calculs at "n" which is the kernel radius and stop at
   * input.(height/width)-n
   * 
   * 
   * @param image : the original image
   * 
   * @param output : the copy of the original image on which the filter will be
   * applied
   * 
   * @param size : size of the considered neighborhood (size of the filter)
   * 
   * The input image is not modified.
   */

  public static void meanFilterSimple(Planar<GrayU8> image, Planar<GrayU8> output, int size) {
    int n = size / 2;
    for (int y = n; y < image.height - n; ++y) {
      for (int x = n; x < image.width - n; ++x) {
        for (int channel = 0; channel < image.getNumBands(); channel++) {
          int r = 0;
          for (int u = -n; u <= n; u++) {
            for (int v = -n; v <= n; v++) {
              r += image.getBand(channel).get(x + u, y + v);
            }
          }
          output.getBand(channel).set(x, y, r / (size * size));
        }
      }
    }
  }

  /*
   * This method applies a mean filter on the copy of the input image.
   * In this version, the borders of the image are processed. For that, 4
   * different methods :
   * SKIP, REFLECT, EXTENDED and NORMALIZED all described in the boofcv
   * documentation.
   * 
   * 
   * @param image : the original image
   * 
   * @param output : the copy of the original image on which the filter will be
   * applied
   * 
   * @param size : size of the considered neighborhood (size of the filter)
   * 
   * @param borderType : the type of border
   * 
   * The input image is not modified.
   */
  public static void meanFilterWithBorders(Planar<GrayU8> image, Planar<GrayU8> output, int size,
      BorderType borderType) {
    for (int y = 0; y < image.height; ++y) {
      for (int x = 0; x < image.width; ++x) {
        int n = size / 2;
        for (int channel = 0; channel < image.getNumBands(); channel++) {
          int r = 0;
          for (int u = -n; u <= n; u++) {
            for (int v = -n; v <= n; v++) {

              int vy = v + y;
              int ux = u + x;

              if (borderType.equals(BorderType.SKIP)) {
                meanFilterSimple(image, output, size);
                return;
              }

              else if (borderType.equals(BorderType.NORMALIZED)) {
                if ((v + y >= 0) && (v + y < image.height) && (u + x >= 0) && (u + x < image.width)) {
                  r += image.getBand(channel).get(x + u, y + v);
                }
              }

              else if (borderType.equals(BorderType.EXTENDED)) {
                vy = (v + y < 0) ? 0 : vy;
                vy = (v + y >= image.height) ? image.height - 1 : vy;
                ux = (u + x < 0) ? 0 : ux;
                ux = (u + x >= image.width) ? image.width - 1 : ux;
                r += image.getBand(channel).get(ux, vy);
              }

              else if (borderType.equals(BorderType.REFLECT)) {
                vy = (v + y < 0) ? -(vy) : vy;
                vy = (v + y >= image.height) ? image.height - (vy - image.height) - 1 : vy;
                ux = (u + x < 0) ? -(ux) : ux;
                ux = (u + x >= image.width) ? image.width - (ux - image.width) - 1 : ux;
                r += image.getBand(channel).get(ux, vy);
              }

              else {
                System.out.println("Error : wrong type of BorderType.");
                return;
              }
            }
          }
          output.getBand(channel).set(x, y, r / (size * size));
        }
      }
    }
  }

  /*
   * This method applies a generic convolution filter by not processing the
   * borders (SKIP).
   * 
   * @param imagr : the original image
   * 
   * @param output : the copy of the original image on which the filter will be
   * applied
   * 
   * @param kernel : array containing the convolution mask coefficients
   * 
   * The input image is not modified.
   */
  public static void convolution(Planar<GrayU8> image, Planar<GrayU8> output, int[][] kernel) {
    int n = kernel.length / 2;
    for (int y = n; y < image.height - n; ++y) {
      for (int x = n; x < image.width - n; ++x) {
        for (int channel = 0; channel < image.getNumBands(); channel++) {
          int r = 0;
          int somme = 0;
          for (int u = -n; u <= n; u++) {
            for (int v = -n; v <= n; v++) {
              r += image.getBand(channel).get(x + u, y + v) * kernel[u + n][v + n];
              somme += kernel[u + n][v + n];
            }
          }
          output.getBand(channel).set(x, y, r / somme);
        }
      }
    }
  }

  /*
   * This method applies a Sobel filter to the output image which is a copy of the
   * input image
   * The input image is not modified.
   * This filter is used for edge detection.
   * 
   * @param image : the original image
   * 
   * @param output : a copy of the input image where the filter is going to be
   * applied
   */
  public static void gradientImageSobel(Planar<GrayU8> image, Planar<GrayU8> output) {
    if (image.imageType.numBands == 3) {
      GrayOutAColorImage(image, output);
    }
    int h1[][] = { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } };
    int h2[][] = { { -1, -2, -1 }, { 0, 0, 0 }, { 1, 2, 1 } };
    for (int y = 1; y < image.height - 1; ++y) {
      for (int x = 1; x < image.width - 1; ++x) {
        int Gy = 0;
        int Gx = 0;
        int j = 0;
        for (int u = y - 1; u <= y + 1; u++) {
          int i = 0;
          for (int v = x - 1; v <= x + 1; v++) {
            Gy += image.getBand(0).get(v, u) * (h1[j][i]);
            Gx += image.getBand(0).get(v, u) * (h2[j][i]);
            i++;
          }
          j++;
        }
        int value = (int) Math.sqrt(Math.pow(Gx, 2) + Math.pow(Gy, 2));
        value = (value > 255) ? 255 : value;
        value = (value < 0) ? 0 : value;
        for (int channel = 0; channel < image.getNumBands(); channel++) {
          output.getBand(channel).set(x, y, value);
        }
      }
    }
  }

  /*
     * This method applies filter that grays out a color image.
     * 
     * @param input : the original image
     * 
     * @param output : the copy of the original image on which the filter will be
     * applied
     * 
     * 
     * The input image is not modified.
     */
    public static void GrayOutAColorImage(Planar<GrayU8> image, Planar<GrayU8> output) {
        for (int y = 0; y < image.height; ++y) {
            for (int x = 0; x < image.width; ++x) {
                int color[] = { 0, 0, 0 };
                for (int i = 0; i < 3; i++) {
                    color[i] = image.getBand(i).get(x, y);
                }
                for (int channel = 0; channel < 3; channel++) {
                    output.getBand(channel).set(x, y, (int) ((color[0] * 0.3) + (color[1] * 0.59) + (color[2] * 0.11)));
                }
            }
        }
    }

    /*
     * This method calculate a hue value, saturation value
     * and value (HSV) from a pixel with R G B components.
     * 
     * @param r,g,b : channels of the pixel
     * 
     * @param hsv : array which is filled with the calculated values h,s and v.
     * 
     */

    public static void rgbToHsv(int r, int g, int b, float[] hsv) {
        float max = Stream.of(r, g, b).max(Comparator.naturalOrder()).get();
        float min = Stream.of(r, g, b).min(Comparator.naturalOrder()).get();

        float h = 0.0F;
        if (max == min) {
            h = 0.0F;
        } else if (max == r) {
            h = 60.0F * ((g - b) / (max - min));
        } else if (max == g) {
            h = 60.0F * ((b - r) / (max - min) + 2.0F);
        } else if (max == b) {
            h = 60.0F * ((r - g) / (max - min) + 4.0F);
        }

        float s = 0.0F;
        if (max == 0) {
            s = 0.0F;
        } else {
            s = (max - min) / max;
        }
        hsv[0] = h;
        hsv[1] = s;
        hsv[2] = max;
    }

    /*
     * This method calculates the new values ​​of the R, G and B channels from the
     * HSV values.
     * 
     * @param h : the hue component
     * 
     * @param s : the saturation component
     * 
     * @param v : the value
     * 
     * @param rgb : array which is filled with the calculated values r,g and b.
     * 
     */
    public static void hsvToRgb(float h, float s, float v, int[] rgb) {
        float C = s * v;
        float H = h / 60.0F;
        float X = C * (1 - Math.abs((H % 2.0F) - 1.0F));

        if (0 <= H && H < 1) {
            rgb[0] = (int) C;
            rgb[1] = (int) X;
            rgb[2] = 0;
        } else if (1 <= H && H < 2) {
            rgb[0] = (int) X;
            rgb[1] = (int) C;
            rgb[2] = 0;
        } else if (2 <= H && H < 3) {
            rgb[0] = (int) 0;
            rgb[1] = (int) C;
            rgb[2] = (int) X;
        } else if (3 <= H && H < 4) {
            rgb[0] = (int) 0;
            rgb[1] = (int) X;
            rgb[2] = (int) C;
        } else if (4 <= H && H < 5) {
            rgb[0] = (int) X;
            rgb[1] = 0;
            rgb[2] = (int) C;
        } else if (5 <= H && H < 6) {
            rgb[0] = (int) C;
            rgb[1] = (int) 0;
            rgb[2] = (int) X;
        }

        float m = (v - C);
        rgb[0] += (int) m;
        rgb[1] += m;
        rgb[2] += m;
    }

    /*
     * This method applies a filter that colors the image.
     * 
     * @param hue : a hue value in the range [0, 360] that replaces the hue of all
     * pixels in the image.
     * 
     * @param image : the original image
     */
    public static void ColorFilter(Planar<GrayU8> image, Planar<GrayU8> output, float hue) {
        if (image.getNumBands() < 3 || hue < 0 || hue > 360) {
            System.err.println("please enter a color or image or a hue value between 0 and 360.");
            return;
        }
        for (int y = 0; y < image.height; ++y) {
            for (int x = 0; x < image.width; ++x) {
                int rgb[] = { 0, 0, 0 };
                float hsv[] = { 0.0F, 0.0F, 0.0F };
                for (int channel = 0; channel < 3; channel++) {
                    rgb[channel] = image.getBand(channel).get(x, y);
                }
                rgbToHsv(rgb[0], rgb[1], rgb[2], hsv);
                hsvToRgb(hue, hsv[1], hsv[2], rgb);
                for (int channel = 0; channel < 3; channel++) {
                    output.getBand(channel).set(x, y, rgb[channel]);
                    ;
                }
            }
        }
    }
}
