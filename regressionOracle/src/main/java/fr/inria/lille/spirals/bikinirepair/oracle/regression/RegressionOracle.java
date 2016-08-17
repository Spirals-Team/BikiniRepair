package fr.inria.lille.spirals.bikinirepair.oracle.regression;

import fr.inria.lille.spirals.bikinirepair.RequestContent;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RegressionOracle extends Remote {

	boolean isValid(RequestContent responseOrigin, RequestContent responseRegression) throws RemoteException;
}

