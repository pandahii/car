package Frame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import java.awt.image.MemoryImageSource;
import java.awt.image.Raster;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.io.File;

import Com.Common;
import Com.ImageEnhance;
import Com.ImageSegment;
import Com.ImageTool;
@SuppressWarnings("rawtypes")
public class MainFrame extends  JFrame {
	
	static final long serialVersionUID = 0;	
	int   iw, ih;
    int[] pixels;   
    ImageEnhance enhance;       
    ImageSegment segment;
	public Common common;
	ImageTool tool;
	
	Image image = null,oimage = null;
	BufferedImage inimage=null,printimage=null;
	MyPanel panel,panel_1;
	FileListPanel filelistpanel;
	FunctionPanel basefunctionpanel;		
	
	
	public void setWindowsUI(){
		try {  
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) 
        {  
            e.printStackTrace();  
        } 
	}	
	
	/*************************************************
     * type - �����ͺ�.   0:Roberts, Roberts1
     *                  1:Kirsch 
     *                  2:Laplace 20:LOG  21:LOG(����)
     *                  3:Prewitt 5:Sobel
     * val  - �Ի������Ĭ��ֵ�ַ���
     * nam  - ���ͼ������ַ���
     *************************************************/ 
	public void imageTrans(int type, String str, String nam)
    {
		Image iImage = this.inimage;
		iw = image.getWidth(null);
		ih = image.getHeight(null);
		switch(type)
		{
		case 1:label2.setText("Kirsch���ӱ�Ե���") ;break;
		case 2:label2.setText("Laplace���ӱ�Ե���");;break; 
		case 20:label2.setText("LOG���ӱ�Ե���");break;
		case 21:label2.setText("LOG(����)���ӱ�Ե���");break;
		case 3:label2.setText("Prewitt���ӱ�Ե���") ;break;
		case 5:label2.setText("Sobel���ӱ�Ե���");break;
		}
    	pixels = common.grabber(iImage, iw, ih);
		if(type != 0)
		    pixels = enhance.detect(pixels, iw, ih, type, 
		             common.getParam(str), true);
		else if(nam.equals("Roberts")){
		    pixels = enhance.robert(pixels, iw, ih, 
	    		     common.getParam(str), true);
		     		label2.setText("Roberts���ӱ�Ե���");}
	    else if(nam.equals("Roberts1")){
	        pixels = enhance.robert1(pixels, iw, ih, 
	    		     common.getParam(str));	
	        		 label2.setText("Roberts1���ӱ�Ե���");}
		//�������е����ز���һ��ͼ��
		ImageProducer ip = new MemoryImageSource(iw, ih, pixels, 0, iw);
		oimage = createImage(ip); 
		printimage = ImageTool.toBufferedImage(oimage);
		panel_1.imageS = printimage;
		panel_1.repaint(); 
    }
	
	public void imageTrans(int type)
	    {  
		 	Image iImage = this.inimage,iImage2 = this.image;
		 	iw = image.getWidth(null);
    		ih = image.getHeight(null);	 
	        int th;
	        byte[] imb;
	    	pixels = common.grabber(iImage, iw, ih);
			switch(type)
			{
				case 6: //1ά��ֵ�ָ�	            
		                th = segment.segment(pixels, iw, ih);	           
		                pixels = common.thSegment(pixels, iw, ih, th);
				        break;
				case 7: //2ά��ֵ�ָ�	            
		                th = segment.segment2(pixels, iw, ih);
		                pixels = common.thSegment(pixels, iw, ih, th);	  
					    break;		
		        case 8: //�����ֵ�ָ�	            
		                th = segment.bestThresh(pixels, iw, ih);
		                pixels = common.thSegment(pixels, iw, ih, th);	  
					    break;
			    case 9: //Otsu�㷨	            
		                th = segment.otsuThresh(pixels, iw, ih);
		                pixels = common.thSegment(pixels, iw, ih, th);	  
					    break;
			    case 10://��Ӱ��
			            int[] pix2 = common.grabber(iImage2, iw, ih);	            
		                pixels = segment.minusImage(pixels, pix2, iw, ih);	                	  
					    break;
			    case 11://ֱ�߼��
			            imb = common.rgb2Bin(pixels, iw, ih);
					    //Hough�任���ֱ��
					    imb = segment.detectLine(imb, iw, ih);				
					    //��2ֵͼ�����image2��ΪARGBͼ������pixels
					    pixels = common.bin2Rgb(imb, iw, ih);	                	  
					    break;
			    case 12://Բ�ܼ��
			            imb = common.rgb2Bin(pixels, iw, ih);
					    //Hough�任���ֱ��
					    imb = segment.detectCirc(imb, iw, ih);				
					    //��2ֵͼ�����image2��ΪARGBͼ������pixels
					    pixels = common.bin2Rgb(imb, iw, ih);	                	  
					    break;
		    }    
		    //�������е����ز���һ��ͼ��
			ImageProducer ip = new MemoryImageSource(iw, ih, pixels, 0, iw);
			oimage = createImage(ip);
			printimage = ImageTool.toBufferedImage(oimage);
			panel_1.imageS = printimage;
			panel_1.repaint(); 
		}
/////////////////////////////////////////////////////////////////////
	
