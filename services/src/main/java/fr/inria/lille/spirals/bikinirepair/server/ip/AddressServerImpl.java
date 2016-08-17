package fr.inria.lille.spirals.bikinirepair.server.ip;

import java.rmi.RemoteException;

public class AddressServerImpl implements AddressServer {

	private String regressionIp;
	private int regressionPort;
	private String reportingIp;
	private int reportingPort;
	private String applicationIp;
	private int applicationPort;
	private String patchIp;
	private int patchPort;

	@Override
	public String getRegressionIP() throws RemoteException {
		return regressionIp;
	}

	@Override
	public void setRegressionIp(String regressionIp) throws RemoteException {
		this.regressionIp = regressionIp;
	}

	@Override
	public int getRegressionPort() throws RemoteException {
		return regressionPort;
	}

	@Override
	public void setRegressionPort(int regressionPort) throws RemoteException {
		this.regressionPort = regressionPort;
	}



	@Override
	public void setReportingPort(int reportingPort) throws RemoteException {
		this.reportingPort = reportingPort;
	}

	@Override
	public String getReportingIP() throws RemoteException {
		return reportingIp;
	}

	@Override
	public void setReportingIp(String reportingIp) throws RemoteException {
		this.reportingIp = reportingIp;
	}

	@Override
	public int getReportingPort() throws RemoteException {
		return reportingPort;
	}



	@Override
	public String getApplicationIP() throws RemoteException {
		return applicationIp;
	}

	@Override
	public void setApplicationIp(String applicationIp) throws RemoteException {
		this.applicationIp = applicationIp;
	}

	@Override
	public int getApplicationPort() throws RemoteException {
		return applicationPort;
	}

	@Override
	public void setApplicationPort(int applicationPort) throws RemoteException {
		this.applicationPort = applicationPort;
	}



	@Override
	public String getPatchIp() throws RemoteException {
		return patchIp;
	}

	@Override
	public void setPatchIp(String ip) throws RemoteException {
		this.patchIp = ip;
	}

	@Override
	public int getPatchPort() throws RemoteException {
		return patchPort;
	}

	@Override
	public void setPatchPort(int port) throws RemoteException {
		this.patchPort = port;
	}
}

