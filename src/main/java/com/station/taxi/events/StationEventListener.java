package com.station.taxi.events;

import com.station.taxi.model.Cab;
import com.station.taxi.model.Passenger;
import com.station.taxi.model.Receipt;

/**
 * 
 * @author alex
 * @author Eran Zimbler
 * @version 0.2
 */
public interface StationEventListener {
	/**
	 * Called when cab thread is ready
	 * @param Cab cab
	 */
	public void onCabReady(Cab cab);
	/**
	 * 
	 * @param cab
	 * @param receipt 
	 */
	public void onCabArrival(Cab cab, Receipt receipt);
	/**
	 * Cab request break event
	 * @param cab
	 */
	public void onBreakRequest(Cab cab);
	/**
	 * Cab request waiting event after arriving to the destination
	 * @param Cab cab
	 */
	public void onWaitingRequest(Cab cab);
	/**
	 * Passenger request Exit event
	 * @param Passenger p
	 */
	public void onExitRequest(Passenger p);
	/**
	 * called when Passenger object is ready
	 * @param Passenger p
	 */
	public void onPassengerReady(Passenger p);
	/**
	 * called when a Passenger object is updated
	 * @param Passenger p
	 */
	public void onPassengerUpdate(Passenger p);
}