	public MainFrame() {
		
		enhance = new ImageEnhance();
        segment = new ImageSegment();
        common  = new Common(); 
        tool = new ImageTool();
        
        getContentPane().setLayout(null);
        
        initMain();
        
        filelistpanel = new FileListPanel();	
    	basefunctionpanel = new FunctionPanel();
        
		panel = new MyPanel();
		panel.setBounds(10, 10, 400, 380);
		getContentPane().add(panel);
		
		panel_1 = new MyPanel();
		panel_1.setBounds(420, 10, 400, 380);
		getContentPane().add(panel_1);
		
		//********************************************************************	
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//setWindowsUI();
		this.setBounds(120, 50, 1000, 633);
		//this.setVisible(true);
	}
	JLabel label1,label2;
	public void initMain(){
		
		label1 = new JLabel("ԭͼ��");
		label1.setBounds(172, 392, 126, 23);
		getContentPane().add(label1);
		
		label2 = new JLabel("Ч��ͼ");
		label2.setBounds(591, 390, 121, 27);
		getContentPane().add(label2);		
				
	}
		
	public static void main(String[] args){
		MainFrame frame = new MainFrame();
		frame.setVisible(true);
	}
	
	class MyPanel extends JPanel{
		
		private static final long serialVersionUID = 1L;
		static final int CUT_IMAGE_WIDTH = 110;
	    static final int CUT_IMAGE_HIGHT = 30;
	    
		BufferedImage oImage = null,cutImage = null,imageS = null;
		boolean canRect = false,isgray = false; 
	    int recTop = 0,recLeft = 0;	 
	    public void initFlag(){
	    	canRect = false;
	    	isgray = false;
	    }
	    
	    public void clearR(){
	    	Graphics g = this.getGraphics();
	    	g.clearRect(0, 0, this.getWidth(), this.getHeight());
	    }  
	    
	    
	    public void shuipin(Graphics g)//ˮƽֱ��ͼ
	    {	          
	        g.clearRect(0, 0, this.getWidth(), this.getHeight());
	        g.setColor(Color.black);	            
	        g.drawRect(0, 0, this.getWidth(), this.getHeight());
	            
	        g.setColor(Color.blue);	            
	        Raster raster = oImage.getRaster();
	 
	        int[] V = new int[raster.getHeight()];	        
	        int[] values = new int[3];
	        System.out.print("ˮƽ��"+raster.getWidth()+"\n"+"��ֱ"+raster.getHeight());
	        int sum=0;
	        for (int j = 0; j < raster.getHeight(); j++)
	        {
	            for (int i = 0; i < raster.getWidth(); i++)
	            {
	                    sum += raster.getPixel(i, j, values)[0];	                    
	            }
	            V[j]=sum/256;
	            sum = 0;
	        }	              
	        for (int i = 0; i < raster.getHeight(); i++)
	        {	//��.�������(),�յ�����();
	            //g.drawLine( i, 240,  i, 240 - V[i] / 20);
	        	  g.drawLine(0,i,V[i],i);
	        }
	 
	    }
	    
