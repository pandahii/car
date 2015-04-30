package Com;


import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import javax.swing.ImageIcon;

public class ImageTool {
	
	//public BufferedImage oImage,cutImage = null,inImage;//
	BufferedImage image;
	public boolean canRect = false,isgray = false,showCut = false;
	
	public int[] shuip;
	public int[] chuiz;
	public int recTop = 0;
	public int recLeft = 0;
    static final int CUT_IMAGE_WIDTH = 110;     
    static final int CUT_IMAGE_HIGHT = 30;
	  
	public BufferedImage huiDuTu(BufferedImage image)//灰度图
    {       
        int iw = image.getWidth();
        int ih = image.getHeight();
        int[] pixArray = new int[image.getWidth() * image.getHeight()];
        int[] grayPix = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, iw, ih, pixArray, 0, iw);
        int r, g, b;
        int y;
        for (int i = 0; i < pixArray.length; i++)
        {
            b = pixArray[i] & 0x000000ff;
            g = (pixArray[i] & 0x0000ff00) >> 8;
            r = (pixArray[i] & 0x00ff0000) >> 16;
            y = (int) (0.299 * r + 0.587 * g + 0.114 * b);
            grayPix[i] = y;
            pixArray[i] = 0xFF000000 | ((y & 0xff) << 16) | ((y & 0xff) << 8) |
                          (y & 0xff);
        }
        BufferedImage bimg = new BufferedImage(iw, ih,
                                               BufferedImage.TYPE_INT_RGB);
        bimg.setRGB(0, 0, iw, ih, pixArray, 0, iw);
        image = bimg;
        canRect = false;
        isgray = true;
        return image;

    }

	public BufferedImage junHengHua(BufferedImage image)//均衡化
    {    
        int iw = image.getWidth();
        int ih = image.getHeight();
        int nTotal = iw * ih;
        int[] pixArray = new int[image.getWidth() * image.getHeight()];
        int[] grayPix = new int[image.getWidth() * image.getHeight()];
        int[] frequencies = new int[256];
        int[] destArray = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, iw, ih, pixArray, 0, iw);
        int r, g, b;
        int y;
        for (int i = 0; i < pixArray.length; i++)
        {
            b = pixArray[i] & 0x000000ff;
            g = (pixArray[i] & 0x0000ff00) >> 8;
            r = (pixArray[i] & 0x00ff0000) >> 16;
            y = (int) (0.299 * r + 0.587 * g + 0.114 * b);
            grayPix[i] = y;
            frequencies[y]++;
        }

        int[] accumulator = new int[256];
        accumulator[0] = frequencies[0];
        for (int i = 1; i < 256; i++)
        {
            accumulator[i] = accumulator[i - 1] + frequencies[i];
        }
        for (int i = 1; i < 256; i++)
        {
            accumulator[i] = (int) ((accumulator[i] / (float) nTotal) * 255);
        }
        for (int i = 0; i < ih; i++)
        {
            for (int j = 0; j < iw; j++)
            {
                int index = i * iw + j;
                int a = accumulator[grayPix[index]];
                destArray[index] = (0xFF000000 | (a << 16) | (a << 8) | a);
            }
        }
        BufferedImage bimg = new BufferedImage(iw, ih,
                                               BufferedImage.TYPE_INT_RGB);
        bimg.setRGB(0, 0, iw, ih, destArray, 0, iw);

        image = bimg;       
        canRect = false;
        isgray = true;
        return image;
    }

    public BufferedImage bianYuanJianCe(BufferedImage images)//边缘检测
    {    
    	image=images;
        int iw = image.getWidth();
        int ih = image.getHeight();
        int nTotal = iw * ih;
        int[] pixArray = new int[image.getWidth() * image.getHeight()];
        int[] grayPix = new int[image.getWidth() * image.getHeight()];
        int[] frequencies = new int[256];
        int[] destArray = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, iw, ih, pixArray, 0, iw);
        int r, g, b;
        int y;
        for (int i = 0; i < pixArray.length; i++)
        {
            b = pixArray[i] & 0x000000ff;
            g = (pixArray[i] & 0x0000ff00) >> 8;
            r = (pixArray[i] & 0x00ff0000) >> 16;
            y = (int) (0.299 * r + 0.587 * g + 0.114 * b);
            grayPix[i] = y;
            frequencies[y]++;
        }

        int[] accumulator = new int[256];
        accumulator[0] = frequencies[0];
        for (int i = 1; i < 256; i++)
        {
            accumulator[i] = accumulator[i - 1] + frequencies[i];
        }
        for (int i = 1; i < 256; i++)
        {
            accumulator[i] = (int) ((accumulator[i] / (float) nTotal) * 255);
        }
        for (int i = 0; i < ih; i++)
        {
            for (int j = 0; j < iw; j++)
            {
                int index = i * iw + j;
                int a = accumulator[grayPix[index]];
                destArray[index] = (0xFF000000 | (a << 16) | (a << 8) | a);
            }
        }
        BufferedImage bimg = new BufferedImage(iw, ih,
                                               BufferedImage.TYPE_INT_RGB);
        bimg.setRGB(0, 0, iw, ih, destArray, 0, iw);

        image = bimg;       
        float[] elements =
                {
                0.0f, -1.0f, 0.0f, -1.0f, 4.0f, -1.0f,
                0.0f, -1.0f, 0.0f};
        convolve(elements);
        images= image;
        image = null;
		return images;
    }

    private float smooth(int[] buff, int i, int w, float k, float sgm)//平滑
    {
        float tmp = 0.0f;
        for (int p = 1; p <= w; p++)
        {
            tmp += (buff[i - p] * gauss(p, sgm) + buff[i + p]
                    * gauss(p, sgm));
        }
        return (buff[i] + tmp) / k;
    }

    private float gauss(int x, float sgm)//高斯
    {
        return (float) (Math.exp( -x * x) / (sgm * sgm * 2.0f));
    }

    private void filter(BufferedImageOp op)//过滤器
    {
        BufferedImage filteredImage = new BufferedImage(image.getWidth(), image
                .getHeight(), image.getType());
        op.filter(image, filteredImage);
        image = filteredImage;       
        canRect = false;
        isgray = false;
    }

    private void convolve(float[] elements)//卷积
    {
        Kernel kernel = new Kernel(3, 3, elements);//内核，算子
        ConvolveOp op = new ConvolveOp(kernel);//卷积类
        filter(op);
    }

    public void chePaiDingWei(BufferedImage image)//车牌定位
    {       
        int iw = image.getWidth();
        int ih = image.getHeight();
        int top = 0;
        int left = 0;
        int[] pixArray = new int[image.getWidth() * image.getHeight()];
        int[] grayPix = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, iw, ih, pixArray, 0, iw);
        int r, g, b;
        int y;
        
        for (int i = 0; i < pixArray.length; i++)
        {
            b = pixArray[i] & 0x000000ff;
            g = (pixArray[i] & 0x0000ff00) >> 8;
            r = (pixArray[i] & 0x00ff0000) >> 16;
            y = (int) (0.299 * r + 0.587 * g + 0.114 * b);
            grayPix[i] = y;
        }
        int[] t = new int[ih];
        // 计算水平扫描线差分值，放入t
        for (int i = 0; i < ih; i++)
        {
            t[i] = 0;
        }
        for (int j = 0; j < ih; j++)
        {
            for (int k = 0; k < iw - 1; k++)
            {
                t[j] += Math.abs(grayPix[j * iw + k + 1]
                                 - grayPix[j * iw + k]);
            }
        }
        // 高斯平滑，选择sgm=0.1,w=8
        float k = 0;
        for (int i = 1; i <= 8; i++)
        {
            k += gauss(i, 0.1f);
        }
        k = k * 2 + 1;
        // System.out.println(k);
        for (int i = 8; i < ih - 8; i++)
        {
            t[i] = (int) smooth(t, i, 8, k, 0.1f);
        }
        shuip=t;
        // 从下到上寻找第一个较明显的波峰（峰值与MAX相差小于MAX/4
        int max = 0;
        int min = t[0];
        for (int i = 0; i < ih; i++)
        {
            if (t[i] > max)
            {
                max = t[i];
            }
            else if (t[i] < min)
            {
                min = t[i];
            }
        }
        for (int i = ih - 18; i > 18; i--)
        {
            if (max - t[i] < max / 4)
            {
                while (t[i - 1] > t[i])
                {
                    i--;
                }
                top = i;
                break;
            }
        }
        top -= 22;
        // 计算车牌所在水平带垂直扫描线的差分值，放入t1
        int[] t1 = new int[iw];
        ih = 35;
        for (int i = 0; i < iw; i++)
        {
            t1[i] = 0;
        }
        for (int i = 0; i < iw; i++)
        {
            for (int j = top; j < top + ih - 1; j++)
            {
                t1[i] += Math.abs(grayPix[(j + 1) * iw + i]
                                  - grayPix[j * iw + i]);
            }
        }
        // 高斯平滑
        for (int i = 8; i < iw - 8; i++)
        {
            t1[i] = (int) smooth(t1, i, 8, k, 0.1f);
        }
        // 查找梯度图中宽度为TmpWidth的区间积分最大的位置
        chuiz=t1;
        max = 0;
        int tmp;
        int TmpWidth = 110;
        for (int i = 0; i < iw - TmpWidth; i++)
        {
            tmp = 0;
            for (int j = i; j < i + TmpWidth; j++)
            {
                tmp += t1[j];
            }
            if (max < tmp)
            {
                max = tmp;
                left = i;
            }
        }
        recLeft = left;
        recTop = top;
        canRect = true;
        System.out.println("--recTop=" + recTop + "--");
        System.out.println("--recLeft=" + recLeft + "--");
    }

    public BufferedImage chePaiQieGe(BufferedImage image)//车牌切割
    {   
    	BufferedImage cutImage = null;
        int iw = image.getWidth();
        int ih = image.getHeight();
        int[] pixArray = new int[iw * ih];
        image.getRGB(0, 0, iw, ih, pixArray, 0, iw);
        int[] cutPicArray = new int[CUT_IMAGE_HIGHT * CUT_IMAGE_WIDTH];
        if (recTop != 0 && recLeft != 0)
        {
            int index = 0;
            for (int i = recTop; i < recTop + CUT_IMAGE_HIGHT; i++)
            {
                for (int j = recLeft; j < recLeft + CUT_IMAGE_WIDTH; j++)
                {
                    cutPicArray[index] = pixArray[i * iw + j];
                    index++;
                }
            }
            BufferedImage bimg = new BufferedImage(CUT_IMAGE_WIDTH,
                    CUT_IMAGE_HIGHT, BufferedImage.TYPE_INT_RGB);
            bimg.setRGB(0, 0, CUT_IMAGE_WIDTH, CUT_IMAGE_HIGHT,
                        cutPicArray, 0, CUT_IMAGE_WIDTH);

            cutImage = bimg;
            isgray = false;
            return cutImage;
        }
        return cutImage;
    }

    public BufferedImage erZhiHua(BufferedImage cutImage)//二值化
    {
        int sum = 0;
        float avg;
        int iw = cutImage.getWidth();
        int ih = cutImage.getHeight();
        int[] pixArray = new int[iw * ih];
        int[] grayPix = new int[iw * ih];
        cutImage.getRGB(0, 0, iw, ih, pixArray, 0, iw);
        int r, g, b;
        int y;
        for (int i = 0; i < pixArray.length; i++)
        {
            b = pixArray[i] & 0x000000ff;
            g = (pixArray[i] & 0x0000ff00) >> 8;
            r = (pixArray[i] & 0x00ff0000) >> 16;
            y = (int) (0.299 * r + 0.587 * g + 0.114 * b);
            grayPix[i] = y;
        }

        for (int i = 0; i < ih; i++)
        {
            for (int j = 0; j < iw; j++)
            {
                sum += grayPix[i * iw + j];
            }
        }
        avg = (float) sum / (float) (iw * ih);
        // 计算灰度图像方差
        float d = 0;
        for (int i = 0; i < ih; i++)
        {
            for (int j = 0; j < iw; j++)
            {
                d += ((grayPix[i * iw + j] - avg) * (grayPix[i * iw + j] - avg));
            }
        }
        d /= (float) (iw * ih);
        // 计算白底黑字牌照阈值
        int t1 = (int) (avg - Math.sqrt(0.3182f * d));
        // 计算黑底白字牌照阈值
        int t2 = (int) (avg + Math.sqrt(0.3182f * d));
        // 用T1进行分类，判断黑点所占比例，如果假设不成立，则使用T2进行分类
        int count = 0;
        for (int i = 0; i < ih; i++)
        {
            for (int j = 0; j < iw; j++)
            {
                if (grayPix[i * iw + j] < t1)
                {
                    count++;
                }
            }
        }
        if ((float) count / (float) (iw * ih) < 0.3182f)
        {
            // 假设不成立，是黑底白字，用T2二值化
            for (int i = 0; i < ih; i++)
            {
                for (int j = 0; j < iw; j++)
                {
                    if (grayPix[i * iw + j] < t2)
                    {
                        pixArray[i * iw + j] = 0xff000000;
                    }
                    else
                    {
                        pixArray[i * iw + j] = 0xffffffff;
                    }
                }
            }
        }
        else
        {
            // 假设成立，用T1二值化
            for (int i = 0; i < ih; i++)
            {
                for (int j = 0; j < iw; j++)
                {
                    if (grayPix[i * iw + j] < t1)
                    {
                        pixArray[i * iw + j] = 0xff000000;
                    }
                    else
                    {
                        pixArray[i * iw + j] = 0xffffffff;
                    }
                }
            }
        }
        // 显示二值化图像
        cutImage.setRGB(0, 0, iw, ih, pixArray, 0, iw);
        return cutImage;

    }

	public static BufferedImage toBufferedImage(Image image)// image to BufferedImage
	{	
	    if (image instanceof BufferedImage) {
	        return (BufferedImage)image;
	     }
	    // This code ensures that all the pixels in the image are loaded
	     image = new ImageIcon(image).getImage();
	    // Determine if the image has transparent pixels; for this method's
	    // implementation, see e661 Determining If an Image Has Transparent Pixels
	    //boolean hasAlpha = hasAlpha(image);
	    // Create a buffered image with a format that's compatible with the screen
	     BufferedImage bimage = null;
	     GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    try {
	        // Determine the type of transparency of the new buffered image
	        int transparency = Transparency.OPAQUE;
	       /* if (hasAlpha) {
	         transparency = Transparency.BITMASK;
	         }*/
	        // Create the buffered image
	
	         GraphicsDevice gs = ge.getDefaultScreenDevice();
	         GraphicsConfiguration gc = gs.getDefaultConfiguration();
	         bimage = gc.createCompatibleImage(
	         image.getWidth(null), image.getHeight(null), transparency);
	     } catch (HeadlessException e) {
	        // The system does not have a screen
	     }
	    if (bimage == null) {
	        // Create a buffered image using the default color model
	        int type = BufferedImage.TYPE_INT_RGB;
	         bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
	     }
	    // Copy image to buffered image
	     Graphics g = bimage.createGraphics();
	    // Paint the image onto the buffered image
	     g.drawImage(image, 0, 0, null);
	     g.dispose();   
	    return bimage;
	}
		
	
	
	
}
