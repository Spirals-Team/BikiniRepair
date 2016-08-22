package fr.inria.lille.spirals.bikinirepair.regression;

import fr.inria.spirals.npefix.resi.context.Decision;
import fr.inria.spirals.npefix.resi.selector.Selector;

import java.rmi.RemoteException;

public interface RegressionSelector extends Selector {
	void setDecision(Decision decision) throws RemoteException;
}
