package fr.inria.lille.spirals.bikinirepair.reporting;

import fr.inria.lille.spirals.bikinirepair.services.Services;
import fr.inria.spirals.npefix.resi.context.Lapse;
import fr.inria.spirals.npefix.resi.context.NPEOutput;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.rmi.RemoteException;
import java.util.List;

@Path("/")
public class EntryPoint {

	@GET
	@Path("decisions")
	@Produces(MediaType.APPLICATION_JSON)
	public String test() throws RemoteException {
		NPEOutput output = new NPEOutput();
		List<Lapse> lapses = Services.getPatchServer().getLapses();
		for (int i = 0; i < lapses.size(); i++) {
			Lapse lapse =  lapses.get(i);
			if (lapse.getOracle() != null && lapse.getOracle().isValid()) {
				output.add(lapse);
			}
		}
		return output.toJSON(null).toString();
	}
}
