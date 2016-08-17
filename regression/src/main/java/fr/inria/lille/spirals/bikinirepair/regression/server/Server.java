package fr.inria.lille.spirals.bikinirepair.regression.server;

import fr.inria.lille.spirals.bikinirepair.regression.RegressionRequest;

import java.rmi.Remote;

public interface Server extends Remote {

	RegressionRequest createRequest(String patch);
}

