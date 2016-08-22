package fr.inria.lille.spirals.bikinirepair.services;

import fr.inria.lille.spirals.bikinirepair.regression.RegressionSelector;
import fr.inria.lille.spirals.bikinirepair.regression.RegressionSelectorImpl;
import fr.inria.spirals.npefix.config.Config;
import fr.inria.spirals.npefix.resi.selector.Selector;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;

public class Services {

	private static final int registryPort = Config.CONFIG.getServerPort();
	private static final String localIp = getDockerIp();
	private static AddressServerImpl addressServer;
	private static RegressionSelector regressionServer;

	private static String getDockerIp() {
		try {
			Enumeration e = NetworkInterface.getNetworkInterfaces();
			while (e.hasMoreElements()) {
				NetworkInterface n = (NetworkInterface) e.nextElement();
				Enumeration ee = n.getInetAddresses();
				while (ee.hasMoreElements()) {
					InetAddress i = (InetAddress) ee.nextElement();
					if (i instanceof Inet4Address && n.getName().equals("docker0")) {
						return i.getHostAddress();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "localhost";
	}

	public static AddressServer getAddressServer() {
		try {
			Registry registry = LocateRegistry.getRegistry(localIp, registryPort);
			return (AddressServer) registry.lookup("AddressServer");
		} catch (Exception e) {
			return null;
		}
	}

	public static Registry createAddressServer() {
		addressServer = new AddressServerImpl();
		return createServer(addressServer, "AddressServer");
	}

	public static Selector getPatchServer() {
		try {
			Registry registry = LocateRegistry.getRegistry(localIp,
					registryPort);
			return (Selector) registry.lookup("PatchServer");
		} catch (Exception e) {
			return null;
		}
	}

	public static Registry createPatchServer(Selector selector) {
		return createServer(selector, "PatchServer");
	}

	public static RegressionSelector getRegression() {
		try {
			Registry registry = LocateRegistry.getRegistry(localIp, registryPort);
			return (RegressionSelector) registry.lookup("Regression");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Registry createRegressionServer() {
		regressionServer = new RegressionSelectorImpl();
		return createServer(regressionServer, "Regression");
	}

	/**
	 * Create a RMI server
	 *
	 * @param server the remote server
	 * @param name the server name
	 * @return the new registry
	 */
	private static Registry createServer(Remote server, String name) {
		Registry registry;

		int port = registryPort;
		String host = localIp;

		Remote skeleton;
		try {
			skeleton = UnicastRemoteObject.exportObject(server, port);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
		try{
			LocateRegistry.getRegistry(host, port).list();
			registry = LocateRegistry.getRegistry(host, port);
		}catch(Exception ex){
			try{
				registry = LocateRegistry.createRegistry(port);
			} catch(Exception e){
				throw new RuntimeException(e);
			}
		}
		try {
			registry.rebind(name, skeleton);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
		return registry;
	}
}

