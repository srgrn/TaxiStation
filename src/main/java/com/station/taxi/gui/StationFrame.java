package com.station.taxi.gui;

import com.station.taxi.logger.LoggerWrapper;
import com.station.taxi.model.Cab;
import com.station.taxi.model.Passenger;
import com.station.taxi.model.TaxiStation;
import com.station.taxi.model.TaxiStation.StateChangeListener;
import com.station.taxi.spring.StationContext;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
/**
 * TaxiStationMain station frame
 * @author alex
 * @author Eran Zimbler
 * @version 0.2
 */
public class StationFrame extends JFrame implements StateChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private WaitingPanel mWaitingPanel;
	private PassengersPanel mPassangerPanel;
	private DrivingPanel mDrivingPanel;
	private TaxiStation mStation;
	private StationContext mContext;
	
	
	public StationFrame(StationContext context) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			SwingUtilities.updateComponentTreeUI(this);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			LoggerWrapper.logException(StationFrame.class.getSimpleName(), e);
		}
		
		mContext = context;
		
		setTitle(TextsBundle.getString("window_title"));
		
    	setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(getFrameDimension());
		getContentPane().setLayout(new BorderLayout());

		setupViews();
		setLocationRelativeTo(null);
		setVisible(true);

	}

	public StationContext getContext() {
		return mContext;
	}
	
	/**
	 * Initialize frame components
	 */
	private void setupViews() {
		 
		getContentPane().add( new StationToolBar(this) , BorderLayout.NORTH);
		JPanel mainPanel = new JPanel();
		getContentPane().add(mainPanel);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		
		mWaitingPanel = new WaitingPanel(this);
		GridLayout gridLayout = (GridLayout) mWaitingPanel.getLayout();
		gridLayout.setColumns(5);
		mWaitingPanel.setBorder(BorderFactory.createTitledBorder(TextsBundle.getString("cabs_panel_title")));
		mainPanel.add(mWaitingPanel);
		mPassangerPanel = new PassengersPanel(this);
		GridLayout gridLayout_2 = (GridLayout) mPassangerPanel.getLayout();
		gridLayout_2.setColumns(5);
		mPassangerPanel.setBorder(BorderFactory.createTitledBorder(TextsBundle.getString("passengers_panel_title")));
		mainPanel.add(mPassangerPanel);
		mDrivingPanel = new DrivingPanel();
		GridLayout gridLayout_1 = (GridLayout) mDrivingPanel.getLayout();
		gridLayout_1.setColumns(5);
		mDrivingPanel.setBorder(BorderFactory.createTitledBorder(TextsBundle.getString("driving_panel_title")));
		mainPanel.add(mDrivingPanel);
		
		setJMenuBar(new StationMenuBar(this));		
	}
	/**
	 * Calculate frame dimensions based on screen size
	 * @return
	 */
	private static Dimension getFrameDimension() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		return new Dimension((int)(screenSize.width * 0.7), (int)(screenSize.height * 0.7));
	}

	/**
	 * Get station instance
	 * @return
	 */
	public TaxiStation getStation() {
		return mStation;
	}

	@Override
	public void onStationStart(TaxiStation station) {
		mStation = station;
		List<Cab> cabs = station.getCabs();
		for(Cab cab: cabs) {
			placeCabInPanel(cab, -1);
		}
		List<Passenger> pmany = mStation.getPassengers();
		for (Passenger p: pmany) {
			addPassangerToLine(p);
		}	
	}
	

	@Override
	public void onCabUpdate(Cab cab, int newState) {
		placeCabInPanel(cab, newState);			
	}

	@Override
	public void onPassengerUpdate(Passenger p) {
		if(p.leftLine())
		{
			removePassangerFromLine(p);
		}
		else
		{
			updatePassenger(p);
		}
	}

	@Override
	public void onCabAdd(Cab cab) {
		placeCabInPanel(cab, -1);
	}

	@Override
	public void onPassengerAdd(Passenger p) {
		addPassangerToLine(p);
	}

	/**
	 * Put car into appropriate panel if it's already there update will be performed
	 * @param cab
	 * @param oldState
	 */
	private void placeCabInPanel(Cab cab, int oldState) {
		if (cab.isDriving()) {
			if (oldState == TaxiStation.CAB_DRIVE) {
				throw new RuntimeException("Wrong state");
			}
			mWaitingPanel.removeCab(cab);
			mDrivingPanel.addOrUpdateCab(cab);
		} else {
			mDrivingPanel.removeCab(cab);
			mWaitingPanel.addOrUpdateCab(cab);
		}
	}
	
	/**
	 * Add passenger to the panel with passengers that are waiting for taxi
	 * @param p
	 */
	private void addPassangerToLine(Passenger p) {
		mPassangerPanel.addPassanger(p);
	}
	/**
	 * 
	 * @param p
	 */
	private void removePassangerFromLine(Passenger p) {
		mPassangerPanel.removePassanger(p);
		
	}
	private void updatePassenger(Passenger p) {
		mPassangerPanel.updatePassenger(p);
		
	}

	/**
	 * Send event to exit from application
	 */
	public void closeWindow() {
		WindowEvent wev = new WindowEvent(this,WindowEvent.WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
	}

	/**
	 * Shows Add new cab dialog 
	 */
	public void showAddCabDialog() {
		NewCabDialog dialog = new NewCabDialog(this);
		dialog.setVisible(true);	
	}

	/**
	 * Shows Add new passenger dialog 
	 */
	public void showAddPassengerDialog() {
		NewPassengerDialog d= new NewPassengerDialog(this);
		d.setVisible(true);
	}
	/**
	 *  gets the keys from the passangerPanel 
	 */
	public boolean checkPassangerViewExists(String name) {
		return mPassangerPanel.checkPassengerExist(name);
	}

}
