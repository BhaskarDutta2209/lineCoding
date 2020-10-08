import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/*
 * @Author 
 */
public class LineCoding extends JFrame implements ActionListener {
	JTextField inpData = new JTextField(12);
	JTextField rcvData = new JTextField(12);
	JComboBox<String> techniques = null;
	JPanel drawPanel = new JPanel();

	//the horizontal and vertical bar length in the drawn graph
	int horizontal = 10;
	int vertical = 40;

	//the origin of the amp vs time graph
	Point origin = new Point(50,100);

	public LineCoding() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1000,400);
		setTitle("Line Coding Techniques....");
		initComponents();
	}

	private void initComponents() {
		JPanel topPanel = new JPanel();
		topPanel.add(new JLabel("Input Data:"));
		topPanel.add(inpData);
		String list[] = {"NRZ-I","NRZ-L","RZ","Manchester","Differential Manchester","AMI","Pseudoternary"};
		techniques = new JComboBox<String>(list);
		topPanel.add(new JLabel("Technique:"));
		topPanel.add(techniques);
		topPanel.add(new JLabel("Received Data:"));
		topPanel.add(rcvData);
		JButton encodeBtn = new JButton("Encode");
		JButton decodeBtn = new JButton("Decode");
		JButton clrBtn = new JButton("Clear");
		topPanel.add(encodeBtn);
		encodeBtn.addActionListener(this);
		topPanel.add(decodeBtn);
		decodeBtn.addActionListener(this);
		topPanel.add(clrBtn);
		clrBtn.addActionListener(this);
		drawPanel.setBackground(Color.WHITE);
		add(drawPanel);
		add(topPanel,BorderLayout.NORTH);
		
	}

	public static void main(String[] args) {
		LineCoding f = new LineCoding();
		f.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Encode")){
			encode();
		}else if(e.getActionCommand().equals("Decode")){
			decode();
		}else if(e.getActionCommand().equals("Clear")){
			repaint();
		}
		
	}

	private void encode() {		
//		drawPanel.getGraphics().drawString("Implement "+techniques.getSelectedItem()+" Encoding Technique!", 400, 100);
//		System.out.println("Ok Boomer");
//		rcvData.setText("Serious Boomer");

		String inputString = inpData.getText();
		int inputLength = inputString.length();
		Point lastPoint = origin;

		if(techniques.getSelectedItem().equals("NRZ-I")) {
//			char[] chars = {'4','q','*'};
//
//			drawPanel.getGraphics().drawLine(10,50,10,130);
//			drawPanel.getGraphics().drawChars(chars,0,2,100,100);
//			drawPanel.getGraphics().setColor(Color.BLUE);
//			drawPanel.getGraphics().drawLine(100,50,100,130);
//			System.out.println(inpData.getText());

//			Point startingPoint = new Point(origin.x,origin.y-(vertical/2));

//			Point lastPoint = draw1ofNRZ_I(50,150,1);
//			lastPoint = draw1ofNRZ_I(lastPoint.x,lastPoint.y,0);
//			draw0ofNRZ_I(lastPoint.x,lastPoint.y);

			int direction = 1;

			//drawing the first bit
			if(inputString.charAt(0) == '0') {
				lastPoint = draw0ofNRZ_I(origin.x,origin.y - (vertical/2));
			} else if(inputString.charAt(0) == '1') {
				lastPoint = draw1ofNRZ_I(origin.x,origin.y + (vertical/2),direction);
				direction = (direction + 1)%2;
			}

			for(int i=1; i<inputLength; i++) {
				char bit = inputString.charAt(i);
				if(bit == '0') {
					lastPoint = draw0ofNRZ_I(lastPoint.x,lastPoint.y);
				} else if(bit == '1') {
					lastPoint = draw1ofNRZ_I(lastPoint.x, lastPoint.y, direction);
					direction = (direction + 1)%2;
				}
			}

		}
		else if(techniques.getSelectedItem().equals("NRZ-L")) {

			//drawing the first bit
			if(inputString.charAt(0) == '0') {
				lastPoint = drawHorizontalBar(origin.x,origin.y - (vertical/2));
			} else if(inputString.charAt(1) == '1') {
				lastPoint = drawHorizontalBar(origin.x,origin.y + (vertical/2));
			}

			for(int i=1; i<inputLength; i++) {
				char presentChar = inputString.charAt(i);
				char prevChar = inputString.charAt(i-1);
				if((presentChar == '0' && prevChar=='0') || (presentChar == '1' && prevChar == '1'))
					lastPoint = drawHorizontalBar(lastPoint.x,lastPoint.y);
				else if(presentChar == '1' && prevChar == '0') {
					lastPoint = drawVerticalBar(lastPoint.x, lastPoint.y, 1);
					lastPoint = drawHorizontalBar(lastPoint.x, lastPoint.y);
				} else if(presentChar == '0' && prevChar == '1') {
					lastPoint = drawVerticalBar(lastPoint.x, lastPoint.y, 0);
					lastPoint = drawHorizontalBar(lastPoint.x, lastPoint.y);
				}
			}

		}
		else if(techniques.getSelectedItem().equals("RZ")) {

			for(int i=0; i<inputLength; i++) {
				char presentBit = inputString.charAt(i);
				if(presentBit == '0')
					lastPoint = draw0ofRZ(lastPoint.x, lastPoint.y);
				else if(presentBit == '1')
					lastPoint = draw1ofRZ(lastPoint.x, lastPoint.y);
			}

		}

	}

	private void decode() {
		drawPanel.getGraphics().drawString("Implement "+techniques.getSelectedItem()+" Decoding Technique!", 400, 100);
	}

	//The below methods are used to draw horizontal and vertical lines

	private Point drawHorizontalBar(int startingPointX, int startingPointY) {
		drawPanel.getGraphics().drawLine(startingPointX,startingPointY,startingPointX+horizontal,startingPointY);
		return new Point(startingPointX+horizontal,startingPointY);
	}

	private Point drawVerticalBar(int startingPointX, int startingPointY, int direction) {
		if(direction == 0) {
			//direction 0 means +ve to -ve
			drawPanel.getGraphics().drawLine(startingPointX,startingPointY,startingPointX,startingPointY+vertical);
			return new Point(startingPointX,startingPointY+vertical);
		} else if(direction == 1) {
			//direction 1 means -ve to +ve
			drawPanel.getGraphics().drawLine(startingPointX,startingPointY,startingPointX,startingPointY-vertical);
			return new Point(startingPointX,startingPointY-vertical);
		}
		return null;
	}

	//The below methods are used to draw lines for NRZ-I encoding technique

	private Point draw1ofNRZ_I(int startingPointX, int startingPointY, int direction) {
		if(direction == 0) {
			//direction 0 means transition from +ve to -ve
			drawPanel.getGraphics().drawLine(startingPointX,startingPointY,startingPointX,startingPointY+vertical);
			drawPanel.getGraphics().drawLine(startingPointX,startingPointY+vertical,startingPointX+horizontal,startingPointY+vertical);
			return new Point(startingPointX+horizontal,startingPointY+vertical);
		} else if(direction == 1) {
			//direction 1 means transition from -ve to +ve
			drawPanel.getGraphics().drawLine(startingPointX,startingPointY,startingPointX,startingPointY-vertical);
			drawPanel.getGraphics().drawLine(startingPointX,startingPointY-vertical,startingPointX+horizontal,startingPointY-vertical);
			return new Point(startingPointX+horizontal,startingPointY-vertical);
		}
		return null;
	}

	private Point draw0ofNRZ_I(int startingPointX, int startingPointY) {
		drawPanel.getGraphics().drawLine(startingPointX,startingPointY,startingPointX+horizontal,startingPointY);
		return new Point(startingPointX+horizontal,startingPointY);
	}

	//The below methods are used to draw 0 and 1 for RZ encoding technique

	private Point draw1ofRZ(int startingPointX, int startingPointY) {
		drawPanel.getGraphics().drawLine(startingPointX,startingPointY,startingPointX,startingPointY-(vertical/2));
		drawPanel.getGraphics().drawLine(startingPointX,startingPointY-(vertical/2),startingPointX+(horizontal/2),startingPointY-(vertical/2));
		drawPanel.getGraphics().drawLine(startingPointX+(horizontal/2),startingPointY-(vertical/2),startingPointX+(horizontal/2),startingPointY);
		drawPanel.getGraphics().drawLine(startingPointX+(horizontal/2),startingPointY,startingPointX+(horizontal),startingPointY);
		return new Point(startingPointX+horizontal,startingPointY);
	}

	private Point draw0ofRZ(int startingPointX, int startingPointY) {
		drawPanel.getGraphics().drawLine(startingPointX,startingPointY,startingPointX,startingPointY+(vertical/2));
		drawPanel.getGraphics().drawLine(startingPointX,startingPointY+(vertical/2),startingPointX+(horizontal/2),startingPointY+(vertical/2));
		drawPanel.getGraphics().drawLine(startingPointX+(horizontal/2),startingPointY+(vertical/2),startingPointX+(horizontal/2),startingPointY);
		drawPanel.getGraphics().drawLine(startingPointX+(horizontal/2),startingPointY,startingPointX+(horizontal),startingPointY);
		return new Point(startingPointX+horizontal,startingPointY);
	}

}