package fr.inria.lille.spirals.bikinirepair.regression;

import fr.inria.spirals.npefix.resi.context.Decision;
import fr.inria.spirals.npefix.resi.context.Lapse;
import fr.inria.spirals.npefix.resi.context.Location;
import fr.inria.spirals.npefix.resi.selector.AbstractSelector;
import fr.inria.spirals.npefix.resi.selector.RegressionSelector;
import fr.inria.spirals.npefix.resi.strategies.Strategy;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RegressionSelectorImpl extends AbstractSelector implements RegressionSelector {

	Lapse lapse;
	Location currentLocation = null;

	public RegressionSelectorImpl() {
	}

	@Override
	public <T> Decision<T> select(List<Decision<T>> decisions)  throws RemoteException {
		for (int i = 0; i < lapse.getDecisions().size(); i++) {
			Decision<T> tDecision = lapse.getDecisions().get(i);
			if (tDecision.getLocation().equals(currentLocation)) {
				return tDecision;
			}
		}
		return null;
	}

	@Override
	public List<Strategy> getStrategies() throws RemoteException {
		return getAllStrategies();
	}

	@Override
	public Set<Decision> getSearchSpace() throws RemoteException {
		return new HashSet<Decision>(lapse.getDecisions());
	}

	@Override
	public void setLapse(Lapse lapse) throws RemoteException {
		this.lapse = lapse;
	}


	public boolean isToHandle(Strategy.ACTION action, boolean isNull, Location location) throws RemoteException {
		currentLocation = location;
		if (lapse == null) {
			return false;
		}
		for (int j = 0; j < lapse.getDecisions().size(); j++) {
			Decision decision =  lapse.getDecisions().get(j);
			if (location.equals(decision.getLocation())) {
				return true;
			}
		}
		return false;
	}



}

