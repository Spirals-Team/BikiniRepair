package fr.inria.lille.spirals.bikinirepair.regression;

import fr.inria.lille.spirals.bikinirepair.DockerImage;
import fr.inria.lille.spirals.bikinirepair.Services;
import fr.inria.spirals.npefix.resi.context.Decision;
import fr.inria.spirals.npefix.resi.context.Lapse;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class PatchValidation implements Serializable {

	public PatchValidation() {

	}

	public void run(String image) {
		List<Decision> allDecisions = new ArrayList<>();
		try {
			List<Lapse> lapses = Services.getPatchServer().getLapses();
			for (int i = 0; i < lapses.size(); i++) {
				Lapse lapse =  lapses.get(i);
				if (lapse.getOracle().isValid()) {
					List<Decision> decisions = lapse.getDecisions();
					allDecisions.addAll(decisions);
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < allDecisions.size(); i++) {
			Decision decision = allDecisions.get(i);

			((RegressionSelector)Services.getRegression()).setDecision(decision);

			DockerImage docker = new DockerImage(image);
			docker.run(true, "regression");
		}
	}
}