	    public void shuip(Graphics g)//ˮƽֱ��ͼ
	    {	          
	        g.clearRect(0, 0, this.getWidth(), this.getHeight());
	        g.setColor(Color.black);	            
	        g.drawRect(0, 0, this.getWidth(), this.getHeight());
	            
	        g.setColor(Color.blue);	             
	        int[] V =tool.shuip;     
	        for (int i = 0; i < tool.shuip.length; i++)
	        {	//��.�������(),�յ�����();
	            //g.drawLine( i, 240,  i, 240 - V[i] / 20);
	        	  g.drawLine(0,i,V[i]/16,i);
	        }
	 
	    }
	    
	    public void chuiz(Graphics g)//
	    {	          
	        g.clearRect(0, 0, this.getWidth(), this.getHeight());
	        g.setColor(Color.black);	            
	        g.drawRect(0, 0, this.getWidth(), this.getHeight());
	            
	        g.setColor(Color.blue);	             
	        int[] V =tool.chuiz;     
	        for (int i = 0; i < tool.chuiz.length; i++)
	        {	//��.�������(),�յ�����();
	            //g.drawLine( i, 240,  i, 240 - V[i] / 20);
	        	g.drawLine(i,0,i,V[i]/4);
	        }
	 
	    }
	    
	    
	    
	    
	    public void chuizhi(Graphics g)
	    {	          
	        g.clearRect(0, 0, this.getWidth(), this.getHeight());
	        g.setColor(Color.black);	            
	        g.drawRect(0, 0, 266, 240);
	            
	        g.setColor(Color.blue);	            
	        Raster raster = oImage.getRaster();
	 
	        int[] V = new int[raster.getWidth()];	        
	        int[] values = new int[3];
	        System.out.print("ˮƽ��"+raster.getWidth()+"\n"+"��ֱ"+raster.getHeight());
	        int sum=0;
	        for (int i = 0; i < raster.getWidth(); i++)
	        {
	            for (int j = 0; j < raster.getHeight(); j++)
	            {
	                    sum += raster.getPixel(i, j, values)[0];	                    
	            }
	            V[i]=sum/256;
	            sum = 0;
	        }	              
	        for (int i = 0; i < raster.getWidth(); i++)
	        {	//��.�������(),�յ�����();
	            //g.drawLine( i, 240,  i, 240 - V[i] / 20);
	        	  g.drawLine(i,0,i,V[i]);
	        }
	 
	    }
	    	    	   	  	     
		public void paint(Graphics g)
	    {
			if(imageS!=null)
	    	{	
	    		g.clearRect(0, 0, panel.getWidth(),panel.getHeight());        	
	            g.drawImage(imageS,  0,  0, this);        
	    	} 	
	        if (canRect)
	        {
	        	
	            g.setColor(Color.red);
	            g.drawRect(recLeft, recTop, CUT_IMAGE_WIDTH, CUT_IMAGE_HIGHT);
	            
	        }

	        if (oImage != null && isgray)
	        {    
	        	g.clearRect(0, 0, this.getWidth(), this.getHeight());
	            g.setColor(Color.black);	            
	            g.drawRect(0, 0, 266, 240);
	            
	            g.setColor(Color.blue);	            
	            Raster raster = oImage.getRaster();
	            int[] table = new int[256];
	            int[] values = new int[3];
	            for (int i = 0; i < raster.getWidth(); i++)
	            {
	                for (int j = 0; j < raster.getHeight(); j++)
	                {
	                    int index = raster.getPixel(i, j, values)[0];
	                    table[index]++;
	                }
	            }
	            for (int i = 0; i < 256; i++)
	            {
	                g.drawLine( i, 240,  i, 240 - table[i] / 20);
	                switch(i)
	                {
	                case 0:  g.drawString("0", i, 250);break;
	                case 50: g.drawString("50", i, 250);break;
	                case 100:  g.drawString("100", i, 250);break;
	                case 150: g.drawString("150", i, 250);break;
	                case 200:  g.drawString("200", i, 250);break;
	                case 255: g.drawString("255", i, 250);break;
	                default :break;
	                }
	                	
	               
	            }
	        }	    
	    }
	}

	class FileListPanel extends JPanel{
	
		private static final long serialVersionUID = 1L;
		
		JFileChooser fileChooser;
	    MediaTracker tracker = new MediaTracker(this);
	    int N=0;
	    
