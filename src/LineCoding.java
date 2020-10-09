import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
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

	//defining a list for bauds and a default baud for the first bit
	List<Integer> baudList = new ArrayList<Integer>();
	int baud = 0;

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
			horizontal = 10;
			vertical = 40;
			origin = new Point(50,100);
			baud = 0;
			baudList.clear();
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

			int direction = 1;

			//drawing the first bit
			if(inputString.charAt(0) == '0') {
				baud = -5;
				baudList.add(baud);
				lastPoint = draw0ofNRZ_I(origin.x,origin.y - (vertical/2));
			} else if(inputString.charAt(0) == '1') {
				baud = +5;
				baudList.add(baud);
				lastPoint = draw1ofNRZ_I(origin.x,origin.y + (vertical/2),direction);
				direction = (direction + 1)%2;
			}

			for(int i=1; i<inputLength; i++) {
				char bit = inputString.charAt(i);
				if(bit == '0') {
					baudList.add(baud);
					lastPoint = draw0ofNRZ_I(lastPoint.x,lastPoint.y);
				} else if(bit == '1') {
					baud = -1 * baud;
					baudList.add(baud);
					lastPoint = draw1ofNRZ_I(lastPoint.x, lastPoint.y, direction);
					direction = (direction + 1)%2;
				}
			}

			System.out.println(baudList);

		}
		else if(techniques.getSelectedItem().equals("NRZ-L")) {

			//drawing the first bit
			if(inputString.charAt(0) == '0') {
				baudList.add(-5);
				lastPoint = drawHorizontalBar(origin.x,origin.y - (vertical/2));
			} else if(inputString.charAt(1) == '1') {
				baudList.add(+5);
				lastPoint = drawHorizontalBar(origin.x,origin.y + (vertical/2));
			}

			for(int i=1; i<inputLength; i++) {
				char presentChar = inputString.charAt(i);
				char prevChar = inputString.charAt(i-1);
				if((presentChar == '0' && prevChar=='0') || (presentChar == '1' && prevChar == '1')) {
					lastPoint = drawHorizontalBar(lastPoint.x, lastPoint.y);
					if(presentChar == '0')
						baudList.add(-5);
					else if(presentChar == '1')
						baudList.add(+5);
				}
				else if(presentChar == '1' && prevChar == '0') {
					baudList.add(+5);
					lastPoint = drawVerticalBar(lastPoint.x, lastPoint.y, 1);
					lastPoint = drawHorizontalBar(lastPoint.x, lastPoint.y);
				} else if(presentChar == '0' && prevChar == '1') {
					baudList.add(-5);
					lastPoint = drawVerticalBar(lastPoint.x, lastPoint.y, 0);
					lastPoint = drawHorizontalBar(lastPoint.x, lastPoint.y);
				}
			}

		}
		else if(techniques.getSelectedItem().equals("RZ")) {

			for(int i=0; i<inputLength; i++) {
				char presentBit = inputString.charAt(i);
				if(presentBit == '0') {
					baudList.add(-5);
					baudList.add(0);
					lastPoint = draw0ofRZ(lastPoint.x, lastPoint.y);
				}
				else if(presentBit == '1') {
					baudList.add(+5);
					baudList.add(0);
					lastPoint = draw1ofRZ(lastPoint.x, lastPoint.y);
				}
			}

		}
		else if(techniques.getSelectedItem().equals("Manchester")) {

			//drawing the first bit
			if(inputString.charAt(0) == '0') {
				baudList.add(-5);
				baudList.add(+5);
				lastPoint = draw0ofManchester(origin.x,origin.y+(vertical/2));
			} else if(inputString.charAt(0) == '1') {
				baudList.add(+5);
				baudList.add(-5);
				lastPoint = draw1ofManchester(origin.x,origin.y-(vertical/2));
			}

			for(int i=1; i<inputLength; i++) {
				char presentBit = inputString.charAt(i);
				if(presentBit == '0') {
					baudList.add(-5);
					baudList.add(+5);
					lastPoint = draw0ofManchester(lastPoint.x, lastPoint.y);
				}
				else if(presentBit == '1') {
					baudList.add(+5);
					baudList.add(-5);
					lastPoint = draw1ofManchester(lastPoint.x, lastPoint.y);
				}
			}

		}
		else if(techniques.getSelectedItem().equals("Differential Manchester")) {

			//drawing the first bit
			if(inputString.charAt(0) == '0') {
				baudList.add(-5);
				baudList.add(+5);
				baud = +5;
				drawPanel.getGraphics().drawLine(origin.x,origin.y+(vertical/2),origin.x+(horizontal/2),origin.y+(vertical/2));
				drawPanel.getGraphics().drawLine(origin.x+(horizontal/2),origin.y+(vertical/2),origin.x+(horizontal/2),origin.y-(vertical/2));
				drawPanel.getGraphics().drawLine(origin.x+(horizontal/2),origin.y-(vertical/2),origin.x+horizontal,origin.y-(vertical/2));
				lastPoint = new Point(origin.x+horizontal,origin.y-(vertical/2));
			} else {
				baudList.add(+5);
				baudList.add(-5);
				baud = -5;
				drawPanel.getGraphics().drawLine(origin.x,origin.y-(vertical/2),origin.x+(horizontal/2),origin.y-(vertical/2));
				drawPanel.getGraphics().drawLine(origin.x+(horizontal/2),origin.y-(vertical/2),origin.x+(horizontal/2),origin.y+(vertical/2));
				drawPanel.getGraphics().drawLine(origin.x+(horizontal/2),origin.y+(vertical/2),origin.x+horizontal,origin.y+(vertical/2));
				lastPoint = new Point(origin.x+horizontal,origin.y+(vertical/2));
			}

			for(int i=1; i<inputLength; i++) {
				char presentBit = inputString.charAt(i);
				if(presentBit == '0') {
					baud = baud * -1;
					baudList.add(baud);
					baud = -1*baud;
					baudList.add(baud);
					lastPoint = draw0ofDifferentialManchester(lastPoint.x, lastPoint.y);
				}
				else {
					baudList.add(baud);
					baud = -1*baud;
					baudList.add(baud);
					lastPoint = draw1ofDifferentialManchester(lastPoint.x, lastPoint.y);
				}
			}

		}
		else if(techniques.getSelectedItem().equals("AMI")) {
			int direction = 1;
			baud = +5;
			for(int i=0; i<inputLength; i++) {
				char presentBit = inputString.charAt(i);
				if(presentBit == '0') {
					baudList.add(0);
					lastPoint = drawHorizontalBar(lastPoint.x, lastPoint.y);
				}
				else if(presentBit == '1') {
					baudList.add(baud);
					baud = -1*baud;
					lastPoint = draw1ofAMI(lastPoint.x, lastPoint.y, direction);
					direction = (direction + 1)%2;
				}
			}
		}
		else if(techniques.getSelectedItem().equals("Pseudoternary")) {
			//this is same as AMI with only change of, for 0 voltage alternates.
			//the draw1ofAMI is used here for 0

			int direction = 1;
			baud = +5;

			for(int i=0; i<inputLength; i++) {
				char presentBit = inputString.charAt(i);
				if(presentBit == '0') {
					baudList.add(baud);
					baud = -1*baud;
					lastPoint = draw1ofAMI(lastPoint.x, lastPoint.y, direction);
					direction = (direction+1)%2;
				} else {
					baudList.add(0);
					lastPoint = drawHorizontalBar(lastPoint.x, lastPoint.y);
				}
			}
		}
		System.out.println(baudList);
	}

	private void decode() {
//		drawPanel.getGraphics().drawString("Implement "+techniques.getSelectedItem()+" Decoding Technique!", 400, 100);

		if(techniques.getSelectedItem().equals("NRZ-I"))
			rcvData.setText(decodeNRZ_I(baudList));
		else if(techniques.getSelectedItem().equals("NRZ-L"))
			rcvData.setText(decodeNRZ_L(baudList));
		else if(techniques.getSelectedItem().equals("RZ"))
			rcvData.setText(decodeRZ(baudList));
		else if(techniques.getSelectedItem().equals("Manchester"))
			rcvData.setText(decodeManchester(baudList));
		else if(techniques.getSelectedItem().equals("Differential Manchester"))
			rcvData.setText(decodeDifferentialManchester(baudList));
		else if(techniques.getSelectedItem().equals("AMI"))
			rcvData.setText(decodeAMI(baudList));
		else if(techniques.getSelectedItem().equals("Pseudoternary"))
			rcvData.setText(decodePseudoternary(baudList));

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

	//The below methods are used to draw 0 and 1 for Manchester encoding technique

	private Point draw0ofManchester(int startingPointX, int startingPointY) {
		if(startingPointY < origin.y) {
			//this means the previous end point was in +ve side i.e. this will be out of phase
			drawPanel.getGraphics().drawLine(startingPointX,startingPointY,startingPointX,startingPointY+vertical);
			//setting the new starting point y coordinate
			startingPointY = startingPointY+vertical;
		}

		drawPanel.getGraphics().drawLine(startingPointX,startingPointY,startingPointX+(horizontal/2),startingPointY);
		drawPanel.getGraphics().drawLine(startingPointX+(horizontal/2),startingPointY,startingPointX+(horizontal/2),startingPointY-vertical);
		drawPanel.getGraphics().drawLine(startingPointX+(horizontal/2),startingPointY-vertical,startingPointX+horizontal,startingPointY-vertical);

		return new Point(startingPointX+horizontal,startingPointY-vertical);
	}

	private Point draw1ofManchester(int startingPointX, int startingPointY) {
		if(startingPointY > origin.y) {
			//this means the previous end point was in -ve side i.e. this will be out of phase
			drawPanel.getGraphics().drawLine(startingPointX,startingPointY,startingPointX,startingPointY-vertical);
			//setting the new starting point y coordinate
			startingPointY = startingPointY-vertical;
		}

		drawPanel.getGraphics().drawLine(startingPointX,startingPointY,startingPointX+(horizontal/2),startingPointY);
		drawPanel.getGraphics().drawLine(startingPointX+(horizontal/2),startingPointY,startingPointX+(horizontal/2),startingPointY+vertical);
		drawPanel.getGraphics().drawLine(startingPointX+(horizontal/2),startingPointY+vertical,startingPointX+horizontal,startingPointY+vertical);

		return new Point(startingPointX+horizontal,startingPointY+vertical);
	}

	//The below methods are used to draw 0 and 1 for Differential Manchester Encoding

	private Point draw1ofDifferentialManchester(int startingPointX, int startingPointY) {
		drawPanel.getGraphics().drawLine(startingPointX,startingPointY,startingPointX+(horizontal/2),startingPointY);
		if(startingPointY < origin.y) {
			//this means in +ve side
			drawPanel.getGraphics().drawLine(startingPointX+(horizontal/2),startingPointY,startingPointX+(horizontal/2),startingPointY+vertical);
			drawPanel.getGraphics().drawLine(startingPointX+(horizontal/2),startingPointY+vertical,startingPointX+horizontal,startingPointY+vertical);
			return new Point(startingPointX+horizontal,startingPointY+vertical);
		} else {
			//this means in -ve side
			drawPanel.getGraphics().drawLine(startingPointX+(horizontal/2),startingPointY,startingPointX+(horizontal/2),startingPointY-vertical);
			drawPanel.getGraphics().drawLine(startingPointX+(horizontal/2),startingPointY-vertical,startingPointX+horizontal,startingPointY-vertical);
			return new Point(startingPointX+horizontal,startingPointY-vertical);
		}
	}

	private Point draw0ofDifferentialManchester(int startingPointX, int startingPointY) {
		if(startingPointY < origin.y) {
			//this means in +ve side
			drawPanel.getGraphics().drawLine(startingPointX,startingPointY,startingPointX,startingPointY+vertical);
			drawPanel.getGraphics().drawLine(startingPointX,startingPointY+vertical,startingPointX+(horizontal/2),startingPointY+vertical);
			drawPanel.getGraphics().drawLine(startingPointX+(horizontal/2),startingPointY+vertical,startingPointX+(horizontal/2),startingPointY);
			drawPanel.getGraphics().drawLine(startingPointX+(horizontal/2),startingPointY,startingPointX+horizontal,startingPointY);
			return new Point(startingPointX+horizontal,startingPointY);
		} else {
			//this means in -ve side
			drawPanel.getGraphics().drawLine(startingPointX,startingPointY,startingPointX,startingPointY-vertical);
			drawPanel.getGraphics().drawLine(startingPointX,startingPointY-vertical,startingPointX+(horizontal/2),startingPointY-vertical);
			drawPanel.getGraphics().drawLine(startingPointX+(horizontal/2),startingPointY-vertical,startingPointX+(horizontal/2),startingPointY);
			drawPanel.getGraphics().drawLine(startingPointX+(horizontal/2),startingPointY,startingPointX+horizontal,startingPointY);
			return new Point(startingPointX+horizontal,startingPointY);
		}
	}

	//The below methods is used to draw 1 in AMI encoding technique

	private Point draw1ofAMI(int startingPointX, int startingPointY, int direction) {
		if(direction == 1) {
			//this means 1 is in the +ve side
			drawPanel.getGraphics().drawLine(startingPointX,startingPointY,startingPointX,startingPointY-vertical);
			drawPanel.getGraphics().drawLine(startingPointX,startingPointY-vertical,startingPointX+horizontal,startingPointY-vertical);
			drawPanel.getGraphics().drawLine(startingPointX+horizontal,startingPointY-vertical,startingPointX+horizontal,startingPointY);
			return new Point(startingPointX+horizontal,startingPointY);
		} else if(direction == 0) {
			//this means 1 is in the -ve side
			drawPanel.getGraphics().drawLine(startingPointX,startingPointY,startingPointX,startingPointY+vertical);
			drawPanel.getGraphics().drawLine(startingPointX,startingPointY+vertical,startingPointX+horizontal,startingPointY+vertical);
			drawPanel.getGraphics().drawLine(startingPointX+horizontal,startingPointY+vertical,startingPointX+horizontal,startingPointY);
			return new Point(startingPointX+horizontal,startingPointY);
		}
		return null;
	}

	//The below function is used to decode NRZ-I baud array
	private String decodeNRZ_I(List<Integer> receivedBaudList) {
		String result = "";
		int lengthOfList = receivedBaudList.size();
		int previousBaud = 0;

		if(receivedBaudList.get(0) == -5) {
			result = result+"0";
			previousBaud = -5;
		} else if(receivedBaudList.get(0) == 5) {
			result = result+"1";
			previousBaud = 5;
		}
		for(int i=1; i<lengthOfList; i++) {
			if(receivedBaudList.get(i).equals(previousBaud)) {
				result = result + "0";
				previousBaud = receivedBaudList.get(i);
			}
			else {
				result = result + "1";
				previousBaud = receivedBaudList.get(i);
			}
		}

		return result;
	}

	//The below function is used to decode NRZ-L baud array
	private String decodeNRZ_L(List<Integer> receivedBaudList) {
		String result = "";
		for(int i : receivedBaudList) {
			if(i == -5)
				result = result + "0";
			else if(i == 5)
				result = result + "1";
		}
		return result;
	}

	//The below function is used to decode RZ baud array
	private String decodeRZ(List<Integer> receivedBaudList) {
		int receivedLength = receivedBaudList.size();
		String result = "";
		for(int i=0; i<receivedLength; i+=2) {
			if(receivedBaudList.get(i) == -5)
				result = result + "0";
			else if(receivedBaudList.get(i) == 5)
				result = result + "1";
		}
		return result;
	}

	//The below function is used to decode Manchester baud array
	private String decodeManchester(List<Integer> receivedBaudList) {
		int receivedLength = receivedBaudList.size();
		String result = "";
		for(int i=0; i<receivedLength; i+=2) {
			if(receivedBaudList.get(i) == 5 && receivedBaudList.get(i+1) == -5)
				result = result + "1";
			else if(receivedBaudList.get(i) == -5 && receivedBaudList.get(i+1) == 5)
				result = result + "0";
		}
		return result;
	}

	//The below function is used to decode Differential Manchester baud array
	private String decodeDifferentialManchester(List<Integer> receivedBaudList) {
		int receivedLength = receivedBaudList.size();
		String result = "";
		if(receivedBaudList.get(0) == -5)
			result = result + "0";
		else if(receivedBaudList.get(0) == +5)
			result = result + "1";

		for(int i=2; i<receivedLength; i+=2) {
			if(receivedBaudList.get(i) == receivedBaudList.get(i-1))
				result = result + "1";
			else if(receivedBaudList.get(i) == (-1 * receivedBaudList.get(i-1)))
				result = result + "0";
		}

		return result;
	}

	//The below function is used to decode AMI baud array
	private String decodeAMI(List<Integer> receivedBaudList) {
		String result = "";

		for(int i : receivedBaudList)
			result += (i == 0) ? "0" : "1";

		return  result;
	}

	//The below function is used to decode Pseudoternary
	private String decodePseudoternary(List<Integer> receivedBaudList) {
		String result = "";
		for(int i : receivedBaudList)
			result += (i == 0) ? "1" : "0";

		return result;
	}

}