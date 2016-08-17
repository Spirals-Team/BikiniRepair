package fr.inria.lille.spirals.bikinirepair.server.ip;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AddressServer extends Remote {

	String getRegressionIP() throws RemoteException;

	void setRegressionIp(String regressionIp) throws RemoteException;

	int getRegressionPort()  throws RemoteException;

	void setRegressionPort(int regressionPort) throws RemoteException;

	String getReportingIP() throws RemoteException;

	void setReportingIp(String reportingIp) throws RemoteException;

	int getReportingPort()  throws RemoteException;

	void setReportingPort(int reportingPort) throws RemoteException;


	String getApplicationIP() throws RemoteException;

	void setApplicationIp(String applicationIp) throws RemoteException;

	int getApplicationPort()  throws RemoteException;

	void setApplicationPort(int applicationPort) throws RemoteException;

	String getPatchIp() throws RemoteException;

	void setPatchIp(String ip) throws RemoteException;

	int getPatchPort()  throws RemoteException;

	void setPatchPort(int port) throws RemoteException;
}

