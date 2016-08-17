package fr.inria.lille.spirals.bikinirepair.regression;

import fr.inria.spirals.npefix.resi.context.Decision;
import fr.inria.spirals.npefix.resi.selector.AbstractSelector;
import fr.inria.spirals.npefix.resi.strategies.Strategy;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RegressionSelector extends AbstractSelector {

	Decision decision;

	public RegressionSelector() {
	}

	public RegressionSelector(Decision decision) {
		this.decision = decision;
	}

	@Override
	public <T> Decision<T> select(List<Decision<T>> decisions)  throws RemoteException {
		for (int i = 0; i < decisions.size(); i++) {
			Decision<T> tDecision = decisions.get(i);
			if (tDecision.equals(decision)) {
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
		return new HashSet<>(Collections.singleton(decision));
	}

	public void setDecision(Decision decision) {
		this.decision = decision;
	}
}