	    private FileListModel fileListModel;
	    private JList fileList;
	    int selectedIndex = -1;
	    JScrollPane fileListScrollPane;
		
		public FileListPanel(){
			
			this.fileChooser = new JFileChooser();
	        this.fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	        this.fileChooser.setFileFilter(new ImageFileFilter());				     
			
			JLabel lblAsdasd = new JLabel("ͼƬĿ¼");
			lblAsdasd.setBounds(876, 10, 61, 23);
			getContentPane().add(lblAsdasd);
			
			JButton btnZairutupian = new JButton("��");
			btnZairutupian.setBounds(830, 529, 65, 23);
			btnZairutupian.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	openFileAction(evt);
	            }
	        });
			
			getContentPane().add(btnZairutupian);
			
			JButton btnSave = new JButton("����");
			btnSave.setBounds(905, 529, 69, 23);
			btnSave.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent evt) {
	            	saveFileAction(evt);
	            }
	        });
			getContentPane().add(btnSave);
			
			//********************************************************************
			fileListScrollPane = new JScrollPane();
			fileListScrollPane.setBounds(829, 32, 145, 494);
			getContentPane().add(fileListScrollPane);
			fileListScrollPane.setBorder(BorderFactory.createEtchedBorder());
			fileListScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			
			fileList = new JList();
			fileList.setBackground(UIManager.getDefaults().getColor("Panel.background"));
	        fileList.setFont(new Font("Arial", 0, 11));
	        fileList.addListSelectionListener(new ListSelectionListener() {
	            public void valueChanged(ListSelectionEvent evt) {
	                fileListValueChanged(evt);
	            }
	        });

			fileListScrollPane.setViewportView(fileList);
		}
		
		@SuppressWarnings({ "static-access", "unchecked" })
		private void openFileAction(ActionEvent evt) {
			int returnValue;
	        String fileURL;	
	        
	        this.fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	        this.fileChooser.setDialogTitle("  ");
	        
	        returnValue = this.fileChooser.showOpenDialog((Component)evt.getSource());
	        
	        if (returnValue != this.fileChooser.APPROVE_OPTION) return;
	        
	        fileURL = this.fileChooser.getSelectedFile().getAbsolutePath();
	        File selectedFile = new File(fileURL);
	        
	        this.fileListModel = new FileListModel();
	        for (String fileName : selectedFile.list()) {
	            if (!ImageFileFilter.accept(fileName)) continue; // not a image
	            this.fileListModel.addFileListModelEntry(fileName, selectedFile+File.separator+fileName);
	        }
	        this.fileList.setModel(fileListModel);
	        
	    }
		
		@SuppressWarnings("static-access")
		private void saveFileAction(ActionEvent evt) {	
			int returnValue;
	        String fileURL;
	        JFileChooser filechoose = new JFileChooser();
	        filechoose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    filechoose.setDialogTitle("  ");
			if(printimage==null)
			{
				JOptionPane.showMessageDialog(null, "���ȴ�ͼ��!");
			}
			else{
				returnValue = this.fileChooser.showOpenDialog((Component)evt.getSource());
				if (returnValue != this.fileChooser.APPROVE_OPTION) return;
				fileURL = this.fileChooser.getSelectedFile().getAbsolutePath();
				File saveFile = new File(fileURL);
		        common.saveImageAsJPEG(printimage, saveFile);			
			}
	    }
		
		private void fileListValueChanged(ListSelectionEvent evt) {
		        int selectedNow = this.fileList.getSelectedIndex();
		        String path = null;		      
		        if (selectedNow != -1 && this.selectedIndex != selectedNow) {
		            this.selectedIndex = selectedNow;
		            path = ((FileListModel.FileListModelEntry)this.fileListModel.getElementAt(selectedNow)).fullPath;
		            image = common.openImage(path, tracker); 	  
		            image = image.getScaledInstance(panel.getWidth(), panel.getHeight(), Image.SCALE_SMOOTH );		            
		            inimage = ImageTool.toBufferedImage(image);
		            printimage = inimage;
		            panel.imageS = inimage;
		            iw = image.getWidth(null);
		    		ih = image.getHeight(null);	    	
	        		panel.repaint(); 
		        }
		    }
			
	}
	
	class FunctionPanel extends JPanel implements ItemListener, ActionListener{
		private static final long serialVersionUID = 1L;
		
		JButton btnZairutupian,BY_sure_button,fenge_sure_button,Hough_LINE,Hough_ZONE,
				shuiPingTu,chuiZhiTu,huiDuZhiFangTu,huiDuHua,junHengHua,bianYuanJianCe,
				chePaiDingWei,chePaiFenGe,haoMaFenGe,erZhiHua;
		private JTextField textField;
		Choice bianyuan_ch, fenge_ch;
		JLabel label_3;
		
		public FunctionPanel(){
			JPanel panel_2 = new JPanel();
			panel_2.setBackground(Color.GREEN);
			panel_2.setBounds(10, 420, 810, 170);
			getContentPane().add(panel_2);
			panel_2.setLayout(null);
			
			JLabel lblNewLabel_2 = new JLabel("ͼ��ָ�");
			lblNewLabel_2.setBounds(29, 48, 54, 21);
			panel_2.add(lblNewLabel_2);
			
			JLabel lblNewLabel_3 = new JLabel("��Ե���");
			lblNewLabel_3.setBounds(29, 10, 54, 21);
			panel_2.add(lblNewLabel_3);
			
			JLabel lblNewLabel_4 = new JLabel("Hough�任");
			lblNewLabel_4.setBounds(20, 89, 63, 21);
			panel_2.add(lblNewLabel_4);
			
			textField = new JTextField("500");
			textField.setBounds(300, 9, 84, 24);
			panel_2.add(textField);
			textField.setColumns(10);
			
			JLabel label = new JLabel("���뷧ֵ");
			label.setBounds(236, 10, 54, 21);
			panel_2.add(label);
			
			label_3 = new JLabel("����400~600");
			label_3.setBounds(394, 10, 84, 21);
			panel_2.add(label_3);
			
			BY_sure_button = new JButton("ȷ��");
			BY_sure_button.setBounds(488, 9, 93, 23);
			panel_2.add(BY_sure_button);
			BY_sure_button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					bianYuanAction(evt);
				}
			});
			
			fenge_sure_button = new JButton("ȷ��");
			fenge_sure_button.setBounds(488, 47, 93, 23);
			panel_2.add(fenge_sure_button);
			fenge_sure_button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					fengeAction(evt);
				}
			});
			
			Hough_LINE = new JButton("���ֱ��");
			Hough_LINE.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					houghAction(evt);
				}
			});
			Hough_LINE.setBounds(93, 88, 133, 23);
			panel_2.add(Hough_LINE);
			
			Hough_ZONE = new JButton("���Բ��");
			Hough_ZONE.setBounds(251, 88, 133, 23);
			panel_2.add(Hough_ZONE);
			Hough_ZONE.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					houghAction(evt);
				}
			});
			//////////////////////////////////////////////////////
			bianyuan_ch = new Choice();
			bianyuan_ch.add("Kirsch����");
			bianyuan_ch.add("Laplace����");
			bianyuan_ch.add("LOG����");
			bianyuan_ch.add("LOG����(����)");
			bianyuan_ch.add("Prewitt����");
			bianyuan_ch.add("Roberts����");
			bianyuan_ch.add("Roberts����(ƽ�����)");
			bianyuan_ch.add("Sobel����");
			bianyuan_ch.addItemListener(this);
			bianyuan_ch.setBounds(89, 10, 133, 21);
			panel_2.add(bianyuan_ch);
			
			fenge_ch = new Choice();
			fenge_ch.add("1ά��ֵ�ָ�");
			fenge_ch.add("2ά��ֵ�ָ�");
			fenge_ch.add("�����ֵ�ָ�");
			fenge_ch.add("Otsu�㷨");
			fenge_ch.add("��Ӱ��");
			fenge_ch.setBounds(89, 48, 133, 21);
			panel_2.add(fenge_ch);
			
			shuiPingTu = new JButton("ˮƽֱ��ͼ");
			shuiPingTu.setBounds(604, 10, 93, 44);
			shuiPingTu.addActionListener(this);
			panel_2.add(shuiPingTu);
			
			chuiZhiTu = new JButton("��ֱֱ��ͼ");
			chuiZhiTu.setBounds(707, 10, 93, 44);
			chuiZhiTu.addActionListener(this);
			panel_2.add(chuiZhiTu);
			
			
			erZhiHua = new JButton("��ֵ����");
			erZhiHua.setBounds(707, 67, 93, 44);
			erZhiHua.addActionListener(this);
			panel_2.add(erZhiHua);
			
			huiDuHua = new JButton("�ҶȻ�");
			huiDuHua.setBounds(93, 136, 93, 23);
			huiDuHua.addActionListener(this);
			panel_2.add(huiDuHua);
			
			junHengHua = new JButton("���⻯");
			junHengHua.setBounds(197, 136, 93, 23);
			junHengHua.addActionListener(this);
			panel_2.add(junHengHua);
			
			bianYuanJianCe = new JButton("��Ե���");
			bianYuanJianCe.setBounds(300, 136, 93, 23);
			bianYuanJianCe.addActionListener(this);
			panel_2.add(bianYuanJianCe);
			
			chePaiDingWei = new JButton("���ƶ�λ");
			chePaiDingWei.setBounds(403, 136, 93, 23);
			chePaiDingWei.addActionListener(this);
			panel_2.add(chePaiDingWei);
			
			chePaiFenGe = new JButton("���Ʒָ�");
			chePaiFenGe.setBounds(506, 136, 93, 23);
			chePaiFenGe.addActionListener(this);
			panel_2.add(chePaiFenGe);
			
			huiDuZhiFangTu = new JButton("�Ҷ�ֱ��ͼ");
			huiDuZhiFangTu.setBounds(604, 67, 93, 44);
			huiDuZhiFangTu.addActionListener(this);
			panel_2.add(huiDuZhiFangTu);
			
			JLabel lblNewLabel = new JLabel("  Car");
			lblNewLabel.setBounds(20, 140, 54, 15);
			panel_2.add(lblNewLabel);	
		}
		
		public void itemStateChanged(ItemEvent e) {
			int i = bianyuan_ch.getSelectedIndex();			
			switch(i)
			{
			case 0:{
					label_3.setText("(����400~600)");
					textField.setText("500");
						break;
				   }
			case 1:{
					label_3.setText("(����50~150)");
					textField.setText("100");
						break;
					}
			case 2:{
					label_3.setText("(����50~150)");
					textField.setText("100");
						break;
					}
			case 3:{
					label_3.setText("(����50~150)");
					textField.setText("100");	
						break;
					}				
			case 4:{
					label_3.setText("(����100~250)");
					textField.setText("180");	
						break;
					}	
			case 5:{
					label_3.setText("(����30~100)");
					textField.setText("50");	
						break;
					}	
			case 6:{
					label_3.setText("(����20~60)");
					textField.setText("40");
					break;
					}	
			case 7:{
					label_3.setText("(����100~300)");
					textField.setText("200");
						break;
					}	
			default:break;
				}
		}	
		
		private void bianYuanAction(ActionEvent evt) {	
			int i = bianyuan_ch.getSelectedIndex();
			String s = textField.getText();
			if(image==null)
			{
				JOptionPane.showMessageDialog(null, "���ȴ�ͼ��!");
			}
			else{	
				switch(i)
				{
				case 0:{
						imageTrans(1, s, "Kirsch");						
							break;
					   }
				case 1:{
						imageTrans(2, s, "Laplace");   
							break;
						}
				case 2:{
						imageTrans(20, s, "LOG");
							break;
						}
				case 3:{
						imageTrans(21, s, "LOG(����)");  	
							break;
						}				
				case 4:{
						imageTrans( 3, s, "Prewitt");
							break;
						}	
				case 5:{
						imageTrans( 0, s, "Roberts");
							break;
						}	
				case 6:{
						imageTrans( 0, s, "Roberts1");
						break;
						}	
				case 7:{
						imageTrans( 5, s,"Sobel");
							break;
						}	
				default:break;
					}
				
				
		
			}
	    }
		
		private void houghAction(ActionEvent evt) {	
			
			if(image==null)
			{
				JOptionPane.showMessageDialog(null, "���ȴ�ͼ��!");
			}
			else{
				if(evt.getSource()==Hough_LINE){
					imageTrans(11);
				}
				
				if(evt.getSource()==Hough_ZONE){
					imageTrans(12);
				}
			}
		}
			
		private void fengeAction(ActionEvent evt) {
			int i = fenge_ch.getSelectedIndex(),k=i+6;
			
			if(image==null)
			{
				JOptionPane.showMessageDialog(null, "���ȴ�ͼ��!");
			}else{
				
				switch(i)
				{
				case 0:{
							imageTrans(k);
							break;
					   }
				case 1:{
							imageTrans(k);
							break;
						}
				case 2:{
							imageTrans(k);
							break;
						}
				case 3:{
							imageTrans(k);
							break;
						}				
				case 4:{
							imageTrans(k);	
							break;
						}	
				default:break;
					}
				
			}
	    }
		
		public void actionPerformed(ActionEvent e) {
			if(printimage==null)
				{
					JOptionPane.showMessageDialog(null, "���ȴ�ͼ��!");
				}
			else{
			if(e.getSource()== shuiPingTu){
									
				panel.imageS = printimage;
				panel_1.oImage = printimage;
				
				panel.clearR();
				panel.repaint();
				label1.setText(label2.getText());
				
				panel_1.initFlag();								
				panel_1.shuipin(panel_1.getGraphics());
				
			}
			if(e.getSource()== chuiZhiTu){
							
				panel.imageS = printimage;
				panel_1.oImage = printimage;
				
				panel.clearR();
				panel.repaint();															
				panel_1.chuizhi(panel_1.getGraphics());
				
			}

			if(e.getSource()==huiDuHua){
					
					printimage = tool.huiDuTu(inimage);
					panel.imageS = printimage;
					panel_1.oImage = printimage;
					
					panel.clearR();
					panel.repaint(); 
					label1.setText("�Ҷ�ͼ");
					label2.setText("�Ҷ�ֱ��ͼ");
					panel_1.isgray = true;
					panel_1.paint(panel_1.getGraphics());
									
			}
			if(e.getSource()==junHengHua){
							
					printimage = tool.junHengHua(inimage);
					panel_1.oImage = printimage;
					panel.imageS =printimage;
					panel.repaint(); 
					
					label1.setText("���⻯��ͼ��");
					label2.setText("�����ֱ��ͼ");
					
					panel_1.isgray = true;
					panel_1.paint(panel_1.getGraphics());
										
			}
			if(e.getSource()==bianYuanJianCe){
				
					printimage = tool.bianYuanJianCe(inimage);
										
					panel.imageS = inimage;
					panel_1.imageS = printimage;
					panel.repaint(); 
					
					label1.setText("ԭͼ");
					label2.setText("��Ե���ͼ");
					
					//panel_1.isgray = true;
					panel_1.clearR();
					panel_1.paint(panel_1.getGraphics());
								
			}
			if(e.getSource()==chePaiDingWei){
					
					printimage = tool.bianYuanJianCe(inimage);
					tool.chePaiDingWei(printimage);
					panel.imageS = printimage;
					
					label1.setText("ԭͼ");
					//label2.setText("��ֱͶӰֱ��ͼ");
					label2.setText("ˮƽͶӰֱ��ͼ");
					panel.canRect = true; 
					panel.recLeft = tool.recLeft;
					panel.recTop = tool.recTop;
					panel.print(panel.getGraphics());
					panel_1.chuiz(panel_1.getGraphics());
					
				
				
			}
			if(e.getSource()==chePaiFenGe){
				
					printimage = tool.chePaiQieGe(inimage);
					panel_1.imageS = printimage;
						
					panel_1.clearR();					
					panel_1.paint(panel_1.getGraphics());				
				
			}
			if(e.getSource()==erZhiHua){
					printimage = tool.erZhiHua(printimage);
					panel_1.imageS = printimage;
				
					panel_1.clearR();					
					panel_1.paint(panel_1.getGraphics());
				
			}
			if(e.getSource()==huiDuZhiFangTu){
				    printimage = tool.huiDuTu(inimage);
				    panel_1.imageS = printimage;
					
					panel_1.clearR();					
					panel_1.paint(panel_1.getGraphics());
			}
			
			}
			panel_1.initFlag();
		}
	
	}
	
}
