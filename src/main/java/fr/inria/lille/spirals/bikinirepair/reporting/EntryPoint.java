package fr.inria.lille.spirals.bikinirepair.reporting;

import fr.inria.lille.spirals.bikinirepair.services.Services;
import fr.inria.spirals.npefix.resi.context.Lapse;
import fr.inria.spirals.npefix.resi.context.NPEOutput;
import org.json.JSONArray;
import org.json.JSONObject;

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
		List<Lapse> lapses = Services.getPatchServer().getLapses();
		NPEOutput output = new NPEOutput();
		/*for (int i = 0; i < lapses.size(); i++) {
			Lapse lapse = lapses.get(i);
			if (lapse.getOracle().isValid()) {
				output.add(lapse);
			}
		}*/
		output.addAll(lapses);
		JSONObject jsonObject = output.toJSON(Dashboard.spoon);
		if (jsonObject.has("executions")) {
			JSONArray executions = jsonObject.getJSONArray("executions");
			for (int i = 0; i < output.size(); i++) {
				Lapse lapse = output.get(i);
				JSONObject jsonLapse = executions.getJSONObject(i);

			/*Experiment experiment = new Experiment();
			String diff = lapse.toDiff(Dashboard.spoon);
			if (diff != null && !diff.isEmpty()) {
				double score = experiment.probabilityPatch(
						Dashboard.spoon.getModelBuilder().getInputSources()
								.iterator().next(), 3, new FullTokenizer(),
						getChange(diff));
				System.out.println(score);
				jsonLapse.put("perplexity", score);
			} else {
				jsonLapse.put("perplexity", Double.MAX_VALUE);
			}*/
			}
		}

		return jsonObject.toString(2);
	}

	private static String getChange(String diff) {
		StringBuilder output = new StringBuilder();


		String currentPatch = "";
		String[] lines = diff.split("\n");
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (!line.startsWith("+ ")) {
				if (currentPatch != null && !currentPatch.isEmpty()) {
					output.append(currentPatch);
					currentPatch = "";
				}
			} else {
				currentPatch += line.replace("+ ", "") + "\n";
			}
		}

		return output.toString();
	}
}
